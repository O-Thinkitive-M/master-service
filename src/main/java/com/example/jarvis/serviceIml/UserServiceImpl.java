package com.example.jarvis.serviceIml;

import com.example.jarvis.entity.Account;
import com.example.jarvis.entity.User;
import com.example.jarvis.exception.ResourceNotFoundException;
import com.example.jarvis.feignclient.AccountClient;
import com.example.jarvis.repo.UserRepo;
import com.example.jarvis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AccountClient accountClient;

    @Override
    public String createUser(User user) {
        try {
            if(!user.getFirstname().isEmpty() && !user.getLastname().isEmpty() && !user.getEmail().isEmpty() && !user.getPhone().isEmpty())
            {


                userRepo.save(user);
                return "User Successfully Created!";
            }
            else {
                return "Please enter required fields!";
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "Something went wrong!";
    }

    @Override
    public User getSingleUser(Long id) {
           User u= userRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("User with this ID does not exists!"));
           u.setAccount(accountClient.getAccountsByUserId(id));
           return u;
    }
    @Override
    public List<User> getALlusers() {
        try {
                   List<User> users= userRepo.findAll();
                   List<User> us= users.stream().map(user -> {
                        user.setAccount(
                                accountClient.getAccountsByUserId(user.getId()));
                                return user;
                    }).collect(Collectors.toList());
                    return us;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public String deleteUser(Long id) {
        try {
            userRepo.deleteById(id);
            return "User Delete Successfully!";
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "Something went wrong!";
    }

    @Override
    public String updateUser(User user) {
        try
        {
            userRepo.save(user);

            return "User Update Successfully!";
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "Something went wrong!";
    }

    public void addUser(User request)
    {
        userRepo.save(request);
    }







}
