package taskmanager.model;

import java.util.Objects;

public class Task {
    protected Integer id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected final TaskType type;

    protected Task(String name, String description, TaskType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public Task(String name, String description) {
        this(name, description, TaskType.REGULAR);
    }

    public Task(Integer id, String name, String description, TaskStatus status) {
        this(name, description, TaskType.REGULAR);
        this.id = id;
        this.status = status;
    }

    public TaskType getType() {
        return type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "task_manager.model.Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status && type == task.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, type);
    }
}
