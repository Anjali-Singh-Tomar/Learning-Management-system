package org.employdemy.library.lms.service;

import org.employdemy.library.lms.dto.DashboardResponseDTO;
import org.employdemy.library.lms.model.TransactionStatus;
import org.employdemy.library.lms.repository.BookRepository;
import org.employdemy.library.lms.repository.TransactionRepository;
import org.employdemy.library.lms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DashboardService {

    private final BookRepository bookRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Autowired
    public DashboardService(BookRepository bookRepository, TransactionRepository transactionRepository, UserRepository userRepository){
        this.bookRepository=bookRepository;
        this.transactionRepository=transactionRepository;
        this.userRepository=userRepository;
    }

    public DashboardResponseDTO getAdminDashboard(){

        DashboardResponseDTO dto=new DashboardResponseDTO();

        dto.setTotalBooks(bookRepository.count());
        dto.setBooksBorrowed(transactionRepository.countByStatus(TransactionStatus.BORROWED));
        dto.setOverdue(transactionRepository.countByStatusAndDueDateBefore(TransactionStatus.BORROWED, LocalDate.now()));
        dto.setActiveMembers(userRepository.countByActiveTrue());

        return dto;

    }

}
