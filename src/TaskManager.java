import java.util.*;

public class TaskManager {
    private static Integer countTask = 0;
    private Map<Integer, Task> tasksRegular = new HashMap<>();
    private Map<Integer, Epic> tasksEpic = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();

    public void addRegularTask(String name, String description) {
        tasksRegular.put(countTask, new Task(countTask, name, description, TaskStatus.NEW));
        System.out.println("Добавлена обычная задача с id: " + countTask);
        countTask++;
    }

    public Task getRegularTaskById(Integer id) {
        Task task = tasksRegular.get(id);
        if (task == null) {
            System.out.println("Нет обычной задачи c id: " + id);
        }
        return task;
    }

    public List<Task> getListOfRegularTasks() {
        if(tasksRegular.isEmpty()) {
            System.out.println("Ни одной обычной задачи пока не добавлено");
            return null;
        }
        List<Task> list = new ArrayList<>();
        for(Integer id : tasksRegular.keySet()) {
            list.add(tasksRegular.get(id));
        }
        return list;
    }

    // update function takes as a parameters individual fields of `Task` instead of the entire `Task` object
    // Reasons for this design choice:
    // 1. Simplifies the `Main` class by avoiding unnecessary `Task` object creation
    // 2. Supports scalability by enabling targeted updates to specific `Task` fields in the future

    public void updateRegularTaskById(Integer id, String name, String description, TaskStatus status) {
        Task task = getRegularTaskById(id);
        if(task == null) {
            return;
        }
        task.setName(name);
        task.setDescription(description);
        task.setStatus(status);
        System.out.println("Обычная задача с id: " + id + " обновлена");
    }

    public void deleteRegularTaskById(Integer id) {
        if(getRegularTaskById(id) == null) {
           return;
        }
        tasksRegular.remove(id);
        System.out.println("Обычная задача с id: " + id + " удалена");
    }

    public void deleteAllRegularTasks() {
        if(tasksRegular.isEmpty()) {
            System.out.println("Обычных задач еще не создано");
            return;
        }
        tasksRegular.clear();
        System.out.println("Все обычные задачи удалены");
    }

    public void addEpicTask(String name, String description) {
        tasksEpic.put(countTask, new Epic(countTask, name, description, TaskStatus.NEW));
        System.out.println("Добавлена epic задача с id: " + countTask);
        countTask++;
    }

    private void updateEpicStatus (Epic epic) {
        TaskStatus status;
        List<Integer> listSubTaskId = epic.getListSubTaskId();

        if(listSubTaskId.isEmpty()) {
            status = TaskStatus.NEW;
        } else {
            status = getSubTaskById(listSubTaskId.getFirst()).getStatus();
            for(Integer subTaskId : listSubTaskId) {
                if (getSubTaskById(subTaskId).getStatus() != status) {
                    status = TaskStatus.IN_PROGRESS;
                }
            }
        }
        epic.setStatus(status);
    }

    public Epic getEpicById(Integer id) {
        Epic task = tasksEpic.get(id);
        if (task == null) {
            System.out.println("Нет epic задачи c id: " + id);
        }
        return task;
    }

    public List<Epic> getListOfEpic() {
        if(tasksEpic.isEmpty()) {
            System.out.println("Ни одной задачи типа epic пока не добавлено");
            return null;
        }
        List<Epic> list = new ArrayList<>();
        for(Integer id : tasksEpic.keySet()) {
            list.add(tasksEpic.get(id));
        }
        return list;
    }

    public void updateEpicById(Integer id, String name, String description) {
        Epic epic = getEpicById(id);
        if(epic == null) {
            return;
        }
        epic.setName(name);
        epic.setDescription(description);
        System.out.println("Epic задача с id: " + id + " обновлена");
    }

    public void deleteEpicTaskById(Integer id) {
        Epic epic = getEpicById(id);
        if(epic == null) {
            return;
        }

        List<Integer> listSubTaskId = epic.getListSubTaskId();
        if(!listSubTaskId.isEmpty()) {
            for(Integer subTaskId : listSubTaskId) {
                subTasks.remove(subTaskId);
            }
        }
        tasksEpic.remove(id);
        System.out.println("Epic задача со всеми подзадачами с id: " + id + " удалена");
    }

    public void deleteAllEpicTasks() {
        if(tasksEpic.isEmpty()) {
            System.out.println("Epic задач еще не создано");
            return;
        }
        while (!tasksEpic.isEmpty()) {
            List<Integer> listId = new ArrayList<>(tasksEpic.keySet());
            deleteEpicTaskById(listId.getFirst());
        }
        System.out.println("Все epic задачи c их подзадачами удалены");
    }

    public void addSubTask(String name, String description, Integer epicId) {
        Epic epic = getEpicById(epicId);
        SubTask subTask = epic.createNewSubTask(countTask, name, description);
        subTask.setEpicId(epicId);
        subTasks.put(countTask, subTask);
        System.out.println("Добавлена подзадача с id: " + countTask);
        countTask++;
    }

    public SubTask getSubTaskById(Integer id) {
        SubTask task = subTasks.get(id);
        if (task == null) {
            System.out.println("Нет  подзадачи c id: " + id);
        }
        return task;
    }

    public List<SubTask> getListOfSubtasks(List<Integer> ids) {
        List<SubTask> list = new ArrayList<>();
        for(Integer id : ids) {
            list.add(subTasks.get(id));
        }
        return list;
    }

    public List<SubTask> getListOfAllSubTasks() {
        return getListOfSubtasks(new ArrayList<>(subTasks.keySet()));
    }

    public List<SubTask> getListOfSubTasksByEpicId(Integer id) {
        Epic epic = getEpicById(id);
        if(epic == null) {
            return null;
        }
        return getListOfSubtasks(epic.getListSubTaskId());
    }

    public void updateSubTaskById(Integer id, String name, String description, TaskStatus status) {
        SubTask subTask = getSubTaskById(id);
        if(subTask == null) {
            return;
        }
        subTask.setName(name);
        subTask.setDescription(description);
        subTask.setStatus(status);
        updateEpicStatus(getEpicById(subTask.getEpicId()));
    }

    public void deleteSubTaskById(Integer id) {
        SubTask subTask = getSubTaskById(id);
        if(subTask == null) {
            return;
        }
        Epic epic = tasksEpic.get(subTask.getEpicId());
        epic.deleteSubTaskIdFromEpic(subTask.getId());
        updateEpicStatus(epic);
        subTasks.remove(id);
        System.out.println("Подзадача с id: " + id + " удалена");
    }

    public void deleteAllSubTasks(){
        if(subTasks.isEmpty()) {
            System.out.println("Подзадач еще не создано");
            return;
        }
        while (!subTasks.isEmpty()) {
            List<Integer> listId = new ArrayList<>(subTasks.keySet());
            deleteSubTaskById(listId.getFirst());
        }
        System.out.println("Все подзадачи удалены");
    }
}
