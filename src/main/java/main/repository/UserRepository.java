package main.repository;

import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.email = ?1 ")
    Optional<User> findByEmail(String email);
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.code = ?1 ")
    Optional<User> findByCode(String code);
}
