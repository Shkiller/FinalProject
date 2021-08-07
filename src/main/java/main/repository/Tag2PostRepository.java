package main.repository;

import main.model.Tag2Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface Tag2PostRepository extends CrudRepository<Tag2Post,Integer> {
    @Query("SELECT t2p " +
            "FROM Tag2Post t2p " +
            "WHERE t2p.post.id = ?1"
    )
    List<Tag2Post> findByPost(int id);
}
