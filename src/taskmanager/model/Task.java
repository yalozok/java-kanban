package taskmanager.model;

import java.util.Objects;

public class Task {
    protected Integer id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected final TaskType type;

    protected Task(Builder<?> builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.status = builder.status;
        this.type = builder.type;
    }

    public static class Builder<T extends Builder<T>> {
        private Integer id;
        private String name;
        private String description;
        private TaskStatus status;
        protected TaskType type = TaskType.REGULAR;

        public Builder(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public T id(Integer id) {
            this.id = id;
            return self();
        }

        public T status(TaskStatus status) {
            this.status = status;
            return self();
        }

        protected T self() {
            return (T) this;
        }

        public Task build() {
            return new Task(this);
        }
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
