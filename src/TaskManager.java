import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    // Создание задачи.
    void addNewTask(Task task);
    // Создание Эпика.
    void addNewEpic(Epic epic);
    // Создание подзадачи.
    void addNewSubtask(SubTask subTask);

    // Обновление задачи.
    void updateTask(Task task);
    // Обновление Эпика.
    void updateEpic(Epic epic);
    // Обновление подзадачи.
    void updateSubtask(SubTask subTask);

    // Удаление задач
    void removeTasks();
    // Удаление Эпиков
    void removeEpics();
    // Удаление подзадач
    void removeSubtasks();
    // Удаление задачи по ID
    void removeTaskById(int id);
    // Удаление Эпика по ID
    void removeEpicById(int id);
    // Удаление подзадачи по ID
    void removeSubtaskById(int id);

    // Получение списка задач.
    ArrayList<Task> getTasksList();
    // Получение списка Эпиков.
    ArrayList<Epic> getEpicsList();
    // Получение списка подзадач.
    ArrayList<SubTask> getSubtasksList();
    // Получение списка всех подзадач определённого эпика.
    ArrayList<SubTask> getSubtasksListByEpicId(int epicId);

    // Получение задачи по идентификатору.
    Task getTaskById(int id);
    // Получение Эпика по идентификатору.
    Epic getEpicById(int id);
    // Получение подзадачи по идентификатору.
    SubTask getSubtaskById(int id);
    // Получаем историю просмотренных задач
    List<Task> getHistory();
}
