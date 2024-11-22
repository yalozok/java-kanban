package task_manager.service;

import task_manager.model.Task;

import java.util.List;

public interface HistoryManager {
    public void add(Task task);

    List<Task> getHistory();
}
