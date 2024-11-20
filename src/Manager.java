public class Manager {
    TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
