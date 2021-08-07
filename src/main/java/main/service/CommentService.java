package main.service;

import main.api.request.CommentRequest;
import main.api.response.CommentResponse;
import main.exception.BadRequestException;
import main.model.PostComment;
import main.model.User;
import main.repository.PostCommentRepository;
import main.repository.PostRepository;
import main.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CommentService {
    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final int MIN_TEXT_LENGTH = 3;

    public CommentService(PostCommentRepository postCommentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository) {
        this.postCommentRepository = postCommentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<CommentResponse> commentAdd(CommentRequest commentRequest, Principal principal) throws BadRequestException {
        CommentResponse commentResponse = new CommentResponse();
        if (commentRequest.getText().length() <= MIN_TEXT_LENGTH) {
            Map<String, String> errors = new HashMap<>();
            errors.put("text", "Текст комментария не задан или слишком короткий");
            commentResponse.setErrors(errors);
            commentResponse.setResult(false);
            return new ResponseEntity<>(commentResponse, HttpStatus.BAD_REQUEST);
        } else {
            User currentUser = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
            PostComment postComment = new PostComment();
            postComment.setPost(postRepository
                    .findById(commentRequest.getPostId())
                    .orElseThrow(BadRequestException::new));
            if (commentRequest.getParentId()!=null) {
                postComment.setParent(postCommentRepository
                        .findById(commentRequest.getParentId())
                        .orElseThrow(BadRequestException::new));
            }
            postComment.setUser(currentUser);
            postComment.setTime(new Date());
            postComment.setText(commentRequest.getText());
            commentResponse.setId(postCommentRepository.save(postComment).getId());
            return new ResponseEntity<>(commentResponse, HttpStatus.OK);
        }

    }
}
