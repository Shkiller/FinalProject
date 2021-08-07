package main.service;

import main.api.request.ModerationRequest;
import main.api.response.ModerationResponse;
import main.model.ModerationStatusType;
import main.model.Post;
import main.model.User;
import main.repository.PostRepository;
import main.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class ModerationService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final String ACCEPT = "accept";

    public ModerationService(UserRepository userRepository,
                             PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public ModerationResponse moderation(ModerationRequest moderationRequest, Principal principal) {
        ModerationResponse moderationResponse = new ModerationResponse();
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Optional<Post> postOptional = postRepository.findById(moderationRequest.getPostId());
        if(postOptional.isEmpty())
            return moderationResponse;
        Post post = postOptional.get();
        post.setModerator(currentUser);
        if (moderationRequest.getDecision().equals(ACCEPT)) {
            post.setModerationStatus(ModerationStatusType.ACCEPTED);
        } else {
            post.setModerationStatus(ModerationStatusType.DECLINED);
        }
        postRepository.save(post);
        moderationResponse.setResult(true);
        return moderationResponse;
    }
}
