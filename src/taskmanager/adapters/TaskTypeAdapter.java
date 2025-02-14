package taskmanager.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import taskmanager.model.Epic;
import taskmanager.model.SubTask;
import taskmanager.model.Task;
import taskmanager.model.TaskStatus;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskTypeAdapter implements JsonSerializer<Task>, JsonDeserializer<Task> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public JsonElement serialize(Task task, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("id", task.getId() != null ? new JsonPrimitive(task.getId()) : JsonNull.INSTANCE);
        jsonObject.add("name", task.getName() != null ? new JsonPrimitive(task.getName()) : JsonNull.INSTANCE);
        jsonObject.add("description", task.getDescription() != null ? new JsonPrimitive(task.getDescription()) : JsonNull.INSTANCE);
        jsonObject.add("status", task.getStatus() != null ? new JsonPrimitive(task.getStatus().toString()) : JsonNull.INSTANCE);
        jsonObject.add("duration", task.getDuration().isPresent() ?
                context.serialize(task.getDuration().get()) : JsonNull.INSTANCE);
        jsonObject.add("startTime", task.getStartTime().isPresent() ?
                context.serialize(task.getStartTime().get()) : JsonNull.INSTANCE);

        switch (task) {
            case Epic epic -> {
                jsonObject.addProperty("type", "epic");
                jsonObject.add("subTasks", context.serialize(epic.getSubTasks()));
            }
            case SubTask subTask -> {
                jsonObject.addProperty("type", "subtask");
                jsonObject.addProperty("epicId", subTask.getEpicId());
            }
            default -> jsonObject.addProperty("type", "task");
        }
        return jsonObject;
    }

    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.has("type") ? jsonObject.get("type").getAsString() : "task";
        Integer id = jsonObject.has("id") ? jsonObject.get("id").getAsInt() : null;
        String name = jsonObject.has("name") ? jsonObject.get("name").getAsString() : null;
        String description = jsonObject.has("description") ? jsonObject.get("description").getAsString() : null;
        TaskStatus status = jsonObject.has("status") ? context.deserialize(jsonObject.get("status"), TaskStatus.class) : null;
        Duration duration = null;
        if (jsonObject.has("duration") && !jsonObject.get("duration").isJsonNull()) {
            duration = Duration.parse(jsonObject.get("duration").getAsString());
        }
        LocalDateTime startTime = null;
        if (jsonObject.has("startTime") && !jsonObject.get("startTime").isJsonNull()) {
            startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(), FORMATTER);
        }


        List<SubTask> subTasks = new ArrayList<>();

        if (jsonObject.has("subTasks") && !jsonObject.get("subTasks").isJsonNull()) {
            subTasks = context.deserialize(jsonObject.get("subTasks"), new TypeToken<List<SubTask>>() {
            }.getType());
        }

        return switch (type) {
            case "epic" -> new Epic.Builder(name, description)
                    .id(id)
                    .status(status)
                    .subTasks(subTasks)
                    .build();
            case "subtask" -> {
                Integer epicId = jsonObject.has("epicId") ? jsonObject.get("epicId").getAsInt() : null;
                yield new SubTask.Builder(name, description, epicId)
                        .id(id)
                        .status(status)
                        .schedule(startTime, duration)
                        .build();
            }
            default -> new Task.Builder<>(name, description)
                    .id(id)
                    .status(status)
                    .schedule(startTime, duration)
                    .build();
        };
    }
}


