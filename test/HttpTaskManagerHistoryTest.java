import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.model.Epic;
import taskmanager.model.SubTask;
import taskmanager.model.Task;
import taskmanager.server.HttpTaskServer;
import taskmanager.service.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class HttpTaskManagerHistoryTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    public HttpTaskManagerHistoryTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        Integer epicId = manager.addEpic(new Epic.Builder("epicName", "epicDescription").build());
        Integer subTaskId = manager.addSubTask(new SubTask.Builder("subtaskName", "subtaskDescription", epicId)
                .schedule(LocalDateTime.now(), Duration.ofMinutes(10)).build());
        Integer taskId = manager.addTask(new Task.Builder<>("taskName", "taskDescription")
                .schedule(LocalDateTime.now().plusHours(1), Duration.ofMinutes(1)).build());

        HttpClient client = HttpClient.newHttpClient();
        URI urlHistory = URI.create("http://localhost:8080/history");
        HttpRequest requestBefore = HttpRequest.newBuilder().uri(urlHistory).GET().build();
        HttpResponse<String> responseBefore = client.send(requestBefore, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, responseBefore.statusCode());

        Epic epicSaved = manager.getEpicById(epicId);
        SubTask subtaskSaved = manager.getSubTaskById(subTaskId);
        Task taskSaved = manager.getTaskById(taskId);

        HttpRequest requestAfter = HttpRequest.newBuilder().uri(urlHistory).GET().build();
        HttpResponse<String> responseAfter = client.send(requestAfter, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, responseAfter.statusCode());

        List<Task> history = manager.getHistory();
        Assertions.assertNotNull(history, "Задачи не сохранились в истории");
        Assertions.assertEquals(3, history.size(), "Неверное количество задач");
        Assertions.assertEquals(epicSaved, history.get(0), "Epic в истории не совпадает");
        Assertions.assertEquals(subtaskSaved, history.get(1), "SubTask в истории не совпадает");
        Assertions.assertEquals(taskSaved, history.get(2), "Task в истории не совпадает");
    }
}
