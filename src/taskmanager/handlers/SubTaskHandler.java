package taskmanager.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.model.SubTask;
import taskmanager.model.TaskOverlapException;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class SubTaskHandler extends BaseHandler implements HttpHandler {
    Gson gson;
    TaskManager manager;

    public SubTaskHandler(Gson gson, TaskManager manager) {
        this.gson = gson;
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET":
                    handleGetSubTask(exchange);
                    break;
                case "POST":
                    handleStoreSubTask(exchange);
                    break;
                case "DELETE":
                    handleDeleteSubTaskById(exchange);
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

    private void handleGetSubTask(HttpExchange exchange) throws IOException {
        Optional<Integer> id = super.getId(exchange);

        if (id.isPresent()) {
            try {
                SubTask subtask = manager.getSubTaskById(id.get());
                super.sendText(exchange, gson.toJson(subtask));
            } catch (NoSuchElementException e) {
                super.sendNotFound(exchange);
            }
        } else {
            List<SubTask> tasks = manager.getAllSubTasks();
            super.sendText(exchange, gson.toJson(tasks));
        }
    }

    private void handleStoreSubTask(HttpExchange exchange) throws IOException {
        Optional<Integer> id = super.getId(exchange);
        Optional<String> subtaskStr = super.parseBody(exchange.getRequestBody());
        if (!subtaskStr.isPresent()) {
            super.sendBadRequest(exchange);
            return;
        }

        SubTask subtask = gson.fromJson(subtaskStr.get(), SubTask.class);

        if (id.isPresent() && manager.hasTask(id.get())) {
            manager.updateSubTask(subtask);
        } else if (id.isPresent()) {
            super.sendNotFound(exchange);
            return;
        } else {
            manager.addSubTask(subtask);
        }
        super.sendSuccess(exchange);
    }

    private void handleDeleteSubTaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> id = super.getId(exchange);

        if (id.isPresent()) {
            try {
                manager.deleteSubTaskById(id.get());
            } catch (NoSuchElementException e) {
                sendNotFound(exchange);
            }
        } else {
            try {
                manager.deleteAllSubTasks();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.sendSuccess(exchange);
    }
}
