package main.repository;

import main.model.CaptchaCode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaptchaRepository extends JpaRepository<CaptchaCode, Integer> {
    @Query("SELECT c " +
            "FROM CaptchaCode c " +
            "WHERE c.code = ?1 ")
    Optional<CaptchaCode> findByCode (String code);
    @Query("SELECT c " +
            "FROM CaptchaCode c " +
            "WHERE c.secretCode = ?1 ")
    CaptchaCode findBySecretCode (String secretCode);
}
