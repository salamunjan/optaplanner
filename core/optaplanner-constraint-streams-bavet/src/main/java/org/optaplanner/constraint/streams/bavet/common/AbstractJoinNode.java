package org.optaplanner.constraint.streams.bavet.common;

import java.util.ArrayDeque;
import java.util.Queue;

import org.optaplanner.constraint.streams.bavet.common.collection.TupleList;
import org.optaplanner.constraint.streams.bavet.common.collection.TupleListEntry;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

/**
 * This class has two direct children: {@link AbstractIndexedJoinNode} and {@link AbstractUnindexedJoinNode}.
 * The logic in either is identical, except that the latter removes all indexing work.
 * Therefore any time that one of the classes changes,
 * the other should be inspected if it could benefit from applying the change there too.
 *
 * @param <LeftTuple_>
 * @param <Right_>
 */
public abstract class AbstractJoinNode<LeftTuple_ extends Tuple, Right_, OutTuple_ extends Tuple, MutableOutTuple_ extends OutTuple_>
        extends AbstractNode
        implements LeftTupleLifecycle<LeftTuple_>, RightTupleLifecycle<UniTuple<Right_>> {

    protected final int inputStoreIndexLeftOutTupleList;
    protected final int inputStoreIndexRightOutTupleList;
    /**
     * Calls for example {@link AbstractScorer#insert(Tuple)} and/or ...
     */
    private final TupleLifecycle<OutTuple_> nextNodesTupleLifecycle;

    protected final boolean isFiltering;

    protected final int outputStoreIndexLeftOutEntry;
    protected final int outputStoreIndexRightOutEntry;
    protected final Queue<OutTuple_> dirtyTupleQueue;

    protected AbstractJoinNode(int inputStoreIndexLeftOutTupleList, int inputStoreIndexRightOutTupleList,
            TupleLifecycle<OutTuple_> nextNodesTupleLifecycle, boolean isFiltering,
            int outputStoreIndexLeftOutEntry, int outputStoreIndexRightOutEntry) {
        this.inputStoreIndexLeftOutTupleList = inputStoreIndexLeftOutTupleList;
        this.inputStoreIndexRightOutTupleList = inputStoreIndexRightOutTupleList;
        this.nextNodesTupleLifecycle = nextNodesTupleLifecycle;
        this.isFiltering = isFiltering;
        this.outputStoreIndexLeftOutEntry = outputStoreIndexLeftOutEntry;
        this.outputStoreIndexRightOutEntry = outputStoreIndexRightOutEntry;
        dirtyTupleQueue = new ArrayDeque<>(1000);
    }

    protected abstract MutableOutTuple_ createOutTuple(LeftTuple_ leftTuple, UniTuple<Right_> rightTuple);

    protected abstract void setOutTupleLeftFacts(MutableOutTuple_ outTuple, LeftTuple_ leftTuple);

    protected abstract void setOutTupleRightFact(MutableOutTuple_ outTuple, UniTuple<Right_> rightTuple);

    protected abstract boolean testFiltering(LeftTuple_ leftTuple, UniTuple<Right_> rightTuple);

    protected final void insertOutTuple(LeftTuple_ leftTuple, UniTuple<Right_> rightTuple) {
        MutableOutTuple_ outTuple = createOutTuple(leftTuple, rightTuple);
        TupleList<MutableOutTuple_> outTupleListLeft = leftTuple.getStore(inputStoreIndexLeftOutTupleList);
        TupleListEntry<MutableOutTuple_> outEntryLeft = outTupleListLeft.add(outTuple);
        outTuple.setStore(outputStoreIndexLeftOutEntry, outEntryLeft);
        TupleList<MutableOutTuple_> outTupleListRight = rightTuple.getStore(inputStoreIndexRightOutTupleList);
        TupleListEntry<MutableOutTuple_> outEntryRight = outTupleListRight.add(outTuple);
        outTuple.setStore(outputStoreIndexRightOutEntry, outEntryRight);
        dirtyTupleQueue.add(outTuple);
    }

    protected void updateOutTupleLeft(MutableOutTuple_ outTuple, LeftTuple_ leftTuple) {
        setOutTupleLeftFacts(outTuple, leftTuple);
        doUpdateOutTuple(outTuple);
    }

    protected void updateOutTupleRight(MutableOutTuple_ outTuple, UniTuple<Right_> rightTuple) {
        setOutTupleRightFact(outTuple, rightTuple);
        doUpdateOutTuple(outTuple);
    }

    private final void doUpdateOutTuple(OutTuple_ outTuple) {
        switch (outTuple.getState()) {
            case CREATING:
            case UPDATING:
                // Don't add the tuple to the dirtyTupleQueue twice
                break;
            case OK:
                outTuple.setState(BavetTupleState.UPDATING);
                dirtyTupleQueue.add(outTuple);
                break;
            // Impossible because they shouldn't linger in the indexes
            case DYING:
            case ABORTING:
            case DEAD:
            default:
                throw new IllegalStateException("Impossible state: The tuple (" + outTuple.getState() + ") in node (" +
                        this + ") is in an unexpected state (" + outTuple.getState() + ").");
        }
    }

    protected final void retractOutTuple(MutableOutTuple_ outTuple) {
        TupleListEntry<MutableOutTuple_> outEntryLeft = outTuple.removeStore(outputStoreIndexLeftOutEntry);
        outEntryLeft.remove();
        TupleListEntry<MutableOutTuple_> outEntryRight = outTuple.removeStore(outputStoreIndexRightOutEntry);
        outEntryRight.remove();
        switch (outTuple.getState()) {
            case CREATING:
                // Don't add the tuple to the dirtyTupleQueue twice
                // Kill it before it propagates
                outTuple.setState(BavetTupleState.ABORTING);
                break;
            case OK:
                outTuple.setState(BavetTupleState.DYING);
                dirtyTupleQueue.add(outTuple);
                break;
            case UPDATING:
                // Don't add the tuple to the dirtyTupleQueue twice
                // Kill the original propagation
                outTuple.setState(BavetTupleState.DYING);
                break;
            // Impossible because they shouldn't linger in the indexes
            case DYING:
            case ABORTING:
            case DEAD:
            default:
                throw new IllegalStateException("Impossible state: The tuple (" + outTuple.getState() + ") in node (" +
                        this + ") is in an unexpected state (" + outTuple.getState() + ").");
        }
    }

    @Override
    public final void calculateScore() {
        for (OutTuple_ tuple : dirtyTupleQueue) {
            switch (tuple.getState()) {
                case CREATING:
                    nextNodesTupleLifecycle.insert(tuple);
                    tuple.setState(BavetTupleState.OK);
                    break;
                case UPDATING:
                    nextNodesTupleLifecycle.update(tuple);
                    tuple.setState(BavetTupleState.OK);
                    break;
                case DYING:
                    nextNodesTupleLifecycle.retract(tuple);
                    tuple.setState(BavetTupleState.DEAD);
                    break;
                case ABORTING:
                    tuple.setState(BavetTupleState.DEAD);
                    break;
                case OK:
                case DEAD:
                default:
                    throw new IllegalStateException("Impossible state: The tuple (" + tuple + ") in node (" +
                            this + ") is in an unexpected state (" + tuple.getState() + ").");
            }
        }
        dirtyTupleQueue.clear();
    }

}
