import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, SubTask> subTasks;
    private final HistoryManager history;
    private final TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.history = Managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    // Создание задачи.
    @Override
    public void addNewTask(Task task) {
        validateTaskTime(task);
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
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
        validateTaskTime(subTask);
        subTask.setId(nextId++);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubtasks().add(subTask.getId());
        updateEpicStatus(subTask.getEpicId());
        updateEpicFields(subTask.getEpicId());
        if (subTask.getStartTime() != null) {
            prioritizedTasks.add(subTask);
        }
    }

    // Обновление задачи.
    @Override
    public void updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());
        prioritizedTasks.remove(oldTask);
        validateTaskTime(task);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
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
        SubTask oldSubTask = subTasks.get(subTask.getId());
        prioritizedTasks.remove(oldSubTask);
        validateTaskTime(subTask);
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(subTask.getEpicId());
        updateEpicFields(subTask.getEpicId());
        if (subTask.getStartTime() != null) {
            prioritizedTasks.add(subTask);
        }
    }

    // Удаление задач
    @Override
    public void removeTasks() {
        tasks.values().forEach(task -> {
            history.remove(task.getId());
            prioritizedTasks.remove(task);
        });
        tasks.clear();
    }

    // Удаление Эпиков
    @Override
    public void removeEpics() {
        epics.values().forEach(epic -> history.remove(epic.getId()));
        subTasks.values().forEach(subTask -> {
            history.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        });
        epics.clear();
        subTasks.clear();
    }

    // Удаление подзадач
    @Override
    public void removeSubtasks() {
        subTasks.values().forEach(subTask -> {
            history.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        });
        subTasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtasks().clear();
            epic.setStatus(Status.NEW);
        });
    }

    // Удаление задачи по ID
    @Override
    public void removeTaskById(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        history.remove(id);
    }

    // Удаление Эпика по ID
    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        epic.getSubtasks().forEach(subTaskId -> {
            prioritizedTasks.remove(subTasks.get(subTaskId));
            subTasks.remove(subTaskId);
            history.remove(subTaskId);
        });
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
        prioritizedTasks.remove(subTask);
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
        return epics.get(epicId).getSubtasks().stream()
                .map(subTasks::get)
                .collect(Collectors.toCollection(ArrayList::new));
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

    // Получаем список задач в порядке приоритета
    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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

    // Проверяем пересечения задач по времени
    private void validateTaskTime(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return;
        }
        boolean hasIntersection = prioritizedTasks.stream()
                .anyMatch(existingTask -> checkTasksIntersection(existingTask, newTask));
        if (hasIntersection) {
            throw new IllegalStateException("Задача пересекается по времени с уже существующей задачей");
        }
    }

    private boolean checkTasksIntersection(Task task1, Task task2) {
        if (task1 == null || task2 == null || task1.equals(task2)) {
            return false;
        }
        if (task1.getStartTime() == null || task2.getStartTime() == null ||
                task1.getDuration() == null || task2.getDuration() == null) {
            return false;
        }
        return task1.getStartTime().isBefore(task2.getEndTime()) &&
                task2.getStartTime().isBefore(task1.getEndTime());
    }
}