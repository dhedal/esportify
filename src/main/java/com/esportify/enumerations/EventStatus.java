package com.esportify.enumerations;

import com.esportify.deserializer.EventStatusDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = EventStatusDeserializer.class)
public enum EventStatus {
    UNDEFINED(0, "indéfini"),
    PENDING(1, "En attente"),
    VALIDATED(2, "Validé"),
    FULL(3, "Complet"),
    CANCELLED(4, "Annulé");

    private Integer key;
    private String label;

    EventStatus(Integer key, String label) {
        this.key = key;
        this.label = label;
    }

    public Integer getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public static EventStatus getByKey(int key) {
        return Stream.of(EventStatus.values())
                .filter(status -> status.getKey().intValue() == key)
                .findFirst()
                .orElse(UNDEFINED);
    }
}
