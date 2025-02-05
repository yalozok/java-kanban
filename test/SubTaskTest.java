import taskmanager.model.SubTask;
import taskmanager.model.TaskType;
import taskmanager.model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class SubTaskTest {
    private static SubTask subTask;

    @BeforeEach
    public void beforeEach() {
        subTask = new SubTask.Builder( "Помыть посуду", "Загрузить машинку", 1).id(0).status(TaskStatus.IN_PROGRESS).build();
    }

    @Test
    void shouldReturnSubTaskType() {
        Assertions.assertEquals(TaskType.SUB_TASK, subTask.getType(),
                "SubTask получает неверный type");
    }

    @Test
    void getEpicId() {
        Assertions.assertEquals(1, subTask.getEpicId(),
                "SubTask получает неверный epic id");
    }

    @Test
    void subTaskIdShouldNotEqualsEpicId() {
        Assertions.assertNotEquals(subTask.getId(), subTask.getEpicId(),
                "SubTask id и epic id не могут совпадать");
    }

    @Test
    void shouldBeEqualsIfIdsEquals() {
        SubTask subTaskToCompare = new SubTask.Builder("Помыть собаку", "Загрузить машинку", 1).id(0).status(TaskStatus.IN_PROGRESS).build();
        Assertions.assertNotEquals(subTask, subTaskToCompare,
                "SubTask с одинаковым id должны быть равны");
    }
}