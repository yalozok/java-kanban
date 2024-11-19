public class SubTask extends Task {
    private final TaskType type = TaskType.SUB_TASK;
    private Integer epicId;

    public SubTask (String name, String description) {
        super(name, description);
    }

    public SubTask (Integer id, String name, String description, TaskStatus status, Integer epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", epicId=" + epicId +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}
