package org.optaplanner.examples.pas.domain;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * AKA RoomProperty.
 */
@JsonIdentityInfo(scope = Equipment.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Equipment extends AbstractPersistableJackson {

    private String name;

    public Equipment() { // For Jackson.
    }

    public Equipment(long id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
