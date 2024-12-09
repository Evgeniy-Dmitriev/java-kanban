import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    static TaskManager taskManager;

    @BeforeAll
    static void beforeAll() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldPreviousTaskVersionInHistory() {
        Task task = new Task("Name", "Description");
        taskManager.addNewTask(task);

        String previousTaskName = task.getName();
        String previousTaskDescription = task.getDescription();

        taskManager.getTaskById(task.getId());

        taskManager.updateTask(new Task("New_name", "New_description", task.getStatus(), task.getId()));

        Task previousTaskVersion = taskManager.getHistory().get(0);

        assertEquals(previousTaskName, previousTaskVersion.getName(),
                "Предыдущее имя задачи на момент получения должно быть таким же как в истории просмотров."
        );
        assertEquals(previousTaskDescription, previousTaskVersion.getDescription(),
                "Предыдущее описание задачи на момент получения должно быть таким же как в истории просмотров."
        );
    }

}