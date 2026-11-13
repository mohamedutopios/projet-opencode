package com.example.tasks.task;

import com.example.tasks.error.TaskNotFoundException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final Clock clock;

    public TaskService(TaskRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public List<Task> list() {
        return repository.findAll();
    }

    public Task get(UUID id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task create(CreateTaskRequest request) {
        Instant now = clock.instant();
        Task task = new Task(
                UUID.randomUUID(),
                request.title().strip(),
                stripOrNull(request.description()),
                request.status() != null ? request.status() : TaskStatus.TODO,
                now,
                now);
        return repository.save(task);
    }

    public Task update(UUID id, UpdateTaskRequest request) {
        Task existing = get(id);
        Task updated = existing.withChanges(
                stripOrNull(request.title()), stripOrNull(request.description()), request.status(), clock.instant());
        return repository.save(updated);
    }

    public void delete(UUID id) {
        if (!repository.deleteById(id)) {
            throw new TaskNotFoundException(id);
        }
    }

    private static String stripOrNull(String value) {
        return value == null ? null : value.strip();
    }
}
