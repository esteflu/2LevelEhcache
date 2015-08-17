package com.lundberg.services;

import com.lundberg.domain.User;
import com.lundberg.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    //@CachePut(value = "UserCache")
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
