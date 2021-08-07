package main.controller;

import main.api.response.ResultErrorsResponse;
import main.exception.IncorrectFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;


import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class DefaultAdvice {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResultErrorsResponse> handleStorageFileNotFound(MaxUploadSizeExceededException exc) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        Map<String,String> errors = new HashMap<>();
        errors.put("image","Размер файла превышает допустимый размер");
        resultErrorsResponse.setErrors(errors);
        return new ResponseEntity<>(resultErrorsResponse, HttpStatus.BAD_REQUEST);
    }
        @ExceptionHandler(IncorrectFormatException.class)
    public ResponseEntity<ResultErrorsResponse> handleStorageFileNotFound(IncorrectFormatException exc) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        Map<String,String> errors = new HashMap<>();
        errors.put("image","Отправлен файл не формата изображение jpg, png");
        resultErrorsResponse.setErrors(errors);
        return new ResponseEntity<>(resultErrorsResponse, HttpStatus.BAD_REQUEST);
    }
}
