import taskmanager.model.*;
import taskmanager.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void addTasksWithScheduleConflict() {
        Task task = new Task.Builder<>("task1", "description task1")
                .schedule(LocalDateTime.now(), Duration.ofMinutes(20)).build();
        Task taskConflict = new Task.Builder<>("taskConflict", "description taskConflict")
                .schedule(LocalDateTime.now(), Duration.ofMinutes(5)).build();

        manager.addTask(task);
        Assertions.assertThrows(TaskOverlapException.class, () -> manager.addTask(taskConflict));
    }

    @Test
    void updateTasksWithSchedule() {
        Task task1 = new Task.Builder<>("task1", "description task1")
                .schedule(LocalDateTime.now(), Duration.ofMinutes(20)).build();
        Task task2 = new Task.Builder<>("task2", "description task2")
                .schedule(LocalDateTime.now().plusHours(1), Duration.ofMinutes(5)).build();

        Integer taskId1 = manager.addTask(task1);
        Integer taskId2 = manager.addTask(task2);
        List<Task> prioritizedTask = manager.getPrioritizedTasks();
        Assertions.assertEquals(taskId1, prioritizedTask.getFirst().getId(), "Последовательность задач не верная");

        Task task2Updated = new Task.Builder<>("taskUpdated", "description taskUpdated")
                .id(taskId2).schedule(LocalDateTime.now().minusHours(1), Duration.ofMinutes(5)).build();
        manager.updateTask(task2Updated);
        List<Task> prioritizedTaskUpdated = manager.getPrioritizedTasks();
        Assertions.assertEquals(taskId2, prioritizedTaskUpdated.getFirst().getId(), "Последовательность задач не верная");
    }

    @Test
    void removeTaskWithSchedule() {
        Task task1 = new Task.Builder<>("task1", "description task1")
                .schedule(LocalDateTime.now(), Duration.ofMinutes(20)).build();
        Task task2 = new Task.Builder<>("task2", "description task2")
                .schedule(LocalDateTime.now().plusHours(1), Duration.ofMinutes(5)).build();
        Integer taskId1 = manager.addTask(task1);
        Integer taskId2 = manager.addTask(task2);
        List<Task> prioritizedTask = manager.getPrioritizedTasks();
        Assertions.assertEquals(2, prioritizedTask.size(), "Количество приоритетных задач не совпадает");
        Assertions.assertEquals(taskId1, prioritizedTask.getFirst().getId(), "Последовательность задач не верная");

        manager.deleteTaskById(taskId1);
        List<Task> prioritizedTaskUpdated = manager.getPrioritizedTasks();
        Assertions.assertEquals(1, prioritizedTaskUpdated.size(), "Количество приоритетных задач не совпадает");
        Assertions.assertEquals(taskId2, prioritizedTaskUpdated.getFirst().getId(), "Последовательность задач не верная");
    }
}