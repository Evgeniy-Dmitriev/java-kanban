package task;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, null, null);
        subTasks = new ArrayList<>();
    }

    public Epic(String name, String description, int id, ArrayList<Integer> subTasks) {
        super(name, description, null, null);
        this.setId(id);
        this.subTasks = subTasks;
    }

    public ArrayList<Integer> getSubtasks() {
        return subTasks;
    }

    public void setSubtasks(ArrayList<Integer> subTasks) {
        this.subTasks = subTasks;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
