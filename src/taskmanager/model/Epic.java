package taskmanager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Epic extends Task {
    private List<SubTask> subTasks;

    private Epic(Builder builder) {
        super(builder);
        this.subTasks = builder.subTasks;
    }

    public static class Builder extends Task.Builder<Builder> {
        private List<SubTask> subTasks = new ArrayList<>();

        public Builder(String name, String description) {
            super(name, description);
            this.type = TaskType.EPIC;
        }

        public Builder subTasks(List<SubTask> subTaskIds) {
            this.subTasks = subTaskIds;
            return self();
        }

        @Override
        public Builder schedule(LocalDateTime startTime, Duration duration) {
            throw new UnsupportedOperationException("Epic's schedule is derived from its subtasks");
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Epic build() {
            return new Epic(this);
        }
    }

    public List<SubTask> getSubTasks() {
        return this.subTasks;
    }

    public void setSubTask(SubTask subTask) {
        subTasks.add(subTask);
        updateStatus();
    }

    public void removeSubTask(Integer id) {
        subTasks = subTasks.stream()
                .filter(subTask -> !subTask.getId().equals(id))
                .collect(Collectors.toList());
        updateStatus();
    }

    public void updateSubTask(SubTask updatedSubTask) {
        for (int i = 0; i < subTasks.size(); i++) {
            if (subTasks.get(i).getId().equals(updatedSubTask.getId())) {
                subTasks.set(i, updatedSubTask);
                break;
            }
        }
        updateStatus();
    }

    public void updateStatus() {
        if (subTasks.isEmpty()) {
            this.setStatus(TaskStatus.NEW);
        }

        Set<TaskStatus> uniqueStatuses = subTasks.stream()
                .map(SubTask::getStatus)
                .collect(Collectors.toSet());

        this.setStatus(uniqueStatuses.size() == 1 ? uniqueStatuses.iterator().next() : TaskStatus.IN_PROGRESS);
    }

    @Override
    public Optional<LocalDateTime> getStartTime() {
        return subTasks.stream()
                .map(SubTask::getStartTime)
                .flatMap(Optional::stream)
                .min(LocalDateTime::compareTo);
    }

    @Override
    public Optional<Duration> getDuration() {
        return subTasks.stream()
                .map(SubTask::getDuration)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(Duration::plus);
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        return subTasks.stream()
                .map(SubTask::getEndTime)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(LocalDateTime::compareTo);
    }

    @Override
    public void setSchedule(LocalDateTime startTime, Duration duration) {
        throw new UnsupportedOperationException("Epic's schedule is calculated from its subtasks");
    }

    public String getFormattedSubTaskIds() {
        return subTasks.stream()
                .map(SubTask::getId)
                .map(String::valueOf)
                .collect(Collectors.joining("&"));
    }

    @Override
    public String toString() {
        return super.toString() +
                ", listSubTasks=" + subTasks;
    }
}
