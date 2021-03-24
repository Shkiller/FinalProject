package main.service;

import main.api.response.LoginResponse;
import main.api.response.UserLoginResponse;
import main.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class CheckService {
    private final UserRepository userRepository;

    public CheckService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponse getCheck(Principal principal) {
        if(principal==null)
            return new LoginResponse();
        else{
            main.model.User currentUser = userRepository.findByEmail(principal.getName())
                    .orElseThrow(()->new UsernameNotFoundException(principal.getName()));
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
}
