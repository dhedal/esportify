package com.esportify.converter;


import com.esportify.enumerations.EventStatus;
import com.esportify.enumerations.ParticipationStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ParticipationStatusConverter implements AttributeConverter<ParticipationStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ParticipationStatus participationStatus) {
        return participationStatus == null ? ParticipationStatus.UNDEFINED.getKey() : participationStatus.getKey();
    }

    @Override
    public ParticipationStatus convertToEntityAttribute(Integer key) {
        return key == null ? ParticipationStatus.UNDEFINED : ParticipationStatus.getByKey(key);
    }
}
