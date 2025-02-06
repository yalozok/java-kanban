package taskmanager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class Task implements Comparable<Task> {
    protected Integer id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected final TaskType type;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    protected Task(Builder<?> builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.status = builder.status;
        this.type = builder.type;
        this.duration = builder.duration;
        this.startTime = builder.startTime;
    }

    public static class Builder<T extends Builder<T>> {
        private Integer id;
        private String name;
        private String description;
        private TaskStatus status;
        protected TaskType type = TaskType.REGULAR;
        private Duration duration;
        private LocalDateTime startTime;

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

        public T schedule(LocalDateTime startTime, Duration duration) {
            this.startTime = startTime;
            this.duration = duration;
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

    public static DateTimeFormatter getFormatter() {
        return formatter;
    }

    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public void setSchedule(LocalDateTime startTime, Duration duration) {
        this.startTime = startTime;
        this.duration = duration;
    }

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    public Optional<LocalDateTime> getEndTime() {
        if (startTime != null && duration != null) {
            return Optional.of(startTime.plus(duration));
        }
        return Optional.empty();
    }

    public String formatStartTime() {
        Optional<LocalDateTime> startTimeOptional = getStartTime();
        if (startTimeOptional.isPresent()) {
            LocalDateTime startTimeFormat = startTimeOptional.get();
            return startTimeFormat.format(formatter);
        }
        return "";
    }

    public String formatEndTime() {
        Optional<LocalDateTime> endTimeOptional = getEndTime();
        if (endTimeOptional.isPresent()) {
            LocalDateTime endTimeFormat = endTimeOptional.get();
            return endTimeFormat.format(formatter);
        }
        return "";
    }

    public Long formatDuration() {
        Optional<Duration> durationOptional = getDuration();
        return durationOptional.map(Duration::toMinutes).orElse(0L);
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", startTime=" + formatStartTime() +
                ", duration=" + formatDuration() +
                ", endTime=" + formatEndTime();
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

    @Override
    public int compareTo(Task otherTask) {
        if (this.getStartTime().isEmpty() && otherTask.getStartTime().isEmpty()) {
            return Integer.compare(this.getId(), otherTask.getId());
        } else if (this.getStartTime().isEmpty()) {
            return 1;
        } else if (otherTask.getStartTime().isEmpty()) {
            return -1;
        }
        return Comparator.comparing( (Task task) -> task.getStartTime().get(), Comparator.naturalOrder())
                .thenComparing(Task::getId)
                .compare(this, otherTask);
    }
}
