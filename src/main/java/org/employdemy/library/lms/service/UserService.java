package org.employdemy.library.lms.service;

import lombok.AllArgsConstructor;
import org.employdemy.library.lms.dto.UserRequestDTO;
import org.employdemy.library.lms.dto.UserResponseDTO;
import org.employdemy.library.lms.dto.UserUpdateDTO;
import org.employdemy.library.lms.exception.ResourceNotFoundException;
import org.employdemy.library.lms.mapper.UserMapper;
import org.employdemy.library.lms.model.NotificationSettings;
import org.employdemy.library.lms.model.NotificationType;
import org.employdemy.library.lms.model.Role;
import org.employdemy.library.lms.model.User;
import org.employdemy.library.lms.repository.NotificationSettingRepository;
import org.employdemy.library.lms.repository.TransactionRepository;
import org.employdemy.library.lms.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TransactionRepository transactionRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final NotificationService notificationService;


    public UserResponseDTO createUser(UserRequestDTO dto) {
        User saved = userRepository.save(userMapper.toEntity(dto));

        //send notification to librarians about the new user
        List<User> librarian = userRepository.findByRole(Role.LIBRARIAN);

        for (User m : librarian) {
                notificationService.sendNotification(
                        m.getId(),
                        "New User Added",
                        "A new User '" + saved.getName() + "' has been added.",
                        NotificationType.NEW_USER
                );
        }
        //create a notification setting for the user created
        NotificationSettings notificationSettings=new NotificationSettings(saved
                                        ,true,true,true);
        notificationSettingRepository.save(notificationSettings);

        //send a welcome mail to new user
        notificationService.sendNotification(
                saved.getId(),
                "Welcome "+saved.getName(),
                "Welcome to our Library " + saved.getName() + ", enlighten yourself with books.",
                NotificationType.NEW_USER
        );

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

        int borrowedCount=transactionRepository.countAllBorrowedBooks(id);
        return userMapper.toDTO(user,borrowedCount);
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

    public String getActivateUser(Boolean active,Long userId){


        User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User does not exist with id "+userId));

        if(active.equals(true) && user.isActive()==true){
            return "User is already in active state";
        }else if (active.equals(false) && user.isActive()==false){
            return "User is already in Inactive state";
        }else if(active.equals(false) && user.isActive()==true){
            user.setActive(false);
            userRepository.save(user);
            return "User is successfully Deactivated";
        }else {
            user.setActive(true);
            userRepository.save(user);
            return "User is Successfully Activated";
        }


    }
}
