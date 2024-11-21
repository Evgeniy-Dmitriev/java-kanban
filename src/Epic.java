import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList <Integer> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        subTasks = new ArrayList<>();
    }

    public Epic(String name, String description, Status status, int id, ArrayList<Integer> subTask) {
        super(name, description, status, id);
        this.subTasks = subTask;
    }

    public ArrayList<Integer> getSubtasks() {
        return subTasks;
    }

    public void setSubtasks(ArrayList<Integer> subTasks) {
        this.subTasks = subTasks;
    }
}
