package com.esportify.converter;

import com.esportify.enumerations.EventStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EventStatusConverter implements AttributeConverter<EventStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(EventStatus eventStatus) {
        return eventStatus == null ? EventStatus.UNDEFINED.getKey() : eventStatus.getKey();
    }

    @Override
    public EventStatus convertToEntityAttribute(Integer key) {
        return key == null ? EventStatus.UNDEFINED : EventStatus.getByKey(key);
    }
}
