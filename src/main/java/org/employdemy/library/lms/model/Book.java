package org.employdemy.library.lms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "books", uniqueConstraints = @UniqueConstraint(columnNames = "isbn"))
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    private String author;

    @NotBlank
    @Column(unique = true)
    private String isbn;

    @Enumerated(EnumType.STRING)
    private Genre genre;   // Replaced category with genre

    private String publisher;

    private Integer publishedYear;

    @Min(0)
    private Integer totalCopies = 0;

    @Min(0)
    private Integer availableCopies = 0;

    private boolean active = true;

    private String imageName;
    private String imageType;

    @Lob
    private byte[] imageData;

    @Version
    private Long version;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    @PrePersist
    public void prePersist() {
        if (this.totalCopies == null) this.totalCopies = 0;
        if (this.availableCopies == null || this.availableCopies == 0) {
            this.availableCopies = this.totalCopies;
        }
    }
}
