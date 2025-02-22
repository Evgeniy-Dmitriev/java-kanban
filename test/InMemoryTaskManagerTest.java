import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    static TaskManager taskManager;
    static Task task;
    static Epic epic;
    static SubTask subTask;

    @BeforeAll
    static  void beforeAll(){
        taskManager = Managers.getDefault();
        task = new Task("TaskName", "TaskDescription", null, null);
        epic = new Epic("EpicName", "EpicDescription");
    }

    @Test
    void testAddNewTask() {
        taskManager.addNewTask(task);
        int id = taskManager.getTasksList().getFirst().getId();

        assertEquals(task.getName(), taskManager.getTaskById(id).getName(), "Названия задач должны совпадать.");
        assertEquals(task.getDescription(), taskManager.getTaskById(id).getDescription(), "Описания задач должны совпадать.");
    }

    @Test
    void testAddNewEpic() {
        taskManager.addNewEpic(epic);
        int id = taskManager.getEpicsList().getFirst().getId();

        assertEquals(epic.getName(), taskManager.getEpicById(id).getName(), "Названия эпиков должны совпадать.");
        assertEquals(epic.getDescription(), taskManager.getEpicById(id).getDescription(), "Описания эпиков должны совпадать.");
    }

    @Test
    void testAddNewSubtask() {
        taskManager.addNewEpic(epic);
        int epicId = taskManager.getEpicsList().getFirst().getId();

        subTask = new SubTask("SubtaskName", "SubtaskDescription", epicId, null, null);
        taskManager.addNewSubtask(subTask);
        int id = taskManager.getSubtasksList().getFirst().getId();

        assertEquals(subTask.getName(), taskManager.getSubtaskById(id).getName(), "Названия подзадач должны совпадать.");
        assertEquals(subTask.getDescription(), taskManager.getSubtaskById(id).getDescription(), "Описания подзадач должны совпадать.");
        assertEquals(subTask.getEpicId(), taskManager.getSubtaskById(id).getEpicId(), "ID эпиков подзадач должны быть равны.");
    }

    @Test
    void shouldClearTasksFromMemory() {
        Task task = new Task("Name", "Description",
                Duration.ofMinutes(60),
                LocalDateTime.now());
        manager.addNewTask(task);

        manager.removeTasks();
        assertTrue(manager.getTasksList().isEmpty());
    }

    @Override
    InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }
}