package нttp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.HttpTaskServer;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerPrioritizedTest {

    private Task task;
    private Task task2;
    private final TaskManager taskManager = Managers.getDefault();
    private final HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerPrioritizedTest() throws IOException {
    }

    @BeforeEach
    void setUp() throws IOException {
        taskManager.removeTasks();
        taskManager.removeSubtasks();
        taskManager.removeEpics();
        httpTaskServer.start();
        task = new Task("Имя задачи 1", "Описание задачи 1",
                Status.NEW,
                1,
                Duration.ofMinutes(55),
                LocalDateTime.of(2025, 2, 7, 16, 27));
        task2 = new Task("Имя задачи 2", "Описание задачи 2",
                Status.NEW,
                2,
                Duration.ofMinutes(55),
                LocalDateTime.of(2025, 2, 7, 15, 27));
        taskManager.addNewTask(task);
        taskManager.addNewTask(task2);
    }

    @AfterEach
    void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    void getPrioritizedTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Ожидается код статуса - 200");

        Type taskType = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> prioritized = gson.fromJson(response.body(), taskType);

        assertNotNull(prioritized, "Задачи не возвращаются");
        assertEquals(2, prioritized.size(), "Некорректное количество задач");
        assertEquals("Имя задачи 2", prioritized.get(0).getName(), "Некорректное имя задачи");
        assertEquals("Имя задачи 1", prioritized.get(1).getName(), "Некорректное имя задачи");
    }
}