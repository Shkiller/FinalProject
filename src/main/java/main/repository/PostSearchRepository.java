package main.repository;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostSearchRepository extends PagingAndSortingRepository<Post, Integer> {
    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id and pvl.value = 1 " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time <= CURRENT_DATE() AND p.text LIKE  %?1% " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(pvl) DESC"
    )
    Page<Post> findPostsByTextContaining(String query,Pageable pageable);
    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id and pvl.value = 1 " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time <= CURRENT_DATE() AND DATE_FORMAT(p.time, '%Y-%m-%d') = DATE_FORMAT(?1, '%Y-%m-%d')" +
            "GROUP BY p.id "
    )
    Page<Post> findPostsByDate(String date, Pageable pageable);
    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id and pvl.value = 1 " +
            "RIGHT JOIN Tag2Post t2p on t2p.post = p.id " +
            "RIGHT JOIN Tag t on t.id = t2p.tag " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time <= CURRENT_DATE() AND t.name = ?1 " +
            "GROUP BY t2p.id "
    )
    Page<Post> findPostsByTagName(String tag, Pageable pageable);
}
