public class Task {
    protected final Integer id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    private final TaskType type = TaskType.REGULAR;

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", type=" + type +
                '}';
    }

    public Task(Integer id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }
    public TaskType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void updateTask (Task task) {
        this.name = task.name;
        this.description = task.description;
        this.status = task.status;
    }


}
