package task_manager.service;

public class TaskNode<Task> {
    public Task data;
    public TaskNode<Task> next;
    public TaskNode<Task> prev;

    public TaskNode(Task task) {
        this.data = task;
        this.prev = null;
        this.next = null;
    }
}
