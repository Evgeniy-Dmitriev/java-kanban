import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    static TaskManager taskManager;

    @BeforeAll
    static void beforeAll() {
        taskManager = Managers.getDefault();
    }

    @Test
    void testEquals() {
        SubTask subTask1 = new SubTask("Name1", "Description1",2);
        SubTask subTask2 = new SubTask("Name2", "Description2", 3);
        subTask1.setId(3);
        subTask2.setId(3);

        assertEquals(subTask1, subTask2, "Объекты не равны!");
    }

    @Test
    void shouldNotSubtaskAsItsOwnEpic() {
        Epic epic = new Epic("Name", "Description");
        taskManager.addNewEpic(epic);

        SubTask subtask = new SubTask("Name", "Description", epic.getId());
        taskManager.addNewSubtask(subtask);

        assertNotEquals(subtask.getEpicId(), subtask.getId(), "Подзадача не должна быть своим собственным Эпиком.");
    }
}