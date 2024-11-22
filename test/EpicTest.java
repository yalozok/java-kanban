import task_manager.model.Epic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class EpicTest {
    private static Epic epic;
    private List<Integer> subtaskIds = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        subtaskIds.add(1);
        subtaskIds.add(2);
        epic = new Epic(0, "Помыть посуду", "Загрузить машинку", subtaskIds);
    }

    @Test
    void shouldReturnCorrectListOfSubTaskIds() {
        Assertions.assertEquals(subtaskIds, epic.getSubTaskIds(),
                "Epic получает неверный список subtask ids");
    }

    @Test
    void shouldUpdateCorrectListOfSubTaskIds() {
        subtaskIds.add(3);
        epic.setSubTaskIds(subtaskIds);
        Assertions.assertEquals(subtaskIds, epic.getSubTaskIds(),
                "Epic получает неверный список subtask ids");
    }

    @Test
    public void shouldBeEqualsIfIdsEquals() {
        Epic epicToCompare = new Epic(0, "Помыть собаку", "Загрузить машинку", subtaskIds);
        Assertions.assertNotEquals(epic, epicToCompare, "Epic с одинаковым id должны быть равны");
    }
}