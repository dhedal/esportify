package com.esportify.enumerations;

import java.util.stream.Stream;

public enum ParticipationStatus {
    UNDEFINED(0, "indéfini"),
    PENDING(1, "en attente"),
    APPROVED(2, "approuvé"),
    REJECTED(3, "rejeté");

    private Integer key;
    private String label;

    ParticipationStatus(Integer key, String label) {
        this.key = key;
        this.label = label;
    }

    public Integer getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public static ParticipationStatus getByKey(int key) {
        return Stream.of(ParticipationStatus.values())
                .filter(status -> status.getKey().intValue() == key)
                .findFirst()
                .orElse(UNDEFINED);
    }
}
