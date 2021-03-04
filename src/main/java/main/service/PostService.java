package main.service;

import main.api.response.Post4PostResponse;
import main.api.response.PostsResponse;
import main.api.response.User4PostResponse;
import main.model.ModerationStatusType;
import main.repository.PostRepository;
import main.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;


    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public ResponseEntity getPosts() {
        List<Post4PostResponse> postList = new ArrayList<>();
        PostsResponse postsResponse = new PostsResponse();


        postRepository.findAll().forEach(p -> {
            if (p.getIsActive() == 1 && p.getModerationStatus().equals(ModerationStatusType.ACCEPTED) && p.getTime().compareTo(new Date()) <1) {
                Post4PostResponse post = new Post4PostResponse();
                post.setId(p.getId());
                post.setTimestamp(p.getTime().getTime()/1000);
                User4PostResponse user = new User4PostResponse();
                user.setId(p.getUser().getId());
                user.setName(p.getUser().getName());
                post.setUser(user);
                post.setTitle(p.getTitle());
                post.setAnnounce(p.getText());
                post.setLikeCount((int) p.getPostVotes().stream().filter(postVote -> postVote.getValue() == 1).count());
                post.setDislikeCount((int) p.getPostVotes().stream().filter(postVote -> postVote.getValue() == -1).count());
                post.setCommentCount(p.getPostComments().size());
                post.setViewCount(p.getViewCount());
                postList.add(post);
            }
        });


        Post4PostResponse[] posts4PostResponse = postList.toArray(new Post4PostResponse[0]);
        postsResponse.setCount(posts4PostResponse.length);
        postsResponse.setPosts(posts4PostResponse);

        return new ResponseEntity(postsResponse, HttpStatus.OK);
    }


}
