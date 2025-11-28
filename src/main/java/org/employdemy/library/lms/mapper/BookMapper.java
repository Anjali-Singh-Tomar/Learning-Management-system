package org.employdemy.library.lms.mapper;

import org.employdemy.library.lms.dto.BookRequestDTO;
import org.employdemy.library.lms.dto.BookResponseDTO;
import org.employdemy.library.lms.model.Book;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class BookMapper {

    // --------------------------------------------------------
    // ENTITY → RESPONSE DTO
    // --------------------------------------------------------
    public BookResponseDTO toDTO(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setGenre(book.getGenre());
        dto.setPublisher(book.getPublisher());
        dto.setPublishedYear(book.getPublishedYear());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setAvailableCopies(book.getAvailableCopies());
        dto.setActive(book.isActive());

        // ⭐ NEW IMAGE HANDLING
        dto.setImageName(book.getImageName());
        dto.setImageType(book.getImageType());

        if (book.getImageData() != null) {
            dto.setImageBase64(Base64.getEncoder()
                    .encodeToString(book.getImageData()));
        }

        return dto;
    }

    // --------------------------------------------------------
    // REQUEST DTO → ENTITY
    // --------------------------------------------------------
    public Book toEntity(BookRequestDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setGenre(dto.getGenre());
        book.setPublisher(dto.getPublisher());
        book.setPublishedYear(dto.getPublishedYear());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getTotalCopies()); // initially available
        book.setActive(true);

        // ⭐ NEW IMAGE FIELDS
        book.setImageName(dto.getImageName());
        book.setImageType(dto.getImageType());
        book.setImageData(dto.getImageData());

        return book;
    }

    // --------------------------------------------------------
    // UPDATE ENTITY from REQUEST DTO
    // --------------------------------------------------------
    public void updateEntity(Book book, BookRequestDTO dto) {
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setGenre(dto.getGenre());
        book.setPublisher(dto.getPublisher());
        book.setPublishedYear(dto.getPublishedYear());

        // Update total/available copies logic
        if (dto.getTotalCopies() != null) {
            int oldTotal = book.getTotalCopies();
            int newTotal = dto.getTotalCopies();

            if (newTotal > oldTotal) {
                int diff = newTotal - oldTotal;
                book.setAvailableCopies(book.getAvailableCopies() + diff);
            }

            book.setTotalCopies(newTotal);
        }

        // ⭐ UPDATE IMAGE ONLY IF NEW IMAGE PROVIDED
        if (dto.getImageData() != null) {
            book.setImageName(dto.getImageName());
            book.setImageType(dto.getImageType());
            book.setImageData(dto.getImageData());
        }
    }
}
