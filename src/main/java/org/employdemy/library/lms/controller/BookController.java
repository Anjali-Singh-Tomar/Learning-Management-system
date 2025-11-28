package org.employdemy.library.lms.controller;

import org.employdemy.library.lms.dto.BookRequestDTO;
import org.employdemy.library.lms.dto.BookResponseDTO;
import org.employdemy.library.lms.model.Genre;
import org.employdemy.library.lms.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // ----------------------------------------------------
    // 1. ADD BOOK-Done
    // ----------------------------------------------------
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping("/add")
    public ResponseEntity<BookResponseDTO> addBook(
            @Valid @RequestBody BookRequestDTO request) {

        return ResponseEntity.ok(bookService.addBook(request));
    }

    // ----------------------------------------------------
    // 2. GET ALL BOOKS-no change required
    // ----------------------------------------------------
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN','MEMBER')")
    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    // ----------------------------------------------------
    // 3. GET SINGLE BOOK
    // ----------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBook(id));
    }

    // ----------------------------------------------------
    // 4. UPDATE BOOK--working: active state cant update through this method
    // ----------------------------------------------------
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequestDTO dto
    ) {
        return ResponseEntity.ok(bookService.updateBook(id, dto));
    }

    // ----------------------------------------------------
    // 5. DELETE / DEACTIVATE BOOK--no change required
    // ----------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id); // soft delete
        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------
    // 7. SEARCH BY AUTHOR or TITLE -done
    // ----------------------------------------------------
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN','MEMBER')")
    @GetMapping("/search")
    public ResponseEntity<List<BookResponseDTO>> searchBooks(@RequestParam String keyword) {
        return ResponseEntity.ok(bookService.searchBooks(keyword));
    }

    // ----------------------------------------------------
    // 8. FILTER BY GENRE - no change required
    // ----------------------------------------------------
    @GetMapping("/filter/genre")
    public ResponseEntity<List<BookResponseDTO>> filterByGenre(@RequestParam Genre genre) {
        System.out.println("done");
        return ResponseEntity.ok(bookService.filterByGenre(genre));
    }

    // ----------------------------------------------------
    // 9. ACTIVE BOOKS ONLY
    // ----------------------------------------------------
    @GetMapping("/active")
    public ResponseEntity<List<BookResponseDTO>> getActiveBooks() {
        return ResponseEntity.ok(bookService.getActiveBooks());
    }
}
