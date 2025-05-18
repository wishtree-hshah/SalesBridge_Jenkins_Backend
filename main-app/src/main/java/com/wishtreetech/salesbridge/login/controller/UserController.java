package com.wishtreetech.salesbridge.login.controller;

import com.wishtreetech.commonutils.dto.ResponseDto;
import com.wishtreetech.salesbridge.login.dto.UserDto;
import com.wishtreetech.salesbridge.login.entity.User;
import com.wishtreetech.salesbridge.login.mapper.UserMapper;
import com.wishtreetech.salesbridge.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * The type User controller.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    /**
     * Add a new user.
     *
     * @param userDto the user DTO
     * @return the response entity
     */
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @PostMapping
    public ResponseEntity<ResponseDto<String>> addUser(@RequestBody UserDto userDto) {
        if (userDto == null) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "User cannot be null");
        }
        if(userDto.getRole() == null) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Role cannot be null");
        }
        try {
            User user = userMapper.convertToEntity(userDto);
            userService.addUser(user);
            return ResponseEntity.ok(new ResponseDto<>(true, "User added successfully"));
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Get the currently authenticated user by email.
     *
     * @param authentication the authentication
     * @return the response entity
     */
    @PreAuthorize("hasAuthority('READ_USER')")
    @GetMapping
    public ResponseEntity<ResponseDto<UserDto>> getUserByEmail(Authentication authentication) {
        Optional<User> user = userService.getUserByEmail((String) authentication.getPrincipal());

        return user.map(value -> ResponseEntity.ok(new ResponseDto<>(true, userMapper.convertToDto(value))))
                .orElseGet(() -> buildErrorResponse(HttpStatus.NOT_FOUND, "User not found", UserDto.class));
    }

    /**
     * Get all users.
     *
     * @return the response entity
     */
    @PreAuthorize("hasAuthority('READ_ALL_USER')")
    @GetMapping("/all")
    public ResponseEntity<ResponseDto<List<UserDto>>> getAllUsers() {
        List<UserDto> allUsers = userService.getAllUsers()
                .stream()
                .map(userMapper::convertToDto)
                .toList();

        return ResponseEntity.ok(new ResponseDto<>(true, allUsers));
    }

    /**
     * Update an existing user.
     *
     * @param userDto the user DTO
     * @return the response entity
     */
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    @PutMapping
    public ResponseEntity<ResponseDto<String>> updateUser(@RequestBody UserDto userDto, Authentication authentication) {
        if (userDto == null) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "User cannot be null");
        }

        try {
            if(userDto.getRole() == null) {
                return buildErrorResponse(HttpStatus.BAD_REQUEST, "Role cannot be null");
            }
            if(userDto.getEmail().equals(authentication.getPrincipal())) {
                return buildErrorResponse(HttpStatus.FORBIDDEN, "You can not update your own account");
            }
            User user = userMapper.convertToEntity(userDto);
            userService.updateUser(user);
            return ResponseEntity.ok(new ResponseDto<>(true, "User updated successfully"));
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('DELETE_USER')")
    @DeleteMapping
    public ResponseEntity<ResponseDto<String>> deleteUser(@RequestBody UserDto userDto, Authentication authentication) {
        if (userDto == null) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "User cannot be null");
        }

        try {
            User user = userMapper.convertToEntity(userDto);
            user = userService.getUserByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
            if(user.getEmail().equals(authentication.getPrincipal())) {
                return buildErrorResponse(HttpStatus.FORBIDDEN, "You can not delete your own account");
            }
            userService.deleteUser(user.getId());
            return ResponseEntity.ok(new ResponseDto<>(true, "User deleted successfully"));
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Generic method to build an error response.
     *
     * @param status  HTTP status code
     * @param message Error message
     * @param <T>     Type of response data
     * @return ResponseEntity with ResponseDto<T>
     */
    private <T> ResponseEntity<ResponseDto<T>> buildErrorResponse(HttpStatus status, String message, Class<T> responseType) {
        return ResponseEntity.status(status)
                .body(new ResponseDto<>(false, null));
    }

    /**
     * Overloaded method for string-based error responses.
     *
     * @param status  HTTP status code
     * @param message Error message
     * @return ResponseEntity with ResponseDto<String>
     */
    private ResponseEntity<ResponseDto<String>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ResponseDto<>(false, message));
    }
}
