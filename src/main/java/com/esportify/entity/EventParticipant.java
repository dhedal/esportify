package com.esportify.entity;

import com.esportify.enumerations.EventParticipantStatus;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "event_participant", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "user_id"})
})
public class EventParticipant extends AbstractEntity implements Serializable {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User participant;
    @Column(nullable = false)
    private EventParticipantStatus status;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getParticipant() {
        return participant;
    }

    public void setParticipant(User user) {
        this.participant = user;
    }

    public EventParticipantStatus getStatus() {
        return status;
    }

    public void setStatus(EventParticipantStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventParticipant that = (EventParticipant) o;
        return Objects.equals(event, that.event) &&
                Objects.equals(participant, that.participant) &&
                Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, participant, getUuid());
    }
}
