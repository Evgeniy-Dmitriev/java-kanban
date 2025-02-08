import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, SubTask> subTasks;
    private final HistoryManager history;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.history = Managers.getDefaultHistory();
    }

    // Создание задачи.
    @Override
    public void addNewTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    // Создание Эпика.
    @Override
    public void addNewEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    // Создание подзадачи.
    @Override
    public void addNewSubtask(SubTask subTask) {
        subTask.setId(nextId++);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubtasks().add(subTask.getId());
        updateEpicStatus(subTask.getEpicId());
        updateEpicFields(subTask.getEpicId());
    }

    // Обновление задачи.
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    // Обновление Эпика.
    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
        updateEpicFields(epic.getId());
    }

    // Обновление подзадачи.
    @Override
    public void updateSubtask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(subTask.getEpicId());
        updateEpicFields(subTask.getEpicId());
    }

    // Удаление задач
    @Override
    public void removeTasks() {
        for (Integer id : tasks.keySet()) {
            history.remove(id);
        }
        tasks.clear();
    }

    // Удаление Эпиков
    @Override
    public void removeEpics() {
        for (Integer id : epics.keySet()) {
            history.remove(id);
        }
        for (Integer id : subTasks.keySet()) {
            history.remove(id);
        }
        epics.clear();
        subTasks.clear();
    }

    // Удаление подзадач
    @Override
    public void removeSubtasks() {
        for (Integer id : subTasks.keySet()) {
            history.remove(id);
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.setStatus(Status.NEW);
        }
    }

    // Удаление задачи по ID
    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        history.remove(id);
    }

    // Удаление Эпика по ID
    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (Integer subTaskId : epic.getSubtasks()) {
            subTasks.remove(subTaskId);
            history.remove(subTaskId);
        }
        epics.remove(id);
        history.remove(id);
    }

    // Удаление подзадачи по ID
    @Override
    public void removeSubtaskById(int id) {
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicId());
        subTasks.remove(id);
        epic.getSubtasks().remove(Integer.valueOf(id));
        updateEpicStatus(epic.getId());
        updateEpicFields(subTask.getEpicId());
        history.remove(id);
    }

    // Получение списка задач.
    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    // Получение списка Эпиков.
    @Override
    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    // Получение списка подзадач.
    @Override
    public ArrayList<SubTask> getSubtasksList() {
        return new ArrayList<>(subTasks.values());
    }

    // Получение списка всех подзадач определённого эпика.
    @Override
    public ArrayList<SubTask> getSubtasksListByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<SubTask> subTasksListByEpic = new ArrayList<>();
        for (Integer id : epic.getSubtasks()) {
            subTasksListByEpic.add(subTasks.get(id));
        }
        return subTasksListByEpic;
    }

    // Получение задачи по идентификатору.
    @Override
    public Task getTaskById(int id) {
        history.add(tasks.get(id));
        return tasks.get(id);
    }

    // Получение Эпика по идентификатору.
    @Override
    public Epic getEpicById(int id) {
        history.add(epics.get(id));
        return epics.get(id);
    }

    // Получение подзадачи по идентификатору.
    @Override
    public SubTask getSubtaskById(int id) {
        history.add(subTasks.get(id));
        return subTasks.get(id);
    }


    // Получаем историю просмотренных задач
    @Override
    public List<Task> getHistory() {
        return history.getHistory();
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

    // Обновление полей Эпика (startTime, duration, endTime)
    public void updateEpicFields(int epicId) {
        Epic epic = epics.get(epicId);
        List<SubTask> subTasks = getSubtasksListByEpicId(epicId);
        if (subTasks.isEmpty()) {
            return;
        }
        Duration totalDuration = subTasks.stream()
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
        LocalDateTime earliestStartTime = subTasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        LocalDateTime latestEndTime = subTasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        epic.setDuration(totalDuration);
        epic.setStartTime(earliestStartTime);
        epic.setEndTime(latestEndTime);
    }
}