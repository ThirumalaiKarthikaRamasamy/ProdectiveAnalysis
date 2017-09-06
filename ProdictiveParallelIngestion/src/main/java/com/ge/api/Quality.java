package com.ge.api;

import com.google.gson.*;

import java.lang.reflect.Type;

public enum Quality {
    BAD("0"),
    UNCERTAIN("1"),
    NOT_APPLICABLE("2"),
    GOOD("3");

    private String name;

    Quality(String v) {
        this.name = v;
    }

    public static Quality fromString(String name) {
        if (name != null)
            for (Quality quality : Quality.values())
                if (name.equalsIgnoreCase(quality.name))
                    return quality;
        return null;
    }

    public String getName() {
        return this.name;
    }

    public static class Deserializer implements JsonDeserializer<Quality> {

        @Override
        public Quality deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return Quality.fromString(jsonElement.getAsString());
        }
    }

    public static class Serializer implements JsonSerializer<Quality> {

        @Override
        public JsonElement serialize(Quality quality, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonParser parser = new JsonParser();
            return parser.parse(quality.getName());
        }
    }
}