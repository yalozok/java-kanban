package taskmanager.service;

import taskmanager.model.Epic;
import taskmanager.model.SubTask;
import taskmanager.model.Task;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        File file = new File("task.csv");

        FileBackedTaskManager managerToFile = FileBackedTaskManager.loadFromFile(file);
        Task task = new Task("task1", "description task1");
        Epic epic = new Epic("epic1", "description epic1");
        SubTask subTask = new SubTask("subtask1", "description subtask1");
        SubTask subTask2 = new SubTask("subtask2", "description subtask2");

        Integer epicId = managerToFile.addEpic(epic);
        managerToFile.addSubTask(subTask, epicId);
        managerToFile.addTask(task);
        managerToFile.addSubTask(subTask2, epicId);

        FileBackedTaskManager managerToFile2 = FileBackedTaskManager.loadFromFile(file);
        System.out.println(managerToFile2.getAllTasks());
        System.out.println(managerToFile2.getAllEpics());
        System.out.println(managerToFile2.getAllSubTasks());
    }
}
