package taskmanager.model;

public class ManagerSaveException extends RuntimeException {
    private static final String MESSAGE = "Custom ManagerSaveException: ";

    public ManagerSaveException(String detail, Throwable cause) {
        super(formatMessage(detail), cause);
    }

    private static String formatMessage(String detail) {
        return MESSAGE + detail;
    }
}
