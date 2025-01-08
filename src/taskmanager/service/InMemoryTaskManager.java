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
            status = subTasks.get(listSubTaskId.getFirst()).getStatus();
            for (Integer subTaskId : listSubTaskId) {
                if (subTasks.get(subTaskId).getStatus() != status) {
                    status = TaskStatus.IN_PROGRESS;
                }
            }
        }
        epic.setStatus(status);
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
        List<Integer> listSubTaskId = epics.get(id).getSubTaskIds();
        if (!listSubTaskId.isEmpty()) {
            for (Integer subTaskId : listSubTaskId) {
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
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
    public Integer addSubTask(SubTask subTask, Integer epicId) {
        if (!hasEpic(epicId)) {
            return null;
        }
        Integer id = getId();
        subTask.setId(id);
        subTask.setStatus(TaskStatus.NEW);
        subTask.setEpicId(epicId);
        subTasks.put(id, subTask);
        setSubTaskIdToEpic(id, epicId);
        updateEpicStatus(epicId);
        updateId();
        return id;
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
    public List<SubTask> getAllSubTasksByEpic(Integer epicId) {
        if (!hasEpic(epicId)) {
            return new ArrayList<>();
        }
        List<Integer> ids = epics.get(epicId).getSubTaskIds();
        List<SubTask> list = new ArrayList<>();
        for (Integer id : ids) {
            list.add(subTasks.get(id));
        }
        return list;
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
        updateEpicStatus(subTask.getEpicId());
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        if (!hasSubTask(id)) {
            return;
        }
        Integer epicId = subTasks.get(id).getEpicId();
        deleteSubTaskIdFromEpic(id, epicId);
        updateEpicStatus(epicId);
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
