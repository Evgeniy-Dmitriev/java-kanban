import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Integer> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        status = Status.NEW;
        subTasks = new ArrayList<>();
    }

    public Epic(String name, String description, Status status, int id, ArrayList<Integer> subTask) {
        super(name, description, status, id);
        this.subTasks = subTask;
    }
}
