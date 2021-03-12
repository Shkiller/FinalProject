package main.controller;

import main.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiPostController {
    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/api/post")
    private ResponseEntity post(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                @RequestParam(name = "limit", defaultValue = "10") int limit,
                                @RequestParam(name = "mode") String mode) {
        return new ResponseEntity(postService.getPosts(offset, limit, mode), HttpStatus.OK);
    }

}
