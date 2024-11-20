public class Main {

    public static void main(String[] args) {
        TaskManager manager = new Manager().getDefault();
        System.out.println("Поехали!");

        System.out.println("TASK CREATION");
        Task task1 = new Task("Помыть посуду", "Загрузить машинку");
        Task task2 = new Task("Полить цветы", "На всех окнах");
        manager.addTask(task1);
        manager.addTask(task2);
        Epic epic1 = new Epic("Выучить язык", "Английский");
        Epic epic2 = new Epic("Заняться спортом", "С тренажерами");
        SubTask subTask1 = new SubTask("Записаться на курсы", "Хорошие");
        SubTask subTask2 = new SubTask("Купить книги", "Недорогие");
        SubTask subTask3 = new SubTask("Купить кроссовки", "Модные");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubTask(subTask1, epic1.getId());
        manager.addSubTask(subTask2, epic1.getId());
        manager.addSubTask(subTask3, epic2.getId());
        printListOfTasks(manager);

        System.out.println("TASK UPDATING");

        manager.updateTask(new Task(task1.getId(), "Постирать белье", task1.getDescription(), TaskStatus.DONE));
        manager.updateTask(new Task(task2.getId(), task2.getName(), "У бабушки", TaskStatus.IN_PROGRESS));
        manager.updateEpic(new Epic(epic1.getId(), epic1.getName(), "Нидерландский", epic1.getSubTaskIds()));
        manager.updateSubTask(new SubTask(subTask1.getId(), subTask1.getName(), subTask1.getDescription(), TaskStatus.IN_PROGRESS, subTask1.getEpicId()));
        manager.updateSubTask(new SubTask(subTask2.getId(), subTask2.getName(), subTask2.getDescription(), TaskStatus.DONE, subTask2.getEpicId()));
        manager.updateEpic(new Epic(epic2.getId(), epic2.getName(), "На свежем воздухе", epic2.getSubTaskIds()));
        manager.updateSubTask(new SubTask(subTask3.getId(), subTask3.getName(), subTask3.getDescription(), TaskStatus.DONE, subTask3.getEpicId()));
        System.out.println(manager.getAllSubTasksByEpic(epic1.getId()));
        printListOfTasks(manager);

        System.out.println("HISTORY");
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubTaskById(subTask1.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubTaskById(subTask1.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubTaskById(subTask1.getId());
        System.out.println(manager.getHistory());

        System.out.println("TASK DELETION");
        manager.deleteTaskById(task1.getId());
        manager.deleteEpicById(epic1.getId());
        printListOfTasks(manager);
    }

    private static void printListOfTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getAllSubTasksByEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-".repeat(20));
    }
}
