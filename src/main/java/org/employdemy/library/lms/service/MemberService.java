package org.employdemy.library.lms.service;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.*;
import org.employdemy.library.lms.exception.ResourceNotFoundException;
import org.employdemy.library.lms.mapper.UserMapper;
import org.employdemy.library.lms.model.Transaction;
import org.employdemy.library.lms.model.User;
import org.employdemy.library.lms.repository.BookRepository;
import org.employdemy.library.lms.repository.TransactionRepository;
import org.employdemy.library.lms.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final BookRepository bookRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    private BCryptPasswordEncoder encoder= new BCryptPasswordEncoder(10);

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public MemberDashboardResponseDTO getMemberOverview(Long memberId){
        long currentlyBorrowed= transactionRepository.countByUser_IdAndReturnedAtIsNull(memberId);
        long booksRead=transactionRepository.countByUser_IdAndReturnedAtIsNotNull(memberId);

        long dueSoon;
        if(currentlyBorrowed>0){
            dueSoon= transactionRepository.countDueSoon(memberId, LocalDate.now(),LocalDate.now().plusDays(5));
        } else dueSoon=0L;

        List<Transaction> transactions = transactionRepository.findOverdueTransactions();

        return new MemberDashboardResponseDTO(
                currentlyBorrowed,
                booksRead,
                dueSoon
        );
    }

    public List<RecommendedBookDTO> getRecommendedBooks(Long memberId) {

        // Fetch only IDs from JPQL — no LOBs touched
        List<Long> bookIds = bookRepository.findRecommendedBookIds(memberId);

        return bookIds.stream()
                .map(id -> bookRepository.findById(id).orElse(null))
                .filter(book -> book != null)
                .map(book -> new RecommendedBookDTO(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getGenre(),
                        book.getPublishedYear(),
                        book.getImageData() != null
                                ? Base64.getEncoder().encodeToString(book.getImageData())
                                : null,
                        book.getImageName(),
                        book.getImageType()
                ))
                .toList();
    }

    @Transactional
    public List<BorrowedBookDTO> getAllBooks(Long memberId) {

        return transactionRepository.findTransactionHistory(memberId)
                .stream()
                .map(t -> {
                    BorrowedBookDTO dto = new BorrowedBookDTO();
                    dto.setTransactionId(t.getId());
                    dto.setBookId(t.getBook().getId());
                    dto.setTitle(t.getBook().getTitle());
                    dto.setAuthor(t.getBook().getAuthor());
                    dto.setBorrowedAt(t.getBorrowedAt());
                    dto.setDueDate(t.getDueDate());
                    dto.setStatus(t.getStatus());
                    dto.setReturnedAt(t.getReturnedAt());
                    return dto;
                })
                .toList();
    }

    @Transactional
    public List<MyBooksDTO> getMyBooks(Long memberId){
        List<Transaction> list=transactionRepository.findCurrentBorrowed(memberId);

        return list.stream().map(t -> {
            LocalDate today=LocalDate.now();
            int daysLeft=(int) today.until(t.getDueDate()).getDays();


            return new MyBooksDTO(
                    t.getId(),
                    t.getBook().getId(),
                    t.getBook().getTitle(),
                    t.getBook().getAuthor(),
                    t.getBorrowedAt().toString(),
                    t.getDueDate().toString(),
                    daysLeft,
                    transactionRepository.findStatus(t.getId()),
                    t.getBook().getImageData() != null
                            ? Base64.getEncoder().encodeToString(t.getBook().getImageData())
                            : null

            );
        }).toList();

    }

    //------------------------------------------------------------------------------------------------------------------
    //Member Settings
    //------------------------------------------------------------------------------------------------------------------

    public void updateUserName(Long userId, String newName){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(!user.isActive())
            throw new RuntimeException("Inactive user cannot update profile");

        user.setName(newName.trim());
        userRepository.save(user);
    }


    public void changePassword(Long userId, ChangePasswordDTO dto){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(!passwordEncoder.matches(dto.getOldPassword(),user.getPassword())){
            throw new RuntimeException("Old password is incorrect");
        }

        else if(passwordEncoder.matches(dto.getNewPassword(), user.getPassword())){
            throw new RuntimeException("New Password Cannot be same as the Old Password");
        }

        else{
            User updatedUser=userMapper.changePassword(user, dto.getNewPassword());
            userRepository.save(updatedUser);
        }


    }
}

