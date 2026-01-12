package org.employdemy.library.lms.service;

import lombok.RequiredArgsConstructor;
import org.employdemy.library.lms.dto.*;
import org.employdemy.library.lms.exception.ResourceNotFoundException;
import org.employdemy.library.lms.model.*;
import org.employdemy.library.lms.repository.BookRepository;
import org.employdemy.library.lms.repository.TransactionRepository;
import org.employdemy.library.lms.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class LibrarianService {

    private final BookRepository bookRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;


    public LibrarianOverviewResponse getLibrarianOverview(){

        LocalDate today=LocalDate.now();

        long todayIssued=transactionRepository.countByStatusAndBorrowedAt(TransactionStatus.BORROWED,today);
        long todayReturned=transactionRepository.countByStatusAndReturnedAt(TransactionStatus.RETURNED,today);
        long dueToday=transactionRepository.countByReturnedAtIsNullAndDueDate(today);
        long activeMembers=userRepository.countByActiveTrue();

        return new LibrarianOverviewResponse(
                todayIssued,
                todayReturned,
                dueToday,
                activeMembers
        );
    }

    @Transactional
    public List<TodayActivityDTO> getTodayActivity() {

        LocalDate today = LocalDate.now();

        List<TodayActivityDTO> todayActivity =
                Stream.concat(
                        transactionRepository.findIssuedToday(today)
                                .stream()
                                .map(t -> new TodayActivityDTO(
                                        t.getId(),
                                        t.getBook().getTitle(),
                                        t.getUser().getName(),
                                        t.getBorrowedAt(),
                                        "Issue"
                                )),
                        transactionRepository.findReturnedToday(today)
                                .stream()
                                .map(t -> new TodayActivityDTO(
                                        t.getId(),
                                        t.getBook().getTitle(),
                                        t.getUser().getName(),
                                        t.getReturnedAt(),
                                        "Return"
                                ))
                ).toList();

        return todayActivity;
    }

    @Transactional
    public List<PendingReturnDTO> getPendingReturns() {

        LocalDate today = LocalDate.now();
        LocalDate sevenDays = today.plusDays(7);

        return transactionRepository.findPendingReturns(today, sevenDays)
                .stream()
                .map(t -> {

                    long daysLeft = ChronoUnit.DAYS.between(today, t.getDueDate());

                    String dueLabel;
                    if (daysLeft == 0) {
                        dueLabel = "Today";
                    } else if (daysLeft == 1) {
                        dueLabel = "Tomorrow";
                    } else {
                        dueLabel = "In " + daysLeft + " days";
                    }

                    return new PendingReturnDTO(
                            t.getId(),
                            t.getUser().getName(),
                            t.getBook().getTitle(),
                            t.getDueDate(),
                            dueLabel
                    );
                })
                .toList();
    }

    @Transactional
    public IssueBookResponseDTO issueBook(IssueBookRequestDTO dto){
        User user =userRepository.findById(dto.getUserId())
                .orElseThrow(()-> new ResourceNotFoundException("user not found: "+dto.getUserId()));

        if(!user.isActive()){
            throw new RuntimeException("User is not Active");
        }

        Book book=bookRepository.findById(dto.getBookId())
                .orElseThrow(()-> new ResourceNotFoundException("Book not found: "+dto.getBookId()));

        if(book.getAvailableCopies()<=0){
            throw new RuntimeException("No Copies Available for this book");
        }

        boolean alreadyBorrowed= transactionRepository
                .existsByUserAndBookAndReturnedAtIsNull(user,book);

        if(alreadyBorrowed){
            throw new RuntimeException("User already borrowed this book");
        }

        Transaction transaction=new Transaction();
        transaction.setUser(user);
        transaction.setBook(book);
        transaction.setBorrowedAt(LocalDate.now());
        transaction.setDueDate(dto.getDueDate());
        transaction.setStatus(TransactionStatus.BORROWED);

        book.setAvailableCopies(book.getAvailableCopies()-1);

        transactionRepository.save(transaction);
        bookRepository.save(book);

        notificationService.sendNotification(
                transaction.getUser().getId(),
                "Book Issued",
                "Your Book "+transaction.getBook().getTitle()+"has been successfully issued by the Librarian",
                NotificationType.ISSUE
        );

        return new IssueBookResponseDTO(
                transaction.getId(),
                user.getName(),
                book.getTitle(),
                dto.getDueDate(),
                "Book Issued Successfully"
        );
    }


    @Transactional
    public List<ManageBooksDTO> getManageBooks(){
        List<Book> book=bookRepository.findAll();

        return book.stream()
                .map(b->{
                    ManageBooksDTO dto=new ManageBooksDTO();
                    dto.setTitle(b.getTitle());
                    dto.setAuthor(b.getAuthor());
                    dto.setIsbn(b.getIsbn());
                    dto.setGenre(b.getGenre());
                    dto.setStatus(b.getAvailableCopies()>0?"Available":"Borrowed");
                    dto.setAvailableCopies(b.getAvailableCopies());
                    return dto;
                })
                .toList();

    }

    public List<ManageMembersDTO> manageMembers(){
        List<User> users=userRepository.findByRole(Role.MEMBER);

        return users.stream()
                .map(u->{
                    ManageMembersDTO dto=new ManageMembersDTO();
                    dto.setId(u.getId());
                    dto.setMemberName(u.getName());
                    dto.setMemberId(u.getEmpId());
                    dto.setMemberEmail(u.getEmail());
                    dto.setJoiningDate(u.getJoiningDate());
                    dto.setBorrowedCount(transactionRepository.countByUser_IdAndReturnedAtIsNull(u.getId()));
                    dto.setTotalBorrowed(transactionRepository.countByUser_IdAndReturnedAtIsNotNull(u.getId()));
                    dto.setOverdue(transactionRepository.countOverdueByUser(u.getId(),LocalDate.now()));
                    dto.setStatus(u.isActive()?"Active":"Not Active");

                    return dto;
                })
                .toList();
    }


    public List<LibrarianBorrowedDTO> getBorrowedBooks(){
        List<Transaction> transactions=transactionRepository.findAllCurrentBorrowed();

        LocalDate today=LocalDate.now();

        return transactions.stream()
                .map(t->{
                    LibrarianBorrowedDTO dto=new LibrarianBorrowedDTO();
                    dto.setId(t.getId());
                    dto.setUserName(t.getUser().getName());
                    dto.setBookName(t.getBook().getTitle());
                    dto.setBorrowDate(t.getBorrowedAt());
                    dto.setDueDate(t.getDueDate());
                    dto.setStatus(today.isAfter(t.getDueDate())?"Overdue":"Active");
                    return dto;
                })
                .toList();
    }


    public String markAsReturned(Long id){
        Transaction transaction=transactionRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Transaction not found for id -:"+id));

        if(transaction.getStatus()!=TransactionStatus.RETURNED){
            transaction.setStatus(TransactionStatus.RETURNED);
            transaction.setReturnedAt(LocalDate.now());
        }else{
            return "Book is Already Returned";
        }

        transactionRepository.save(transaction);
        return "Book has been returned Successfully";
    }

//    public MemberDetailsDTO getmemberDetails(Long userId){
//        User user = userRepository.findById(userId)
//                .orElseThrow(()->new ResourceNotFoundException("User Doesn't exist"));
//
//        if(!user.isActive()){
//            throw new RuntimeException("User is not in active state");
//        }
//
//        long currentlyBorrowed=transactionRepository.countByUser_IdAndReturnedAtIsNull(userId);
//        long totalBorrowed=transactionRepository.countByUser_IdAndReturnedAtIsNotNull(userId);
//        long overdue=transactionRepository.countOverdueByUser(userId,LocalDate.now());
//
//        return new MemberDetailsDTO(
//                user.getName(),
//                user.getEmpId(),
//                user.getEmail(),
//                user.isActive(),
//                user.getJoiningDate(),
//                currentlyBorrowed,
//                totalBorrowed,
//                overdue
//        );
//    }

    public List<ManageMembersDTO> getSearchMembers(String keyword){

        List<User> users=userRepository.searchUsers(keyword);

        return users.stream()
                .map(u->{
                    ManageMembersDTO dto=new ManageMembersDTO();
                    dto.setId(u.getId());
                    dto.setMemberName(u.getName());
                    dto.setMemberId(u.getEmpId());
                    dto.setMemberEmail(u.getEmail());
                    dto.setJoiningDate(u.getJoiningDate());
                    dto.setBorrowedCount(transactionRepository.countByUser_IdAndReturnedAtIsNull(u.getId()));
                    dto.setTotalBorrowed(transactionRepository.countByUser_IdAndReturnedAtIsNotNull(u.getId()));
                    dto.setOverdue(transactionRepository.countOverdueByUser(u.getId(),LocalDate.now()));
                    dto.setStatus(u.isActive()?"Active":"Not Active");
                    dto.setRole(u.getRole());

                    return dto;
                })
                .toList();

    }

    public List<ManageMembersDTO> getFilterMembers(Boolean status){

        List<User> users=userRepository.findUsersByActiveStatus(status);

        return users.stream()
                .map(u->{
                    ManageMembersDTO dto=new ManageMembersDTO();
                    dto.setId(u.getId());
                    dto.setMemberName(u.getName());
                    dto.setMemberId(u.getEmpId());
                    dto.setMemberEmail(u.getEmail());
                    dto.setJoiningDate(u.getJoiningDate());
                    dto.setBorrowedCount(transactionRepository.countByUser_IdAndReturnedAtIsNull(u.getId()));
                    dto.setTotalBorrowed(transactionRepository.countByUser_IdAndReturnedAtIsNotNull(u.getId()));
                    dto.setOverdue(transactionRepository.countOverdueByUser(u.getId(),LocalDate.now()));
                    dto.setStatus(u.isActive()?"Active":"Not Active");

                    return dto;
                })
                .toList();

    }




}
