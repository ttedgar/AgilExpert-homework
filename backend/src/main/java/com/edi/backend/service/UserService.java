package com.edi.backend.service;

import com.edi.backend.domain.Menu;
import com.edi.backend.domain.User;
import com.edi.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(String name) {
        Menu mainMenu = new Menu();
        mainMenu.setName("Main Menu");

        User user = new User();
        user.setName(name);
        user.setMainMenu(mainMenu);
        mainMenu.setOwner(user);

        return userRepository.save(user);
    }

    public User updateName(String id, String newName) {
        User user = getById(id);
        user.setName(newName);
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
