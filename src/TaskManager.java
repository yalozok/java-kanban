import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class TaskManager {
    private static Integer countTask = 0;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();

    private static Integer getId() {
        return countTask;
    }

    private static void updateId() {
        countTask++;
    }

//TASK

    public void addTask(Task task) {
        task.setId(getId());
        task.setStatus(TaskStatus.NEW);
        tasks.put(getId(), task);
        updateId();
    }

    private boolean hasTask(Integer id) {
        return tasks.containsKey(id);
    }

    public Task getTaskById(Integer id) {
        return hasTask(id) ? tasks.get(id) : null;
    }

    public List<Task> getAllTasks() {
        if (tasks.isEmpty()) {
            return null;
        }
        return createTaskList(tasks);
    }

    private <T extends Task> List<T> createTaskList(Map<Integer, T> collection) {
        List<T> list = new ArrayList<>();
        for (Integer id : collection.keySet()) {
            list.add(collection.get(id));
        }
        return list;
    }

    public void updateTask(Task task) {
        if (hasTask(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void deleteTaskById(Integer id) {
        if (hasTask(id)) {
            tasks.remove(id);
        }
    }

    public void deleteAllTasks() {
        if (tasks.isEmpty()) {
            return;
        }
        tasks.clear();
    }

//    EPIC

    public void addEpic(Epic epic) {
        epic.setId(getId());
        epic.setStatus(TaskStatus.NEW);
        epics.put(getId(), epic);
        updateId();
    }

    private boolean hasEpic(Integer id) {
        return epics.containsKey(id);
    }

    private void setSubTaskIdToEpic(Integer subTaskId, Integer epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> subTaskIds = epic.getSubTaskIds();
        subTaskIds.add(subTaskId);
        epic.setSubTaskIds(subTaskIds);
    }

    private void deleteSubTaskIdFromEpic(Integer subTaskId, Integer epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> subTaskIds = epic.getSubTaskIds();
        subTaskIds.remove(subTaskId);
        epic.setSubTaskIds(subTaskIds);
    }

    private void updateEpicStatus(Integer id) {
        TaskStatus status;
        Epic epic = epics.get(id);
        List<Integer> listSubTaskId = epic.getSubTaskIds();

        if (listSubTaskId.isEmpty()) {
            status = TaskStatus.NEW;
        } else {
            status = getSubTaskById(listSubTaskId.getFirst()).getStatus();
            for (Integer subTaskId : listSubTaskId) {
                if (getSubTaskById(subTaskId).getStatus() != status) {
                    status = TaskStatus.IN_PROGRESS;
                }
            }
        }
        epic.setStatus(status);
    }

    public Epic getEpicById(Integer id) {
        return hasEpic(id) ? epics.get(id) : null;
    }

    public List<Epic> getAllEpics() {
        if (epics.isEmpty()) {
            return null;
        }
        return createTaskList(epics);
    }

    public void updateEpic(Epic epic) {
        if (hasEpic(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    public void deleteEpicById(Integer id) {
        if (!hasEpic(id)) {
            return;
        }
        List<Integer> listSubTaskId = getEpicById(id).getSubTaskIds();
        if (!listSubTaskId.isEmpty()) {
            for (Integer subTaskId : listSubTaskId) {
                subTasks.remove(subTaskId);
            }
        }
        epics.remove(id);
    }

    public void deleteAllEpics() {
        if (epics.isEmpty()) {
            return;
        }
        while (!epics.isEmpty()) {
            List<Integer> listId = new ArrayList<>(epics.keySet());
            deleteEpicById(listId.getFirst());
        }
    }

//    SUBTASK

    public void addSubTask(SubTask subTask, Integer epicId) {
        if (!hasEpic(epicId)) {
            return;
        }
        subTask.setId(getId());
        subTask.setStatus(TaskStatus.NEW);
        subTask.setEpicId(epicId);
        subTasks.put(getId(), subTask);
        setSubTaskIdToEpic(getId(), epicId);
        updateEpicStatus(epicId);
        updateId();
    }

    private boolean hasSubTask(Integer id) {
        return subTasks.containsKey(id);
    }

    public SubTask getSubTaskById(Integer id) {
        return hasSubTask(id) ? subTasks.get(id) : null;
    }

    public List<SubTask> getAllSubTasksByEpic(Integer epicId) {
        if(!hasEpic(epicId)) {
            return null;
        }
        List<Integer> ids = epics.get(epicId).getSubTaskIds();
        List<SubTask> list = new ArrayList<>();
        for (Integer id : ids) {
            list.add(subTasks.get(id));
        }
        return list;
    }

    public List<SubTask> getAllSubTasks() {
        if(subTasks.isEmpty()) {
            return null;
        }
        return createTaskList(subTasks);
    }

    public void updateSubTask(SubTask subTask) {
        if(!hasSubTask(subTask.getId())) {
            return;
        }
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(subTask.getEpicId());
    }

    public void deleteSubTaskById(Integer id) {
        if (!hasSubTask(id)) {
            return;
        }
        Integer epicId = subTasks.get(id).getEpicId();
        deleteSubTaskIdFromEpic(id, epicId);
        updateEpicStatus(epicId);
        subTasks.remove(id);
    }

    public void deleteAllSubTasks() {
        if (subTasks.isEmpty()) {
            return;
        }
        while (!subTasks.isEmpty()) {
            List<Integer> listId = new ArrayList<>(subTasks.keySet());
            deleteSubTaskById(listId.getFirst());
        }
    }
}
