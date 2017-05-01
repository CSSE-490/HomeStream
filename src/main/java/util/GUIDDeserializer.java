package util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.sun.jna.platform.win32.Guid;

import java.lang.reflect.Type;

public class GUIDDeserializer implements JsonDeserializer<Guid.GUID> {
    @Override
    public Guid.GUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Guid.GUID.fromString(json.getAsString());
    }
}
