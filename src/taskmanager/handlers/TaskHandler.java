package taskmanager.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.model.Task;
import taskmanager.model.TaskOverlapException;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TaskHandler extends BaseHandler implements HttpHandler {
    Gson gson;
    TaskManager manager;

    public TaskHandler(Gson gson, TaskManager manager) {
        this.gson = gson;
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET":
                    handleGetTask(exchange);
                    break;
                case "POST":
                    handleStoreTask(exchange);
                    break;
                case "DELETE":
                    handleDeleteTaskById(exchange);
                    break;
                default:
                    super.sendBadRequest(exchange);
            }

        } catch (TaskOverlapException e) {
            super.sendHasInteractions(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
        }
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        Optional<Integer> id = super.getId(exchange);

        if (id.isPresent()) {
            try {
                Task task = manager.getTaskById(id.get());
                super.sendText(exchange, gson.toJson(task));
            } catch (NoSuchElementException e) {
                super.sendNotFound(exchange);
            }
        } else {
            List<Task> tasks = manager.getAllTasks();
            super.sendText(exchange, gson.toJson(tasks));
        }
    }

    private void handleStoreTask(HttpExchange exchange) throws IOException {
        Optional<Integer> id = super.getId(exchange);
        Optional<String> taskStr = super.parseBody(exchange.getRequestBody());
        if (!taskStr.isPresent()) {
            super.sendBadRequest(exchange);
            return;
        }

        Task task = gson.fromJson(taskStr.get(), Task.class);

        if (id.isPresent() && manager.hasTask(id.get())) {
            manager.updateTask(task);
        } else if (id.isPresent()) {
            super.sendNotFound(exchange);
            return;
        } else {
            manager.addTask(task);
        }
        super.sendSuccess(exchange);
    }

    private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> id = super.getId(exchange);

        if (id.isPresent()) {
            try {
                manager.deleteTaskById(id.get());
            } catch (NoSuchElementException e) {
                sendNotFound(exchange);
            }
        } else {
            try {
                manager.deleteAllTasks();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.sendSuccess(exchange);
    }
}
