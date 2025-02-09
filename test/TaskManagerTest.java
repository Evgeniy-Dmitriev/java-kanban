import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    abstract T createManager();

    @BeforeEach
    void setUp() {
        manager = createManager();
    }

    @Test
    void shouldCreateTask() {
        Task task = new Task("Name", "Description", Status.NEW, 1,
                Duration.ofMinutes(60),
                LocalDateTime.now());
        manager.addNewTask(task);
        Task addedNewTask = manager.getTaskById(task.getId());

        assertNotNull(addedNewTask);
        assertEquals(task.getName(), addedNewTask.getName());
        assertEquals(task.getDescription(), addedNewTask.getDescription());
        assertEquals(task.getStatus(), addedNewTask.getStatus());
    }

    @Test
    void shouldCreateEpicWithCorrectStatus() {
        Epic epic = new Epic("Name", "Description", 2, new ArrayList<>());
        manager.addNewEpic(epic);
        Epic addedNewEpic = manager.getEpicById(epic.getId());

        assertEquals(Status.NEW, addedNewEpic.getStatus());

        SubTask subtask1 = new SubTask("Name1", "Description1", Status.IN_PROGRESS, 3,
                addedNewEpic.getId(),
                Duration.ofMinutes(30),
                LocalDateTime.now());
        SubTask subtask2 = new SubTask("Name2", "Description2", Status.DONE, 4,
                addedNewEpic.getId(),
                Duration.ofMinutes(30),
                LocalDateTime.now().plusHours(1));
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(addedNewEpic.getId()).getStatus());
    }

    @Test
    void shouldPreventTimeOverlap() {
        LocalDateTime startTime = LocalDateTime.now();

        Task task1 = new Task("Name1", "Description1",
                Duration.ofMinutes(60),
                startTime);
        manager.addNewTask(task1);

        Task task2 = new Task("Name2", "Description2",
                Duration.ofMinutes(60),
                startTime.plusMinutes(30));

        assertThrows(IllegalStateException.class, () -> manager.addNewTask(task2));
    }

    @Test
    void shouldManageSubtaskEpicRelation() {
        Epic epic = new Epic("Name", "Description", 7, new ArrayList<>());
        manager.addNewEpic(epic);
        Epic addedNewEpic = manager.getEpicById(epic.getId());

        SubTask subtask = new SubTask("Name", "Description", Status.NEW, 8, addedNewEpic.getId(),
                Duration.ofMinutes(60),
                LocalDateTime.now());
        manager.addNewSubtask(subtask);
        SubTask addedNewSubtask = manager.getSubtaskById(subtask.getId());

        assertEquals(addedNewEpic.getId(), addedNewSubtask.getEpicId());
        assertTrue(manager.getEpicById(addedNewEpic.getId()).getSubtasks().contains(addedNewSubtask.getId()));
    }

    @Test
    void shouldUpdateEpicStatusBasedOnSubtasks() {
        Epic epic = new Epic("Name", "Description", 9, new ArrayList<>());
        manager.addNewEpic(epic);
        Epic addedNewEpic = manager.getEpicById(epic.getId());

        assertEquals(Status.NEW, addedNewEpic.getStatus());

        SubTask subtask = new SubTask("Name", "Description", Status.DONE, 10, addedNewEpic.getId(),
                Duration.ofMinutes(30),
                LocalDateTime.now());
        manager.addNewSubtask(subtask);

        assertEquals(Status.DONE, manager.getEpicById(addedNewEpic.getId()).getStatus());
    }
}