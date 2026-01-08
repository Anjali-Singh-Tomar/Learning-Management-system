package org.employdemy.library.lms.service;

import lombok.AllArgsConstructor;
import org.employdemy.library.lms.dto.BookRequestDTO;
import org.employdemy.library.lms.dto.BookResponseDTO;
import org.employdemy.library.lms.dto.BrowseBookDTO;
import org.employdemy.library.lms.exception.ResourceAlreadyExistsException;
import org.employdemy.library.lms.exception.ResourceDeactivatedException;
import org.employdemy.library.lms.exception.ResourceNotFoundException;
import org.employdemy.library.lms.mapper.BookMapper;
import org.employdemy.library.lms.model.*;
import org.employdemy.library.lms.repository.BookRepository;
import org.employdemy.library.lms.repository.TransactionRepository;
import org.employdemy.library.lms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final TransactionRepository transactionRepository;


    // ------------------------------------------------------------------------
    // 1. ADD BOOK--Done
    // ------------------------------------------------------------------------
    public BookResponseDTO addBook(BookRequestDTO dto) {

        // Check if ISBN already exists
        if (bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new RuntimeException("A book with this ISBN already exists");
        }

        // Convert DTO → Entity using Mapper
        Book book = bookMapper.toEntity(dto);

        // Save to DB
        Book savedBook = bookRepository.save(book);

        List<User> members = userRepository.findByRole(Role.MEMBER);


        for (User m : members) {
            notificationService.sendNotification(
                    m.getId(),
                    "New Book Added",
                    "A new book '" + savedBook.getTitle() + "' has been added.",
                    NotificationType.NEW_BOOK
            );
        }
        // Convert back Entity → ResponseDTO
        return bookMapper.toDTO(savedBook);
    }


    // ------------------------------------------------------------------------
    // 2. GET SINGLE BOOK (Entity helper)
    // ------------------------------------------------------------------------
    public Book getBookEntity(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    }

    public BookResponseDTO getBook(Long id) {
        return bookMapper.toDTO(getBookEntity(id));
    }

    // ------------------------------------------------------------------------
    // 3. GET ALL BOOKS-no change required
    // ------------------------------------------------------------------------
    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------------
    // 3. GET ALL BOOKS FOR MEMBER
    // ------------------------------------------------------------------------
    public List<BrowseBookDTO> getBrowseBooks(Long memberId) {

        List<Book> books = bookRepository.findAll();
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + memberId));
        List<Transaction> transactions = transactionRepository.findByUser(user);

        Map<Long, TransactionStatus> bookStatusMap = new HashMap<>();

        for (Transaction tx : transactions) {
            TransactionStatus status = tx.getStatus();

            // consider only active states
            if (status == TransactionStatus.REQUESTED ||
                    status == TransactionStatus.BORROWED ||
                    status == TransactionStatus.RENEWED ||
                    status == TransactionStatus.RETURN_REQUESTED) {
                    //setting desired status for this endpoint
                    if(status == TransactionStatus.RENEWED ||
                            status == TransactionStatus.RETURN_REQUESTED)
                        status = TransactionStatus.BORROWED;
                bookStatusMap.put(tx.getBook().getId(), status);
            }
        }

        List<BrowseBookDTO> browseBooks = new ArrayList<>();

        for (Book book : books) {

            TransactionStatus status = bookStatusMap.get(book.getId());

            BrowseBookDTO dto = new BrowseBookDTO(
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getGenre(),
                    book.getPublishedYear(),
                    book.getImageName(),
                    book.getImageType(),
                    (book.getImageData() != null)
                            ? Base64.getEncoder().encodeToString(book.getImageData())
                            : null,
                    book.isActive(),
                    book.getAvailableCopies(),
                    (status == null) ? "NOT_REQUESTED" : status.name()
            );

            browseBooks.add(dto);
        }
        return  browseBooks;
    }

    // ------------------------------------------------------------------------
    // 4. UPDATE BOOK--working: active state cant update through this method
    // ------------------------------------------------------------------------
    public BookResponseDTO updateBook(Long id, BookRequestDTO dto) {
        Book book = getBookEntity(id);

        // ISBN should stay unique
        if (!book.getIsbn().equals(dto.getIsbn()) &&
                bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new ResourceAlreadyExistsException("ISBN already exists: " + dto.getIsbn());
        }

        bookMapper.updateEntity(book, dto);
        Book updated = bookRepository.save(book);
        return bookMapper.toDTO(updated);
    }

    // ------------------------------------------------------------------------
    // 5.DEACTIVATE BOOK
    // ------------------------------------------------------------------------
    public void deactivateBook(Long id) {
        Book book = getBookEntity(id);
        if(!book.isActive()){
            throw new ResourceDeactivatedException("Book Not Active");
        }
        book.setActive(false); // Soft delete-- active status changed
        bookRepository.save(book);
    }

    // ------------------------------------------------------------------------
    // 5.DELETE BOOK
    // ------------------------------------------------------------------------
    public void deleteBook(Long id) {
        Book book = getBookEntity(id);
        bookRepository.delete(book);
        // permanent delete
    }

    // ------------------------------------------------------------------------
    // 6. SEARCH BOOKS
    // ------------------------------------------------------------------------

    //by author + title
    @Transactional
    public List<BookResponseDTO> searchBooks(String keyword) {
        return bookRepository.searchBooks(keyword).stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public List<BookResponseDTO> filterByGenre(List<Genre> genres) {
        return bookRepository.findByGenreIn(genres).stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<BookResponseDTO> getActiveBooks() {
        return bookRepository.findByActiveTrue().stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }
}
