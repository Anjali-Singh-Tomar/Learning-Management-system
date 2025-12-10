package org.employdemy.library.lms.service;

import org.employdemy.library.lms.dto.UserRequestDTO;
import org.employdemy.library.lms.dto.UserResponseDTO;
import org.employdemy.library.lms.dto.UserUpdateDTO;
import org.employdemy.library.lms.exception.ResourceNotFoundException;
import org.employdemy.library.lms.mapper.UserMapper;
import org.employdemy.library.lms.model.User;
import org.employdemy.library.lms.repository.TransactionRepository;
import org.employdemy.library.lms.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TransactionRepository transactionRepository;

    public UserService(UserRepository userRepository, UserMapper userMapper, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.transactionRepository=transactionRepository;
    }

    public UserResponseDTO createUser(UserRequestDTO dto) {
        User saved = userRepository.save(userMapper.toEntity(dto));
        return userMapper.toDTO(saved);
    }

    //GET AL USER WITH TOTAL NUMBER OF TRANSACTIONS
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    int borrowedCount = transactionRepository.countAllBorrowedBooks(user.getId());
                    return userMapper.toDTO(user, borrowedCount);
                })
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return userMapper.toDTO(user);
    }

    public UserResponseDTO updateUser(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setEmpId(dto.getEmpId());

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(user.getPassword());
        }

        User updated = userRepository.save(user);
        return userMapper.toDTO(updated);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        userRepository.delete(user);
    }
}
