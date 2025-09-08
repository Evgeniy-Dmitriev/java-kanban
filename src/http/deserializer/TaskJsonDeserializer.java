package http.deserializer;

import com.google.gson.*;
import http.HttpTaskServer;
import task.Epic;
import task.SubTask;
import task.Task;

import java.lang.reflect.Type;

public class TaskJsonDeserializer implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String taskType = jsonObject.get("type").getAsString().toUpperCase();

        switch (taskType) {
            case "TASK":
                return HttpTaskServer.getRegularGson().fromJson(jsonObject, Task.class);
            case "EPIC":
                return HttpTaskServer.getRegularGson().fromJson(jsonObject, Epic.class);
            case "SUBTASK":
                return HttpTaskServer.getRegularGson().fromJson(jsonObject, SubTask.class);
            default:
                throw new JsonParseException("Неизвестный тип задачи: " + taskType);
        }
    }
}
