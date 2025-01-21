package com.esportify.enumerations;

import com.esportify.deserializer.EventStatusDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = EventStatusDeserializer.class)
public enum EventParticipantStatus {
    UNDEFINED(0, "indéfini"),
    PENDING(1, "en attente"),
    APPROVED(2, "approuvé"),
    REJECTED(3, "rejeté");

    private Integer key;
    private String label;

    EventParticipantStatus(Integer key, String label) {
        this.key = key;
        this.label = label;
    }

    public Integer getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public static EventParticipantStatus getByKey(int key) {
        return Stream.of(EventParticipantStatus.values())
                .filter(status -> status.getKey().intValue() == key)
                .findFirst()
                .orElse(UNDEFINED);
    }
}
