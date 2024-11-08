public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        System.out.println("Поехали!");

        taskManager.addRegularTask("Помыть посуду", "Загрузить машинку");
        taskManager.addRegularTask("Переезд", "Собрать все вещи");
        Task task1 = taskManager.getRegularTaskById(0);
        Task task2 = taskManager.getRegularTaskById(1);
        System.out.println(task1.toString() + " and " + task2.toString());
    }
}
