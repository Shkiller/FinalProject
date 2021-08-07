package main.service;

import main.api.request.profile.ProfileImageRequest;
import main.api.request.profile.ProfileRequest;
import main.api.response.ResultErrorsResponse;
import main.exception.IncorrectFormatException;
import main.model.User;
import main.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final StorageService storageService;

    private final int PASS_LENGTH = 6;

    public ProfileService(UserRepository userRepository, StorageService storageService) {
        this.userRepository = userRepository;
        this.storageService = storageService;
    }

    public ResultErrorsResponse myProfile(ProfileImageRequest profileRequest, Principal principal) throws IOException, IncorrectFormatException {
        Map<String, String> errors = new HashMap<>();
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        if (profileRequest.getPassword() != null)
            if (profileRequest.getPassword().length() < PASS_LENGTH) {
                errors.put("password", "Пароль короче 6-ти символов");
            } else {
                currentUser.setPassword(profileRequest.getPassword());
            }
        if (profileRequest.getEmail() != null)
            if (!currentUser.getEmail().equals(profileRequest.getEmail()))
                if (userRepository.findByEmail(profileRequest.getEmail()).isPresent()) {
                    errors.put("email", "Этот e-mail уже зарегистрирован");
                } else {
                    currentUser.setEmail(profileRequest.getEmail());
                }
        if (profileRequest.getName() != null)
            if (!currentUser.getName().equals(profileRequest.getName()))
                if (!profileRequest.getName().replaceAll("[1-9a-zA-Z]", "").equals("")) {
                    errors.put("name", "Имя указано неверно");
                } else {
                    currentUser.setName(profileRequest.getName());
                }
        if (profileRequest.getPhoto() != null) {
            if (!profileRequest.getPhoto().getOriginalFilename().substring(profileRequest.getPhoto().getOriginalFilename().lastIndexOf('.')).equals(".png")
                    && !profileRequest.getPhoto().getOriginalFilename().substring(profileRequest.getPhoto().getOriginalFilename().lastIndexOf('.')).equals(".jpg")) {
                errors.put("image", "Отправлен файл не формата изображение jpg, png");
            } else if (profileRequest.getPhoto().getSize() > 5242880) {
                errors.put("image", "Размер файла превышает допустимый размер");
            } else currentUser.setPhoto(storageService.storeAvatar(profileRequest.getPhoto()));
        }
        if (errors.size() == 0) {
            userRepository.save(currentUser);
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

    public ResultErrorsResponse myProfile(ProfileRequest profileRequest, Principal principal) throws IOException, IncorrectFormatException {
        Map<String, String> errors = new HashMap<>();
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        if (profileRequest.getPassword() != null)
            if (profileRequest.getPassword().length() < PASS_LENGTH) {
                errors.put("password", "Пароль короче 6-ти символов");
            } else {
                currentUser.setPassword(profileRequest.getPassword());
            }
        if (profileRequest.getEmail() != null)
            if (!currentUser.getEmail().equals(profileRequest.getEmail()))
                if (userRepository.findByEmail(profileRequest.getEmail()).isPresent()) {
                    errors.put("email", "Этот e-mail уже зарегистрирован");
                } else {
                    currentUser.setEmail(profileRequest.getEmail());
                }
        if (profileRequest.getName() != null)
            if (!currentUser.getName().equals(profileRequest.getName()))
                if (!profileRequest.getName().replaceAll("[1-9a-zA-Z]", "").equals("")) {
                    errors.put("name", "Имя указано неверно");
                } else {
                    currentUser.setName(profileRequest.getName());
                }
        if (profileRequest.getRemovePhoto() == 1) {
            Files.delete(Path.of(currentUser.getPhoto()));
            currentUser.setPhoto(null);
        }
        if (errors.size() == 0) {
            userRepository.save(currentUser);
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
}
