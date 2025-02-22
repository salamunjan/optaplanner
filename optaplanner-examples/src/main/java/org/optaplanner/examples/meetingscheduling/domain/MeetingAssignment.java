package org.optaplanner.examples.meetingscheduling.domain;

import org.apache.commons.text.WordUtils;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningEntity
public class MeetingAssignment extends AbstractPersistable {

    private Meeting meeting;
    private boolean pinned;

    // Planning variables: changes during planning, between score calculations.
    private TimeGrain startingTimeGrain;
    private Room room;

    public MeetingAssignment() {
    }

    public MeetingAssignment(long id, Meeting meeting, TimeGrain startingTimeGrain, Room room) {
        super(id);
        this.meeting = meeting;
        this.startingTimeGrain = startingTimeGrain;
        this.room = room;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    @PlanningPin
    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    @PlanningVariable(valueRangeProviderRefs = { "timeGrainRange" })
    public TimeGrain getStartingTimeGrain() {
        return startingTimeGrain;
    }

    public void setStartingTimeGrain(TimeGrain startingTimeGrain) {
        this.startingTimeGrain = startingTimeGrain;
    }

    @PlanningVariable(valueRangeProviderRefs = { "roomRange" })
    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public int calculateOverlap(MeetingAssignment other) {
        if (startingTimeGrain == null || other.getStartingTimeGrain() == null) {
            return 0;
        }
        // start is inclusive, end is exclusive
        int start = startingTimeGrain.getGrainIndex();
        int otherStart = other.startingTimeGrain.getGrainIndex();
        int otherEnd = otherStart + other.meeting.getDurationInGrains();
        if (otherEnd < start) {
            return 0;
        }
        int end = start + meeting.getDurationInGrains();
        if (end < otherStart) {
            return 0;
        }
        return Math.min(end, otherEnd) - Math.max(start, otherStart);
    }

    public Integer getLastTimeGrainIndex() {
        if (startingTimeGrain == null) {
            return null;
        }
        return startingTimeGrain.getGrainIndex() + meeting.getDurationInGrains() - 1;
    }

    public String getStartingDateTimeString() {
        if (startingTimeGrain == null) {
            return null;
        }
        return startingTimeGrain.getDateTimeString();
    }

    public int getRoomCapacity() {
        if (room == null) {
            return 0;
        }
        return room.getCapacity();
    }

    public int getRequiredCapacity() {
        return meeting.getRequiredCapacity();
    }

    public String getLabel() {
        int wrapLength = Math.max(8 * meeting.getDurationInGrains(), 16);
        return "<html><center>" + WordUtils.wrap(meeting.getTopic(), wrapLength, "<br/>", false) + "</center></html>";
    }

    @Override
    public String toString() {
        return meeting.toString();
    }

}
