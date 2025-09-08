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
# Task Manager

## Модели данных (ожидаемая структура)

```java
class Task {
    int id;
    String title;
    String description;
    Status status;
    LocalDateTime startTime;
    Duration duration;
}

class Epic extends Task {
    List<Integer> subtaskIds; // IDs подзадач
    LocalDateTime endTime;    // Расчетное время завершения
}

class SubTask extends Task {
    int epicId; // ID родительского эпика
}

enum Status {
    NEW, IN_PROGRESS, DONE
}
```

## 🚀 Основные возможности

### ✅ Управление задачами
- **Создание** задач, эпиков и подзадач
- **Чтение** списков и отдельных элементов
- **Обновление** существующих записей
- **Удаление** (одиночное и групповое)

### 🔄 Отслеживание зависимостей
- Автоматическое обновление статуса эпика при изменении подзадач
- Валидация связей между подзадачами и эпиками
- Расчет времени выполнения для эпиков

### 📊 История и аналитика
- **История просмотров** - последние просмотренные задачи
- **Приоритизация** - задачи упорядочены по времени начала
- **Статус трекинг** - автоматическое обновление прогресса

### 🎯 Особенности реализации
- **Типизация** - строгая типизация для всех операций
- **Идентификация** - уникальные ID для всех сущностей
- **Иерархия** - четкое разделение на задачи/эпики/подзадачи
- **Валидация** - проверка корректности операций

## 💻 Пример использования

```java
// Создание менеджера
TaskManager manager = new InMemoryTaskManager();

// Добавление эпика и подзадач
Epic epic = new Epic("Разработка проекта", "Описание");
manager.addNewEpic(epic);

SubTask design = new SubTask("Дизайн", "Создать макеты", epic.getId());
SubTask development = new SubTask("Разработка", "Написать код", epic.getId());
manager.addNewSubtask(design);
manager.addNewSubtask(development);

// Получение списка подзадач эпика
List<SubTask> epicSubtasks = manager.getSubtasksListByEpicId(epic.getId());

// Отметка подзадачи как выполненной
development.setStatus(Status.DONE);
manager.updateSubtask(development);

// Автоматическое обновление статуса эпика
// Status эпика будет пересчитан автоматически
```

## 🧪 Тестирование

Рекомендуемые тестовые сценарии:

```java
// Тестирование CRUD операций
testAddAndGetTask();
testUpdateTask();
testDeleteTask();

// Тестирование связей эпиков и подзадач
testEpicStatusCalculation();
testSubtaskEpicRelationship();
testDeleteEpicWithSubtasks();

// Тестирование истории
testHistoryTracking();
testHistoryLimit();
testDuplicateHistoryPrevention();

// Тестирование приоритизации
testTaskPrioritization();
testTimeOverlapValidation();
```

## 🔧 Планы по развитию

### Краткосрочные улучшения
1. **Сохранение состояния** - сериализация в файл/БД
2. **Временные интервалы** - проверка пересечений по времени
3. **Уведомления** - напоминания о дедлайнах

### Среднесрочные улучшения
1. **REST API** - веб-интерфейс для управления
2. **Пользователи** - система многопользовательского доступа
3. **Теги и категории** - расширенная организация задач

### Долгосрочные улучшения
1. **Графический интерфейс** - desktop или web приложение
2. **Мобильное приложение** - iOS/Android клиенты
3. **Интеграции** - календари, email уведомления

## 📦 Зависимости

Проект требует:
- **Java 8+** (используются Collections, Generics)
- **JUnit 5** (для тестирования)
- **Optional** (для обработки отсутствующих значений)

## 🎯 Принципы проектирования

1. **SRP** - каждый класс имеет одну ответственность
2. **OCP** - открытость для расширения через интерфейсы
3. **LSP** - эпики и подзадачи совместимы с базовой задачей
4. **ISP** - разделенные интерфейсы для разных операций
5. **DIP** - зависимость от абстракций (интерфейсов)

## 👥 Разработчики

Проект разработан как учебный проект для демонстрации навыков ООП и проектирования систем на Java.
