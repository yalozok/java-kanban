package taskmanager.model;

public enum Header {
    ID("getId"),
    NAME("getName"),
    DESCRIPTION("getDescription"),
    STATUS("getStatus"),
    TYPE("getType"),
    EPIC_ID("getEpicId"),
    SUBTASK_IDS("getFormattedSubTaskIds");

    public final String methodName;

    Header(String methodName) {
        this.methodName = methodName;
    }
}
