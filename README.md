# Task Manager (Java Core)

A lightweight task tracking system built with pure Java, without any external frameworks.

## Key Features:

- Supports three task types: `Task`, `Epic`, and `SubTask` (linked to `Epics`)
- Automatic status management (`NEW`, `IN_PROGRESS`, `DONE`) with Epic status updated based on its SubTasks
- Task viewing history tracking
- Prioritized task list with time conflict (overlap) detection
- Two storage implementations: in-memory and file-based (`java.nio.file`)

## Technologies & Concepts:

- Core Java (OOP, interfaces, collections)
- Custom task and history managers (`TaskManager`, `HistoryManager`)
- File operations using `java.nio.file`
- Clean architecture and SOLID principles in practice
- Emphasis on code readability, modularity, and reusability
- Fully covered with unit tests using JUnit 5 (`org.junit.jupiter`)
