package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void sendText(HttpExchange exchange, String text, int code) throws IOException {
        byte[] response = text.getBytes(UTF_8);
        exchange.sendResponseHeaders(code, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendJson(HttpExchange exchange, String json) throws IOException {
        byte[] response = json.getBytes(UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendWrongRequest(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 400);
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 404);
    }

    protected void sendHasInteractions(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 406);
    }

    protected void sendError(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 500);
    }
}
