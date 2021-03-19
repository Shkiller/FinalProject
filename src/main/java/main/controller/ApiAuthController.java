package main.controller;

import main.api.request.RegisterRequest;
import main.service.CaptchaService;
import main.service.CheckService;
import main.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/auth/")
public class ApiAuthController {
    private final CheckService checkService;
    private final CaptchaService captchaService;
    private final RegisterService registerService;
    public ApiAuthController(CheckService checkService, CaptchaService captchaService, RegisterService registerService) {
        this.checkService = checkService;

        this.captchaService = captchaService;
        this.registerService = registerService;
    }

    @GetMapping("/check")
    private ResponseEntity check() {

        return new ResponseEntity(checkService.getCheck(), HttpStatus.OK);
    }
    @GetMapping("/captcha")
    private ResponseEntity captcha() throws IOException, NoSuchAlgorithmException {

        return new ResponseEntity(captchaService.getCaptcha(), HttpStatus.OK);
    }
    @PostMapping("/register")
    private ResponseEntity register(@RequestBody RegisterRequest request)  {

        return registerService.register(request);
    }
}
