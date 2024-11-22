import task_manager.model.*;
import task_manager.service.*;
import task_manager.model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager manager = new Manager().getDefault();

    @Test
    void addTask() {
        Task task = new Task("Помыть посуду", "Загрузить машинку");
        Integer id = manager.addTask(task);

        Task taskSaved = manager.getTaskById(id);

        Assertions.assertNotNull(taskSaved, "Задача не добавлена");
        Assertions.assertEquals(task, taskSaved, "Задачи не совпадают");

        List<Task> tasks = manager.getAllTasks();
        Assertions.assertNotNull(tasks, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач");
        Assertions.assertEquals(task, tasks.getFirst(), "Задачи не совпадают");
    }

    @Test
    void updateTask() {
        Task task = new Task("Помыть посуду", "Загрузить машинку");
        Integer id = manager.addTask(task);

        manager.updateTask(new Task(id, "Помыть посуду", "Загрузить машинку", TaskStatus.DONE));
        Task taskUpdated = manager.getTaskById(id);
        Assertions.assertEquals(1, manager.getAllTasks().size(), "Неверное количество задач");
        Assertions.assertNotEquals(task, taskUpdated, "Задача не обновилась");
    }

    @Test
    void deleteTask() {
        Integer id1 = manager.addTask(new Task("Помыть посуду", "Загрузить машинку"));
        Integer id2 = manager.addTask(new Task("Помыть собаку", "С шампунем"));

        manager.deleteTaskById(id1);
        List<Task> tasks = manager.getAllTasks();
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач");
        Assertions.assertNull(manager.getTaskById(id1), "Задача не удалена");

        manager.deleteAllTasks();
        List<Task> tasksEmpty = manager.getAllTasks();
        Assertions.assertEquals(0, tasksEmpty.size(), "Неверное количество задач");
    }

    @Test
    void addEpic() {
        Epic epic = new Epic("Выучить язык", "Английский");

        Integer id = manager.addEpic(epic);
        Epic epicSaved = manager.getEpicById(id);
        Assertions.assertNotNull(epicSaved, "task_manager.model.Epic не добавлен");
        Assertions.assertEquals(epic, epicSaved, "Epics не совпадают");

        List<Epic> epics = manager.getAllEpics();
        Assertions.assertNotNull(epics, "Epics не возвращаются");
        Assertions.assertEquals(1, epics.size(), "Количество epics не совпадает");
        Assertions.assertEquals(epic, epics.getFirst(), "Epics не совпадают");
    }

    @Test
    void addEpicWithSubTasks() {
        Integer id = manager.addEpic(new Epic("Выучить язык", "Английский"));
        SubTask subTask1 = new SubTask("Записаться на курсы", "Хорошие");
        SubTask subTask2 = new SubTask("Купить книги", "Недорогие");

        Epic epicSaved = manager.getEpicById(id);
        manager.addSubTask(subTask1, id);
        manager.addSubTask(subTask2, id);
        List<Integer> subTaskIds = epicSaved.getSubTaskIds();

        Assertions.assertNotNull(subTaskIds, "task_manager.model.SubTask ids не возвращаются");
        Assertions.assertEquals(2, subTaskIds.size(), "Количество subtasks не совпадает");
        Assertions.assertEquals(subTask1, manager.getSubTaskById(subTaskIds.getFirst()),
                "SubTasks не совпадают");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Помыть посуду", "Загрузить машинку");
        Integer id = manager.addEpic(epic);

        manager.updateEpic(new Epic(id, "Помыть собаку", "Загрузить машинку"));
        Epic epicUpdated = manager.getEpicById(id);
        Assertions.assertEquals(1, manager.getAllEpics().size(), "Неверное количество epics");
        Assertions.assertNotEquals(epic, epicUpdated, "task_manager.model.Epic не обновился");
    }

    @Test
    void deleteEpicById() {
        Integer epicId = manager.addEpic(new Epic("Выучить язык", "Английский"));
        SubTask subTask1 = new SubTask("Записаться на курсы", "Хорошие");
        SubTask subTask2 = new SubTask("Купить книги", "Недорогие");
        manager.addSubTask(subTask1, epicId);
        manager.addSubTask(subTask2, epicId);

        List<Epic> epics = manager.getAllEpics();
        List<SubTask> subtasks = manager.getAllSubTasks();
        Assertions.assertEquals(1, epics.size(), "Неверное количество Epics");
        Assertions.assertEquals(2, subtasks.size(), "Неверное количество SubTasks");

        manager.deleteEpicById(epicId);
        List<Epic> epicsAfterDeleting = manager.getAllEpics();
        List<SubTask> subtasksAfterDeleting = manager.getAllSubTasks();
        Assertions.assertEquals(0, epicsAfterDeleting.size(), "Неверное количество Epics");
        Assertions.assertEquals(0, subtasksAfterDeleting.size(), "Неверное количество SubTasks");

    }

    @Test
    void addSubTask() {
        Integer epicId = manager.addEpic(new Epic("Выучить язык", "Английский"));
        SubTask subTask = new SubTask("Записаться на курсы", "Хорошие");
        Integer subTaskId = manager.addSubTask(subTask, epicId);

        List<SubTask> subTasks = manager.getAllSubTasks();
        Assertions.assertNotNull(subTasks, "task_manager.model.SubTask ids не возвращаются");
        Assertions.assertEquals(subTask, subTasks.getFirst(), "SubTasks не совпадают");
        Assertions.assertEquals(subTaskId, manager.getEpicById(epicId).getSubTaskIds().getFirst(),
                "task_manager.model.SubTask id не закреплено за epic");
    }

    @Test
    void shouldNotCreateSubtaskWithEpicIdAsSubTaskId() {
        Integer epicId = manager.addEpic(new Epic("Выучить язык", "Английский"));
        SubTask subTask = new SubTask("Записаться на курсы", "Хорошие");
        subTask.setId(epicId);
        subTask.setEpicId(epicId);

        Integer subTaskId = manager.addSubTask(subTask, epicId);
        Assertions.assertNotEquals(subTaskId, manager.getSubTaskById(subTaskId).getEpicId(),
                "task_manager.model.SubTask id и epic id совпадают");
    }

    @Test
    void updateSubTask() {
        Integer epicId = manager.addEpic(new Epic("Выучить язык", "Английский"));
        TaskStatus epicSavedStatus = manager.getEpicById(epicId).getStatus();
        SubTask subTask = new SubTask("Записаться на курсы", "Хорошие");
        Integer subtaskId = manager.addSubTask(subTask, epicId);
        SubTask subTaskToUpdate = new SubTask(subtaskId, "Купить книги", subTask.getDescription(),
                TaskStatus.IN_PROGRESS, epicId);

        manager.updateSubTask(subTaskToUpdate);

        Epic epicUpdated = manager.getEpicById(epicId);
        SubTask subTaskUpdated = manager.getSubTaskById(subtaskId);
        Assertions.assertEquals(1, manager.getAllSubTasks().size(), "Неверное количество subtasks");
        Assertions.assertNotEquals(subTask, subTaskUpdated, "Subtask не обновился");
        Assertions.assertNotEquals(epicSavedStatus, epicUpdated.getStatus(),
                "Статусы epic совпадают после обновления");
    }

    @Test
    void deleteSubTaskById() {
        Integer epicId = manager.addEpic(new Epic("Выучить язык", "Английский"));
        SubTask subTask1 = new SubTask("Записаться на курсы", "Хорошие");
        Integer subtaskId = manager.addSubTask(subTask1, epicId);

        manager.deleteSubTaskById(subtaskId);
        List<SubTask> subtasks = manager.getAllSubTasks();
        Assertions.assertEquals(0, subtasks.size(), "Неверное количество subtasks");
        Assertions.assertEquals(0, manager.getEpicById(epicId).getSubTaskIds().size(),
                "Неверное количество subtasks в epic");
    }

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
        Assertions.assertEquals(epicSaved, history.get(0), "task_manager.model.Epic в истории не совпадает");
        Assertions.assertEquals(subTaskSaved, history.get(1), "task_manager.model.SubTask в истории не совпадает");
        Assertions.assertEquals(taskSaved, history.get(2), "task_manager.model.Task в истории не совпадает");

    }

    @Test
    void getHistoryAfterUpdate() {
        Integer taskId = manager.addTask(new Task("Помыть посуду", "Загрузить машинку"));
        Task taskSaved = manager.getTaskById(taskId);

        manager.updateTask(new Task(taskId, "Помыть посуду", "Загрузить машинку", TaskStatus.DONE));
        Task taskSaved2 = manager.getTaskById(taskId);

        List<Task> history = manager.getHistory();
        Assertions.assertNotNull(history, "Задачи не сохранились в истории");
        Assertions.assertEquals(2, history.size(), "Неверное количество задач");
        Assertions.assertNotEquals(history.get(0), history.get(1), "Сохранены одинаковые версии задач");
    }

    @Test
    void addToHistory() {
        HistoryManager historyManager = new Manager().getDefaultHistory();

        historyManager.add(new Task("Помыть посуду", "Загрузить машинку"));
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }
}