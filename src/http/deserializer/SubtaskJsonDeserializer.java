package http.deserializer;

import com.google.gson.*;
import http.HttpTaskServer;
import task.SubTask;
import task.Task;

import java.lang.reflect.Type;

public class SubtaskJsonDeserializer implements JsonDeserializer<Task> {
    @Override
    public SubTask deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return HttpTaskServer.getRegularGson().fromJson(jsonObject, SubTask.class);
    }
}
