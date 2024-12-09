import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    static TaskManager taskManager;
    static HistoryManager historyManager;
    static Task task;

    @Test
    void getDefault() {
        taskManager = Managers.getDefault();

        assertNotNull(taskManager, "taskManager должен быть создан.");
        assertTrue(taskManager.getTasksList().isEmpty(), "taskManager должен быть инициализирован с пустым списком задач");

        task = new Task("Name", "Description");
        taskManager.addNewTask(task);
        assertEquals(1, taskManager.getTasksList().size(), "Размер списка задач должен быть равен 1 после добавления задачи.");

        taskManager.getTaskById(1);
        assertEquals(1, taskManager.getHistory().size(), "Размер списка просмотренных задач должен быть равен 1 после просмотра задачи.");
    }

    @Test
    void getDefaultHistory() {
        historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "historyManager должен быть создан.");
        assertTrue(historyManager.getHistory().isEmpty(), "historyManager должен быть инициализирован с пустым списком просмотренных задач.");
    }
}