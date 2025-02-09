import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import taskmanager.model.Epic;
import taskmanager.model.SubTask;
import taskmanager.model.Task;
import taskmanager.model.TaskStatus;
import taskmanager.service.Manager;
import taskmanager.service.TaskManager;

import java.util.List;

class InMemoryHistoryManagerTest extends InMemoryTaskManagerTest {
    TaskManager manager = new Manager().getDefault();

    @Test
    void getHistory() {
        Integer epicId = manager.addEpic(new Epic.Builder("Помыть посуду", "Загрузить машинку").build());
        Integer subtaskId = manager.addSubTask(new SubTask.Builder("Записаться на курсы", "Хорошие", epicId).build());
        Integer taskId = manager.addTask(new Task.Builder<>("Помыть посуду", "Загрузить машинку").build());

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
        Integer taskId1 = manager.addTask(new Task.Builder<>("Один", "Один").build());
        Integer taskId2 = manager.addTask(new Task.Builder<>("Два", "Два").build());
        Integer taskId3 = manager.addTask(new Task.Builder<>("Три", "Три").build());

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
        Integer taskId = manager.addTask(new Task.Builder<>("Помыть посуду", "Загрузить машинку").build());
        Task taskSaved = manager.getTaskById(taskId);

        manager.updateTask(new Task.Builder<>("Помыть посуду", "Загрузить машинку")
                .id(taskId).status((TaskStatus.DONE)).build());
        Task taskUpdated = manager.getTaskById(taskId);

        List<Task> history = manager.getHistory();
        Assertions.assertNotNull(history, "Задача не сохранились в истории");
        Assertions.assertEquals(1, history.size(), "Неверное количество задач");
        Assertions.assertNotEquals(history.getFirst(), taskSaved, "Сохранена неверная версия задачи");
        Assertions.assertEquals(history.getFirst(), taskUpdated, "Сохранена неверная версия задачи");
    }

    @Test
    void getHistory_RemoveEpicWithSubtasks_SubTasksRemovedFromHistory() {
        Integer epicId = manager.addEpic(new Epic.Builder("Выучить английский", "Разговорный").build());
        Integer subtaskId1 = manager.addSubTask(new SubTask.Builder("Записаться на курсы", "Хорошие", epicId).build());
        Integer subtaskId2 = manager.addSubTask(new SubTask.Builder("Найти репетитора", "С опытом", epicId).build());

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
        Integer taskId1 = manager.addTask(new Task.Builder<>("Один", "Один").build());
        Integer taskId2 = manager.addTask(new Task.Builder<>("Два", "Два").build());

        manager.getTaskById(taskId1);
        manager.getTaskById(taskId2);
        manager.getTaskById(taskId1);
        Task task2 = manager.getTaskById(taskId2);
        manager.deleteTaskById(taskId1);

        List<Task> history = manager.getHistory();
        Assertions.assertEquals(1, history.size(), "Неверное количество задач");
        Assertions.assertEquals(history.getFirst(), task2, "Сохранена неверная задача");
    }

    @Test
    void getHistory_WithCorrectRemoving_WhenDuplicateTaskAdded() {
        Task task1 = new Task.Builder<>("Один", "Один").build();
        Task task2 = new Task.Builder<>("Два", "Два").build();
        Integer taskId1 = manager.addTask(task1);
        Integer taskId2 = manager.addTask(task2);
        manager.getTaskById(taskId1);
        manager.getTaskById(taskId2);
        List<Task> history = manager.getHistory();
        Assertions.assertEquals(history.getFirst(), task1, "Сохранена неверная задача");
        Assertions.assertEquals(history.getLast(), task2, "Сохранена неверная задача");

        manager.getTaskById(taskId1);
        List<Task> historyAfterDuplicate = manager.getHistory();
        Assertions.assertEquals(task2, historyAfterDuplicate.getFirst(), "Сохранена неверная задача");
        Assertions.assertEquals(task1, historyAfterDuplicate.getLast(), "Сохранена неверная задача");
        Assertions.assertEquals(2, historyAfterDuplicate.size(), "Неверное количество задач");
    }
}
