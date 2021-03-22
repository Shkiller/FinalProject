package main.repository;

import main.model.Post;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;




@Repository
public interface PostSortingRepository extends PagingAndSortingRepository<Post, Integer> {
    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id and pvl.value = 1 " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time <= CURRENT_DATE() " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(pvl) DESC"
    )
    Page<Post> findPostsOrderByLikes(Pageable pageable);
    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id and pvl.value = 1 " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time <= CURRENT_DATE() " +
            "GROUP BY p.id " +
            "ORDER BY p.time DESC"
    )
    Page<Post> findPostsOrderRecentTime(Pageable pageable);
    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id and pvl.value = 1 " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time <= CURRENT_DATE() " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(pc) DESC"
    )
    Page<Post> findPostsOrderByComments(Pageable pageable);
    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id and pvl.value = 1 " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time <= CURRENT_DATE() " +
            "GROUP BY p.id " +
            "ORDER BY p.time"
    )
    Page<Post> findPostsOrderByEarlyTime(Pageable pageable);
}