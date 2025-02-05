import taskmanager.model.Task;
import taskmanager.model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.model.TaskType;

class TaskTest {
    private static Task task;

    @BeforeEach
    public void beforeEach() {
        task = new Task.Builder<>("Помыть посуду", "Загрузить машинку").id(0).status(TaskStatus.IN_PROGRESS).build();
    }

    @Test
    void shouldReturnCorrectFields() {
        Assertions.assertEquals(0, task.getId(), "Task получает неверный id");
        Assertions.assertEquals("Помыть посуду", task.getName(), "Task получает неверное имя");
        Assertions.assertEquals("Загрузить машинку", task.getDescription(), "Task получает неверное описание");
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, task.getStatus(), "Task получает неверный статус");
        Assertions.assertEquals(TaskType.REGULAR, task.getType(), "Task получает неверный статус");
    }

    @Test
    void shouldUpdateCorrectId() {
        task.setId(1);
        Assertions.assertEquals(1, task.getId(), "Task получает неверный id");
    }

    @Test
    void shouldUpdateCorrectName() {
        task.setName("Помыть собаку");
        Assertions.assertEquals("Помыть собаку", task.getName(),
                "Task получает неверное имя");
    }

    @Test
    void shouldUpdateCorrectDescription() {
        task.setDescription("С шампунем");
        Assertions.assertEquals("С шампунем", task.getDescription(),
                "Task получает неверное описание");
    }

    @Test
    void shouldUpdateCorrectStatus() {
        task.setStatus(TaskStatus.DONE);
        Assertions.assertEquals(TaskStatus.DONE, task.getStatus(),
                "Task получает неверный статус");
    }

    @Test
    void shouldBeEqualsIfIdsEquals() {
        Task taskToCompare = new Task.Builder<>("Помыть собаку", "Загрузить машинку").id(0).status(TaskStatus.IN_PROGRESS).build();
        Assertions.assertNotEquals(task, taskToCompare,
                "Task с одинаковым id должны быть равны");
    }
}