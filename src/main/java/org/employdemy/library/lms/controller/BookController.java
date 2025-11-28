package org.employdemy.library.lms.controller;

import org.employdemy.library.lms.dto.BookRequestDTO;
import org.employdemy.library.lms.dto.BookResponseDTO;
import org.employdemy.library.lms.model.Book;
import org.employdemy.library.lms.model.Genre;
import org.employdemy.library.lms.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    // ----------------------------------------------------
    @PostMapping(consumes = "multipart/form-data")
    public BookResponseDTO createBook(
            @RequestPart("data") BookRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {

        if (imageFile != null && !imageFile.isEmpty()) {
            dto.setImageName(imageFile.getOriginalFilename());
            dto.setImageType(imageFile.getContentType());
            dto.setImageData(imageFile.getBytes());
        }

        return bookService.createBook(dto);
    }

    //-----------------------------------------------------------
    // Get The Book image using Id
    //-------------------------------------------------------------
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getBookImage(@PathVariable Long id) {
        Book book = bookService.getBookEntity(id);

        return ResponseEntity.ok()
                .header("Content-Type", book.getImageType())
                .header("Content-Disposition", "inline; filename=\"" + book.getImageName() + "\"")
                .body(book.getImageData());
    }



    // ----------------------------------------------------
    // 2. GET ALL BOOKS
    // ----------------------------------------------------
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
    // 4. UPDATE BOOK
    // ----------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequestDTO dto
    ) {
        return ResponseEntity.ok(bookService.updateBook(id, dto));
    }

    // ----------------------------------------------------
    // 5. DELETE / DEACTIVATE BOOK
    // ----------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id); // soft delete
        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------
    // 6. SEARCH BY TITLE
    // ----------------------------------------------------
    @GetMapping("/search/title")
    public ResponseEntity<List<BookResponseDTO>> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.searchByTitle(title));
    }

    // ----------------------------------------------------
    // 7. SEARCH BY AUTHOR
    // ----------------------------------------------------
    @GetMapping("/search/author")
    public ResponseEntity<List<BookResponseDTO>> searchByAuthor(@RequestParam String author) {
        return ResponseEntity.ok(bookService.searchByAuthor(author));
    }

    // ----------------------------------------------------
    // 8. FILTER BY GENRE
    // ----------------------------------------------------
    @GetMapping("/filter/genre")
    public ResponseEntity<List<BookResponseDTO>> filterByGenre(@RequestParam Genre genre) {
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
