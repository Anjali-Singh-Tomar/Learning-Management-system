package org.employdemy.library.lms.service;

import lombok.AllArgsConstructor;
import org.employdemy.library.lms.dto.BookRequestDTO;
import org.employdemy.library.lms.dto.BookResponseDTO;
import org.employdemy.library.lms.exception.ResourceAlreadyExistsException;
import org.employdemy.library.lms.exception.ResourceNotFoundException;
import org.employdemy.library.lms.mapper.BookMapper;
import org.employdemy.library.lms.model.Book;
import org.employdemy.library.lms.model.Genre;
import org.employdemy.library.lms.model.Role;
import org.employdemy.library.lms.model.User;
import org.employdemy.library.lms.repository.BookRepository;
import org.employdemy.library.lms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService;


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
                    "A new book '" + savedBook.getTitle() + "' has been added."
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
    // 5. DELETE / DEACTIVATE BOOK
    // ------------------------------------------------------------------------
    public void deleteBook(Long id) {
        Book book = getBookEntity(id);

        book.setActive(false); // Soft delete
        bookRepository.save(book);
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
    public List<BookResponseDTO> filterByGenre(Genre genre) {
        return bookRepository.findByGenre(genre).stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<BookResponseDTO> getActiveBooks() {
        return bookRepository.findByActiveTrue().stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }
}
