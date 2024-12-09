import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void testEquals() {
        Task task1 = new Task("Name1", "Description1");
        Task task2 = new Task("Name2", "Description2");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2, "Объекты не равны!");
    }
}