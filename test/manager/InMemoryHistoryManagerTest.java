package manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    static TaskManager taskManager;

    @BeforeAll
    static void beforeAll() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldPreviousTaskVersionInHistory() {
        Task task = new Task("Name", "Description", null, null);
        taskManager.addNewTask(task);

        String previousTaskName = task.getName();
        String previousTaskDescription = task.getDescription();

        taskManager.getTaskById(task.getId());

        taskManager.updateTask(new Task("New_name", "New_description", task.getStatus(), task.getId(), null, null));

        Task previousTaskVersion = taskManager.getHistory().get(0);

        assertEquals(previousTaskName, previousTaskVersion.getName(),
                "Предыдущее имя задачи на момент получения должно быть таким же как в истории просмотров."
        );
        assertEquals(previousTaskDescription, previousTaskVersion.getDescription(),
                "Предыдущее описание задачи на момент получения должно быть таким же как в истории просмотров."
        );

        taskManager.removeTaskById(task.getId());
    }

    @Test
    void shouldCorrectlyAddAndRemoveTasksInHistoryManager() {
        taskManager.addNewTask(new Task("Name1", "Description1", null, null));                  // id = 2
        taskManager.addNewTask(new Task("Name2", "Description2", null, null));                  // id = 3
        taskManager.addNewEpic(new Epic("Name3", "Description3"));                  // id = 4
        taskManager.addNewSubtask(new SubTask("Name4", "Description4", 4, null, null));  // id = 5
        taskManager.addNewSubtask(new SubTask("Name5", "Description5", 4, null, null));  // id = 6

        taskManager.getTaskById(2);
        assertEquals(1, taskManager.getHistory().size(), "Размер списка задач должен быть равен 1");
        assertEquals("Name1", taskManager.getHistory().get(0).getName(),
                "Имя должно быть Name1");
        assertEquals("Description1", taskManager.getHistory().get(0).getDescription(),
                "Описание должно быть Description1");

        taskManager.getTaskById(3);
        assertEquals(2, taskManager.getHistory().size(), "Размер списка задач должен быть равен 2");
        assertEquals("Name2", taskManager.getHistory().get(1).getName(),
                "Имя должно быть Name2");
        assertEquals("Description2", taskManager.getHistory().get(1).getDescription(),
                "Описание должно быть Description2");

        taskManager.getEpicById(4);
        assertEquals(3, taskManager.getHistory().size(), "Размер списка задач должен быть равен 3");
        assertEquals("Name3", taskManager.getHistory().get(2).getName(),
                "Имя должно быть Name3");
        assertEquals("Description3", taskManager.getHistory().get(2).getDescription(),
                "Описание должно быть Description3");

        taskManager.getSubtaskById(5);
        assertEquals(4, taskManager.getHistory().size(), "Размер списка задач должен быть равен 4");
        assertEquals("Name4", taskManager.getHistory().get(3).getName(),
                "Имя должно быть Name4");
        assertEquals("Description4", taskManager.getHistory().get(3).getDescription(),
                "Описание должно быть Description4");

        taskManager.getSubtaskById(6);
        assertEquals(5, taskManager.getHistory().size(), "Размер списка задач должен быть равен 5");
        assertEquals("Name5", taskManager.getHistory().get(4).getName(),
                "Имя должно быть Name5");
        assertEquals("Description5", taskManager.getHistory().get(4).getDescription(),
                "Описание должно быть Description5");

        taskManager.getSubtaskById(6);
        assertEquals(5, taskManager.getHistory().size(), "Размер списка задач должен быть равен 5");

        taskManager.removeEpicById(4);
        assertEquals(2, taskManager.getHistory().size(), "Размер списка задач должен быть равен 2");

        taskManager.removeTaskById(3);
        assertEquals(1, taskManager.getHistory().size(), "Размер списка задач должен быть равен 1");
    }
}