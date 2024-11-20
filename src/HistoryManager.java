import java.util.List;

public interface HistoryManager {
    public void add(Task task);

    List<Task> getHistory();
}
