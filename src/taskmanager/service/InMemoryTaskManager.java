package taskmanager.service;

import taskmanager.model.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class InMemoryTaskManager implements TaskManager {
    private static Integer countTask = 0;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();
    private HistoryManager historyManager = new Manager().getDefaultHistory();

    private static Integer getId() {
        return countTask;
    }

    private static void updateId() {
        countTask++;
    }

    //TASK
    @Override
    public Integer addTask(Task task) {
        Integer id = getId();
        task.setId(id);
        task.setStatus(TaskStatus.NEW);
        tasks.put(id, task);
        updateId();
        return id;
    }

    protected void addTaskFromFile(Integer id, Task task) {
        tasks.put(id, task);
    }

    private boolean hasTask(Integer id) {
        return tasks.containsKey(id);
    }

    @Override
    public Task getTaskById(Integer id) {
        if (!hasTask(id)) {
            return null;
        }
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public List<Task> getAllTasks() {
        if (tasks.isEmpty()) {
            return new ArrayList<>();
        }
        return createTaskList(tasks);
    }

    private <T extends Task> List<T> createTaskList(Map<Integer, T> collection) {
        return new ArrayList<>(collection.values());
    }

    @Override
    public void updateTask(Task task) {
        if (hasTask(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        if (hasTask(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllTasks() {
        if (tasks.isEmpty()) {
            return;
        }
        for (Integer id : tasks.keySet()) {
            deleteTaskById(id);
        }
    }

    //    EPIC
    @Override
    public Integer addEpic(Epic epic) {
        Integer id = getId();
        epic.setId(id);
        epic.setStatus(TaskStatus.NEW);
        epics.put(id, epic);
        updateId();
        return id;
    }

    protected void addEpicFromFile(Integer id, Epic epic) {
        epics.put(id, epic);
    }

    private boolean hasEpic(Integer id) {
        return epics.containsKey(id);
    }

    @Override
    public Epic getEpicById(Integer id) {
        if (!hasEpic(id)) {
            return null;
        }
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public List<Epic> getAllEpics() {
        if (epics.isEmpty()) {
            return new ArrayList<>();
        }
        return createTaskList(epics);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (hasEpic(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void deleteEpicById(Integer id) {
        if (!hasEpic(id)) {
            return;
        }
        List<SubTask> listSubTasks = epics.get(id).getSubTasks();
        if (!listSubTasks.isEmpty()) {
            for (SubTask subTask : listSubTasks) {
                subTasks.remove(subTask.getId());
                historyManager.remove(subTask.getId());
            }
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
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
    @Override
    public Integer addSubTask(SubTask subTask) {
        Integer id = getId();
        Integer epicId = subTask.getEpicId();
        subTask.setId(id);
        subTask.setStatus(TaskStatus.NEW);
        subTasks.put(id, subTask);
        Epic epic = epics.get(epicId);
        epic.setSubTask(subTask);
        updateId();
        return id;
    }

    protected void addSubTaskFromFile(Integer id, SubTask subTask) {
        subTasks.put(id, subTask);
    }

    private boolean hasSubTask(Integer id) {
        return subTasks.containsKey(id);
    }

    public SubTask getSubTaskById(Integer id) {
        if (!hasSubTask(id)) {
            return null;
        }
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        if (subTasks.isEmpty()) {
            return new ArrayList<>();
        }
        return createTaskList(subTasks);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (!hasSubTask(subTask.getId()) || !hasEpic(subTask.getEpicId())) {
            return;
        }
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.updateSubTask(subTask);
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        if (!hasSubTask(id)) {
            return;
        }
        Epic epic = epics.get(subTasks.get(id).getEpicId());
        epic.removeSubTask(id);
        historyManager.remove(id);
        subTasks.remove(id);
    }

    @Override
    public void deleteAllSubTasks() {
        if (subTasks.isEmpty()) {
            return;
        }
        while (!subTasks.isEmpty()) {
            List<Integer> listId = new ArrayList<>(subTasks.keySet());
            deleteSubTaskById(listId.getFirst());
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
