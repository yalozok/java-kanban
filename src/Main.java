public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        System.out.println("Поехали!");


        taskManager.addRegularTask("Помыть посуду", "Загрузить машинку");
        taskManager.addRegularTask("Переезд", "Собрать все вещи");
        Task task1 = taskManager.getRegularTaskById(0);
        Task task2 = taskManager.getRegularTaskById(1);
        taskManager.deleteRegularTaskById(3);
        taskManager.updateRegularTaskById(task1.getId(), "Полить цветы", "На всех окнах", TaskStatus.DONE);
        System.out.println(task1);
        task1 = taskManager.getRegularTaskById(0);
        System.out.println(task1);
        taskManager.deleteAllRegularTasks();
        task1 = taskManager.getRegularTaskById(0);
        System.out.println(task1 + " " + task2);
    }
}
