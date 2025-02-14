package taskmanager.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import taskmanager.adapters.*;
import taskmanager.handlers.*;
import taskmanager.model.Epic;
import taskmanager.model.SubTask;
import taskmanager.model.Task;
import taskmanager.service.Manager;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private static TaskManager manager;
    private static HttpServer server;

    public HttpTaskServer(TaskManager manager) throws IOException {
        Gson gson = getGson();
        HttpTaskServer.manager = manager;

        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(gson, manager));
        server.createContext("/epics", new EpicHandler(gson, manager));
        server.createContext("/subtasks", new SubTaskHandler(gson, manager));
        server.createContext("/history", new HistoryHandler(gson, manager));
        server.createContext("/prioritized", new PrioritizedHandler(gson, manager));
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskTypeAdapter())
                .registerTypeAdapter(Epic.class, new TaskTypeAdapter())
                .registerTypeAdapter(SubTask.class, new TaskTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(new TypeToken<Optional<Duration>>() {
                }.getType(), new OptionalTypeAdapter<>(Duration.class))
                .registerTypeAdapter(new TypeToken<Optional<LocalDateTime>>() {
                }.getType(), new OptionalTypeAdapter<>(LocalDateTime.class))
                .create();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(new Manager().getDefault());
        httpTaskServer.startServer();
    }

    public void startServer() {
        server.start();
        System.out.println("Http сервер запущен на " + PORT + " порту.");
    }

    public void stopServer() {
        server.stop(0);
        System.out.println("Http сервер остановлен.");
    }
}
