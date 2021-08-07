package main.service;

import main.api.request.RegisterRequest;
import main.api.response.register.RegisterResponse;
import main.exception.RegisterClosedException;
import main.model.CaptchaCode;
import main.model.GlobalSetting;
import main.model.User;
import main.repository.CaptchaRepository;
import main.repository.SettingsRepository;
import main.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class RegisterService {
    private final UserRepository userRepository;
    private final CaptchaRepository captchaRepository;
    private final SettingsRepository settingsRepository;
    private final int PASS_LENGTH = 6;
    private final int MULTIUSER_MODE = 1;

    public RegisterService(UserRepository userRepository, CaptchaRepository captchaRepository, SettingsRepository settingsRepository) {
        this.userRepository = userRepository;
        this.captchaRepository = captchaRepository;
        this.settingsRepository = settingsRepository;
    }

    public RegisterResponse register(RegisterRequest request) throws RegisterClosedException {

        if (settingsRepository.findById(1).orElse(new GlobalSetting()).getValue().equals("NO"))
            throw new RegisterClosedException();
        Map<String, String> errors = new HashMap<>();
        CaptchaCode captchaCode = captchaRepository.findBySecretCode(request.getCaptchaSecret());
        if (request.getPassword().length() < PASS_LENGTH) {
            errors.put("password", "Пароль короче 6-ти символов");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }
        if (!request.getName().replaceAll("[1-9a-zA-Z]", "").equals("")) {
            errors.put("name", "Имя указано неверно");
        }
        if (!captchaCode.getCode().equals(request.getCaptcha())) {
            errors.put("captcha", "Код с картинки введён неверно");
        }
        if (errors.size() == 0) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
            captchaRepository.delete(captchaCode);
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRegistrationTime(new Date());
            user.setIsModerator((byte) 0);
            userRepository.save(user);
            RegisterResponse registerCompleteResponse = new RegisterResponse();
            registerCompleteResponse.setResult(true);
            return registerCompleteResponse;
        } else {
            RegisterResponse registerFailResponse = new RegisterResponse();
            registerFailResponse.setResult(false);
            registerFailResponse.setErrors(errors);
            return registerFailResponse;
        }
    }
}
