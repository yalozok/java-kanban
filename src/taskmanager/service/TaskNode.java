package taskmanager.service;

public class TaskNode<T> {
    public T data;
    public TaskNode<T> next;
    public TaskNode<T> prev;

    public TaskNode(T task) {
        this.data = task;
        this.prev = null;
        this.next = null;
    }
}
