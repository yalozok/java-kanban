import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private static Integer countTask = 0;
    Map<Integer, Task> tasksRegular = new HashMap<>();
    Map<Integer, Epic> tasksEpic = new HashMap<>();
    Map<Integer, SubTask> subTasks = new HashMap<>();

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

    public void updateRegularTaskById(Integer id, String name, String description, TaskStatus status) {
        if(getRegularTaskById(id) == null) {
            return;
        }
        tasksRegular.put(id, new Task(id, name, description, status));
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

    public Epic getEpicById(Integer id) {
        Epic task = (Epic) tasksEpic.get(id);
        if (task == null) {
            System.out.println("Нет epic задачи c id: " + id);
        }
        return task;
    }

    public void updateEpicById(Integer id, String name, String description) {
        Epic epicTask = getEpicById(id);
        if(epicTask == null) {
            return;
        }

        TaskStatus status;
        List<Integer> listSubTaskId = epicTask.getListSubTaskId();

        if(listSubTaskId.isEmpty()) {
            status = TaskStatus.NEW;
        } else {
            status = getSubTaskById(listSubTaskId.getFirst()).getStatus();
            for(Integer subTaskId : listSubTaskId) {
                if (getSubTaskById(subTaskId).getStatus() != status) {
                    status = TaskStatus.IN_PROGRESS;
                    return;
                }
            }
        }
        tasksRegular.put(id, new Task(id, name, description, status));
        System.out.println("Epic задача с id: " + id + " обновлена");
    }

    public void deleteEpicTaskById(Integer id) {
        Epic epicTask = getEpicById(id);
        if(epicTask != null) {
            return;
        }

        List<Integer> listSubTaskId = epicTask.getListSubTaskId();
        if(!listSubTaskId.isEmpty()) {
            for(Integer subTaskId : listSubTaskId) {
                deleteSubTaskById(subTaskId);
            }
        }
        tasksEpic.remove(id);
        System.out.println("Epic задача со всеми подзадачами с id: " + id + " удалена");
    }

    public void deleteAllEpicTasks() {
        if(tasksEpic.isEmpty()) {
            return;
        }
        for (Integer id : tasksEpic.keySet()) {
            deleteEpicTaskById(id);
        }

        System.out.println("Все epic задачи c их подзадачами удалены");
    }

    public void addSubTask(String name, String description, Integer epicId) {
        Epic epic = getEpicById(epicId);
        SubTask subTask = epic.createNewSubTask(countTask, name, description);
        subTasks.put(countTask, subTask);
        System.out.println("Добавлена подзадача с id: " + countTask);
        countTask++;
    }

    public SubTask getSubTaskById(Integer id) {
        SubTask task = (SubTask) subTasks.get(id);
        if (task == null) {
            System.out.println("Нет  подзадачи c id: " + id);
        }
        return task;
    }

    public void updateSubTaskById(Integer id, String name, String description, TaskStatus status) {
        subTasks.put(id, new SubTask(id, name, description, status));
        Epic epic = getEpicById(getSubTaskById(id).getEpicIdBySubtask());
        updateEpicById(epic.getId(), epic.getName(), epic.getDescription());
    }

    public void deleteSubTaskById(Integer id) {
        if(getRegularTaskById(id) == null) {
            return;
        }
        subTasks.remove(id);
        System.out.println("Подзадача с id: " + id + " удалена");
    }
}
