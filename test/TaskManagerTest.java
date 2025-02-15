import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.model.*;
import taskmanager.service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    @BeforeEach
    void setUp() {
        manager = createManager();
    }

    abstract T createManager();

    @Test
    void addTask() {
        Task task = new Task.Builder<>("Помыть посуду", "Загрузить машинку").build();
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
        Task task = new Task.Builder<>("Помыть посуду", "Загрузить машинку").build();
        Integer id = manager.addTask(task);

        manager.updateTask(new Task.Builder<>("Помыть посуду", "Загрузить машинку").id(id).status(TaskStatus.DONE).build());
        Task taskUpdated = manager.getTaskById(id);
        Assertions.assertEquals(1, manager.getAllTasks().size(), "Неверное количество задач");
        Assertions.assertNotEquals(task, taskUpdated, "Задача не обновилась");
    }

    @Test
    void deleteTask() {
        Integer id1 = manager.addTask(new Task.Builder<>("Помыть посуду", "Загрузить машинку").build());
        Integer id2 = manager.addTask(new Task.Builder<>("Помыть собаку", "С шампунем").build());

        manager.deleteTaskById(id1);
        List<Task> tasks = manager.getAllTasks();
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач");
        Assertions.assertThrows(NoSuchElementException.class, () -> manager.deleteTaskById(id1));

        manager.deleteAllTasks();
        List<Task> tasksEmpty = manager.getAllTasks();
        Assertions.assertEquals(0, tasksEmpty.size(), "Неверное количество задач");
    }

    @Test
    void addEpic() {
        Epic epic = new Epic.Builder("Выучить язык", "Английский").build();

        Integer id = manager.addEpic(epic);
        Epic epicSaved = manager.getEpicById(id);
        Assertions.assertNotNull(epicSaved, "Epic не добавлен");
        Assertions.assertEquals(epic, epicSaved, "Epics не совпадают");

        List<Epic> epics = manager.getAllEpics();
        Assertions.assertNotNull(epics, "Epics не возвращаются");
        Assertions.assertEquals(1, epics.size(), "Количество epics не совпадает");
        Assertions.assertEquals(epic, epics.getFirst(), "Epics не совпадают");
    }

    @Test
    void addEpicWithSubTasks() {
        Integer id = manager.addEpic(new Epic.Builder("Выучить язык", "Английский").build());
        SubTask subTask1 = new SubTask.Builder("Записаться на курсы", "Хорошие", id).build();
        SubTask subTask2 = new SubTask.Builder("Купить книги", "Недорогие", id).build();

        Epic epicSaved = manager.getEpicById(id);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        List<SubTask> subTasks = epicSaved.getSubTasks();

        Assertions.assertNotNull(subTasks, "SubTask ids не возвращаются");
        Assertions.assertEquals(2, subTasks.size(), "Количество subtasks не совпадает");
        Assertions.assertEquals(subTask1, manager.getSubTaskById(subTasks.getFirst().getId()),
                "SubTasks не совпадают");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic.Builder("Помыть посуду", "Загрузить машинку").build();
        Integer id = manager.addEpic(epic);

        manager.updateEpic(new Epic.Builder("Помыть собаку", "Загрузить машинку").id(id).build());
        Epic epicUpdated = manager.getEpicById(id);
        Assertions.assertEquals(1, manager.getAllEpics().size(), "Неверное количество epics");
        Assertions.assertNotEquals(epic, epicUpdated, "Epic не обновился");
    }

    @Test
    void deleteEpicById() {
        Integer epicId = manager.addEpic(new Epic.Builder("Выучить язык", "Английский").build());
        SubTask subTask1 = new SubTask.Builder("Записаться на курсы", "Хорошие", epicId).build();
        SubTask subTask2 = new SubTask.Builder("Купить книги", "Недорогие", epicId).build();
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

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
        Integer epicId = manager.addEpic(new Epic.Builder("Выучить язык", "Английский").build());
        SubTask subTask = new SubTask.Builder("Записаться на курсы", "Хорошие", epicId).build();
        Integer subTaskId = manager.addSubTask(subTask);

        List<SubTask> subTasks = manager.getAllSubTasks();
        Assertions.assertNotNull(subTasks, "SubTask ids не возвращаются");
        Assertions.assertEquals(subTask, subTasks.getFirst(), "SubTasks не совпадают");
        Assertions.assertEquals(subTaskId, manager.getEpicById(epicId).getSubTasks().getFirst().getId(),
                "SubTask id не закреплено за epic");
    }

    @Test
    void shouldNotCreateSubtaskWithEpicIdAsSubTaskId() {
        Integer epicId = manager.addEpic(new Epic.Builder("Выучить язык", "Английский").build());
        SubTask subTask = new SubTask.Builder("Записаться на курсы", "Хорошие", epicId).id(epicId).build();

        Integer subTaskId = manager.addSubTask(subTask);
        Assertions.assertNotEquals(subTaskId, manager.getSubTaskById(subTaskId).getEpicId(),
                "SubTask id и epic id совпадают");
    }

    @Test
    void updateSubTask() {
        Integer epicId = manager.addEpic(new Epic.Builder("Выучить язык", "Английский").build());
        TaskStatus epicSavedStatus = manager.getEpicById(epicId).getStatus();
        SubTask subTask = new SubTask.Builder("Записаться на курсы", "Хорошие", epicId).build();
        Integer subtaskId = manager.addSubTask(subTask);
        SubTask subTaskToUpdate = new SubTask.Builder("Купить книги", subTask.getDescription(), epicId)
                .id(subtaskId).status(TaskStatus.IN_PROGRESS).build();

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
        Integer epicId = manager.addEpic(new Epic.Builder("Выучить язык", "Английский").build());
        SubTask subTask1 = new SubTask.Builder("Записаться на курсы", "Хорошие", epicId).build();
        Integer subtaskId = manager.addSubTask(subTask1);

        manager.deleteSubTaskById(subtaskId);
        List<SubTask> subtasks = manager.getAllSubTasks();
        Assertions.assertEquals(0, subtasks.size(), "Неверное количество subtasks");
        Assertions.assertEquals(0, manager.getEpicById(epicId).getSubTasks().size(),
                "Неверное количество subtasks в epic");
    }

    @Test
    void getPrioritizedTaskWithSchedule() {
        Task task = new Task.Builder<>("task1", "description task1")
                .schedule(LocalDateTime.now(), Duration.ofMinutes(10)).build();
        Task taskWithoutSchedule = new Task.Builder<>("task2", "description task2").build();
        Task taskFirst = new Task.Builder<>("taskFirst", "description taskFirst")
                .schedule(LocalDateTime.now().minusHours(1), Duration.ofMinutes(5)).build();

        manager.addTask(task);
        manager.addTask(taskWithoutSchedule);
        Integer taskFirstId = manager.addTask(taskFirst);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        Assertions.assertEquals(2, prioritizedTasks.size(), "Количество приоритетных задач не совпадает");
        Assertions.assertEquals(3, manager.getAllTasks().size(), "Количество обычных задач не совпадает");
        Assertions.assertFalse(prioritizedTasks.contains(taskWithoutSchedule), "Задача без schedule сохранилась");
        Assertions.assertEquals(taskFirstId, prioritizedTasks.getFirst().getId(), "Последовательность задач не верная");
    }
}
