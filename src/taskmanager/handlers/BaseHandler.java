package taskmanager.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected void sendSuccess(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(406, 0);
        exchange.close();
    }

    protected void sendBadRequest(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(400, 0);
        exchange.close();
    }

    protected Optional<Integer> getId(HttpExchange exchange) {
        URI requestURI = exchange.getRequestURI();
        try {
            return Optional.of(Integer.parseInt(requestURI.getPath().split("/")[2]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    protected Optional<String> getStringAfterId(HttpExchange exchange) {
        URI requestURI = exchange.getRequestURI();
        String[] pathSegments = requestURI.getPath().split("/");

        if (pathSegments.length > 3 && !pathSegments[3].trim().isEmpty()) {
            return Optional.of(pathSegments[3].trim());
        }
        return Optional.empty();
    }

    protected Optional<String> parseBody(InputStream bodyStream) {
        try {
            String bodyString = new String(bodyStream.readAllBytes(), DEFAULT_CHARSET);
            return bodyString.isEmpty() ? Optional.empty() : Optional.of(bodyString);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}

