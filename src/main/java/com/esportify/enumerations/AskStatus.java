package com.esportify.enumerations;

import com.esportify.deserializer.AskStatusDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = AskStatusDeserializer.class)
public enum AskStatus {
    UNDEFINED(0, "indéfini"),
    PENDING(1, "En attente de validation"),
    APPROVED(2, "Demande acceptée"),
    REJECTED(3, "Demande rrefusée");

    private Integer key;
    private String label;

    AskStatus(Integer key, String label) {
        this.key = key;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getKey() {
        return key;
    }

    public static AskStatus getByKey(int key) {
        return Stream.of(AskStatus.values())
                .filter(askStatus -> askStatus.key.intValue() == key)
                .findFirst()
                .orElse(UNDEFINED);
    }
}
