import taskmanager.model.Epic;
import taskmanager.model.SubTask;
import taskmanager.model.TaskStatus;
import taskmanager.model.TaskType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class EpicTest {
    private static Epic epic;
    private List<SubTask> subTasks = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        subTasks.add(new SubTask.Builder("SubTask1", "Description SubTask1", 0).id(1).status(TaskStatus.NEW).build());
        subTasks.add(new SubTask.Builder("SubTask2", "Description SubTask2", 0).id(2).status(TaskStatus.NEW).build());
        epic = new Epic.Builder("Помыть посуду", "Загрузить машинку").id(0).subTasks(subTasks).build();
    }

    @Test
    void shouldReturnEpicType() {
        Assertions.assertEquals(TaskType.EPIC, epic.getType(),
                "Epic получает неверный type");
    }

    @Test
    void shouldReturnCorrectListOfSubTasks() {
        Assertions.assertEquals(subTasks, epic.getSubTasks(),
                "Epic получает неверный список subtask");
    }

    @Test
    void shouldUpdateCorrectListOfSubTasks() {
        SubTask subtask = new SubTask.Builder("SubTask3", "Description SubTask3", 0).build();
        subTasks.add(subtask);
        epic.setSubTask(subtask);
        Assertions.assertEquals(subTasks, epic.getSubTasks(),
                "Epic получает неверный список subtask");
    }

    @Test
    void shouldBeEqualsIfIdsEquals() {
        Epic epicToCompare = new Epic.Builder("Помыть собаку", "Загрузить машинку")
                .id(0).subTasks(subTasks).build();
        Assertions.assertNotEquals(epic, epicToCompare, "Epic с одинаковым id должны быть равны");
    }

    @Test
    void shouldNotTakeTimeAndDurationForCreation() {
        Epic.Builder builder = new Epic.Builder("EpicWithData", "DescriptionEpicWithData");
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(10);
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> builder.schedule(startTime, duration),
                "Epic принимает время и дату в конструкторе.");
    }

    @Test
    void shouldNotSetScheduleByItself() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(10);
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> epic.setSchedule(startTime, duration), "Epic может установить время, дату и продолжительность через setter");
    }

    @Test
    void shouldNotReturnGetEndTimeWithEmptySubTasksList() {
        Epic epicWithEmptySubtasks = new Epic.Builder("EpicWithEmptySubtasks", "DescriptionEpicWithEmptySubtasks")
                .build();
        Assertions.assertEquals(Optional.empty(), epicWithEmptySubtasks.getEndTime(),
                "Epic может получить время окончания задачи с пустым списком subtasks");
        Assertions.assertEquals(Optional.empty(), epic.getEndTime(),
                "Epic может получить время окончания задачи с subtasks без schedules");
    }

    @Test
    void calculateCorrectSchedule() {
        LocalDateTime startTimeSubTask3 = LocalDateTime.now();
        LocalDateTime startTimeSubTask4 = LocalDateTime.now().plusMinutes(5);
        Duration durationSubTask3 = Duration.ofMinutes(10);
        Duration durationSubTask4 = Duration.ofMinutes(20);

        SubTask subTask3 = new SubTask.Builder("SubTask3", "DescriptionSubTask3", 0)
                .schedule(startTimeSubTask3, durationSubTask3).build();
        SubTask subTask4 = new SubTask.Builder("SubTask4", "DescriptionSubTask4", 0)
                .schedule(startTimeSubTask4, durationSubTask4).build();
        epic.setSubTask(subTask3);
        epic.setSubTask(subTask4);
        Assertions.assertEquals(Optional.of(startTimeSubTask3), epic.getStartTime(), "Старт Epic вычисляется неверно");
        Assertions.assertEquals(Optional.of(durationSubTask3.plus(durationSubTask4)), epic.getDuration(), "Продолжительность Epic вычисляется неверно");
        Assertions.assertEquals(subTask4.getEndTime(), epic.getEndTime(), "Финиш Epic вычисляется неверно");
    }

    @Test
    void shouldHasStatusNEWifAllSubtasksNEW() {
        List<SubTask> listSubTasks = new ArrayList<>();
        listSubTasks.add(new SubTask.Builder("subTask1", "description subTask1", 2)
                .status(TaskStatus.NEW).build());
        listSubTasks.add(new SubTask.Builder("subTask2", "description subTask2", 2)
                .status(TaskStatus.NEW).build());
        Epic epicForStatus = new Epic.Builder("epicForStatus", "description epicForStatus")
                .id(2).subTasks(listSubTasks).build();
        epicForStatus.updateStatus();
        Assertions.assertEquals(TaskStatus.NEW, epicForStatus.getStatus(), "Статус вычисляется неверно");
    }

    @Test
    void shouldHasStatusDONEifAllSubtasksDONE() {
        List<SubTask> listSubTasks = new ArrayList<>();
        listSubTasks.add(new SubTask.Builder("subTask1", "description subTask1", 2)
                .status(TaskStatus.DONE).build());
        listSubTasks.add(new SubTask.Builder("subTask2", "description subTask2", 2)
                .status(TaskStatus.DONE).build());
        Epic epicForStatus = new Epic.Builder("epicForStatus", "description epicForStatus")
                .id(2).subTasks(listSubTasks).build();
        epicForStatus.updateStatus();
        Assertions.assertEquals(TaskStatus.DONE, epicForStatus.getStatus(), "Статус вычисляется неверно");
    }

    @Test
    void shouldHasStatusIN_PROGRESSifAllSubtasksNEWandDONE() {
        List<SubTask> listSubTasks = new ArrayList<>();
        listSubTasks.add(new SubTask.Builder("subTask1", "description subTask1", 2)
                .status(TaskStatus.NEW).build());
        listSubTasks.add(new SubTask.Builder("subTask2", "description subTask2", 2)
                .status(TaskStatus.DONE).build());
        Epic epicForStatus = new Epic.Builder("epicForStatus", "description epicForStatus")
                .id(2).subTasks(listSubTasks).build();
        epicForStatus.updateStatus();
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epicForStatus.getStatus(), "Статус вычисляется неверно");
    }

    @Test
    void shouldCorrectUpdateStatusAfterStatusUpdatedInSubTask() {
        epic.updateStatus();
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус вычисляется неверно");
        SubTask subtaskFirst = epic.getSubTasks().getFirst();

        epic.updateSubTask(new SubTask.Builder(subtaskFirst.getName(), subtaskFirst.getDescription(), epic.getId())
                .id(subtaskFirst.getId()).status(TaskStatus.DONE).build());
        epic.updateStatus();
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус вычисляется неверно");
    }

}