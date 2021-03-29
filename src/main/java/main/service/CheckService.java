package main.service;

import main.api.response.LoginResponse;
import main.api.response.UserLoginResponse;
import main.model.User;
import main.repository.PostRepository;
import main.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class CheckService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CheckService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public LoginResponse getCheck(Principal principal) {
        if(principal==null)
            return new LoginResponse();
        else{
            User currentUser = userRepository.findByEmail(principal.getName())
                    .orElseThrow(()->new UsernameNotFoundException(principal.getName()));
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setResult(true);
            UserLoginResponse userLoginResponse = new UserLoginResponse();
            userLoginResponse.setId(currentUser.getId());
            userLoginResponse.setEmail(currentUser.getEmail());
            userLoginResponse.setName(currentUser.getName());
            userLoginResponse.setPhoto(currentUser.getPhoto());
            if(currentUser.getIsModerator()==(byte)1){
                userLoginResponse.setModeration(true);
                userLoginResponse.setModerationCount(postRepository.findPostsByModerationStatus().size());
                userLoginResponse.setSettings(true);
            }
            else{
                userLoginResponse.setModeration(false);
                userLoginResponse.setModerationCount(0);
                userLoginResponse.setSettings(false);
            }
            loginResponse.setUserLoginResponse(userLoginResponse);
            return loginResponse;
        }
    }
}
