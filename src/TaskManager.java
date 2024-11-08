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

}
