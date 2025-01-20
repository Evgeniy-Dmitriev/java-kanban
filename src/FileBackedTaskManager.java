import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : getTasksList()) {
                fileWriter.write(toString(task) + "\n");
            }
            for (Epic epic : getEpicsList()) {
                fileWriter.write(toString(epic) + "\n");
            }
            for (SubTask subTask : getSubtasksList()) {
                fileWriter.write(toString(subTask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл", e);
        }
    }

    private String toString(Task task) {
        String type = task.getClass().getName().toUpperCase();
        String epicId = "";
        if (type.equals("SUBTASK")) {
            epicId = String.valueOf(((SubTask) task).getEpicId());
        }
        return String.join(",",
                String.valueOf(task.getId()),
                type,
                task.getName(),
                String.valueOf(task.getStatus()),
                task.getDescription(),
                epicId
        );
    }

    private static Task fromString(String value) {
        String[] taskArray = value.split(",");
        int id = Integer.parseInt(taskArray[0]);
        String type = taskArray[1];
        String name = taskArray[2];
        Status status = Status.valueOf(taskArray[3]);
        String description = taskArray[4];

        Task task = null;
        switch (type) {
            case "TASK":
                task = new Task(name, description);
                break;
            case "EPIC":
                task = new Epic(name, description);
                break;
            case "SUBTASK":
                int epicId = Integer.parseInt(taskArray[5]);
                task = new SubTask(name, description, epicId);
                break;
        }
        task.setId(id);
        task.setStatus(status);
        return task;
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addNewSubtask(SubTask subTask) {
        super.addNewSubtask(subTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        super.updateSubtask(subTask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) {
                    continue;
                }
                Task task = fromString(line);
                String type = task.getClass().getName();
                switch (type) {
                    case "Task":
                        fileBackedTaskManager.tasks.put(task.getId(), task);
                        break;
                    case "Epic":
                        fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                        break;
                    case "SubTask":
                        fileBackedTaskManager.subTasks.put(task.getId(), (SubTask) task);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadFromFileException("Ошибка при выгрузке из файла", e);
        }

        return fileBackedTaskManager;
    }

    public static void main(String[] args) throws IOException {
        File file = File.createTempFile("file", ".csv", null);
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        Task task = new Task("Имя задачи 1", "Описание задачи 1");
        fileBackedTaskManager.addNewTask(task);
        Task task2 = new Task("Имя задачи 2", "Описание задачи 2");
        fileBackedTaskManager.addNewTask(task2);

        Epic epic = new Epic("Имя Эпика 1", "Описание эпика 1");
        fileBackedTaskManager.addNewEpic(epic);

        SubTask subtask = new SubTask("Имя подзадачи 1", "Описание подзадачи 1", epic.getId());
        fileBackedTaskManager.addNewSubtask(subtask);
        SubTask subtask2 = new SubTask("Имя подзадачи 2", "Описание подзадачи 2", epic.getId());
        fileBackedTaskManager.addNewSubtask(subtask2);

        FileBackedTaskManager loadedFileBackedTaskManager = loadFromFile(file);

        System.out.println("Количество задач: " + loadedFileBackedTaskManager.getTasksList().size());
        System.out.println("Количество Эпиков: " + loadedFileBackedTaskManager.getEpicsList().size());
        System.out.println("Количество Подзадач: " + loadedFileBackedTaskManager.getSubtasksList().size());
    }
}

class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}

class ManagerLoadFromFileException extends RuntimeException {
    public ManagerLoadFromFileException(String message, Throwable cause) {
        super(message, cause);
    }
}