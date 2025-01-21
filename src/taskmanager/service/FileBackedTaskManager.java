package taskmanager.service;

import taskmanager.model.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

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
                fileBackedManager.addTaskFromString(taskString, headers);
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
            Set<String> headers = createFileHeader(tasks);
            fileWriter.write(String.join(",", headers) + "\n");
            for (Task task : tasks) {
                fileWriter.write(taskToString(task, headers));
                fileWriter.write("\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Problem while writeTasksToFile", e);
        }
    }

    private Set<String> createFileHeader(List<Task> tasks) {
        Set<String> headers = new LinkedHashSet<>();
        for (Task task : tasks) {
            Class<?> className = task.getClass();
            while (className != null) {
                Set<String> partFields = new LinkedHashSet<>();
                for (Field field : className.getDeclaredFields()) {
                    partFields.add(field.getName());
                }
                partFields.addAll(headers);
                headers = partFields;
                className = className.getSuperclass();
            }
        }
        return headers;
    }

    private String taskToString(Task task, Set<String> headers) {
        StringBuilder taskBuilder = new StringBuilder();
        Class<?> taskClass = task.getClass();

        for (String header : headers) {
            String methodName = "get" + header.substring(0, 1).toUpperCase() + header.substring(1);
            if (methodName.equals("getSubTaskIds")) {
                methodName = "getFormattedSubTaskIds";
            }
            if (hasMethod(taskClass, methodName)) {
                try {
                    Method getter = taskClass.getMethod(methodName);
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

    private void addTaskFromString(String taskString, String headersString) {
        Map<String, String> taskMap = taskStringToMap(taskString, headersString);
        Integer id = Integer.parseInt(taskMap.get("id"));
        String name = taskMap.get("name");
        String description = taskMap.get("description");
        TaskStatus status = TaskStatus.valueOf(taskMap.get("status"));


        switch (taskMap.get("type")) {
            case "EPIC":
                List<Integer> subTaskIds = stringToArray(taskMap.get("subTaskIds"));
                Epic epic = new Epic(id, name, description, subTaskIds);
                epic.setStatus(status);
                super.addEpicFromFile(id, epic);
                break;
            case "SUB_TASK":
                Integer epicId = Integer.parseInt(taskMap.get("epicId"));
                SubTask subTask = new SubTask(id, name, description, status, epicId);
                super.addSubTaskFromFile(id, subTask);
                break;
            default:
                Task task = new Task(id, name, description, status);
                super.addTaskFromFile(id, task);
        }
    }

    private Map<String, String> taskStringToMap(String taskString, String headersString) {
        Map<String, String> taskMap = new HashMap<>();
        String[] taskFields = taskString.split(",", -1);
        String[] headers = headersString.split(",", -1);

        for (int i = 0; i < headers.length; i++) {
            taskMap.put(headers[i], taskFields[i]);
        }
        return taskMap;
    }

    private List<Integer> stringToArray(String arrayString) {
        List<Integer> arr = new ArrayList<>();
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
