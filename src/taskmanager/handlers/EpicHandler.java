package taskmanager.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.model.Epic;
import taskmanager.model.SubTask;
import taskmanager.model.TaskOverlapException;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class EpicHandler extends BaseHandler implements HttpHandler {
    Gson gson;
    TaskManager manager;

    public EpicHandler(Gson gson, TaskManager manager) {
        this.gson = gson;
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET":
                    handleGetEpic(exchange);
                    break;
                case "POST":
                    handleStoreEpic(exchange);
                    break;
                case "DELETE":
                    handleDeleteEpicById(exchange);
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

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> id = super.getId(exchange);

        if (id.isEmpty()) {
            List<Epic> epics = manager.getAllEpics();
            super.sendText(exchange, gson.toJson(epics));
            return;
        }

        try {
            Epic epic = manager.getEpicById(id.get());
            Optional<String> requestSubtasks = super.getStringAfterId(exchange);

            if (requestSubtasks.isPresent()) {
                List<SubTask> subtasks = epic.getSubTasks();
                if (subtasks.isEmpty()) {
                    super.sendNotFound(exchange);
                } else {
                    super.sendText(exchange, gson.toJson(subtasks));
                }
            } else {
                super.sendText(exchange, gson.toJson(epic));
            }
        } catch (NoSuchElementException e) {
            super.sendNotFound(exchange);
        }
    }

    private void handleStoreEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> id = super.getId(exchange);
        Optional<String> epicStr = super.parseBody(exchange.getRequestBody());
        if (!epicStr.isPresent()) {
            super.sendBadRequest(exchange);
            return;
        }

        Epic epic = gson.fromJson(epicStr.get(), Epic.class);

        if (id.isPresent() && manager.hasEpic(id.get())) {
            manager.updateEpic(epic);
        } else if (id.isPresent()) {
            super.sendNotFound(exchange);
            return;
        } else {
            manager.addEpic(epic);
        }
        super.sendSuccess(exchange);
    }

    private void handleDeleteEpicById(HttpExchange exchange) throws IOException {
        Optional<Integer> id = super.getId(exchange);

        if (id.isPresent()) {
            try {
                manager.deleteEpicById(id.get());
            } catch (NoSuchElementException e) {
                sendNotFound(exchange);
            }
        } else {
            try {
                manager.deleteAllEpics();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.sendSuccess(exchange);
    }
}
