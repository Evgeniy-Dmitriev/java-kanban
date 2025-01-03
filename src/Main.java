public class Main {
    static TaskManager tm;

    public static void main(String[] args) {
        tm = new InMemoryTaskManager();

        tm.addNewTask(new Task("Название задачи", "Описание задачи"));
        tm.addNewTask(new Task("Название задачи_2", "Описание задачи_2"));
        System.out.println("Список задач:");
        for (Task task : tm.getTasksList()) {
            System.out.println(task);
        }

        tm.addNewEpic(new Epic("Название Эпика", "Описание Эпика"));
        tm.addNewEpic(new Epic("Название Эпика_2", "Описание Эпика_2"));
        System.out.println("Список эпиков:");
        for (Epic epic : tm.getEpicsList()) {
            System.out.println(epic);
        }

        tm.addNewSubtask(new SubTask("Название подзадачи", "Описание подзадачи", 3));
        tm.addNewSubtask(new SubTask("Название подзадачи_2", "Описание подзадачи_2", 3));
        tm.addNewSubtask(new SubTask("Название подзадачи_3", "Описание подзадачи_3", 4));
        System.out.println("Список подзадач Эпика с ID=3:");
        for (SubTask subTask : tm.getSubtasksListByEpicId(3)) {
            System.out.println(subTask);
        }
        System.out.println("Список подзадач Эпика с ID=4:");
        for (SubTask subTask : tm.getSubtasksListByEpicId(4)) {
            System.out.println(subTask);
        }

        System.out.println("Получаем задачи по ID:");
        System.out.println(tm.getTaskById(1));
        System.out.println(tm.getTaskById(2));
        System.out.println("Получаем Эпики по ID:");
        System.out.println(tm.getEpicById(3));
        System.out.println(tm.getEpicById(4));
        System.out.println("Получаем подзадачи по ID:");
        System.out.println(tm.getSubtaskById(5));
        System.out.println(tm.getSubtaskById(6));
        System.out.println(tm.getSubtaskById(7));
        System.out.println("Получаем список getHistory:");
        for (Task task : tm.getHistory()) {
            System.out.println(task);
        }
        System.out.println("Получаем задачи по ID:");
        System.out.println(tm.getTaskById(1));
        System.out.println(tm.getTaskById(2));
        System.out.println("Получаем Эпики по ID:");
        System.out.println(tm.getEpicById(3));
        System.out.println(tm.getEpicById(4));
        System.out.println("Получаем подзадачи по ID:");
        System.out.println(tm.getSubtaskById(5));
        System.out.println(tm.getSubtaskById(6));
        System.out.println(tm.getSubtaskById(7));
        System.out.println("Получаем список getHistory:");
        for (Task task : tm.getHistory()) {
            System.out.println(task);
        }


        tm.updateTask(new Task("New_Название задачи", "New_Описание задачи", Status.DONE, 1));
        tm.updateTask(new Task("New_Название задачи_2", "New_Описание задачи_2", Status.IN_PROGRESS, 2));
        tm.updateSubtask(new SubTask("New_Название подзадачи", "New_Описание подзадачи", Status.IN_PROGRESS, 5, 3));
        tm.updateSubtask(new SubTask("New_Название подзадачи_2", "New_Описание подзадачи_2", Status.DONE, 6, 3));
        tm.updateSubtask(new SubTask("New_Название подзадачи_3", "New_Описание подзадачи_3", Status.DONE, 7, 4));

        System.out.println("Список задач после обновления:");
        for (Task task : tm.getTasksList()) {
            System.out.println(task);
        }
        System.out.println("Список эпиков после обновления:");
        for (Epic epic : tm.getEpicsList()) {
            System.out.println(epic);
            System.out.println("Список подзадач Эпика:");
            for (SubTask subTask : tm.getSubtasksListByEpicId(epic.getId())) {
                System.out.println("--> " + subTask);
            }
        }


        tm.removeTaskById(1);
        tm.removeEpicById(3);
        System.out.println("Список задач после удаления:");
        for (Task task : tm.getTasksList()) {
            System.out.println(task);
        }
        System.out.println("Список эпиков после удаления:");
        for (Epic epic : tm.getEpicsList()) {
            System.out.println(epic);
        }
        System.out.println("Список подзадач после удаления:");
        for (SubTask subTask : tm.getSubtasksList()) {
            System.out.println(subTask);
        }

        System.out.println("Получаем список getHistory:");
        for (Task task : tm.getHistory()) {
            System.out.println(task);
        }
    }
}