package http.deserializer;

import com.google.gson.*;
import http.HttpTaskServer;
import task.Epic;
import task.Task;

import java.lang.reflect.Type;

public class EpicJsonDeserializer implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return HttpTaskServer.getRegularGson().fromJson(jsonObject, Epic.class);
    }
}

