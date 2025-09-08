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

public class HttpTaskManagerEpicsTest {

    private Epic epic;
    private final TaskManager taskManager = Managers.getDefault();
    private final HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerEpicsTest() throws IOException {
    }

    @BeforeEach
    void setUp() throws IOException {
        taskManager.removeTasks();
        taskManager.removeSubtasks();
        taskManager.removeEpics();
        httpTaskServer.start();
        epic = new Epic("Имя эпика 1", "Описание эпика 1", 1, new ArrayList<>());
        taskManager.addNewEpic(epic);
    }

    @AfterEach
    void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    void getEpicsTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics");
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
        List<Epic> epics = gson.fromJson(response.body(), taskType);

        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Некорректное количество эпиков");
        assertEquals("Имя эпика 1", epics.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    void getSubtasksByEpicIdTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics/1/subtasks");
        SubTask subTask1 = new SubTask("Имя подзадачи 1", "Описание подзадачи 1", 1,
                Duration.ofMinutes(55),
                LocalDateTime.of(2025, 2, 7, 16, 27));
        SubTask subTask2 = new SubTask("Имя подзадачи 2", "Описание подзадачи 2", 1,
                Duration.ofMinutes(55),
                LocalDateTime.of(2025, 2, 7, 17, 27));
        taskManager.addNewSubtask(subTask1);
        taskManager.addNewSubtask(subTask2);
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
        assertEquals(2, subTasks.size(), "Некорректное количество подзадач");
        assertEquals("Имя подзадачи 1", subTasks.get(0).getName(), "Некорректное имя подзадачи");
        assertEquals("Имя подзадачи 2", subTasks.get(1).getName(), "Некорректное имя подзадачи");
    }

    @Test
    void getEpicByIdTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics/1");
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
        Epic actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Эпик не возвращается");
        assertEquals("Имя эпика 1", actual.getName(), "Некорректное имя эпика");
    }

    @Test
    void postEpicTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics");
        Epic epic2 = new Epic("Имя эпика 2", "Описание эпика 2");
        String json = gson.toJson(epic2);
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
        assertEquals("Имя эпика 2", taskManager.getEpicById(2).getName(), "Некорректное имя эпика");
        assertEquals("Описание эпика 2", taskManager.getEpicById(2).getDescription(),
                "Некорректное описание эпика");
    }

    @Test
    void deleteEpicByIdTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics/1");
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
