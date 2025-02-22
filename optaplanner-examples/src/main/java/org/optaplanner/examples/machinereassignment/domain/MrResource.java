package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class MrResource extends AbstractPersistableJackson {

    private int index;
    private boolean transientlyConsumed;
    private int loadCostWeight;

    @SuppressWarnings("unused")
    MrResource() { // For Jackson.
    }

    public MrResource(long id) {
        super(id);
    }

    public MrResource(int index, boolean transientlyConsumed, int loadCostWeight) {
        this.index = index;
        this.transientlyConsumed = transientlyConsumed;
        this.loadCostWeight = loadCostWeight;
    }

    public MrResource(long id, int index, boolean transientlyConsumed, int loadCostWeight) {
        super(id);
        this.index = index;
        this.transientlyConsumed = transientlyConsumed;
        this.loadCostWeight = loadCostWeight;
    }

    public int getIndex() {
        return index;
    }

    public boolean isTransientlyConsumed() {
        return transientlyConsumed;
    }

    public int getLoadCostWeight() {
        return loadCostWeight;
    }

}
