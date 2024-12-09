import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    // Добавление задачи в список истории
    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        while (history.size() >= MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
        history.add(task);
    }

    // Получение списка истории
    @Override
    public List<Task> getHistory() {
        return history;
    }
}
