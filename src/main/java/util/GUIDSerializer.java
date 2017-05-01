package util;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.sun.jna.platform.win32.Guid;

import java.lang.reflect.Type;

public class GUIDSerializer implements JsonSerializer<Guid.GUID> {

    @Override
    public JsonElement serialize(Guid.GUID src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.toGuidString());
    }
}
