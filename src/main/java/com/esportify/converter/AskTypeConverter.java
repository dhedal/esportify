package com.esportify.converter;

import com.esportify.enumerations.AskType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AskTypeConverter  implements AttributeConverter<AskType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(AskType askType) {
        return askType == null ? AskType.SIMPLE.getKey() : askType.getKey();
    }

    @Override
    public AskType convertToEntityAttribute(Integer key) {
        return key == null ? AskType.SIMPLE : AskType.getByKey(key);
    }
}
