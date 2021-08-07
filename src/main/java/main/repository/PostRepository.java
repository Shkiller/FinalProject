package main.repository;

import main.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id and pvl.value = 1 " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time <= CURRENT_DATE() " +
            "GROUP BY p.id "
    )
    List<Post> findPosts();

    @Query("SELECT p " +
            "FROM Post p " +
            "WHERE p.moderationStatus = 'NEW'"
    )
    List<Post> findPostsByModerationStatus();

    @Query("SELECT SUM(viewCount) " +
            "FROM Post " +
            "WHERE user.id = ?1 " +
            "GROUP BY user ")
    Optional<Integer> findViewsCountByUser(int id);
    @Query("SELECT COUNT(*) " +
            "FROM Post " +
            "WHERE user.id = ?1 " +
            "GROUP BY user ")
    Optional<Integer> findPostCountByUser(int id);
    @Query("SELECT MIN(time) " +
            "FROM Post " +
            "WHERE user.id = ?1 " +
            "GROUP BY user ")
    Optional<Date> findLatestPostByUser(int id);
    @Query("SELECT SUM(viewCount) " +
            "FROM Post ")
    Optional<Integer> findViewsCount();
    @Query("SELECT COUNT(*) " +
            "FROM Post ")
    Optional<Integer> findPostCount();
    @Query("SELECT MIN(time) " +
            "FROM Post ")
    Optional<Date> findLatestPost();
}
