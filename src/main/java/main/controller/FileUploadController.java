package main.controller;

import main.exception.IncorrectFormatException;
import main.service.StorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;


import java.io.IOException;


@Controller
public class FileUploadController {
    private final StorageService storageService;

    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/api/image")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<String> handleFileUpload(@RequestParam("image") MultipartFile file) throws IOException, IncorrectFormatException {
        return new ResponseEntity<>(storageService.store(file), HttpStatus.OK);
    }

        @Bean
        public MultipartResolver multipartResolver() {
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
            //multipartResolver.setMaxUploadSize(5242880);
            return multipartResolver;
        }

}
