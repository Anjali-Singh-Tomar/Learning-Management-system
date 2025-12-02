package org.employdemy.library.lms.controller;

import jakarta.annotation.security.PermitAll;
import org.employdemy.library.lms.dto.BookRequestDTO;
import org.employdemy.library.lms.dto.BookResponseDTO;
import org.employdemy.library.lms.model.Book;
import org.employdemy.library.lms.model.Genre;
import org.employdemy.library.lms.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@CrossOrigin
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // ----------------------------------------------------
    // 1. CREATE BOOK
    // ----------------------------------------------------
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> createBook(
            @RequestPart("data") BookRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                dto.setImageName(imageFile.getOriginalFilename());
                dto.setImageType(imageFile.getContentType());
                dto.setImageData(imageFile.getBytes());
            }

            return ResponseEntity.ok(bookService.addBook(dto));

        } catch (IOException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "error", "Invalid image format"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to create book"
            ));
        }
    }

    // ----------------------------------------------------
    // 2. GET BOOK IMAGE
    // ----------------------------------------------------
    @GetMapping("/{id}/image")
    @PermitAll
    public ResponseEntity<?> getBookImage(@PathVariable Long id) {
        try {
            Book book = bookService.getBookEntity(id);

            if (book.getImageData() == null) {
                return ResponseEntity.status(404).body(Map.of(
                        "message", "No image found for this book"
                ));
            }

            return ResponseEntity.ok()
                    .header("Content-Type", book.getImageType())
                    .header("Content-Disposition", "inline; filename=\"" + book.getImageName() + "\"")
                    .body(book.getImageData());

        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", "Book not found"
            ));
        }
    }

    // ----------------------------------------------------
    // 3. GET ALL BOOKS
    // ----------------------------------------------------
    @GetMapping
    @PermitAll
    public ResponseEntity<?> getAllBooks() {
        try {
            return ResponseEntity.ok(bookService.getAllBooks());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to load books"
            ));
        }
    }

    // ----------------------------------------------------
    // 4. GET SINGLE BOOK
    // ----------------------------------------------------
    @GetMapping("/{id}")
    @PermitAll
    public ResponseEntity<?> getBook(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookService.getBook(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", "Book not found"
            ));
        }
    }

    // ----------------------------------------------------
    // 5. UPDATE BOOK
    // ----------------------------------------------------
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequestDTO dto
    ) {
        try {
            return ResponseEntity.ok(bookService.updateBook(id, dto));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of(
                    "error", "Failed to update book"
            ));
        }
    }

    // ----------------------------------------------------
    // 6. DELETE BOOK
    // ----------------------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Book deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", "Book not found"
            ));
        }
    }

    // ----------------------------------------------------
    // 7. SEARCH BY TITLE or AUTHOR
    // ----------------------------------------------------
    @GetMapping("/search")
    @PermitAll
    public ResponseEntity<?> searchBooks(@RequestParam String keyword) {
        try {
            List<BookResponseDTO> results = bookService.searchBooks(keyword);

            if (results.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "message", "No books found for search: " + keyword
                ));
            }

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Search failed"
            ));
        }
    }

    // ----------------------------------------------------
    // 8. FILTER BY GENRE
    // ----------------------------------------------------
    @GetMapping("/filter/genre")
    @PermitAll
    public ResponseEntity<?> filterByGenre(@RequestParam Genre genre) {
        try {
            return ResponseEntity.ok(bookService.filterByGenre(genre));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to filter by genre"
            ));
        }
    }

    // ----------------------------------------------------
    // 9. ACTIVE BOOKS ONLY
    // ----------------------------------------------------
    @GetMapping("/active")
    @PermitAll
    public ResponseEntity<?> getActiveBooks() {
        try {
            return ResponseEntity.ok(bookService.getActiveBooks());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to load active books"
            ));
        }
    }
}
