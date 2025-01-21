package com.esportify.deserializer;


import com.esportify.enumerations.EventParticipantStatus;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class EventParticipantStatusDeserializer extends JsonDeserializer<EventParticipantStatus> {
    @Override
    public EventParticipantStatus deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        int key = node.get("key").intValue();
        return EventParticipantStatus.getByKey(key);
    }
}
