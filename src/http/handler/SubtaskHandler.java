package http.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exeption.NotFoundException;
import exeption.TaskIntersectionException;
import manager.TaskManager;
import task.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
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
                        List<SubTask> subTasks = taskManager.getSubtasksList();
                        String response = gson.toJson(subTasks);
                        sendJson(exchange, response);
                        System.out.println("200. Подзадачи получены.");
                    } else {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            SubTask subTask = taskManager.getSubtaskById(id);
                            String response = gson.toJson(subTask);
                            sendJson(exchange, response);
                            System.out.println("200. Подзадача #" + id + " получена.");
                        } catch (NotFoundException e) {
                            sendNotFound(exchange, "Подзадача не найдена, указан неверный ID");
                        }
                    }
                    break;
                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    SubTask subTask = gson.fromJson(body, SubTask.class);
                    try {
                        if (subTask.getId() > 0) {
                            taskManager.updateSubtask(subTask);
                        } else {
                            taskManager.addNewSubtask(subTask);
                        }
                        System.out.println("201. Подзадача сохранена.");
                        sendText(exchange, "Подзадача сохранена", 201);
                    } catch (JsonSyntaxException e) {
                        sendWrongRequest(exchange, "Некорректный формат JSON");
                    } catch (TaskIntersectionException e) {
                        sendHasInteractions(exchange, "Подзадача пересекается с существующими");
                    }
                    break;
                case "DELETE":
                    if (parts.length < 3) {
                        sendNotFound(exchange, "Подзадача не найдена, не указан ID");
                    } else {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            taskManager.removeSubtaskById(id);
                            System.out.println("200. Подзадача #" + id + " удалена.");
                            sendText(exchange, "Подзадача удалена", 200);
                        } catch (NotFoundException e) {
                            sendNotFound(exchange, "Подзадача не найдена, указан неверный ID");
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
