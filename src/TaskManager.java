import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    int nextId = 1;
    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, SubTask> subTasks;

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
    public void addNewTask(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    // Создание подзадачи.
    public void addNewTask(SubTask subTask) {
        subTask.setId(nextId++);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.subTasks.add(subTask.getId());
        updateEpicStatus(subTask.getEpicId());
    }

    // Обновление задачи.
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    // Обновление Эпика.
    public void updateTask(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    // Обновление подзадачи.
    public void updateTask(SubTask subTask) {
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
            epic.subTasks.clear();
            epic.status = Status.NEW;
        }
    }

    // Удаление задачи(Эпика, подзадачи) по идентификатору.
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Integer subTaskId : epic.subTasks) {
                subTasks.remove(subTaskId);
            }
            epics.remove(id);
        } else if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            Epic epic = epics.get(subTask.getEpicId());
            subTasks.remove(id);
            epic.subTasks.remove(Integer.valueOf(id));
            updateEpicStatus(epic.getId());
        } else {
            System.out.println("Задачи с таким ID нет!");
        }
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
        for (Integer id : epic.subTasks) {
            subTasksListByEpic.add(subTasks.get(id));
        }
        return subTasksListByEpic;
    }

    // Обновление статуса Эпика
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        boolean isAllDone = true;
        boolean isAllNew = true;
        for (Integer subTasksId : epic.subTasks) {
            SubTask subTask = subTasks.get(subTasksId);
            if (subTask.status != Status.DONE) {
                isAllDone = false;
            }
            if (subTask.status != Status.NEW) {
                isAllNew = false;
            }
        }
        if (isAllDone) {
            epic.status = Status.DONE;
        } else if (isAllNew) {
            epic.status = Status.NEW;
        } else {
            epic.status = Status.IN_PROGRESS;
        }
    }

}