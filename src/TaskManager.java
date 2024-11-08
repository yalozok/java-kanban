public class TaskManager {
    private Integer countTask = 0;
    TaskCollection tasksRegular = new TaskCollection();

    public void addRegularTask(String name, String description) {
        tasksRegular.addTask(countTask, new Task(countTask, name, description, TaskStatus.NEW));
        countTask++;
    }

    public Task getRegularTaskById(Integer id) {
        return tasksRegular.getTaskById(id);
    }

    public void updateRegularTaskById(Integer id, String name, String description, TaskStatus status) {
        tasksRegular.addTask(id, new Task(id, name, description, status));
    }

}
