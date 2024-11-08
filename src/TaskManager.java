public class TaskManager {
    private Integer countTask = 0;
    TaskCollection tasksRegular = new TaskCollection();

    public void addRegularTask(String name, String description) {
        tasksRegular.addTask(countTask, new Task(countTask, name, description, TaskStatus.NEW));
        System.out.println("Добавлена обычная задача с id: " + countTask);
        countTask++;
    }

    public Task getRegularTaskById(Integer id) {
        Task task = tasksRegular.getTaskById(id);
        if (task == null) {
            System.out.println("Нет обычной задачи id: " + id);
        }
        return task;
    }

    public void updateRegularTaskById(Integer id, String name, String description, TaskStatus status) {
        if(getRegularTaskById(id) != null) {
            tasksRegular.updateTask(id, new Task(id, name, description, status));
            System.out.println("Обычная задача с id: " + id + " обновлена");
        }
    }

    public void deleteRegularTaskById(Integer id) {
        if(getRegularTaskById(id) != null) {
            tasksRegular.deleteTaskById(id);
            System.out.println("Обычная задача с id: " + id + " удалена");
        }
    }

    public void deleteAllRegularTasks() {
        tasksRegular.deleteAllTasks();
        System.out.println("Все обычные задачи удалены");
    }
}
