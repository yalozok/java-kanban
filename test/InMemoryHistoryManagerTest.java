import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import taskManager.model.Epic;
import taskManager.model.SubTask;
import taskManager.model.Task;
import taskManager.model.TaskStatus;
import taskManager.service.Manager;
import taskManager.service.TaskManager;

import java.util.List;

class InMemoryHistoryManagerTest {
    TaskManager manager = new Manager().getDefault();

    @Test
    void getHistory() {
        Integer epicId = manager.addEpic(new Epic("Помыть посуду", "Загрузить машинку"));
        Integer subtaskId = manager.addSubTask(new SubTask("Записаться на курсы", "Хорошие"), epicId);
        Integer taskId = manager.addTask(new Task("Помыть посуду", "Загрузить машинку"));

        Epic epicSaved = manager.getEpicById(epicId);
        SubTask subTaskSaved = manager.getSubTaskById(subtaskId);
        Task taskSaved = manager.getTaskById(taskId);

        List<Task> history = manager.getHistory();
        Assertions.assertNotNull(history, "Задачи не сохранились в истории");
        Assertions.assertEquals(3, history.size(), "Неверное количество задач");
        Assertions.assertEquals(epicSaved, history.get(0), "Epic в истории не совпадает");
        Assertions.assertEquals(subTaskSaved, history.get(1), "SubTask в истории не совпадает");
        Assertions.assertEquals(taskSaved, history.get(2), "Task в истории не совпадает");
    }

    @Test
    void getHistory_CorrectOrder() {
        Integer taskId1 = manager.addTask(new Task("Один", "Один"));
        Integer taskId2 = manager.addTask(new Task("Два", "Два"));
        Integer taskId3 = manager.addTask(new Task("Три", "Три"));

        Task task3 = manager.getTaskById(taskId3);
        manager.getTaskById(taskId2);
        manager.getTaskById(taskId1);
        manager.getTaskById(taskId2);
        manager.getTaskById(taskId1);
        Task task2 = manager.getTaskById(taskId2);

        List<Task> history = manager.getHistory();
        Assertions.assertEquals(3, history.size(), "Неверное количество задач");
        Assertions.assertEquals(task3, history.getFirst(), "Epic в истории не совпадает");
        Assertions.assertEquals(task2, history.getLast(), "Epic в истории не совпадает");
    }

    @Test
    void getHistoryAfterUpdate() {
        Integer taskId = manager.addTask(new Task("Помыть посуду", "Загрузить машинку"));
        Task taskSaved = manager.getTaskById(taskId);

        manager.updateTask(new Task(taskId, "Помыть посуду", "Загрузить машинку", TaskStatus.DONE));
        Task taskUpdated = manager.getTaskById(taskId);

        List<Task> history = manager.getHistory();
        Assertions.assertNotNull(history, "Задача не сохранились в истории");
        Assertions.assertEquals(1, history.size(), "Неверное количество задач");
        Assertions.assertNotEquals(history.getFirst(), taskSaved, "Сохранена неверная версия задачи");
        Assertions.assertEquals(history.getFirst(), taskUpdated, "Сохранена неверная версия задачи");
    }

    @Test
    void getHistory_RemoveEpicWithSubtasks_SubTasksRemovedFromHistory() {
        Integer epicId = manager.addEpic(new Epic("Выучить английский", "Разговорный"));
        Integer subtaskId1 = manager.addSubTask(new SubTask("Записаться на курсы", "Хорошие"), epicId);
        Integer subtaskId2 = manager.addSubTask(new SubTask("Найти репетитора", "С опытом"), epicId);

        manager.getEpicById(epicId);
        manager.getSubTaskById(subtaskId1);
        SubTask subTask2 = manager.getSubTaskById(subtaskId2);

        List<Task> history = manager.getHistory();
        Assertions.assertEquals(3, history.size(), "Неверное количество задач");
        Assertions.assertEquals(history.getLast(), subTask2, "Сохранена неверная задача");

        manager.deleteEpicById(epicId);
        List<Task> historyUpdated = manager.getHistory();
        Assertions.assertEquals(0, historyUpdated.size(), "Неверное количество задач");
    }

    @Test
    void getHistory_RemoveTask() {
        Integer taskId1 = manager.addTask(new Task("Один", "Один"));
        Integer taskId2 = manager.addTask(new Task("Два", "Два"));

        manager.getTaskById(taskId1);
        manager.getTaskById(taskId2);
        manager.getTaskById(taskId1);
        Task task2 = manager.getTaskById(taskId2);
        manager.deleteTaskById(taskId1);

        List<Task> history = manager.getHistory();
        Assertions.assertEquals(1, history.size(), "Неверное количество задач");
        Assertions.assertEquals(history.getFirst(), task2, "Сохранена неверная задача");
    }
}
