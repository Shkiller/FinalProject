package main.service;

import main.api.response.Post4PostResponse;
import main.api.response.PostsResponse;
import main.api.response.User4PostResponse;
import main.model.ModerationStatusType;
import main.repository.PostRepository;
import org.jsoup.Jsoup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final int MAX_TEXT_LENGTH = 150;
    private final int SECOND = 1000;
    private final byte LIKE = 1;
    private final byte DISLIKE = 0;
    private final byte ACTIVE_POST = 1;
    private final String TEXT_END = "...";

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public ResponseEntity getPosts(int offset, int limit, String mode) {
        List<Post4PostResponse> postList = new ArrayList<>();
        PostsResponse postsResponse = new PostsResponse();


        postRepository.findAll().forEach(p -> {
            if (p.getIsActive() == ACTIVE_POST && p.getModerationStatus().equals(ModerationStatusType.ACCEPTED) && p.getTime().compareTo(new Date()) < 1) {
                Post4PostResponse post = new Post4PostResponse();
                post.setId(p.getId());
                post.setTimestamp(p.getTime().getTime() / SECOND);
                User4PostResponse user = new User4PostResponse();
                user.setId(p.getUser().getId());
                user.setName(p.getUser().getName());
                post.setUser(user);
                post.setTitle(p.getTitle());
                String text = Jsoup.parse(p.getText()).text();
                post.setAnnounce(text.substring(0, Math.min(MAX_TEXT_LENGTH, text.length())) + TEXT_END);
                post.setLikeCount((int) p.getPostVotes().stream().filter(postVote -> postVote.getValue() == LIKE).count());
                post.setDislikeCount((int) p.getPostVotes().stream().filter(postVote -> postVote.getValue() == DISLIKE).count());
                post.setCommentCount(p.getPostComments().size());
                post.setViewCount(p.getViewCount());
                postList.add(post);
            }
        });
        Comparator<Post4PostResponse> comparatorRecent = Comparator.comparingLong(Post4PostResponse::getTimestamp).reversed();
        Comparator<Post4PostResponse> comparatorPopular = Comparator.comparingInt(Post4PostResponse::getCommentCount).reversed();
        Comparator<Post4PostResponse> comparatorBest = Comparator.comparingInt(Post4PostResponse::getLikeCount).reversed();
        Comparator<Post4PostResponse> comparatorEarly = Comparator.comparingLong(Post4PostResponse::getTimestamp);
        if (mode.equals("recent")) {
            postList.sort(comparatorRecent);
        } else {
            if (mode.equals("popular")) {
                postList.sort(comparatorPopular);
            } else {
                if (mode.equals("best")) {
                    postList.sort(comparatorBest);
                } else {
                    if (mode.equals("early")) {
                        postList.sort(comparatorEarly);
                    }
                }
            }
        }


        Post4PostResponse[] posts4PostResponse = postList.subList(offset, Math.min(offset + limit, postList.size())).toArray(new Post4PostResponse[0]);
        postsResponse.setCount(postList.size());
        postsResponse.setPosts(posts4PostResponse);

        return new ResponseEntity(postsResponse, HttpStatus.OK);
    }


}
