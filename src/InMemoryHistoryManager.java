import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        this.history = new HashMap<>();
    }

    // Добавление задачи в список истории
    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        linkLast(task);
        history.put(task.getId(), tail);
    }

    // Удаление задачи из списка истории
    @Override
    public void remove(int id) {
        Node node = history.get(id);
        if (node == null) {
            return;
        }
        removeNode(node);
        history.remove(id);
    }

    // Получение списка истории
    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    // Добавление задачи в конец списка
    private void linkLast(Task task) {
        if (task == null) {
            return;
        }
        if (tail == null) {
            head = new Node(null, task, null);
            tail = head;
        } else {
            tail.next = new Node(tail, task, null);
            tail = tail.next;
        }
    }

    // Получение списка задач истории
    private List<Task> getTasks() {
        List<Task> historyList = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null) {
            historyList.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return historyList;
    }

    // Удаление ноды
    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        if (node == head && node == tail) {
            head = null;
            tail = null;
        }
        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }
        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }
    }

    // Класс узлов связного списка
    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Node prev, Task task, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }
}
