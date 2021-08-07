package main.controller;

import main.api.request.PostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.ResultResponse;
import main.api.response.post.PostAddResponse;
import main.api.response.post.PostIdResponse;
import main.api.response.post.PostsResponse;
import main.exception.EntityNotFoundException;
import main.service.PostService;
import main.service.PostVoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
public class ApiPostController {
    private final PostService postService;
    private final PostVoteService postVoteService;

    public ApiPostController(PostService postService, PostVoteService postVoteService) {
        this.postService = postService;
        this.postVoteService = postVoteService;
    }

    @GetMapping("/api/post")
    public ResponseEntity<PostsResponse> post(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                              @RequestParam(name = "limit", defaultValue = "10") int limit,
                                              @RequestParam(name = "mode") String mode) {
        return new ResponseEntity<>(postService.getPosts(offset, limit, mode), HttpStatus.OK);
    }

    @GetMapping("/api/post/search")
    public ResponseEntity<PostsResponse> postSearch(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                                    @RequestParam(name = "limit", defaultValue = "10") int limit,
                                                    @RequestParam(name = "query") String query) {
        return new ResponseEntity<>(postService.getSearch(offset, limit, query), HttpStatus.OK);
    }

    @GetMapping("/api/post/byDate")
    public ResponseEntity<PostsResponse> postByDate(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                                    @RequestParam(name = "limit", defaultValue = "10") int limit,
                                                    @RequestParam(name = "date") String date) {
        return new ResponseEntity<>(postService.getPostsByDate(offset, limit, date), HttpStatus.OK);
    }

    @GetMapping("/api/post/byTag")
    public ResponseEntity<PostsResponse> postByTag(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                                   @RequestParam(name = "limit", defaultValue = "10") int limit,
                                                   @RequestParam(name = "tag") String tag) {
        return new ResponseEntity<>(postService.getPostsByTag(offset, limit, tag), HttpStatus.OK);
    }

    @GetMapping("/api/post/{ID}")
    public ResponseEntity<PostIdResponse> postId(@PathVariable int ID, Principal principal) throws EntityNotFoundException {
        return new ResponseEntity<>(postService.getPostId(ID, principal), HttpStatus.OK);
    }

    @GetMapping("/api/post/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<PostsResponse> moderation(Principal principal,
                                                    @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                    @RequestParam(name = "limit", defaultValue = "10") int limit,
                                                    @RequestParam(name = "status") String status) {
        return new ResponseEntity<>(postService.getModeration(principal, offset, limit, status), HttpStatus.OK);
    }

    @GetMapping("/api/post/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostsResponse> my(Principal principal,
                                            @RequestParam(name = "offset", defaultValue = "0") int offset,
                                            @RequestParam(name = "limit", defaultValue = "10") int limit,
                                            @RequestParam(name = "status") String status) {
        return new ResponseEntity<>(postService.getMy(principal, offset, limit, status), HttpStatus.OK);
    }

    @PostMapping("/api/post")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostAddResponse> postAdd(@RequestBody PostRequest postRequest, Principal principal) {
        return new ResponseEntity<>(postService.postAdd(postRequest, principal), HttpStatus.OK);
    }

    @PutMapping("/api/post/{ID}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostAddResponse> postChange(@PathVariable int ID,
                                                      @RequestBody PostRequest postRequest,
                                                      Principal principal) throws EntityNotFoundException {
        return new ResponseEntity<>(postService.postUpdate(ID, postRequest, principal), HttpStatus.OK);
    }

    @PostMapping("/api/post/like")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> postLike(Principal principal, @RequestBody PostVoteRequest postId) throws EntityNotFoundException {
        return new ResponseEntity<>(postVoteService.addLike(postId, principal), HttpStatus.OK);
    }

    @PostMapping("/api/post/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> postDislike(Principal principal, @RequestBody PostVoteRequest postId) throws EntityNotFoundException {
        return new ResponseEntity<>(postVoteService.addDislike(postId, principal), HttpStatus.OK);
    }
}
