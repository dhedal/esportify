package com.esportify.enumerations;

import com.esportify.deserializer.AskTypeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = AskTypeDeserializer.class)
public enum AskType {
    UNDEFINED(Integer.MIN_VALUE, "Indéfini"),
    SIMPLE(0, "Simple message"),
    ASK_ORGANIZER(1, "Demande à devenir organisateur");

    private Integer key;
    private String label;

    AskType(Integer key, String label) {
        this.key = key;
        this.label = label;
    }

    public Integer getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public static AskType getByKey(int key) {
        return Stream.of(AskType.values())
                .filter(askType -> askType.getKey().intValue() == key)
                .findFirst()
                .orElse(SIMPLE);
    }
}
