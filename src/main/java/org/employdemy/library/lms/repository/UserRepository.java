package org.employdemy.library.lms.repository;

import org.employdemy.library.lms.model.Book;
import org.employdemy.library.lms.model.Role;
import org.employdemy.library.lms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmpId(String empId);

    long countByActiveTrue();
    List<User> findByRole(Role role);

    @Query("""
    SELECT u
    FROM User u
    WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(u.empId) LIKE LOWER(CONCAT('%', :keyword, '%'))
""")
    List<User> searchUsers(@Param("keyword") String keyword);

    @Query("""
    SELECT u
    FROM User u
    WHERE u.active = :active
""")
    List<User> findUsersByActiveStatus(@Param("active") boolean active);
}
