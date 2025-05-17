package com.wishtreetech.salesbridge.login.mapper;

import com.wishtreetech.salesbridge.login.dto.UserDto;
import com.wishtreetech.salesbridge.login.entity.User;
import com.wishtreetech.salesbridge.login.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * The type User mapper.
 */
@Component
public class UserMapper {

    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;

    /**
     * Instantiates a new User mapper.
     *
     * @param modelMapper    the model mapper
     * @param roleRepository the role repository
     */
    public UserMapper(ModelMapper modelMapper, RoleRepository roleRepository) {
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
    }

    /**
     * Convert to entity user.
     *
     * @param userDto the user dto
     * @return the user
     */
    public User convertToEntity(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        user.setRole(roleRepository.findByName(userDto.getRole())); // Convert RoleEnum to Role
        user.setActive(true);
        return user;
    }

    /**
     * Convert to dto user dto.
     *
     * @param user the user
     * @return the user dto
     */
    public UserDto convertToDto(User user) {
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setRole(user.getRole().getName()); // Convert Role to RoleEnum
        return userDto;
    }
}
