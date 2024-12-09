import java.util.List;

public interface HistoryManager {
    // Добавление задачи в список истории
   void add(Task task);
    // Получение списка истории
   List<Task> getHistory();
}
