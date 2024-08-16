package com.example.jarvis.service;

import com.example.jarvis.entity.User;

import java.util.List;

public interface UserService {

    String createUser(User user);
    User getSingleUser(Long id);
    List<User> getALlusers();
    String deleteUser(Long id);
    String updateUser(User user);


    void addUser(User request);
}
