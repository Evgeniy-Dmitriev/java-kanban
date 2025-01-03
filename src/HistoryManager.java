import java.util.List;

public interface HistoryManager {
    // Добавление задачи в список истории
    void add(Task task);

    // Удаление задачи из списка истории
    void remove(int id);

    // Получение списка истории
    List<Task> getHistory();
}
