package task_manager.model;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description) {
        super(name, description, TaskType.SUB_TASK);
    }

    public SubTask(Integer id, String name, String description, TaskStatus status, Integer epicId) {
        super(name, description, TaskType.SUB_TASK);
        this.id = id;
        this.status = status;
        this.epicId = epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "task_manager.model.SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", epicId=" + epicId +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}
