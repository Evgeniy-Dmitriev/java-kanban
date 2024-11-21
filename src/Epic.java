import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList <Integer> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        subTasks = new ArrayList<>();
    }

    public Epic(String name, String description, int id, ArrayList<Integer> subTasks) {
        super(name, description);
        this.setId(id);
        this.subTasks = subTasks;
    }

    public ArrayList<Integer> getSubtasks() {
        return subTasks;
    }

    public void setSubtasks(ArrayList<Integer> subTasks) {
        this.subTasks = subTasks;
    }
}
