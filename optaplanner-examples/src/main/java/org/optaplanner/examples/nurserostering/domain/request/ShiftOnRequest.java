package org.optaplanner.examples.nurserostering.domain.request;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.Shift;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class ShiftOnRequest extends AbstractPersistableJackson {

    private Employee employee;
    private Shift shift;
    private int weight;

    public ShiftOnRequest() { // For Jackson.
    }

    public ShiftOnRequest(long id, Employee employee, Shift shift, int weight) {
        super(id);
        this.employee = employee;
        this.shift = shift;
        this.weight = weight;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return shift + "_ON_" + employee;
    }

}
