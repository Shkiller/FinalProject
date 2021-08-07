package main.repository;

import main.model.Post;
import main.model.Tag;
import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends CrudRepository<Tag, Integer> {
    @Query("SELECT t " +
            "FROM Tag t " +
            "WHERE t.name = ?1"
    )
    Optional<Tag> findTagByName(String name);
}
