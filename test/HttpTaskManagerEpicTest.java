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

class HttpTaskManagerEpicTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerEpicTest() throws IOException {
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
    void addEpic() throws IOException, InterruptedException {
        Epic epic = new Epic.Builder("epicName", "epicDescription").build();
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Epic> epics = manager.getAllEpics();
        Assertions.assertNotNull(epics, "Задачи не возвращаются");
        Assertions.assertEquals(1, epics.size(), "Некорректное количество задач");
        Assertions.assertEquals("epicName", epics.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void getAllEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(0, manager.getAllEpics().size(), "Некорректное количество задач");

        manager.addEpic(new Epic.Builder("epicName", "epicDescription").build());
        HttpRequest requestGetEpic = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> responseGetEpic = client.send(requestGetEpic, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, responseGetEpic.statusCode());
        Assertions.assertEquals(1, manager.getAllEpics().size(), "Некорректное количество задач");
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        Integer epicId = manager.addEpic(new Epic.Builder("epicName", "epicDescription").build());
        HttpClient client = HttpClient.newHttpClient();
        URI urlGetById = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest requestGet = HttpRequest.newBuilder().uri(urlGetById).GET().build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, responseGet.statusCode());
        Assertions.assertEquals("epicName", manager.getEpicById(epicId).getName(), "Некорректное имя задачи");
    }

    @Test
    void correctResponseWhenIdNotFound() throws IOException, InterruptedException {
        manager.addEpic(new Epic.Builder("epicName", "epicDescription").build());
        HttpClient client = HttpClient.newHttpClient();
        URI urlGetById = URI.create("http://localhost:8080/epics/20");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(urlGetById).GET().build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, responseGet.statusCode());
    }

    @Test
    void deleteEpicById() throws IOException, InterruptedException {
        Integer epicId = manager.addEpic(new Epic.Builder("epicName", "epicDescription").build());
        HttpClient client = HttpClient.newHttpClient();
        URI urlDelete = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(urlDelete).DELETE().build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, responseDelete.statusCode());
        List<Epic> epicsFromManager = manager.getAllEpics();
        Assertions.assertEquals(0, epicsFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void deleteAllEpics() throws IOException, InterruptedException {
        manager.addEpic(new Epic.Builder("epicName1", "epicDescription").build());
        manager.addEpic(new Epic.Builder("epicName2", "epicDescription").build());
        Assertions.assertEquals(2, manager.getAllEpics().size(), "Некорректное количество задач");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, responseDelete.statusCode());
        Assertions.assertEquals(0, manager.getAllEpics().size(), "Некорректное количество задач");
    }

    @Test
    void getSubTaskListOfEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Integer epicId = manager.addEpic(new Epic.Builder("epicName", "epicDescription").build());

        URI urlGetList = URI.create("http://localhost:8080/epics/" + epicId + "/subtasks");
        HttpRequest requestGetList1 = HttpRequest.newBuilder().uri(urlGetList).GET().build();
        HttpResponse<String> responseGetList1 = client.send(requestGetList1, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, responseGetList1.statusCode());

        manager.addSubTask(new SubTask.Builder("subtaskName", "subtaskDescription", epicId)
                .schedule(LocalDateTime.now(), Duration.ofMinutes(10)).build());

        HttpRequest requestGetList2 = HttpRequest.newBuilder().uri(urlGetList).GET().build();
        HttpResponse<String> responseGetList2 = client.send(requestGetList2, HttpResponse.BodyHandlers.ofString());
        Epic epic = manager.getAllEpics().getFirst();
        Assertions.assertEquals(200, responseGetList2.statusCode());
        Assertions.assertEquals(1, epic.getSubTasks().size(), "Некорректное количество задач");
        Assertions.assertEquals("subtaskName", epic.getSubTasks().getFirst().getName(), "Некорректное имя задач");
    }
}
