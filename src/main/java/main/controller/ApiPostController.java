package main.controller;

import main.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {
    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("")
    private ResponseEntity post(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                @RequestParam(name = "limit", defaultValue = "10") int limit,
                                @RequestParam(name = "mode") String mode) {
        return postService.getPosts(offset, limit, mode);
    }
    @GetMapping("/search")
    private ResponseEntity search(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                @RequestParam(name = "limit", defaultValue = "10") int limit,
                                @RequestParam(name = "query") String query) {
        return query.trim().equals("")?postService.getPosts(offset, limit, "recent"):postService.getSearchPosts(offset,limit,query.trim());
    }
}
