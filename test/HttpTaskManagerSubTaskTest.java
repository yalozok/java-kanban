import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.model.Epic;
import taskmanager.model.SubTask;
import taskmanager.server.HttpTaskServer;
import taskmanager.service.InMemoryTaskManager;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class HttpTaskManagerSubTaskTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerSubTaskTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
        manager.deleteAllEpics();
        taskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @Test
    void addSubTask() throws IOException, InterruptedException {
        Integer epicId = manager.addEpic(new Epic.Builder("epicName", "epicDescription").build());
        SubTask subtask = new SubTask.Builder("subtaskName", "subtaskDescription", epicId)
                .schedule(LocalDateTime.now(), Duration.ofMinutes(5)).build();
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI urlSubtask = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(urlSubtask).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> responseSubTask = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, responseSubTask.statusCode());

        List<SubTask> subtasksFromManager = manager.getAllSubTasks();
        Assertions.assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("subtaskName", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void shouldNotAddOverlapSubTask() throws IOException, InterruptedException {
        Integer epicId = manager.addEpic(new Epic.Builder("epicName", "epicDescription").build());
        manager.addSubTask(new SubTask.Builder("subtaskName", "subtaskDescription", epicId)
                .schedule(LocalDateTime.now(), Duration.ofMinutes(10)).build());

        HttpClient client = HttpClient.newHttpClient();
        URI urlSubtask = URI.create("http://localhost:8080/subtasks");
        SubTask subtaskOverlap = new SubTask.Builder("subtaskNameOverlap", "subtaskDescriptionOverlap", epicId)
                .schedule(LocalDateTime.now().plusMinutes(1), Duration.ofMinutes(5)).build();
        String subtaskJsonOverlap = gson.toJson(subtaskOverlap);
        HttpRequest requestOverlap = HttpRequest.newBuilder().uri(urlSubtask).POST(HttpRequest.BodyPublishers.ofString(subtaskJsonOverlap)).build();
        HttpResponse<String> responseOverlap = client.send(requestOverlap, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, responseOverlap.statusCode());
        List<SubTask> subtasksFromManager = manager.getAllSubTasks();
        Assertions.assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("subtaskName", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void getSubTaskById() throws IOException, InterruptedException {
        Integer epicId = manager.addEpic(new Epic.Builder("epicName", "epicDescription").build());
        Integer subTaskId = manager.addSubTask(new SubTask.Builder("subtaskName", "subtaskDescription", epicId)
                .schedule(LocalDateTime.now(), Duration.ofMinutes(10)).build());

        HttpClient client = HttpClient.newHttpClient();
        URI urlGetById = URI.create("http://localhost:8080/subtasks/" + subTaskId);
        HttpRequest requestGetAfterAddSubTask = HttpRequest.newBuilder().uri(urlGetById).GET().build();
        HttpResponse<String> responseGetAfterAddSubTask = client.send(requestGetAfterAddSubTask, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, responseGetAfterAddSubTask.statusCode());
        Assertions.assertEquals("subtaskName", manager.getAllSubTasks().getFirst().getName(), "Некорректное имя задачи");

        URI urlGetByWrongId = URI.create("http://localhost:8080/subtasks/20");
        HttpRequest requestGetAfterAdd = HttpRequest.newBuilder().uri(urlGetByWrongId).GET().build();
        HttpResponse<String> responseGetAfterAdd = client.send(requestGetAfterAdd, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, responseGetAfterAdd.statusCode());
    }

    @Test
    void deleteSubTaskById() throws IOException, InterruptedException {
        Integer epicId = manager.addEpic(new Epic.Builder("epicName", "epicDescription").build());
        Integer subtaskId = manager.addSubTask(new SubTask.Builder("subtaskName", "subtaskDescription", epicId)
                .schedule(LocalDateTime.now(), Duration.ofMinutes(10)).build());
        Assertions.assertEquals(1, manager.getAllSubTasks().size(), "Некорректное количество задач");

        HttpClient client = HttpClient.newHttpClient();
        URI urlDeleteById = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(urlDeleteById).DELETE().build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, responseDelete.statusCode());
        List<SubTask> tasksFromManagerAfterDelete = manager.getAllSubTasks();
        Assertions.assertEquals(0, tasksFromManagerAfterDelete.size(), "Некорректное количество задач");
    }
}
