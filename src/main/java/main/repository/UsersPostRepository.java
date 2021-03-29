package main.repository;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UsersPostRepository extends PagingAndSortingRepository<Post, Integer> {
    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN User u ON u.id = p.moderator " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id and pvl.value = 1 " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.moderator.id = ?1 " +
            "GROUP BY p.id " +
            "ORDER BY p.time DESC"
    )
    Page<Post> findPostsByModeratorAccepted(int id, Pageable pageable);
    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN User u ON u.id = p.moderator " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id and pvl.value = 1 " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'DECLINED' AND p.moderator.id = ?1 " +
            "GROUP BY p.id " +
            "ORDER BY p.time DESC"
    )
    Page<Post> findPostsByModeratorDeclined(int id,Pageable pageable);
    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN User u ON u.id = p.moderator " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id and pvl.value = 1 " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'NEW' " +
            "GROUP BY p.id " +
            "ORDER BY p.time DESC"
    )
    Page<Post> findPostsByModerationStatus(Pageable pageable);
    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id and pvl.value = 1 " +
            "WHERE p.isActive = 0 AND p.user.id = ?1 " +
            "GROUP BY p.id " +
            "ORDER BY p.time DESC"
    )
    Page<Post> findPostsByInactive(int id, Pageable pageable);
    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id and pvl.value = 1 " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'NEW' AND p.user.id = ?1 " +
            "GROUP BY p.id " +
            "ORDER BY p.time DESC"
    )
    Page<Post> findPostsByPending(int id,Pageable pageable);
    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id and pvl.value = 1 " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'DECLINED' AND p.user.id = ?1 " +
            "GROUP BY p.id " +
            "ORDER BY p.time DESC"
    )
    Page<Post> findPostsByDeclined(int id,Pageable pageable);
    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id and pvl.value = 1 " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.user.id = ?1 " +
            "GROUP BY p.id " +
            "ORDER BY p.time DESC"
    )
    Page<Post> findPostsByAccepted(int id,Pageable pageable);
}
