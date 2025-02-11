import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import taskmanager.model.Task;
import taskmanager.service.FileBackedTaskManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private Path tempFile;

    @Override
    FileBackedTaskManager createManager() {
        try {
            tempFile = Files.createTempFile("task", ".csv");
        } catch (IOException e) {
            System.out.println("Exception during creating temp file: " + e.getMessage());
        }
        return FileBackedTaskManager.loadFromFile(tempFile.toFile());
    }

    @AfterEach
    public void cleanUp() {
        try {
            Files.deleteIfExists(tempFile);
        } catch (IOException e) {
            System.out.println("Exception during deleting temp file: " + e.getMessage());
        }
    }

    @Test
    void fileExistAndEmpty() {
        Assertions.assertTrue(tempFile.toFile().exists(), "Файла не существует");
        Assertions.assertEquals(0, tempFile.toFile().length(), "Файл не пустой");
    }

    @Test
    void loadTaskFromFile() {
        Task task = new Task.Builder<>("task1", "description task1").build();
        manager.addTask(task);
        FileBackedTaskManager managerLoaded = FileBackedTaskManager.loadFromFile(tempFile.toFile());

        Assertions.assertEquals(1, managerLoaded.getAllTasks().size(), "Количество задач не совпадает");
        Assertions.assertEquals(manager.getAllTasks().getFirst(), managerLoaded.getAllTasks().getFirst(), "Задачи не совпадают");
    }
}
