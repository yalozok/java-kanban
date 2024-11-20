import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    Task getTaskById(Integer id);

    List<Task> getAllTasks();

    void updateTask(Task task);

    void deleteTaskById(Integer id);

    void deleteAllTasks();

    void addEpic(Epic epic);

    Epic getEpicById(Integer id);

    List<Epic> getAllEpics();

    void updateEpic(Epic epic);

    void deleteEpicById(Integer id);

    void deleteAllEpics();

    void addSubTask(SubTask subTask, Integer epicId);

    SubTask getSubTaskById(Integer id);

    List<SubTask> getAllSubTasksByEpic(Integer epicId);

    List<SubTask> getAllSubTasks();

    void updateSubTask(SubTask subTask);

    void deleteSubTaskById(Integer id);

    void deleteAllSubTasks();

}

