package taskmanager.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.model.Task;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHandler implements HttpHandler {
    Gson gson;
    TaskManager manager;

    public PrioritizedHandler(Gson gson, TaskManager manager) {
        this.gson = gson;
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            if (method.equals("GET")) {
                handleGetPrioritized(exchange);
            } else {
                super.sendBadRequest(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
        }
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        List<Task> prioritized = manager.getPrioritizedTasks();

        if (prioritized.isEmpty()) {
            super.sendNotFound(exchange);
        } else {
            super.sendText(exchange, gson.toJson(prioritized));
        }
    }
}
