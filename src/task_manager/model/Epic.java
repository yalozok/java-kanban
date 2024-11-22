package task_manager.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskType.EPIC);
    }

    public Epic(Integer id, String name, String description) {
        super(name, description, TaskType.EPIC);
        this.id = id;
    }

    public Epic(Integer id, String name, String description, List<Integer> subTaskIds) {
        super(name, description, TaskType.EPIC);
        this.id = id;
        this.subTaskIds = subTaskIds;
    }

    public List<Integer> getSubTaskIds() {
        return this.subTaskIds;
    }

    public void setSubTaskIds(List<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    @Override
    public String toString() {
        return "task_manager.model.Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", listSubTaskId=" + subTaskIds +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}
