package main.service;

import main.api.request.LoginRequest;
import main.api.response.LoginResponse;
import main.api.response.LogoutResponse;
import main.api.response.UserLoginResponse;
import main.repository.PostRepository;
import main.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class LoginService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PostRepository postRepository;

    public LoginService(UserRepository userRepository, AuthenticationManager authenticationManager, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.postRepository = postRepository;
    }

    public LoginResponse login(LoginRequest loginRequest)
    {
        Authentication auth = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = (User) auth.getPrincipal();
        main.model.User currentUser = userRepository.findByEmail(user.getUsername())
                .orElseThrow(()->new UsernameNotFoundException(user.getUsername()));
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
    public LogoutResponse logout()
    {
        SecurityContextHolder.clearContext();
        LogoutResponse logoutResponse = new LogoutResponse();
        logoutResponse.setResult(true);
        return logoutResponse;
    }
}
