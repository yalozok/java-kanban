import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final TaskType type = TaskType.EPIC;
    private List<Integer> listSubTaskId = new ArrayList<>();

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", listSubTaskId=" + listSubTaskId +
                ", status=" + status +
                ", type=" + type +
                '}';
    }

    public Epic (Integer id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }

    public SubTask createNewSubTask(Integer id, String name, String description) {
        SubTask subTask = new SubTask(id, name, description, TaskStatus.NEW);
        listSubTaskId.add(id);
        return subTask;
    }

    public void deleteSubTaskIdFromEpic(Integer id) {
        listSubTaskId.remove(id);
    }

    public List<Integer> getListSubTaskId() {
        return this.listSubTaskId;
    }
}
