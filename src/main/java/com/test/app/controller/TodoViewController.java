package com.test.app.controller;

import com.test.app.model.Todo;
import com.test.app.service.TodoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/todos")
public class TodoViewController {
    private final TodoService todoService;

    public TodoViewController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public String showTodos(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";
        model.addAttribute("todos", todoService.getTodosByUsername(username));
        model.addAttribute("username", username);
        return "todo";
    }

    @PostMapping
    public String addTodo(@RequestParam String title, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setUsername(username);
        todoService.createTodo(todo);
        return "redirect:/todos";
    }

    @PostMapping("/edit")
    public String editTodo(@RequestParam Long id, @RequestParam String title, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";
        Todo updated = new Todo();
        updated.setId(id);
        updated.setTitle(title);
        updated.setUsername(username);
        todoService.updateTodo(id, updated);
        return "redirect:/todos";
    }

    @PostMapping("/delete/{id}")
    public String deleteTodo(@PathVariable Long id, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";
        todoService.deleteTodo(id);
        return "redirect:/todos";
    }

    @PostMapping("/toggle/{id}")
    public String toggleCompleted(@PathVariable Long id, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";
        todoService.toggleCompleted(id, username);
        return "redirect:/todos";
    }
}
