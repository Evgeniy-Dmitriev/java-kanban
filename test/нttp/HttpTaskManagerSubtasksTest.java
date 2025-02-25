package нttp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.HttpTaskServer;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
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

public class HttpTaskManagerSubtasksTest {

    private SubTask subTask;
    private final TaskManager taskManager = Managers.getDefault();
    private final HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerSubtasksTest() throws IOException {
    }

    @BeforeEach
    void setUp() throws IOException {
        taskManager.removeTasks();
        taskManager.removeSubtasks();
        taskManager.removeEpics();
        httpTaskServer.start();
        Epic epic = new Epic("Имя эпика", "Описание эпика");
        taskManager.addNewEpic(epic);
        SubTask subTask = new SubTask("Имя подзадачи 1", "Описание подзадачи 1",
                Status.NEW,
                1,
                epic.getId(),
                Duration.ofMinutes(55),
                LocalDateTime.of(2025, 2, 7, 16, 27));
        taskManager.addNewSubtask(subTask);
    }

    @AfterEach
    void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    void getSubtasksTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Ожидается код статуса - 200");

        Type taskType = new TypeToken<ArrayList<SubTask>>() {}.getType();
        List<SubTask> subTasks = gson.fromJson(response.body(), taskType);

        assertNotNull(subTasks, "Подзадачи не возвращаются");
        assertEquals(1, subTasks.size(), "Некорректное количество подзадач");
        assertEquals("Имя подзадачи 1", subTasks.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    void getSubtaskByIdTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/2");
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
        SubTask actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Подзадача не возвращается");
        assertEquals("Имя подзадачи 1", actual.getName(), "Некорректное имя подзадачи");
    }

    @Test
    void postSubtaskTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks");
        SubTask subTask2 = new SubTask("Имя подзадачи 2", "Описание подзадачи 2",
                1,
                Duration.ofMinutes(55),
                LocalDateTime.of(2025, 2, 7, 17, 27));
        String json = gson.toJson(subTask2);
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
        assertEquals("Имя подзадачи 2", taskManager.getSubtaskById(3).getName(), "Некорректное имя подзадачи");
        assertEquals("Описание подзадачи 2", taskManager.getSubtaskById(3).getDescription(),
                "Некорректное описание подзадачи");
    }

    @Test
    void postSubtaskByIdTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks");
        SubTask subTask2 = new SubTask("Имя подзадачи 2", "Описание подзадачи 2",
                Status.DONE,
                2,
                1,
                Duration.ofMinutes(55),
                LocalDateTime.of(2025, 2, 7, 16, 27));
        String json = gson.toJson(subTask2);
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
        assertEquals("Имя подзадачи 2", taskManager.getSubtaskById(2).getName(), "Некорректное имя подзадачи");
        assertEquals("Описание подзадачи 2", taskManager.getSubtaskById(2).getDescription(),
                "Некорректное описание подзадачи");
        assertEquals(2, taskManager.getSubtaskById(2).getId(), "Некорректный id подзадачи");
        assertEquals("DONE", taskManager.getSubtaskById(2).getStatus().toString(),
                "Некорректный статус подзадачи");
    }

    @Test
    void deleteSubtaskByIdTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/2");
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
