import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubTaskTest {
    private static SubTask subTask;

    @BeforeEach
    public void beforeEach() {
        subTask = new SubTask(0, "Помыть посуду", "Загрузить машинку", TaskStatus.IN_PROGRESS, 1);
    }

    @Test
    void setEpicId() {
        subTask.setEpicId(2);
        Assertions.assertEquals(2, subTask.getEpicId(), "SubTask получает неверный epic id");
    }

    @Test
    void getEpicId() {
        Assertions.assertEquals(1, subTask.getEpicId(), "SubTask получает неверный epic id");
    }

    @Test
    void subTaskIdShouldNotEqualsEpicId() {
        Assertions.assertNotEquals(subTask.getId(), subTask.getEpicId(), "SubTask id и epic id не могут совпадать");
    }

    @Test
    public void shouldBeEqualsIfIdsEquals() {
        SubTask subTaskToCompare = new SubTask(0, "Помыть собаку", "Загрузить машинку", TaskStatus.IN_PROGRESS, 1);
        Assertions.assertNotEquals(subTask, subTaskToCompare, "SubTask с одинаковым id должны быть равны");
    }
}