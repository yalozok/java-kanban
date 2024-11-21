import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final TaskType type = TaskType.EPIC;
    private List<Integer> subTaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(Integer id, String name, String description) {
        super(name, description);
        this.id = id;
    }

    public Epic(Integer id, String name, String description, List<Integer> subTaskIds) {
        super(name, description);
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
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", listSubTaskId=" + subTaskIds +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}
