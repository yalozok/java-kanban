package task_manager.service;

import task_manager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, TaskNode<Task>> history = new HashMap<>();
    private TaskNode<Task> head;
    private TaskNode<Task> tail;

    @Override
    public void add(Task task) {
        Integer id = task.getId();
        if (history.containsKey(id)) {
            remove(id);
        }
        TaskNode<Task> currentNode = linkLast(task);
        history.put(id, currentNode);
    }

    private TaskNode<Task> linkLast(Task task) {
        TaskNode<Task> currentNode = new TaskNode<>(task);
        if (head == null) {
            head = currentNode;
        } else {
            tail.next = currentNode;
            currentNode.prev = tail;
        }

        tail = currentNode;
        return currentNode;
    }

    @Override
    public void remove(int id) {
        TaskNode<Task> node = history.get(id);
        if (node != null) {
            removeNode(node);
            history.remove(id);
        }
    }

    private void removeNode(TaskNode<Task> node) {
        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }

        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }

        node.next = null;
        node.prev = null;
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        TaskNode<Task> tmpNode = head;
        while (tmpNode != null) {
            tasksList.add(tmpNode.data);
            tmpNode = tmpNode.next;
        }
        return tasksList;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }
}
