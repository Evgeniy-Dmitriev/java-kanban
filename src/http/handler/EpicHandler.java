package http.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exeption.NotFoundException;
import manager.TaskManager;
import task.Epic;
import task.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager, Gson gson) {
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
                        List<Epic> epics = taskManager.getEpicsList();
                        String response = gson.toJson(epics);
                        sendJson(exchange, response);
                        System.out.println("200. Эпики получены.");
                    } else if (parts.length == 3) {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            Epic epic = taskManager.getEpicById(id);
                            String response = gson.toJson(epic);
                            sendJson(exchange, response);
                            System.out.println("200. Эпик #" + id + " получен.");
                        } catch (NotFoundException e) {
                            sendNotFound(exchange, "Эпик не найден, указан неверный ID");
                        }
                    } else {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            ArrayList<SubTask> subTasks = taskManager.getSubtasksListByEpicId(id);
                            String response = gson.toJson(subTasks);
                            sendJson(exchange, response);
                            System.out.println("200. Подзадачи эпика #" + id + " получены.");
                        } catch (NotFoundException e) {
                            sendNotFound(exchange, "Подзадачи эпика не найдены, указан неверный ID");
                        }
                    }
                    break;
                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(body, Epic.class);
                    try {
                        taskManager.addNewEpic(epic);
                        System.out.println("201. Эпик сохранен.");
                        sendText(exchange, "Эпик сохранен", 201);
                    } catch (JsonSyntaxException e) {
                        sendWrongRequest(exchange, "Некорректный формат JSON");
                    }
                    break;
                case "DELETE":
                    if (parts.length < 3) {
                        sendNotFound(exchange, "Эпик не найден, не указан ID");
                    } else {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            taskManager.removeEpicById(id);
                            System.out.println("200. Эпик #" + id + " удален.");
                            sendText(exchange, "Эпик удален", 200);
                        } catch (NotFoundException e) {
                            sendNotFound(exchange, "Эпик не найден, указан неверный ID");
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

