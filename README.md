# Task Manager (Java Core)

A lightweight task tracking system built with pure Java, extended with a basic HTTP API.

## Key Features:

- Supports three task types: `Task`, `Epic`, and `SubTask` (linked to `Epics`)
- Automatic status management (`NEW`, `IN_PROGRESS`, `DONE`) with Epic status updated based on its SubTasks
- Task viewing history tracking
- Prioritized task list with time conflict (overlap) detection
- Two storage strategies: in-memory and file-based (`java.nio.file`)
- REST-like HTTP interface using `com.sun.net.httpserver.HttpServer` 
- JSON serialization/deserialization using Gson 
- Modular request handlers with custom response utilities and error handling

## Technologies & Concepts:

- Core Java: OOP, interfaces, collections, exceptions
- HTTP API design and request handling via `HttpHandler`
- JSON parsing with `Gson`
- File I/O using `java.nio.file`
- Clean, layered architecture following SOLID principles
- Fully covered with unit tests using JUnit 5 (`org.junit.jupiter`)
