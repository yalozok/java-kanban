package taskmanager.model;

public enum Header {
    ID("getId"),
    NAME("getName"),
    DESCRIPTION("getDescription"),
    STATUS("getStatus"),
    TYPE("getType"),
    EPIC_ID("getEpicId"),
    SUBTASK_IDS("getFormattedSubTaskIds"),
    START_TIME("formatStartTime"),
    DURATION("formatDuration"),
    END_TIME("formatEndTime");

    public final String methodName;

    Header(String methodName) {
        this.methodName = methodName;
    }
}
