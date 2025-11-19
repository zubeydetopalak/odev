package com.test.app.repository;

import com.test.app.model.User;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public User save(User user) {
        users.put(user.getUsername(), user);
        return user;
    }
    public User findByUsername(String username) {
        return users.get(username);
    }
    public boolean existsByUsername(String username) {
        return users.containsKey(username);
    }
}

