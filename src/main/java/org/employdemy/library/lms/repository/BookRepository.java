package org.employdemy.library.lms.repository;

import org.employdemy.library.lms.model.Book;
import org.employdemy.library.lms.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Find book by ISBN (unique)
    Book findByIsbn(String isbn);

    // Search by title (case insensitive)
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Search by author
    List<Book> findByAuthorContainingIgnoreCase(String author);

    // Filter by genre
    List<Book> findByGenre(Genre genre);

    // Active books only
    List<Book> findByActiveTrue();

    // Check if ISBN already exists
    boolean existsByIsbn(String isbn);

    @Query("SELECT b FROM Book b " +
            "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchBooks(@Param("keyword") String keyword);

    List<Book> findByGenreOrderByPublishedYearDesc(Genre genre);
}
