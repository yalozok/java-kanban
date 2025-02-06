package taskmanager.model;

public class SubTask extends Task {
    private Integer epicId;

    private SubTask(Builder builder) {
        super(builder);
        this.epicId = builder.epicId;
    }

    public static class Builder extends Task.Builder<Builder> {
        private Integer epicId;

        public Builder(String name, String description, Integer epicId) {
            super(name, description);
            this.epicId = epicId;
            this.type = TaskType.SUB_TASK;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public SubTask build() {
            return new SubTask(this);
        }
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", epicId=" + epicId;
    }
}
