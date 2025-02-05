package taskmanager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Epic extends Task {
    private List<Integer> subTaskIds;

    private Epic(Builder builder) {
        super(builder);
        this.subTaskIds = builder.subTaskIds;
    }

    public static class Builder extends Task.Builder<Builder> {
        private List<Integer> subTaskIds = new ArrayList<>();

        public Builder(String name, String description) {
            super(name, description);
            this.type = TaskType.EPIC;
        }

        public Builder subTaskIds(List<Integer> subTaskIds) {
            this.subTaskIds = subTaskIds;
            return self();
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

    public List<Integer> getSubTaskIds() {
        return this.subTaskIds;
    }

    public String getFormattedSubTaskIds() {
        return subTaskIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining("&"));
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
