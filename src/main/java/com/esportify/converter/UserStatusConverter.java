package com.esportify.converter;

import com.esportify.enumerations.UserStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserStatusConverter implements AttributeConverter<UserStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(UserStatus userStatus) {
        return userStatus == null ? UserStatus.VISITOR.getKey() : userStatus.getKey();
    }

    @Override
    public UserStatus convertToEntityAttribute(Integer key) {
        return key == null ? UserStatus.VISITOR : UserStatus.getByKey(key);
    }
}
