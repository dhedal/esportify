package com.esportify.enumerations;


import com.esportify.deserializer.UserStatusDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = UserStatusDeserializer.class)
public enum UserStatus {
    VISITOR(0, "Visiteur"),
    PLAYER(1, "Joueur"),
    ORGANIZER(2, "Organisateur"),
    ADMIN(3, "Administrateur");

    private Integer key;
    private String label;

    UserStatus(Integer key, String label) {
        this.key = key;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getKey() {
        return key;
    }

    public static UserStatus getByKey(int key) {
        return Stream.of(UserStatus.values())
                .filter(status -> status.getKey().intValue() == key)
                .findFirst()
                .orElse(VISITOR);
    }
}
