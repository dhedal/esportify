package com.esportify.converter;

import com.esportify.enumerations.AskStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AskStatusConverter implements AttributeConverter<AskStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(AskStatus askStatus) {
        return askStatus == null ? AskStatus.UNDEFINED.getKey() : askStatus.getKey();
    }

    @Override
    public AskStatus convertToEntityAttribute(Integer key) {
        return key == null ? AskStatus.UNDEFINED : AskStatus.getByKey(key);
    }
}
