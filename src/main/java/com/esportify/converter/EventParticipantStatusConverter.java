package com.esportify.converter;


import com.esportify.enumerations.EventParticipantStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EventParticipantStatusConverter implements AttributeConverter<EventParticipantStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(EventParticipantStatus participationStatus) {
        return participationStatus == null ? EventParticipantStatus.UNDEFINED.getKey() : participationStatus.getKey();
    }

    @Override
    public EventParticipantStatus convertToEntityAttribute(Integer key) {
        return key == null ? EventParticipantStatus.UNDEFINED : EventParticipantStatus.getByKey(key);
    }
}
