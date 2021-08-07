package main.service;

import main.api.request.PostVoteRequest;
import main.api.response.ResultResponse;
import main.exception.EntityNotFoundException;
import main.model.PostVote;
import main.model.User;
import main.repository.PostRepository;
import main.repository.PostVoteRepository;
import main.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

@Service
public class PostVoteService {
    private final PostVoteRepository postVoteRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public PostVoteService(PostVoteRepository voteRepository, UserRepository userRepository, PostRepository postRepository) {
        this.postVoteRepository = voteRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public ResultResponse addLike(PostVoteRequest postId, Principal principal) throws EntityNotFoundException {
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Optional<PostVote> postVoteOptional = postVoteRepository.findByPostId(postId.getPostId(), currentUser.getId());
        ResultResponse resultResponse = new ResultResponse();
        if (postVoteOptional.isEmpty()) {
            PostVote postVote = new PostVote();
            postVote.setPost(postRepository.findById(postId.getPostId()).orElseThrow(EntityNotFoundException::new));
            postVote.setUser(currentUser);
            postVote.setTime(new Date());
            postVote.setValue((byte) 1);
            postVoteRepository.save(postVote);
            resultResponse.setResult(true);
        } else {
            PostVote postVote = postVoteOptional.get();
            if (postVote.getValue() == (byte) 0) {

                postVote.setTime(new Date());
                postVote.setValue((byte) 1);
                postVoteRepository.save(postVote);
                resultResponse.setResult(true);
            }
        }
        return resultResponse;
    }

    public ResultResponse addDislike(PostVoteRequest postId, Principal principal) throws EntityNotFoundException {
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Optional<PostVote> postVoteOptional = postVoteRepository.findByPostId(postId.getPostId(), currentUser.getId());
        ResultResponse resultResponse = new ResultResponse();
        if (postVoteOptional.isEmpty()) {
            PostVote postVote = new PostVote();
            postVote.setPost(postRepository.findById(postId.getPostId()).orElseThrow(EntityNotFoundException::new));
            postVote.setUser(currentUser);
            postVote.setTime(new Date());
            postVote.setValue((byte) 0);
            postVoteRepository.save(postVote);
            resultResponse.setResult(true);
        } else {
            PostVote postVote = postVoteOptional.get();
            if (postVote.getValue() == (byte) 1) {
                postVote.setTime(new Date());
                postVote.setValue((byte) 0);
                postVoteRepository.save(postVote);
                resultResponse.setResult(true);
            }
        }
        return resultResponse;
    }
}
