package org.employdemy.library.lms.mapper;

import org.employdemy.library.lms.dto.BookRequestDTO;
import org.employdemy.library.lms.dto.BookResponseDTO;
import org.employdemy.library.lms.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    // Convert Entity → ResponseDTO
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
        return dto;
    }

    // Convert RequestDTO → Entity
    public Book toEntity(BookRequestDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setGenre(dto.getGenre());
        book.setPublisher(dto.getPublisher());
        book.setPublishedYear(dto.getPublishedYear());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getTotalCopies()); // initially equal
        book.setActive(true);
        return book;
    }

    // Update existing book from RequestDTO
    public void updateEntity(Book book, BookRequestDTO dto) {
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setGenre(dto.getGenre());
        book.setPublisher(dto.getPublisher());
        book.setPublishedYear(dto.getPublishedYear());
        book.setTotalCopies(dto.getTotalCopies());

        // adjust available copies if total increases
        if (dto.getTotalCopies() > book.getAvailableCopies()) {
            int diff = dto.getTotalCopies() - book.getTotalCopies();
            book.setAvailableCopies(book.getAvailableCopies() + diff);
        }
    }
}
