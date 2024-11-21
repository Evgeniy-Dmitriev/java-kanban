import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int nextId = 1;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    // Создание задачи.
    public void addNewTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    // Создание Эпика.
    public void addNewEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    // Создание подзадачи.
    public void addNewSubtask(SubTask subTask) {
        subTask.setId(nextId++);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubtasks().add(subTask.getId());
        updateEpicStatus(subTask.getEpicId());
    }

    // Обновление задачи.
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    // Обновление Эпика.
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
    }

    // Обновление подзадачи.
    public void updateSubtask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(subTask.getEpicId());
    }

    // Удаление задач
    public void removeTasks() {
        tasks.clear();
    }

    // Удаление Эпиков
    public void removeEpics() {
        epics.clear();
        subTasks.clear();
    }

    // Удаление подзадач
    public void removeSubtasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.setStatus(Status.NEW);
        }
    }

    // Удаление задачи по ID
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    // Удаление Эпика по ID
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (Integer subTaskId : epic.getSubtasks()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(id);
    }

    // Удаление подзадачи по ID
    public void removeSubtaskById(int id) {
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicId());
        subTasks.remove(id);
        epic.getSubtasks().remove(Integer.valueOf(id));
        updateEpicStatus(epic.getId());
    }

    // Получение списка задач.
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    // Получение списка Эпиков.
    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    // Получение списка подзадач.
    public ArrayList<SubTask> getSubtasksList() {
        return new ArrayList<>(subTasks.values());
    }

    // Получение задачи по идентификатору.
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    // Получение Эпика по идентификатору.
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    // Получение подзадачи по идентификатору.
    public SubTask getSubtaskById(int id) {
        return subTasks.get(id);
    }

    // Получение списка всех подзадач определённого эпика.
    public ArrayList<SubTask> getSubtasksListByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<SubTask> subTasksListByEpic = new ArrayList<>();
        for (Integer id : epic.getSubtasks()) {
            subTasksListByEpic.add(subTasks.get(id));
        }
        return subTasksListByEpic;
    }

    // Обновление статуса Эпика
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            boolean isAllDone = true;
            boolean isAllNew = true;
            for (Integer subTasksId : epic.getSubtasks()) {
                SubTask subTask = subTasks.get(subTasksId);
                if (subTask.getStatus() != Status.DONE) {
                    isAllDone = false;
                }
                if (subTask.getStatus() != Status.NEW) {
                    isAllNew = false;
                }
            }
            if (isAllDone) {
                epic.setStatus(Status.DONE);
            } else if (isAllNew) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

}