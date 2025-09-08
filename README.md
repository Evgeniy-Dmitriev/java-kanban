# Task Manager ✅

**Task Manager** — это система управления задачами с поддержкой иерархической структуры: задачи, эпики и подзадачи. Проект предоставляет полный набор CRUD операций с отслеживанием истории и приоритизацией.

## 📖 О проекте

Task Manager представляет собой Java-приложение для эффективного управления задачами. Система поддерживает три типа сущностей с различным уровнем вложенности и зависимостей, что позволяет организовывать сложные проекты и отслеживать прогресс.

**Ключевые концепции:**
- **Задачи (Tasks)** - базовые единицы работы
- **Эпики (Epics)** - крупные задачи, содержащие подзадачи
- **Подзадачи (Subtasks)** - составные части эпиков
- **История просмотров** - отслеживание последних просмотренных задач
- **Приоритизация** - упорядочивание задач по приоритету

## 🏗 Архитектура и структура

### Интерфейс TaskManager
```java
public interface TaskManager {
    // Основные CRUD операции для всех типов задач
    void addNewTask(Task task);
    void addNewEpic(Epic epic);
    void addNewSubtask(SubTask subTask);
    
    // Операции обновления
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(SubTask subTask);
    
    // Операции удаления (групповые и одиночные)
    void removeTasks();
    void removeEpics();
    void removeSubtasks();
    void removeTaskById(int id);
    void removeEpicById(int id);
    void removeSubtaskById(int id);
    
    // Операции получения данных
    ArrayList<Task> getTasksList();
    ArrayList<Epic> getEpicsList();
    ArrayList<SubTask> getSubtasksList();
    ArrayList<SubTask> getSubtasksListByEpicId(int epicId);
    
    // Получение по идентификатору
    Task getTaskById(int id);
    Epic getEpicById(int id);
    SubTask getSubtaskById(int id);
    
    // Дополнительные функции
    List<Task> getHistory();          // История просмотров
    List<Task> getPrioritizedTasks(); // Приоритизированный список
}
```
