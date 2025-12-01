package org.employdemy.library.lms.mapper;

import org.employdemy.library.lms.dto.UserRequestDTO;
import org.employdemy.library.lms.dto.UserResponseDTO;
import org.employdemy.library.lms.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private BCryptPasswordEncoder encoder= new BCryptPasswordEncoder(10);

    public UserResponseDTO toDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setEmpId(user.getEmpId());
        dto.setActive(user.isActive());
        return dto;
    }

    public User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));//bcrypting the password and saving in the entity
        user.setRole(dto.getRole());
        user.setEmpId(dto.getEmpId());
        return user;
    }
}
