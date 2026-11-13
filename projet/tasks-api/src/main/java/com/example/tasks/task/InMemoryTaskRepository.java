package com.example.tasks.task;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/** Stockage en mémoire, thread-safe. Les données sont perdues au redémarrage. */
@Repository
public class InMemoryTaskRepository implements TaskRepository {

    private final Map<UUID, Task> tasks = new ConcurrentHashMap<>();

    @Override
    public List<Task> findAll() {
        return tasks.values().stream()
                .sorted(Comparator.comparing(Task::createdAt).thenComparing(Task::id))
                .toList();
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public Task save(Task task) {
        tasks.put(task.id(), task);
        return task;
    }

    @Override
    public boolean deleteById(UUID id) {
        return tasks.remove(id) != null;
    }

    @Override
    public void deleteAll() {
        tasks.clear();
    }
}
