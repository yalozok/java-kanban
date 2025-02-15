import com.google.gson.Gson;
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

public class HttpTaskManagerPrioritizedTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerPrioritizedTest() throws IOException {
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
    public void getPrioritized() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");

        HttpRequest requestBefore = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> responseBefore = client.send(requestBefore, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, responseBefore.statusCode());

        Integer epicId = manager.addEpic(new Epic.Builder("epicName", "epicDescription").build());
        Integer subtaskId = manager.addSubTask(new SubTask.Builder("subtaskName", "subtaskDescription", epicId)
                .schedule(LocalDateTime.now(), Duration.ofMinutes(10)).build());
        Integer taskId = manager.addTask(new Task.Builder<>("taskName", "taskDescription")
                .schedule(LocalDateTime.now().plusHours(1), Duration.ofMinutes(1)).build());
        SubTask subtaskSaved = manager.getSubTaskById(subtaskId);
        Task taskSaved = manager.getTaskById(taskId);

        HttpRequest requestAfter = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> responseAfter = client.send(requestAfter, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, responseAfter.statusCode());

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        Assertions.assertNotNull(prioritizedTasks, "Задачи не сохранились в списке приоритетов");
        Assertions.assertEquals(2, prioritizedTasks.size(), "Неверное количество задач");
        Assertions.assertEquals(subtaskSaved, prioritizedTasks.getFirst(), "SubTask в истории не совпадает");
        Assertions.assertEquals(taskSaved, prioritizedTasks.get(1), "Task в истории не совпадает");
    }
}
