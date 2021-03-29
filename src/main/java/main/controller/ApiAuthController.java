package main.controller;

import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import main.service.CaptchaService;
import main.service.CheckService;
import main.service.LoginService;
import main.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth/")
public class ApiAuthController {
    private final LoginService loginService;
    private final CheckService checkService;
    private final CaptchaService captchaService;
    private final RegisterService registerService;

    public ApiAuthController(LoginService loginService, CheckService checkService, CaptchaService captchaService, RegisterService registerService) {
        this.loginService = loginService;
        this.checkService = checkService;
        this.captchaService = captchaService;
        this.registerService = registerService;
    }
    @GetMapping("/logout")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity logout()
    {
        return new ResponseEntity(loginService.logout(),HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity(loginService.login(loginRequest), HttpStatus.OK);
    }

    @GetMapping("/check")
    public ResponseEntity check(Principal principal) {
        return new ResponseEntity(checkService.getCheck(principal), HttpStatus.OK);
    }

    @GetMapping("/captcha")
    public ResponseEntity captcha() throws IOException, NoSuchAlgorithmException {

        return new ResponseEntity(captchaService.getCaptcha(), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequest request) {

        return new ResponseEntity(registerService.register(request), HttpStatus.OK);
    }
}
