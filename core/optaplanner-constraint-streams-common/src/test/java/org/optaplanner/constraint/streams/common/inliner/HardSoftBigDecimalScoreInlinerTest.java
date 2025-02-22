package org.optaplanner.constraint.streams.common.inliner;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.score.TestdataHardSoftBigDecimalScoreSolution;

class HardSoftBigDecimalScoreInlinerTest
        extends AbstractScoreInlinerTest<TestdataHardSoftBigDecimalScoreSolution, HardSoftBigDecimalScore> {

    @Test
    void defaultScore() {
        HardSoftBigDecimalScoreInliner scoreInliner =
                new HardSoftBigDecimalScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardSoftBigDecimalScore.ZERO);
    }

    @Test
    void impactHard() {
        HardSoftBigDecimalScoreInliner scoreInliner =
                new HardSoftBigDecimalScoreInliner(constraintMatchEnabled);

        HardSoftBigDecimalScore constraintWeight = HardSoftBigDecimalScore.ofHard(BigDecimal.valueOf(90));
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.ONE, JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.valueOf(90), BigDecimal.ZERO));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(2), JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.valueOf(270), BigDecimal.ZERO));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.valueOf(90), BigDecimal.ZERO));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.valueOf(0), BigDecimal.ZERO));
    }

    @Test
    void impactSoft() {
        HardSoftBigDecimalScoreInliner scoreInliner =
                new HardSoftBigDecimalScoreInliner(constraintMatchEnabled);

        HardSoftBigDecimalScore constraintWeight = HardSoftBigDecimalScore.ofSoft(BigDecimal.valueOf(90));
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.ONE, JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.valueOf(90)));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(2), JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.valueOf(270)));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.valueOf(90)));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Test
    void impactAll() {
        HardSoftBigDecimalScoreInliner scoreInliner =
                new HardSoftBigDecimalScoreInliner(constraintMatchEnabled);

        HardSoftBigDecimalScore constraintWeight = HardSoftBigDecimalScore.of(BigDecimal.valueOf(10), BigDecimal.valueOf(100));
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.TEN, JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.valueOf(100), BigDecimal.valueOf(1000)));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(20), JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.valueOf(300), BigDecimal.valueOf(3000)));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.valueOf(100), BigDecimal.valueOf(1000)));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Override
    protected SolutionDescriptor<TestdataHardSoftBigDecimalScoreSolution> buildSolutionDescriptor() {
        return TestdataHardSoftBigDecimalScoreSolution.buildSolutionDescriptor();
    }
}
