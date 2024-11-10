public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        System.out.println("Поехали!");

        System.out.println("TASK CREATION");
        taskManager.addRegularTask("Помыть посуду", "Загрузить машинку");
        taskManager.addRegularTask("Полить цветы", "На всех окнах");
        taskManager.addEpicTask("Выучить язык", "Английский");
        taskManager.addSubTask("Записаться на курсы", "Хорошие", 2);
        taskManager.addSubTask("Купить книги", "Недорогие", 2);
        taskManager.addEpicTask("Заняться спортом", "С тренажерами");
        taskManager.addSubTask("Купить кроссовки", "Модные", 5);
        printListOfTasks(taskManager);

        System.out.println("TASK UPDATING");
        taskManager.updateRegularTaskById(0, "Помыть посуду", "Загрузить машинку", TaskStatus.DONE);
        taskManager.updateRegularTaskById(1, "Полить цветы", "На всех окнах", TaskStatus.IN_PROGRESS);
        taskManager.updateEpicById(2, "Выучить язык", "Нидерландский");
        taskManager.updateSubTaskById(3, "Записаться на курсы", "Хорошие", TaskStatus.IN_PROGRESS);
        taskManager.updateSubTaskById(4, "Купить книги", "Недорогие", TaskStatus.DONE);
        taskManager.updateEpicById(5, "Заняться спортом", "На свежем воздухе");
        taskManager.updateSubTaskById(6, "Купить кроссовки", "Модные", TaskStatus.DONE);
        printListOfTasks(taskManager);

        System.out.println("TASK DELETION");
        taskManager.deleteRegularTaskById(1);
        taskManager.deleteEpicTaskById(2);
        printListOfTasks(taskManager);
    }

    private static void printListOfTasks(TaskManager taskManager) {
        System.out.println(taskManager.getListOfRegularTasks());
        System.out.println(taskManager.getListOfEpic());
        System.out.println(taskManager.getListOfAllSubTasks());
        System.out.println("-".repeat(20));
    }
}
