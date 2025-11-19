package com.test.app.service;

import com.test.app.model.Todo;
import com.test.app.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public Optional<Todo> getTodoById(Long id) {
        return todoRepository.findById(id);
    }

    public Todo createTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    public Optional<Todo> updateTodo(Long id, Todo updated) {
        return todoRepository.findById(id).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setDescription(updated.getDescription());
            existing.setCompleted(updated.isCompleted());
            return todoRepository.save(existing);
        });
    }

    public boolean deleteTodo(Long id) {
        if (todoRepository.findById(id).isPresent()) {
            todoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public java.util.List<Todo> getTodosByUsername(String username) {
        return todoRepository.findAllByUsername(username);
    }

    public void toggleCompleted(Long id, String username) {
        todoRepository.findById(id).ifPresent(todo -> {
            if (username.equals(todo.getUsername())) {
                todo.setCompleted(!todo.isCompleted());
                todoRepository.save(todo);
            }
        });
    }
}
