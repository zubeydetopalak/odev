package com.test.app.repository;

import com.test.app.model.Todo;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TodoRepository {
    private final Map<Long, Todo> store = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Todo> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Todo> findById(Long id) {
        System.out.println("executed find by id");
        return Optional.ofNullable(store.get(id));
    }

    public Todo save(Todo todo) {
        if (todo.getId() == null) {
            todo.setId(idGenerator.getAndIncrement());
        }
        store.put(todo.getId(), todo);
        return todo;
    }

    public void deleteById(Long id) {
        store.remove(id);
    }

    public java.util.List<Todo> findAllByUsername(String username) {
        java.util.List<Todo> result = new java.util.ArrayList<>();
        for (Todo todo : store.values()) {
            if (username.equals(todo.getUsername())) {
                result.add(todo);
            }
        }
        return result;
    }

    // toggleCompleted için ek bir fonksiyona gerek yok, mevcut save ve findById kullanılıyor.
}
