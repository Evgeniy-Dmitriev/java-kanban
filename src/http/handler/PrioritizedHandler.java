package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            System.out.println("Началась обработка " + method + " запроса от клиента.");
            System.out.println("http://localhost:8080" + path);

            if (method.equals("GET")) {
                List<Task> tasks = taskManager.getPrioritizedTasks();
                String response = gson.toJson(tasks);
                sendJson(exchange, response);
                System.out.println("200. Список задач в порядке приоритета получен.");
            } else {
                System.out.println("Ждём GET метод, а получили " + method);
                sendText(exchange, "Метод не поддерживается", 405);
            }
        } catch (Exception e) {
            sendError(exchange, "Внутренняя ошибка сервера");
        } finally {
            exchange.close();
        }
    }
}

