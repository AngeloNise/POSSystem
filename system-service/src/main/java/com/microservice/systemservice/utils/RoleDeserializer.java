package com.microservice.systemservice.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.microservice.systemservice.models.Roles;

import java.io.IOException;
import java.util.List;

public class RoleDeserializer extends JsonDeserializer<List<Roles>> {

    @Override
    public List<Roles> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        InnerItems innerItems = jp.readValueAs(InnerItems.class);

        return innerItems.elements;
    }

    private static class InnerItems {
        public List<Roles> elements;
    }
}
