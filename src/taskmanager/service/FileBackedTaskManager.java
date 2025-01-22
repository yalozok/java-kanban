package taskmanager.service;

import taskmanager.model.*;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    private FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedManager = new FileBackedTaskManager(file);

        try (BufferedReader buffer = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
            String headers = buffer.readLine();
            while (buffer.ready()) {
                String taskString = buffer.readLine();
                fileBackedManager.addTaskFromString(taskString);
            }

            return fileBackedManager;
        } catch (Exception e) {
            throw new ManagerSaveException("Problem while loadFromFile", e);
        }
    }

    public void save() {
        List<Task> allTasks = new ArrayList<>();

        allTasks.addAll(super.getAllTasks());
        allTasks.addAll(super.getAllEpics());
        allTasks.addAll(super.getAllSubTasks());

        writeTasksToFile(allTasks);
    }

    private void writeTasksToFile(List<Task> tasks) throws ManagerSaveException {
        try (Writer fileWriter = new FileWriter(file.getAbsolutePath())) {

            String headers = Arrays.stream(Header.values())
                    .map(header -> header.name().toLowerCase())
                    .collect(Collectors.joining(","));
            fileWriter.write(headers + "\n");

            for (Task task : tasks) {
                fileWriter.write(taskToString(task));
                fileWriter.write("\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Problem while writeTasksToFile", e);
        }
    }

    private String taskToString(Task task) {
        StringBuilder taskBuilder = new StringBuilder();
        Class<?> taskClass = task.getClass();

        for (Header header : Header.values()) {
            if (hasMethod(taskClass, header.methodName)) {
                try {
                    Method getter = taskClass.getMethod(header.methodName);
                    taskBuilder.append(getter.invoke(task));
                } catch (Exception e) {
                    String detail = "Problem while taskToString with" + task;
                    throw new ManagerSaveException(detail, e);
                }
            }
            taskBuilder.append(",");
        }
        String taskToString = taskBuilder.toString();
        return taskToString.substring(0, taskToString.length() - 1);
    }

    private void addTaskFromString(String taskString) {
        Map<Header, String> taskMap = taskStringToMap(taskString);
        Integer id = Integer.parseInt(taskMap.get(Header.ID));
        String name = taskMap.get(Header.NAME);
        String description = taskMap.get(Header.DESCRIPTION);
        TaskStatus status = TaskStatus.valueOf(taskMap.get(Header.STATUS));

        switch (taskMap.get(Header.TYPE)) {
            case "EPIC":
                List<Integer> subTaskIds = stringToArray(taskMap.get(Header.SUBTASK_IDS));
                Epic epic = new Epic(id, name, description, subTaskIds);
                epic.setStatus(status);
                super.addEpicFromFile(id, epic);
                break;
            case "SUB_TASK":
                Integer epicId = Integer.parseInt(taskMap.get(Header.EPIC_ID));
                SubTask subTask = new SubTask(id, name, description, status, epicId);
                super.addSubTaskFromFile(id, subTask);
                break;
            default:
                Task task = new Task(id, name, description, status);
                super.addTaskFromFile(id, task);
        }
    }

    private Map<Header, String> taskStringToMap(String taskString) {
        Map<Header, String> taskMap = new HashMap<>();
        String[] taskFields = taskString.split(",", -1);

        for (int i = 0; i < Header.values().length; i++) {
            taskMap.put(Header.values()[i], taskFields[i]);
        }
        return taskMap;
    }

    private List<Integer> stringToArray(String arrayString) {
        List<Integer> arr = new ArrayList<>();
        if (arrayString.isBlank()) {
            return arr;
        }
        String[] stringIntegers = arrayString.split("&");

        for (String s : stringIntegers) {
            arr.add(Integer.parseInt(s.trim()));
        }
        return arr;
    }

    private boolean hasMethod(Class<?> taskClass, String methodName) {
        try {
            taskClass.getMethod(methodName);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @Override
    public Integer addTask(Task task) {
        Integer id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Integer addEpic(Epic epic) {
        Integer id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Integer addSubTask(SubTask subtask, Integer epicId) {
        Integer id = super.addSubTask(subtask, epicId);
        save();
        return id;
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }
}
