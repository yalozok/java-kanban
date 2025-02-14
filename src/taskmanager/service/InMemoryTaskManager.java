package taskmanager.service;

import taskmanager.model.*;

import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    private static Integer countTask = 0;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();
    private TreeSet<Task> prioritizedTasks = new TreeSet<>();
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
        return storeTask(task);
    }

    private Integer storeTask(Task task) {
        Optional<LocalDateTime> startTimeOpt = task.getStartTime();
        Integer id = task.getId();

        if (startTimeOpt.isPresent() && validateSchedule(task)) {
            prioritizedTasks.add(task);
            tasks.put(id, task);
            updateId();
            return id;
        } else if (startTimeOpt.isEmpty()) {
            tasks.put(id, task);
            updateId();
            return id;
        } else {
            throw new TaskOverlapException();
        }
    }

    protected void addTaskFromFile(Task task) {
        storeTask(task);
    }

    public boolean hasTask(Integer id) {
        return tasks.containsKey(id);
    }

    @Override
    public Task getTaskById(Integer id) {
        if (!hasTask(id)) {
            throw new NoSuchElementException("Task with ID " + id + " not found");
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
            storeTask(task);
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        if (hasTask(id)) {
            Task task = tasks.get(id);
            prioritizedTasks.remove(task);
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            throw new NoSuchElementException("Task with ID " + id + " not found");
        }
    }

    @Override
    public void deleteAllTasks() {
        if (tasks.isEmpty()) {
            return;
        }
        List<Integer> taskIds = new ArrayList<>(tasks.keySet());
        for (Integer id : taskIds) {
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

    public boolean hasEpic(Integer id) {
        return epics.containsKey(id);
    }

    @Override
    public Epic getEpicById(Integer id) {
        if (!hasEpic(id)) {
            throw new NoSuchElementException("Epic with ID " + id + " not found");
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
        Epic epic = epics.get(epicId);
        epic.setSubTask(subTask);
        updateId();
        return storeSubTask(subTask);
    }

    private Integer storeSubTask(SubTask subTask) {
        Optional<LocalDateTime> startTimeOpt = subTask.getStartTime();
        Integer id = subTask.getId();

        if (startTimeOpt.isPresent() && validateSchedule(subTask)) {
            prioritizedTasks.add(subTask);
            subTasks.put(id, subTask);
            updateId();
            return id;
        } else if (startTimeOpt.isEmpty()) {
            subTasks.put(id, subTask);
            updateId();
            return id;
        } else {
            throw new TaskOverlapException();
        }
    }

    protected void addSubTaskFromFile(SubTask subTask) {
        storeSubTask(subTask);
    }

    public boolean hasSubTask(Integer id) {
        return subTasks.containsKey(id);
    }

    public SubTask getSubTaskById(Integer id) {
        if (!hasSubTask(id)) {
            throw new NoSuchElementException("SubTask with ID " + id + " not found");
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
        System.out.println("SubTask in Update: " + subTask);

        if (!hasSubTask(subTask.getId()) || !hasEpic(subTask.getEpicId())) {
            return;
        }
        Epic epic = epics.get(subTask.getEpicId());
        epic.updateSubTask(subTask);
        storeSubTask(subTask);
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        if (!hasSubTask(id)) {
            return;
        }
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicId());
        epic.removeSubTask(id);
        prioritizedTasks.remove(subTask);
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean validateSchedule(Task newTask) {
        return prioritizedTasks.stream()
                .map(task -> task.getStartTime().flatMap(start ->
                        task.getEndTime().map(end -> new TimeSlot(start, end))))
                .filter(Optional::isPresent)
                .noneMatch(timeSlot -> timeSlot.get().overlapsWith(newTask));
    }

    private record TimeSlot(LocalDateTime start, LocalDateTime end) {

        boolean overlapsWith(Task task) {
            Optional<LocalDateTime> taskStart = task.getStartTime();
            Optional<LocalDateTime> taskEnd = task.getEndTime();

            if (taskStart.isPresent() && taskEnd.isPresent()) {
                LocalDateTime newStart = taskStart.get();
                LocalDateTime newEnd = taskEnd.get();
                return (start.isBefore(newEnd) && end.isAfter(newStart));
            }
            return false;
        }
    }

}
