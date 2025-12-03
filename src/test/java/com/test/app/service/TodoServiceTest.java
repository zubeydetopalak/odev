package com.test.app.service;

import com.test.app.model.Todo;
import com.test.app.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TodoServiceTest {
    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTodoById() {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Test Todo");
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        Optional<Todo> result = todoService.getTodoById(1L);
        assertTrue(result.isPresent());
        assertEquals("Test Todo", result.get().getTitle());
    }

    @Test
    void testCreateTodo() {
        Todo todo = new Todo();
        todo.setTitle("Save Test");
        when(todoRepository.save(todo)).thenReturn(todo);
        Todo saved = todoService.createTodo(todo);
        assertEquals("Save Test", saved.getTitle());
    }
}
