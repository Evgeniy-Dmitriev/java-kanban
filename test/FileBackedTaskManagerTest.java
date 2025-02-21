import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private final File file = File.createTempFile("file", ".csv", null);

    FileBackedTaskManagerTest() throws IOException {
    }

    @Override
    FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(file);
    }

    @Test
    void shouldSaveAndLoadFromFile() {
        FileBackedTaskManager newManager = new FileBackedTaskManager(file);
        Task task = new Task("Test Task", "Description",
                Duration.ofMinutes(60),
                LocalDateTime.now());
        newManager.addNewTask(task);

        ArrayList<Task> loadedTasks = newManager.getTasksList();

        assertFalse(loadedTasks.isEmpty());
        assertEquals(task.getName(), loadedTasks.get(0).getName());
    }
}
