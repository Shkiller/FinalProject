package main.controller;

import main.exception.EntityNotFoundException;
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
    @GetMapping("/api/post/search")
    private ResponseEntity postSearch(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                @RequestParam(name = "limit", defaultValue = "10") int limit,
                                @RequestParam(name = "query") String query) {
        return new ResponseEntity(postService.getSearch(offset, limit, query), HttpStatus.OK);
    }
    @GetMapping("/api/post/byDate")
    private ResponseEntity postByDate(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                      @RequestParam(name = "limit", defaultValue = "10") int limit,
                                      @RequestParam(name = "date") String date) {
        return new ResponseEntity(postService.getPostsByDate(offset, limit, date), HttpStatus.OK);
    }
    @GetMapping("/api/post/byTag")
    private ResponseEntity postByTag(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                      @RequestParam(name = "limit", defaultValue = "10") int limit,
                                      @RequestParam(name = "tag") String tag) {
        return new ResponseEntity(postService.getPostsByTag(offset, limit, tag), HttpStatus.OK);
    }
    @GetMapping("/api/post/{ID}")
    private ResponseEntity postId(@PathVariable int ID) throws EntityNotFoundException {
        return new ResponseEntity(postService.getPostId(ID), HttpStatus.OK);
    }
}
