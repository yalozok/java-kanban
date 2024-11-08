import java.util.HashMap;
import java.util.Map;

public class TaskCollection {
    private Map<Integer, Task> tasksList = new HashMap<>();

    public void addTask(Integer id, Task task) {
        tasksList.put(id, task);
    }

    public Task getTaskById (Integer id) {
        if(!tasksList.containsKey(id)) {
            return null;
        }
        return tasksList.get(id);
    }

    public void updateTask(Integer id, Task task) {
        tasksList.put(id, task);
    }

    public void deleteTaskById(Integer id) {
        tasksList.remove(id);
    }

    public void deleteAllTasks() {
        tasksList.clear();
    }
}
