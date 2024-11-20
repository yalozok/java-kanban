public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        System.out.println("Поехали!");

        System.out.println("TASK CREATION");
        Task task1 = new Task("Помыть посуду", "Загрузить машинку");
        Task task2 = new Task("Полить цветы", "На всех окнах");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Epic epic1 = new Epic("Выучить язык", "Английский");
        Epic epic2 = new Epic("Заняться спортом", "С тренажерами");
        SubTask subTask1 = new SubTask("Записаться на курсы", "Хорошие");
        SubTask subTask2 = new SubTask("Купить книги", "Недорогие");
        SubTask subTask3 = new SubTask("Купить кроссовки", "Модные");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubTask(subTask1, epic1.getId());
        taskManager.addSubTask(subTask2, epic1.getId());
        taskManager.addSubTask(subTask3, epic2.getId());
        printListOfTasks(taskManager);

        System.out.println("TASK UPDATING");

        taskManager.updateTask(new Task(task1.getId(), "Постирать белье", task1.getDescription(), TaskStatus.DONE));
        taskManager.updateTask(new Task(task2.getId(), task2.getName(), "У бабушки", TaskStatus.IN_PROGRESS));
        taskManager.updateEpic(new Epic(epic1.getId(), epic1.getName(), "Нидерландский", epic1.getSubTaskIds()));
        taskManager.updateSubTask(new SubTask(subTask1.getId(), subTask1.getName(), subTask1.getDescription(), TaskStatus.IN_PROGRESS, subTask1.getEpicId()));
        taskManager.updateSubTask(new SubTask(subTask2.getId(), subTask2.getName(), subTask2.getDescription(), TaskStatus.DONE, subTask2.getEpicId()));
        taskManager.updateEpic(new Epic(epic2.getId(), epic2.getName(), "На свежем воздухе", epic2.getSubTaskIds()));
        taskManager.updateSubTask(new SubTask(subTask3.getId(), subTask3.getName(), subTask3.getDescription(), TaskStatus.DONE, subTask3.getEpicId()));
        System.out.println(taskManager.getAllSubTasksByEpic(epic1.getId()));
        printListOfTasks(taskManager);

        System.out.println("TASK DELETION");
        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteEpicById(epic1.getId());
        printListOfTasks(taskManager);
    }

    private static void printListOfTasks(InMemoryTaskManager taskManager) {
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println("-".repeat(20));
    }
}
