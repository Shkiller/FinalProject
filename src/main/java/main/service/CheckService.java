package main.service;

import main.api.response.CheckResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CheckService {

    public CheckResponse getCheck() {
        return new CheckResponse();
    }
}
