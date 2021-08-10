package main.service;

import main.api.request.PasswordRequest;
import main.api.request.RestoreRequest;
import main.api.response.ResultErrorsResponse;
import main.api.response.ResultResponse;
import main.model.CaptchaCode;
import main.model.User;
import main.repository.CaptchaRepository;
import main.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Service
public class PasswordService {
    private final UserRepository userRepository;
    private final JavaMailSender sender;
    private final CaptchaRepository captchaRepository;

    private final int PASS_LENGTH = 6;
    private final int LENGTH = 16;
    public static final String PATH_TO_PROPERTIES = "src/main/resources/application.yml";
    private final String URL = "http://localhost:8080/login/change-password/";
    private final String SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "0123456789"
            + "abcdefghijklmnopqrstuvxyz";

    public PasswordService(UserRepository userRepository, JavaMailSender sender, CaptchaRepository captchaRepository) {
        this.userRepository = userRepository;
        this.sender = sender;
        this.captchaRepository = captchaRepository;
    }

    public ResultResponse restore(RestoreRequest restoreRequest) throws IOException {
        Optional<User> currentUser = userRepository.findByEmail(restoreRequest.getEmail());
        if (currentUser.isEmpty()) {
            return new ResultResponse();
        } else {
            StringBuilder sb = new StringBuilder(LENGTH);
            for (int i = 0; i < LENGTH; i++) {
                int index = (int) (SYMBOLS.length() * Math.random());
                sb.append(SYMBOLS.charAt(index));
            }
            User user = currentUser.get();
            final SimpleMailMessage simpleMail = new SimpleMailMessage();
            simpleMail.setFrom("senderEmailDev1@gmail.com");
            simpleMail.setTo(restoreRequest.getEmail());
            simpleMail.setSubject("Restore password");
            String url = getURL();
            simpleMail.setText(url + sb);
            user.setCode(sb.toString());
            userRepository.save(user);
            sender.send(simpleMail);
            ResultResponse response = new ResultResponse();
            response.setResult(true);
            return response;
        }
    }



    public ResultErrorsResponse password(PasswordRequest passwordRequest) throws IOException {
        Map<String, String> errors = new HashMap<>();
        Optional<User> currentUser = userRepository.findByCode(passwordRequest.getCode());
        CaptchaCode captchaCode = captchaRepository.findBySecretCode(passwordRequest.getCaptchaSecret());
        if (passwordRequest.getPassword().length() < PASS_LENGTH) {
            errors.put("password", "Пароль короче 6-ти символов");
        }

        if (currentUser.isEmpty()) {
            errors.put("code", "Ссылка для восстановления пароля устарела.\n" +
                    "<a href= \n" +
                    "\""+getURL()+"\">Запросить ссылку снова</a>");
        }
        if (!captchaCode.getCode().equals(passwordRequest.getCaptcha())) {
            errors.put("captcha", "Код с картинки введён неверно");

        }
        if (errors.size() == 0) {
            captchaRepository.delete(captchaCode);
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
            User user = currentUser.get();
            user.setCode(null);
            user.setPassword(passwordEncoder.encode(passwordRequest.getPassword()));
            userRepository.save(user);
            ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
            resultErrorsResponse.setResult(true);
            return resultErrorsResponse;
        } else {
            ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
            resultErrorsResponse.setResult(false);
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
    }
    private String getURL() throws IOException {
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
        prop.load(fileInputStream);
        String url = prop.getProperty("password.url");
        return url;
    }
}
