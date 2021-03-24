package main.service;

import main.api.request.LoginRequest;
import main.api.response.LoginResponse;
import main.api.response.UserLoginResponse;
import main.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public LoginService(UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
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
        userLoginResponse.setModeration(currentUser.getIsModerator()==(byte)1);
        loginResponse.setUserLoginResponse(userLoginResponse);
        return loginResponse;
    }
}
