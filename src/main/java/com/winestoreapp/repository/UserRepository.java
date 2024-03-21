package com.winestoreapp.repository;

import com.winestoreapp.model.RoleName;
import com.winestoreapp.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    //    @Query("FROM User u LEFT JOIN FETCH u.roles r WHERE u.email =
    //    :email AND u.isDeleted = FALSE "
    //            + "AND r.isDeleted = FALSE")
    //    Optional<User> findUserByEmail(String email);

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findUserByEmail(String email);

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findById(Long id);

    Optional<User> findFirstByFirstNameAndLastName(String firstName, String lastName);

    Optional<User> findFirstByFirstNameAndLastNameAndPhoneNumber(
            String firstName, String lastName, String phoneNumber);

    Optional<User> findUserByTelegramChatId(Long telegramChatId);

    @Query("""
            SELECT u FROM User u JOIN u.roles r 
            WHERE r.name = :roleName AND u.isDeleted = FALSE AND r.isDeleted = FALSE""")
    List<User> findUsersByRole(@Param("roleName") RoleName roleName);
}
