package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    static TaskManager taskManager;

    @BeforeAll
    static void beforeAll() {
        taskManager = Managers.getDefault();
    }

    @Test
    void testEquals() {
        Epic epic1 = new Epic("Name1", "Description1");
        Epic epic2 = new Epic("Name2", "Description2");
        epic1.setId(2);
        epic2.setId(2);

        assertEquals(epic1, epic2, "Объекты не равны!");
    }

    @Test
    void shouldNotEpicAddAsSubtaskToItself() {
        Epic epic = new Epic("Name", "Description");
        taskManager.addNewEpic(epic);

        SubTask subtask = new SubTask("Name", "Description", epic.getId(), null, null);
        taskManager.addNewSubtask(subtask);

        for (int id : epic.getSubtasks()) {
            assertNotEquals(id, epic.getId(), "В списке подзадач не должно быть эпика.");
        }
    }
}