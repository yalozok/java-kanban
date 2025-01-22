package taskmanager.service;

import taskmanager.model.*;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
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
        Map<Header, String> taskMap = new EnumMap<>(Header.class);
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

    public static void main(String[] args) {
        Task task1 = new Task("task1", "description task1");
        Task task2 = new Task("task2", "description task2");
        Epic epic1 = new Epic("epic1", "description epic1");
        Epic epic2 = new Epic("epic2", "description epic2");
        SubTask subTask1 = new SubTask("subtask1", "description subtask1");
        SubTask subTask2 = new SubTask("subtask2", "description subtask2");
        SubTask subTask3 = new SubTask("subtask3", "description subtask3");

        FileBackedTaskManager manager;
        Path tempFile;
        try {
            tempFile = Files.createTempFile("tempTask", ".csv");
            manager = loadFromFile(tempFile.toFile());
        } catch (Exception e) {
            throw new ManagerSaveException("Problem while main", e);
        }

        manager.addTask(task1);
        manager.addTask(task2);
        Integer epicIdA = manager.addEpic(epic1);
        Integer epicIdB = manager.addEpic(epic2);
        manager.addSubTask(subTask1, epicIdA);
        manager.addSubTask(subTask2, epicIdA);
        manager.addSubTask(subTask3, epicIdB);

        FileBackedTaskManager manager2 = loadFromFile(tempFile.toFile());
        System.out.println(manager2.getAllTasks());
        System.out.println(manager2.getAllEpics());
        System.out.println(manager2.getAllSubTasks());
    }
}
