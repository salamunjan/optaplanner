package org.optaplanner.examples.common.domain;

import org.optaplanner.core.api.domain.lookup.PlanningId;

public abstract class AbstractPersistableJackson {

    protected Long id;

    protected AbstractPersistableJackson() { // For Jackson.
    }

    protected AbstractPersistableJackson(long id) {
        this.id = id;
    }

    @PlanningId
    public long getId() {
        return id;
    }

    protected void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getClass().getName().replaceAll(".*\\.", "") + "-" + id;
    }

}
