package com.wishtreetech.salesbridge.login.service;


import com.wishtreetech.salesbridge.login.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The interface User service.
 */
@Service
public interface UserService {
    /**
     * Add user user.
     *
     * @param user the user
     * @return the user
     */
    User addUser(User user);

    /**
     * Gets user by email.
     *
     * @param email the email
     * @return the user by email
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Gets user by id.
     *
     * @param id the id
     * @return the user by id
     */
    Optional<User> getUserById(long id);

    /**
     * Update user user.
     *
     * @param user the user
     * @return the user
     */
    User updateUser(User user);

    /**
     * Delete user.
     *
     * @param id the id
     */
    void deleteUser(long id);

    List<User> getAllUsers();
}
