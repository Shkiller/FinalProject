package main.service;

import main.api.response.Post4PostResponse;
import main.api.response.PostsResponse;
import main.api.response.User4PostResponse;
import main.model.Post;
import main.repository.PostSortingRepository;
import org.jsoup.Jsoup;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostService {
    private final PostSortingRepository postSortingRepository;
    private final int MAX_TEXT_LENGTH = 150;
    private final int SECOND = 1000;
    private final byte LIKE = 1;
    private final byte DISLIKE = 0;
    private final String TEXT_END = "...";

    public PostService(PostSortingRepository postSortingRepository) {
        this.postSortingRepository = postSortingRepository;
    }

    public PostsResponse getPosts(int offset, int limit, String mode) {
        List<Post4PostResponse> post4PostResponseList = new ArrayList<>();
        PostsResponse postsResponse = new PostsResponse();
        Pageable pageable = PageRequest.of(offset/limit,limit);
        List<Post> postsList = new ArrayList<>();
        if (mode.equals("recent")) {
            postsList = postSortingRepository.findPostsOrderRecentTime(pageable).toList();
        } else {
            if (mode.equals("popular")) {
                postsList = postSortingRepository.findPostsOrderByComments(pageable).toList();
            } else {
                if (mode.equals("best")) {
                    postsList = postSortingRepository.findPostsOrderByLikes(pageable).toList();
                } else {
                    if (mode.equals("early")) {
                        postsList = postSortingRepository.findPostsOrderByEarlyTime(pageable).toList();
                    }
                }
            }
        }

       postsList.forEach(p -> {
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
                post4PostResponseList.add(post);
        });

        Post4PostResponse[] posts4PostResponse = post4PostResponseList.toArray(new Post4PostResponse[0]);
        postsResponse.setCount((int)postSortingRepository.findPostsOrderByLikes(pageable).getTotalElements());
        postsResponse.setPosts(posts4PostResponse);

        return postsResponse;
    }


}
