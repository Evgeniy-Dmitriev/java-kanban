import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Name1", "Description1", Status.NEW, 1,
                Duration.ofMinutes(60),
                LocalDateTime.of(2025, 2, 7, 10, 0));
        task2 = new Task("Name2", "Description2", Status.IN_PROGRESS, 2,
                Duration.ofMinutes(60),
                LocalDateTime.of(2025, 2, 7, 11, 0));
        task3 = new Task("Name3", "Description3", Status.DONE, 3,
                Duration.ofMinutes(60),
                LocalDateTime.of(2025, 2, 7, 12, 0));
    }

    @Test
    void shouldReturnEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty(), "История должна быть пустой при создании");
    }

    @Test
    void shouldAddTaskToHistory() {
        historyManager.add(task1);

        assertEquals(1, historyManager.getHistory().size(), "Размер истории должен быть 1");
        assertEquals(task1, historyManager.getHistory().get(0), "Первая задача должна быть task1");
    }

    @Test
    void shouldNotDuplicateTaskInHistory() {
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);

        assertEquals(1, historyManager.getHistory().size(),
                "Размер истории должен быть 1 даже после множественного добавления той же задачи");
        assertEquals(task1, historyManager.getHistory().get(0),
                "Задача должна появляться в истории только один раз");
    }

    @Test
    void shouldUpdatePositionOnDuplicateAdd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        assertEquals(2, historyManager.getHistory().size(),
                "Размер истории должен быть 2");
        assertEquals(task2, historyManager.getHistory().get(0),
                "Первой должна быть task2");
        assertEquals(task1, historyManager.getHistory().get(1),
                "Второй должна быть task1");
    }

    @Test
    void shouldRemoveFromBeginning() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId());

        assertEquals(2, historyManager.getHistory().size(),
                "Размер истории должен быть 2 после удаления");
        assertEquals(task2, historyManager.getHistory().get(0),
                "Первой должна быть task2");
        assertEquals(task3, historyManager.getHistory().get(1),
                "Второй должна быть task3");
    }

    @Test
    void shouldRemoveFromMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        assertEquals(2, historyManager.getHistory().size(),
                "Размер истории должен быть 2 после удаления");
        assertEquals(task1, historyManager.getHistory().get(0),
                "Первой должна быть task1");
        assertEquals(task3, historyManager.getHistory().get(1),
                "Второй должна быть task3");
    }

    @Test
    void shouldRemoveFromEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getId());

        assertEquals(2, historyManager.getHistory().size(),
                "Размер истории должен быть 2 после удаления");
        assertEquals(task1, historyManager.getHistory().get(0),
                "Первой должна быть task1");
        assertEquals(task2, historyManager.getHistory().get(1),
                "Второй должна быть task2");
    }

    @Test
    void shouldHandleRemoveFromEmptyHistory() {
        historyManager.remove(999);
        assertTrue(historyManager.getHistory().isEmpty(),
                "История должна оставаться пустой после попытки удаления из пустой истории");
    }

    @Test
    void shouldHandleRemoveNonExistentTask() {
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(999);

        assertEquals(2, historyManager.getHistory().size(),
                "Размер истории не должен измениться при попытке удаления несуществующей задачи");
    }

    @Test
    void shouldMaintainOrder() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        var history = historyManager.getHistory();
        assertEquals(3, history.size(), "История должна содержать 3 задачи");
        assertEquals(task1, history.get(0), "Неверный порядок задач - первая");
        assertEquals(task2, history.get(1), "Неверный порядок задач - вторая");
        assertEquals(task3, history.get(2), "Неверный порядок задач - третья");
    }
}