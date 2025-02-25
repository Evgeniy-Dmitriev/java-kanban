package exeption;

public class TaskIntersectionException extends RuntimeException {
    public TaskIntersectionException() {
    }

    public TaskIntersectionException(String message) {
        super(message);
    }
}
