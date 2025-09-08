package task;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void testEquals() {
        Task task1 = new Task("Name1", "Description1",
                Duration.ofMinutes(50),
                null);
        Task task2 = new Task("Name2", "Description2",
                null,
                LocalDateTime.of(2024, 2, 7, 22, 22));
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2, "Объекты не равны!");
        assertEquals(Duration.ofMinutes(50), task1.getDuration(), "Длительности не равны!");
        assertEquals(LocalDateTime.of(2024, 2, 7, 22, 22),
                task2.getStartTime(), "Стартовое время не равны!");
    }
}