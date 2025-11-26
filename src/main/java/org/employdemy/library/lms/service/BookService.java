package org.employdemy.library.lms.service;

import org.employdemy.library.lms.dto.BookRequestDTO;
import org.employdemy.library.lms.dto.BookResponseDTO;
import org.employdemy.library.lms.exception.ResourceAlreadyExistsException;
import org.employdemy.library.lms.exception.ResourceNotFoundException;
import org.employdemy.library.lms.mapper.BookMapper;
import org.employdemy.library.lms.model.Book;
import org.employdemy.library.lms.model.Genre;
import org.employdemy.library.lms.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    // ------------------------------------------------------------------------
    // 1. CREATE BOOK
    // ------------------------------------------------------------------------
    public BookResponseDTO createBook(BookRequestDTO dto) {

        if (bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new ResourceAlreadyExistsException("Book already exists with ISBN: " + dto.getIsbn());
        }

        Book book = bookMapper.toEntity(dto);
        Book saved = bookRepository.save(book);
        return bookMapper.toDTO(saved);
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
    // 3. GET ALL BOOKS
    // ------------------------------------------------------------------------
    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------------
    // 4. UPDATE BOOK
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
    public List<BookResponseDTO> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<BookResponseDTO> searchByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author).stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }

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
