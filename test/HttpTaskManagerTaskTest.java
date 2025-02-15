import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import taskmanager.model.Task;
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

class HttpTaskManagerTaskTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerTaskTest() throws IOException {
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
    void addTask() throws IOException, InterruptedException {
        Task task = new Task.Builder<>("taskName", "taskDescription")
                .schedule(LocalDateTime.now(), Duration.ofMinutes(5)).build();
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Task> tasks = manager.getAllTasks();
        Assertions.assertNotNull(tasks, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasks.size(), "Некорректное количество задач");
        Assertions.assertEquals("taskName", tasks.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void shouldNotAddOverlapTask() throws IOException, InterruptedException {
        manager.addTask(new Task.Builder<>("taskName", "taskDescription")
                .schedule(LocalDateTime.now(), Duration.ofMinutes(10)).build());
        Task taskOverlap = new Task.Builder<>("taskOverlap", "taskDescription")
                .schedule(LocalDateTime.now(), Duration.ofMinutes(5)).build();
        String taskOverlapJson = gson.toJson(taskOverlap);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest requestOverlap = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskOverlapJson)).build();
        HttpResponse<String> responseOverlap = client.send(requestOverlap, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, responseOverlap.statusCode());

        List<Task> tasks = manager.getAllTasks();
        Assertions.assertEquals(1, tasks.size(), "Некорректное количество задач");
        Assertions.assertEquals("taskName", tasks.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        Integer taskId = manager.addTask(new Task.Builder<>("taskName", "taskDescription")
                .schedule(LocalDateTime.now(), Duration.ofMinutes(1)).build());

        HttpClient client = HttpClient.newHttpClient();
        URI urlUpdate = URI.create("http://localhost:8080/tasks/" + taskId);
        Task taskUpdate = new Task.Builder<>("taskUpdate", "taskDescription").id(taskId).build();
        String taskUpdateJson = gson.toJson(taskUpdate);
        HttpRequest requestUpdate = HttpRequest.newBuilder().uri(urlUpdate).POST(HttpRequest.BodyPublishers.ofString(taskUpdateJson)).build();
        HttpResponse<String> responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = manager.getAllTasks();

        Assertions.assertEquals(201, responseUpdate.statusCode());
        Assertions.assertEquals(1, tasks.size(), "Некорректное количество задач");
        Assertions.assertEquals("taskUpdate", tasks.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void getAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(0, manager.getAllTasks().size(), "Некорректное количество задач");

        manager.addTask(new Task.Builder<>("taskName", "taskDescription")
                .schedule(LocalDateTime.now(), Duration.ofMinutes(1)).build());

        HttpRequest requestGet = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, responseGet.statusCode());
        Assertions.assertEquals(1, manager.getAllTasks().size(), "Некорректное количество задач");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        Integer taskId = manager.addTask(new Task.Builder<>("taskName", "taskDescription")
                .schedule(LocalDateTime.now(), Duration.ofMinutes(1)).build());
        HttpClient client = HttpClient.newHttpClient();
        URI urlGetById = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest requestGetAfterAddTask = HttpRequest.newBuilder().uri(urlGetById).GET().build();
        HttpResponse<String> responseGetAfterAddTask = client.send(requestGetAfterAddTask, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, responseGetAfterAddTask.statusCode());
        Assertions.assertEquals("taskName", manager.getAllTasks().getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void correctResponseWhenIdNotFound() throws IOException, InterruptedException {
        manager.addTask(new Task.Builder<>("taskName", "taskDescription")
                .schedule(LocalDateTime.now(), Duration.ofMinutes(1)).build());
        HttpClient client = HttpClient.newHttpClient();
        URI urlGetById = URI.create("http://localhost:8080/tasks/20");
        HttpRequest requestGetAfterAddTask = HttpRequest.newBuilder().uri(urlGetById).GET().build();
        HttpResponse<String> responseGetAfterAddTask = client.send(requestGetAfterAddTask, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, responseGetAfterAddTask.statusCode());
    }

    @Test
    void deleteTaskById() throws IOException, InterruptedException {
        Integer taskId = manager.addTask(new Task.Builder<>("taskName", "taskDescription")
                .schedule(LocalDateTime.now(), Duration.ofMinutes(1)).build());
        Assertions.assertEquals(1, manager.getAllTasks().size(), "Некорректное количество задач");

        HttpClient client = HttpClient.newHttpClient();
        URI urlDeleteById = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(urlDeleteById).DELETE().build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, responseDelete.statusCode());
        Assertions.assertEquals(0, manager.getAllTasks().size(), "Некорректное количество задач");
    }

    @Test
    void deleteAllTasks() throws IOException, InterruptedException {
        manager.addTask(new Task.Builder<>("taskName1", "taskDescription")
                .schedule(LocalDateTime.now(), Duration.ofMinutes(1)).build());
        manager.addTask(new Task.Builder<>("taskName2", "taskDescription")
                .schedule(LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(1)).build());
        List<Task> tasksFromManager = manager.getAllTasks();
        Assertions.assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest requestGetAfterAddTask = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> responseGetAfterAddTask = client.send(requestGetAfterAddTask, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, responseGetAfterAddTask.statusCode());
        List<Task> tasksFromManagerAfterDelete = manager.getAllTasks();
        Assertions.assertEquals(0, tasksFromManagerAfterDelete.size(), "Некорректное количество задач");
    }
}
