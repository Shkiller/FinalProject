package main.controller;

import main.api.request.LoginRequest;
import main.api.request.PasswordRequest;
import main.api.request.RegisterRequest;
import main.api.request.RestoreRequest;
import main.api.response.*;
import main.api.response.register.RegisterResponse;
import main.exception.RegisterClosedException;
import main.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Properties;

@RestController
@RequestMapping("/api/auth/")
public class ApiAuthController {
    private final LoginService loginService;
    private final CheckService checkService;
    private final CaptchaService captchaService;
    private final RegisterService registerService;
    private final PasswordService passwordService;

    public ApiAuthController(LoginService loginService,
                             CheckService checkService,
                             CaptchaService captchaService,
                             RegisterService registerService,
                             PasswordService passwordService) {
        this.loginService = loginService;
        this.checkService = checkService;
        this.captchaService = captchaService;
        this.registerService = registerService;
        this.passwordService = passwordService;
    }
//    @GetMapping("/logout")
//    @PreAuthorize("hasAuthority('user:write')")
//    public ResponseEntity<LogoutResponse> logout()
//    {
//        return new ResponseEntity<>(loginService.logout(),HttpStatus.OK);
//    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(loginService.login(loginRequest), HttpStatus.OK);
    }

    @GetMapping("/check")
    public ResponseEntity<LoginResponse> check(Principal principal) {
        return new ResponseEntity<>(checkService.getCheck(principal), HttpStatus.OK);
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> captcha() throws IOException, NoSuchAlgorithmException {

        return new ResponseEntity<>(captchaService.getCaptcha(), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) throws RegisterClosedException {
        return new ResponseEntity<>(registerService.register(request), HttpStatus.OK);
    }
    @PostMapping("/restore")
    public ResponseEntity<ResultResponse> restore(@RequestBody RestoreRequest request) {

        return new ResponseEntity<>(passwordService.restore(request), HttpStatus.OK);
    }
    @PostMapping("/password")
    public ResponseEntity<ResultErrorsResponse> password(@RequestBody PasswordRequest request) {

        return new ResponseEntity<>(passwordService.password(request), HttpStatus.OK);
    }
}
