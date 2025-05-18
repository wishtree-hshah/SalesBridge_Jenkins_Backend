package com.wishtreetech.salesbridge.login.service.implementation;

import com.wishtreetech.salesbridge.login.entity.User;
import com.wishtreetech.salesbridge.login.repository.UserRepository;
import com.wishtreetech.salesbridge.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Add user user.
     *
     * @param user the user
     * @return the user
     */
    @Override
    public User addUser(User user) {
        // validate all the filels
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if(existing.isPresent()) {
            User existingUser = existing.get();
            if(existingUser.isActive()) {
                throw new RuntimeException("User with given user is already available.");
            }else {
                existingUser.setActive(true);
                return userRepository.save(existingUser);
            }
        }

        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email).filter(user1 ->  user1.isActive());
        return user;
    }

    /**
     * Gets user by id.
     *
     * @param id the id
     * @return the user by id
     */
    @Override
    public Optional<User> getUserById(long id) {
        return Optional.of(userRepository.getById(id));
    }

    /**
     * Update user user.
     *
     * @param user the user
     * @return the user
     */
    @Override
    public User updateUser(User user) {
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail()).filter(user1 -> user1.isActive());
        if(optionalUser.isPresent()) {
            User user1 = optionalUser.get();
            user1.setName(user.getName() != null ? user.getName() : user1.getName());
            user1.setEmail(user.getEmail() != null ? user.getEmail() : user1.getEmail());
            user1.setActive(user.isActive() != user1.isActive() ? user.isActive() : user1.isActive());
            user1.setRole(user.getRole() != null ? user.getRole() : user1.getRole());
            return userRepository.save(user1);
        }else {
            throw new RuntimeException("User is not available.");
        }
    }

    /**
     * Delete user.
     *
     * @param id the id
     */
    @Override
    public void deleteUser(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()) {
            User user1 = optionalUser.get();
            user1.setActive(false);
            userRepository.save(user1);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll().stream().filter(user -> user.isActive()).collect(Collectors.toList());
    }
}