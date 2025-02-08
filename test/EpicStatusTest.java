import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicStatusTest {
    private TaskManager manager;
    private Epic epic;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
        epic = new Epic("Name1", "Description1");
        manager.addNewEpic(epic);
    }

    @Test
    void shouldBeNewWhenEpicHasNoSubtasks() {
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void shouldBeNewWhenAllSubtasksAreNew() {
        // Создаем подзадачи со статусом NEW
        SubTask subTask1 = new SubTask("Name1", "Description1", Status.NEW, 2, epic.getId(), null, null);
        SubTask subTask2 = new SubTask("Name2", "Description2", Status.NEW, 3, epic.getId(), null, null);
        manager.addNewSubtask(subTask1);
        manager.addNewSubtask(subTask2);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void shouldBeDoneWhenAllSubtasksAreDone() {
        // Создаем подзадачи со статусом DONE
        SubTask subTask1 = new SubTask("Name1", "Description1", Status.DONE, 2, epic.getId(), null, null);
        SubTask subTask2 = new SubTask("Name2", "Description2", Status.DONE, 3, epic.getId(), null, null);
        manager.addNewSubtask(subTask1);
        manager.addNewSubtask(subTask2);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void shouldBeInProgressWhenSubtasksAreNewAndDone() {
        // Создаем подзадачи с разными статусами
        SubTask subTask1 = new SubTask("Name1", "Description1", Status.NEW, 2, epic.getId(), null, null);
        SubTask subTask2 = new SubTask("Name2", "Description2", Status.DONE, 3, epic.getId(), null, null);
        manager.addNewSubtask(subTask1);
        manager.addNewSubtask(subTask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldBeInProgressWhenSubtasksAreInProgress() {
        // Создаем подзадачи со статусом IN_PROGRESS
        SubTask subTask1 = new SubTask("Name1", "Description1", Status.IN_PROGRESS, 2, epic.getId(), null, null);
        SubTask subTask2 = new SubTask("Name2", "Description2", Status.IN_PROGRESS, 3, epic.getId(), null, null);
        manager.addNewSubtask(subTask1);
        manager.addNewSubtask(subTask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldBeInProgressWhenSubtasksHaveMixedProgress() {
        // Создаем подзадачи с разными статусами
        SubTask subTask1 = new SubTask("Name1", "Description1", Status.NEW, 2, epic.getId(), null, null);
        SubTask subTask2 = new SubTask("Name2", "Description2", Status.IN_PROGRESS, 3, epic.getId(), null, null);
        SubTask subTask3 = new SubTask("Name3", "Description3", Status.DONE, 3, epic.getId(), null, null);
        manager.addNewSubtask(subTask1);
        manager.addNewSubtask(subTask2);
        manager.addNewSubtask(subTask3);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldUpdateStatusWhenSubtaskStatusChanges() {
        // Создаем подзадачу со статусом NEW
        SubTask subTask1 = new SubTask("Name1", "Description1", Status.NEW, 2, epic.getId(), null, null);
        manager.addNewSubtask(subTask1);
        assertEquals(Status.NEW, epic.getStatus());

        // Меняем статус подзадачи на IN_PROGRESS
        subTask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subTask1);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());

        // Меняем статус подзадачи на DONE
        subTask1.setStatus(Status.DONE);
        manager.updateSubtask(subTask1);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void shouldUpdateStatusWhenSubtaskIsRemoved() {
        // Создаем подзадачу со статусом DONE
        SubTask subTask1 = new SubTask("Name1", "Description1", Status.DONE, 2, epic.getId(), null, null);
        SubTask subTask2 = new SubTask("Name2", "Description2", Status.NEW, 3, epic.getId(), null, null);
        manager.addNewSubtask(subTask1);
        manager.addNewSubtask(subTask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());

        // Удаляем одну подзадачу
        manager.removeSubtaskById(subTask2.getId());
        assertEquals(Status.DONE, epic.getStatus());

        // Удаляем все подзадачи
        manager.removeSubtaskById(subTask1.getId());
        assertEquals(Status.NEW, epic.getStatus());
    }
}