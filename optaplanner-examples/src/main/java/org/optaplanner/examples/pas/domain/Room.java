package org.optaplanner.examples.pas.domain;

import java.util.List;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = Room.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Room extends AbstractPersistableJackson implements Labeled {

    private String name;

    private Department department;
    private int capacity;
    private GenderLimitation genderLimitation;

    private List<RoomSpecialism> roomSpecialismList;
    private List<RoomEquipment> roomEquipmentList;
    private List<Bed> bedList;

    public Room() { // For Jackson.
    }

    public Room(long id, String name, Department department, int capacity, GenderLimitation genderLimitation) {
        super(id);
        this.name = name;
        this.department = department;
        this.capacity = capacity;
        this.genderLimitation = genderLimitation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public GenderLimitation getGenderLimitation() {
        return genderLimitation;
    }

    public void setGenderLimitation(GenderLimitation genderLimitation) {
        this.genderLimitation = genderLimitation;
    }

    public List<RoomSpecialism> getRoomSpecialismList() {
        return roomSpecialismList;
    }

    public void setRoomSpecialismList(List<RoomSpecialism> roomSpecialismList) {
        this.roomSpecialismList = roomSpecialismList;
    }

    public List<RoomEquipment> getRoomEquipmentList() {
        return roomEquipmentList;
    }

    public void setRoomEquipmentList(List<RoomEquipment> roomEquipmentList) {
        this.roomEquipmentList = roomEquipmentList;
    }

    public List<Bed> getBedList() {
        return bedList;
    }

    public void setBedList(List<Bed> bedList) {
        this.bedList = bedList;
    }

    public int countHardDisallowedAdmissionPart(AdmissionPart admissionPart) {
        return countMissingRequiredRoomProperties(admissionPart.getPatient())
                + department.countHardDisallowedAdmissionPart(admissionPart)
                + countDisallowedPatientGender(admissionPart.getPatient());
        // TODO preferredMaximumRoomCapacity and specialism
    }

    public int countMissingRequiredRoomProperties(Patient patient) {
        int count = 0;
        for (RequiredPatientEquipment requiredPatientEquipment : patient.getRequiredPatientEquipmentList()) {
            Equipment requiredEquipment = requiredPatientEquipment.getEquipment();
            boolean hasRequiredEquipment = false;
            for (RoomEquipment roomEquipment : roomEquipmentList) {
                if (roomEquipment.getEquipment().equals(requiredEquipment)) {
                    hasRequiredEquipment = true;
                }
            }
            if (!hasRequiredEquipment) {
                count += 100000;
            }
        }
        return count;
    }

    public int countDisallowedPatientGender(Patient patient) {
        switch (genderLimitation) {
            case ANY_GENDER:
                return 0;
            case MALE_ONLY:
                return patient.getGender() == Gender.MALE ? 0 : 4;
            case FEMALE_ONLY:
                return patient.getGender() == Gender.FEMALE ? 0 : 4;
            case SAME_GENDER:
                // Constraints check this
                return 1;
            default:
                throw new IllegalStateException("The genderLimitation (" + genderLimitation + ") is not implemented.");
        }
    }

    public int countSoftDisallowedAdmissionPart(AdmissionPart admissionPart) {
        return countMissingPreferredRoomProperties(admissionPart.getPatient());
        // TODO preferredMaximumRoomCapacity and specialism
    }

    public int countMissingPreferredRoomProperties(Patient patient) {
        int count = 0;
        for (PreferredPatientEquipment preferredPatientEquipment : patient.getPreferredPatientEquipmentList()) {
            Equipment preferredEquipment = preferredPatientEquipment.getEquipment();
            boolean hasPreferredEquipment = false;
            for (RoomEquipment roomEquipment : roomEquipmentList) {
                if (roomEquipment.getEquipment().equals(preferredEquipment)) {
                    hasPreferredEquipment = true;
                }
            }
            if (!hasPreferredEquipment) {
                count += 20;
            }
        }
        return count;
    }

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
