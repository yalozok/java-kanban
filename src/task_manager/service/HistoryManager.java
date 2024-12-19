package task_manager.service;

import task_manager.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);

    List<Task> getHistory();
}
