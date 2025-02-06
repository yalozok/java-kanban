package taskmanager.model;

public class TaskOverlapException extends RuntimeException {
    private static final String MESSAGE = "Задача не сохранена в TaskManager, т.к. время ее выполнения перекрывает другую задачу.";
    public TaskOverlapException() {
        super(MESSAGE);
    }
}
