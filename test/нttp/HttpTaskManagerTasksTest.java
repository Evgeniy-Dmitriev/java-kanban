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

public class HttpTaskManagerTasksTest {

    private Task task;
    private final TaskManager taskManager = Managers.getDefault();
    private final HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
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
        taskManager.addNewTask(task);
    }

    @AfterEach
    void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    void getTasksTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks");
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
        List<Task> tasks = gson.fromJson(response.body(), taskType);

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Имя задачи 1", tasks.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void getTaskByIdTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Ожидается код статуса - 200");

        Type taskType = new TypeToken<Task>() {}.getType();
        Task actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Задача не возвращается");
        assertEquals("Имя задачи 1", actual.getName(), "Некорректное имя задачи");
    }

    @Test
    void postTaskTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks");
        Task task2 = new Task("Имя задачи 2", "Описание задачи 2",
                Duration.ofMinutes(55),
                LocalDateTime.of(2025, 2, 7, 17, 27));
        String json = gson.toJson(task2);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(201, response.statusCode(), "Ожидается код статуса - 201");
        assertEquals("Имя задачи 2", taskManager.getTaskById(2).getName(), "Некорректное имя задачи");
        assertEquals("Описание задачи 2", taskManager.getTaskById(2).getDescription(),
                "Некорректное описание задачи");
    }

    @Test
    void postTaskByIdTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks");
        Task task2 = new Task("Имя задачи 2", "Описание задачи 2",
                Status.DONE,
                1,
                Duration.ofMinutes(55),
                LocalDateTime.of(2025, 2, 7, 16, 27));
        String json = gson.toJson(task2);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(201, response.statusCode(), "Ожидается код статуса - 201");
        assertEquals("Имя задачи 2", taskManager.getTaskById(1).getName(), "Некорректное имя задачи");
        assertEquals("Описание задачи 2", taskManager.getTaskById(1).getDescription(),
                "Некорректное описание задачи");
        assertEquals(1, taskManager.getTaskById(1).getId(), "Некорректный id задачи");
        assertEquals("DONE", taskManager.getTaskById(1).getStatus().toString(),
                "Некорректный статус задачи");
    }

    @Test
    void deleteTaskByIdTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Ожидается код статуса - 200");
    }
}
