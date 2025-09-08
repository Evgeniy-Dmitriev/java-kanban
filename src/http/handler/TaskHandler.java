package http.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exeption.NotFoundException;
import exeption.TaskIntersectionException;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            System.out.println("Началась обработка " + method + " запроса от клиента.");
            System.out.println("http://localhost:8080" + path);

            switch (method) {
                case "GET":
                    if (parts.length < 3) {
                        List<Task> tasks = taskManager.getTasksList();
                        String response = gson.toJson(tasks);
                        sendJson(exchange, response);
                        System.out.println("200. Задачи получены.");
                    } else {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            Task task = taskManager.getTaskById(id);
                            String response = gson.toJson(task);
                            sendJson(exchange, response);
                            System.out.println("200. Задача #" + id + " получена.");
                        } catch (NotFoundException e) {
                            sendNotFound(exchange, "Задача не найдена, указан неверный ID");
                        }
                    }
                    break;
                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(body, Task.class);
                    try {
                        if (task.getId() > 0) {
                            taskManager.updateTask(task);
                        } else {
                            taskManager.addNewTask(task);
                        }
                        System.out.println("201. Задача сохранена.");
                        sendText(exchange, "Задача сохранена", 201);
                    } catch (JsonSyntaxException e) {
                        sendWrongRequest(exchange, "Некорректный формат JSON");
                    } catch (TaskIntersectionException e) {
                        sendHasInteractions(exchange, "Задача пересекается с существующими");
                    }
                    break;
                case "DELETE":
                    if (parts.length < 3) {
                        sendNotFound(exchange, "Задача не найдена, не указан ID");
                    } else {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            taskManager.removeTaskById(id);
                            System.out.println("200. Задача #" + id + " удалена.");
                            sendText(exchange, "Задача удалена", 200);
                        } catch (NotFoundException e) {
                            sendNotFound(exchange, "Задача не найдена, указан неверный ID");
                        }
                    }
                    break;
                default:
                    System.out.println("Ждём GET, POST или DELETE метод, а получили " + method);
                    sendText(exchange, "Метод не поддерживается", 405);
            }
        } catch (Exception e) {
            sendError(exchange, "Внутренняя ошибка сервера");
        } finally {
            exchange.close();
        }
    }
}
