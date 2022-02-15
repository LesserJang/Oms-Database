package com.jih10157.omsdatabase.api.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.UUID;

public class UUIDAdapter extends TypeAdapter<UUID> {

    @Override
    public void write(JsonWriter out, UUID value) {
    }

    @Override
    public UUID read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String str = in.nextString();
        return UUID.fromString(
            str.substring(0, 8) + "-" + str.substring(8, 12) + "-" + str.substring(12, 16) + "-"
                + str.substring(16, 20) + "-" + str.substring(20, 32));
    }
}
