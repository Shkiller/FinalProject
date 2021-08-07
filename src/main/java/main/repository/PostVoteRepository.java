package main.repository;

import main.model.PostVote;
import main.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PostVoteRepository extends CrudRepository<PostVote, Integer> {
    @Query("SELECT pv " +
            "FROM PostVote pv " +
            "WHERE pv.post.id = ?1 and pv.user.id = ?2 ")
    Optional<PostVote> findByPostId(int postId, int userId);
    @Query("SELECT COUNT(*) " +
            "FROM PostVote " +
            "WHERE user.id = ?1 AND value = 1 " +
            "GROUP BY user ")
    Optional<Integer> findLikesByUser(int id);
    @Query("SELECT COUNT(*) " +
            "FROM PostVote " +
            "WHERE user.id = ?1 AND value = 0 " +
            "GROUP BY user ")
    Optional<Integer> findDislikesByUser(int id);
    @Query("SELECT COUNT(*) " +
            "FROM PostVote " +
            "WHERE value = 1 ")
    Optional<Integer> findLikesCount();
    @Query("SELECT COUNT(*) " +
            "FROM PostVote " +
            "WHERE value = 0 ")
    Optional<Integer> findDislikesCount();
}
