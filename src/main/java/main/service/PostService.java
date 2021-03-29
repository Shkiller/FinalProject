package main.service;

import main.api.response.post.*;
import main.exception.EntityNotFoundException;
import main.model.Post;
import main.model.User;
import main.repository.*;
import org.jsoup.Jsoup;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

@Service
public class PostService {
    private final PostSearchRepository postSearchRepository;
    private final UsersPostRepository usersPostRepository;
    private final PostSortingRepository postSortingRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final int MAX_TEXT_LENGTH = 150;
    private final int SECOND = 1000;
    private final byte LIKE = 1;
    private final byte DISLIKE = 0;
    private final String TEXT_END = "...";

    public PostService(PostSearchRepository postSearchService,
                       UsersPostRepository usersPostRepository, PostSortingRepository postSortingRepository,
                       PostRepository postRepository,
                       UserRepository userRepository) {
        this.postSearchRepository = postSearchService;
        this.usersPostRepository = usersPostRepository;
        this.postSortingRepository = postSortingRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public PostsResponse getPosts(int offset, int limit, String mode) {
        List<Post4PostResponse> post4PostResponseList = new ArrayList<>();
        PostsResponse postsResponse = new PostsResponse();
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> postsList = new ArrayList<>();
        if (mode.equals("recent")) {
            postsList = postSortingRepository.findPostsOrderRecentTime(pageable).toList();
        } else {
            if (mode.equals("popular")) {
                postsList = postSortingRepository.findPostsOrderByComments(pageable).toList();
            } else {
                if (mode.equals("best")) {
                    postsList = postSortingRepository.findPostsOrderByLikes(pageable).toList();
                } else {
                    if (mode.equals("early")) {
                        postsList = postSortingRepository.findPostsOrderByEarlyTime(pageable).toList();
                    }
                }
            }
        }
        postsList.forEach(p -> {
            Post4PostResponse post = new Post4PostResponse();
            post.setId(p.getId());
            post.setTimestamp(p.getTime().getTime() / SECOND);
            User4PostResponse user = new User4PostResponse();
            user.setId(p.getUser().getId());
            user.setName(p.getUser().getName());
            post.setUser(user);
            post.setTitle(p.getTitle());
            String text = Jsoup.parse(p.getText()).text();
            post.setAnnounce(text.substring(0, Math.min(MAX_TEXT_LENGTH, text.length())) + TEXT_END);
            post.setLikeCount((int) p.getPostVotes().stream().filter(postVote -> postVote.getValue() == LIKE).count());
            post.setDislikeCount((int) p.getPostVotes().stream().filter(postVote -> postVote.getValue() == DISLIKE).count());
            post.setCommentCount(p.getPostComments().size());
            post.setViewCount(p.getViewCount());
            post4PostResponseList.add(post);
        });

        Post4PostResponse[] posts4PostResponse = post4PostResponseList.toArray(new Post4PostResponse[0]);
        postsResponse.setCount((int) postSortingRepository.findPostsOrderByLikes(pageable).getTotalElements());
        postsResponse.setPosts(posts4PostResponse);
        return postsResponse;
    }

    public PostsResponse getSearch(int offset, int limit, String query) {
        List<Post4PostResponse> post4PostResponseList = new ArrayList<>();
        PostsResponse postsResponse = new PostsResponse();
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> postsList;
        if (query.trim().equals("")) {
            postsList = postSortingRepository.findPostsOrderRecentTime(pageable).toList();
            postsResponse.setCount((int) postSortingRepository.findPostsOrderRecentTime(pageable).getTotalElements());
        } else {
            postsResponse.setCount((int) postSearchRepository.findPostsByTextContaining(query, pageable).getTotalElements());
            postsList = postSearchRepository.findPostsByTextContaining(query, pageable).toList();
        }
        postsList.forEach(p -> {
            Post4PostResponse post = new Post4PostResponse();
            post.setId(p.getId());
            post.setTimestamp(p.getTime().getTime() / SECOND);
            User4PostResponse user = new User4PostResponse();
            user.setId(p.getUser().getId());
            user.setName(p.getUser().getName());
            post.setUser(user);
            post.setTitle(p.getTitle());
            String text = Jsoup.parse(p.getText()).text();
            post.setAnnounce(text.substring(0, Math.min(MAX_TEXT_LENGTH, text.length())) + TEXT_END);
            post.setLikeCount((int) p.getPostVotes().stream().filter(postVote -> postVote.getValue() == LIKE).count());
            post.setDislikeCount((int) p.getPostVotes().stream().filter(postVote -> postVote.getValue() == DISLIKE).count());
            post.setCommentCount(p.getPostComments().size());
            post.setViewCount(p.getViewCount());
            post4PostResponseList.add(post);
        });
        Post4PostResponse[] posts4PostResponse = post4PostResponseList.toArray(new Post4PostResponse[0]);
        postsResponse.setPosts(posts4PostResponse);
        return postsResponse;
    }

    public PostsResponse getPostsByDate(int offset, int limit, String date) {

        List<Post4PostResponse> post4PostResponseList = new ArrayList<>();
        PostsResponse postsResponse = new PostsResponse();
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> postsList;
        postsResponse.setCount((int) postSearchRepository.findPostsByDate(date, pageable).getTotalElements());
        postsList = postSearchRepository.findPostsByDate(date, pageable).toList();
        postsList.forEach(p -> {
            Post4PostResponse post = new Post4PostResponse();
            post.setId(p.getId());
            post.setTimestamp(p.getTime().getTime() / SECOND);
            User4PostResponse user = new User4PostResponse();
            user.setId(p.getUser().getId());
            user.setName(p.getUser().getName());
            post.setUser(user);
            post.setTitle(p.getTitle());
            String text = Jsoup.parse(p.getText()).text();
            post.setAnnounce(text.substring(0, Math.min(MAX_TEXT_LENGTH, text.length())) + TEXT_END);
            post.setLikeCount((int) p.getPostVotes().stream().filter(postVote -> postVote.getValue() == LIKE).count());
            post.setDislikeCount((int) p.getPostVotes().stream().filter(postVote -> postVote.getValue() == DISLIKE).count());
            post.setCommentCount(p.getPostComments().size());
            post.setViewCount(p.getViewCount());
            post4PostResponseList.add(post);
        });
        Post4PostResponse[] posts4PostResponse = post4PostResponseList.toArray(new Post4PostResponse[0]);
        postsResponse.setPosts(posts4PostResponse);
        return postsResponse;
    }

    public PostsResponse getPostsByTag(int offset, int limit, String tag) {

        List<Post4PostResponse> post4PostResponseList = new ArrayList<>();
        PostsResponse postsResponse = new PostsResponse();
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> postsList;
        postsResponse.setCount((int) postSearchRepository.findPostsByTagName(tag, pageable).getTotalElements());
        postsList = postSearchRepository.findPostsByTagName(tag, pageable).toList();
        postsList.forEach(p -> {
            Post4PostResponse post = new Post4PostResponse();
            post.setId(p.getId());
            post.setTimestamp(p.getTime().getTime() / SECOND);
            User4PostResponse user = new User4PostResponse();
            user.setId(p.getUser().getId());
            user.setName(p.getUser().getName());
            post.setUser(user);
            post.setTitle(p.getTitle());
            String text = Jsoup.parse(p.getText()).text();
            post.setAnnounce(text.substring(0, Math.min(MAX_TEXT_LENGTH, text.length())) + TEXT_END);
            post.setLikeCount((int) p.getPostVotes().stream().filter(postVote -> postVote.getValue() == LIKE).count());
            post.setDislikeCount((int) p.getPostVotes().stream().filter(postVote -> postVote.getValue() == DISLIKE).count());
            post.setCommentCount(p.getPostComments().size());
            post.setViewCount(p.getViewCount());
            post4PostResponseList.add(post);
        });
        Post4PostResponse[] posts4PostResponse = post4PostResponseList.toArray(new Post4PostResponse[0]);
        postsResponse.setPosts(posts4PostResponse);
        return postsResponse;
    }

    public PostIdResponse getPostId(int id) throws EntityNotFoundException {
        List<Comment4PostIdResponse> commentList = new ArrayList<>();
        List<String> tagList = new ArrayList<>();
        Post post = postRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        PostIdResponse postIdResponse = new PostIdResponse();
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        postIdResponse.setId(post.getId());
        postIdResponse.setTimestamp(post.getTime().getTime() / SECOND);
        postIdResponse.setActive(post.getIsActive() == (byte) 1);
        User4PostResponse user4PostResponse = new User4PostResponse();
        user4PostResponse.setId(post.getUser().getId());
        user4PostResponse.setName(post.getUser().getName());
        postIdResponse.setUser(user4PostResponse);
        postIdResponse.setTitle(post.getTitle());
        postIdResponse.setText(post.getText());
        postIdResponse.setLikeCount((int) post.getPostVotes().stream().filter(postVote -> postVote.getValue() == LIKE).count());
        postIdResponse.setDislikeCount((int) post.getPostVotes().stream().filter(postVote -> postVote.getValue() == DISLIKE).count());
        postIdResponse.setViewCount(post.getViewCount());
        post.getPostComments().forEach(postComment -> {
            Comment4PostIdResponse comment = new Comment4PostIdResponse();
            comment.setId(postComment.getId());
            comment.setTimestamp(postComment.getTime().getTime() / SECOND);
            comment.setText(postComment.getText());
            User4Comment user4Comment = new User4Comment();
            user4Comment.setId(postComment.getUser().getId());
            user4Comment.setName(postComment.getUser().getName());
            user4Comment.setPhoto(postComment.getUser().getPhoto());
            comment.setUser(user4Comment);
            commentList.add(comment);
        });
        post.getTags().forEach(tag -> tagList.add(tag.getName()));
        postIdResponse.setComments(commentList.toArray(new Comment4PostIdResponse[0]));
        postIdResponse.setTags(tagList.toArray(new String[0]));
        return postIdResponse;
    }

    public PostsResponse getModeration(Principal principal, int offset, int limit, String status) {
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        PostsResponse postsResponse = new PostsResponse();
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> postsList = new ArrayList<>();

        if (status.equals("new")) {
            postsList = usersPostRepository.findPostsByModerationStatus(pageable).toList();
            postsResponse.setCount((int) usersPostRepository.findPostsByModerationStatus(pageable).getTotalElements());
        } else {
            if (status.equals("declined")) {
                postsList = usersPostRepository.findPostsByModeratorDeclined(currentUser.getId(), pageable).toList();
                postsResponse.setCount((int) usersPostRepository.findPostsByModeratorDeclined(currentUser.getId(), pageable).getTotalElements());
            } else {
                if (status.equals("accepted")) {
                    postsList = usersPostRepository.findPostsByModeratorAccepted(currentUser.getId(), pageable).toList();
                    postsResponse.setCount((int) usersPostRepository.findPostsByModeratorAccepted(currentUser.getId(), pageable).getTotalElements());
                }
            }
        }
        List<Post4PostResponse> post4PostResponseList = new ArrayList<>();

        postsList.forEach(p -> {
            Post4PostResponse post = new Post4PostResponse();
            post.setId(p.getId());
            post.setTimestamp(p.getTime().getTime() / SECOND);
            User4PostResponse user = new User4PostResponse();
            user.setId(p.getUser().getId());
            user.setName(p.getUser().getName());
            post.setUser(user);
            post.setTitle(p.getTitle());
            String text = Jsoup.parse(p.getText()).text();
            post.setAnnounce(text.substring(0, Math.min(MAX_TEXT_LENGTH, text.length())) + TEXT_END);
            post.setLikeCount((int) p.getPostVotes().stream().filter(postVote -> postVote.getValue() == LIKE).count());
            post.setDislikeCount((int) p.getPostVotes().stream().filter(postVote -> postVote.getValue() == DISLIKE).count());
            post.setCommentCount(p.getPostComments().size());
            post.setViewCount(p.getViewCount());
            post4PostResponseList.add(post);
        });
        Post4PostResponse[] posts4PostResponse = post4PostResponseList.toArray(new Post4PostResponse[0]);
        postsResponse.setPosts(posts4PostResponse);
        return postsResponse;
    }

    public PostsResponse getMy(Principal principal, int offset, int limit, String status) {
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        PostsResponse postsResponse = new PostsResponse();
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> postsList = new ArrayList<>();

        if (status.equals("inactive")) {
            postsList = usersPostRepository.findPostsByInactive(currentUser.getId(), pageable).toList();
            postsResponse.setCount((int) usersPostRepository.findPostsByInactive(currentUser.getId(), pageable).getTotalElements());
        } else {
            if (status.equals("pending")) {
                postsList = usersPostRepository.findPostsByPending(currentUser.getId(), pageable).toList();
                postsResponse.setCount((int) usersPostRepository.findPostsByPending(currentUser.getId(), pageable).getTotalElements());
            } else {
                if (status.equals("declined")) {
                    postsList = usersPostRepository.findPostsByDeclined(currentUser.getId(), pageable).toList();
                    postsResponse.setCount((int) usersPostRepository.findPostsByDeclined(currentUser.getId(), pageable).getTotalElements());
                } else {
                    if (status.equals("published")) {
                        postsList = usersPostRepository.findPostsByAccepted(currentUser.getId(), pageable).toList();
                        postsResponse.setCount((int) usersPostRepository.findPostsByAccepted(currentUser.getId(), pageable).getTotalElements());
                    }
                }
            }
        }
        List<Post4PostResponse> post4PostResponseList = new ArrayList<>();

        postsList.forEach(p -> {
            Post4PostResponse post = new Post4PostResponse();
            post.setId(p.getId());
            post.setTimestamp(p.getTime().getTime() / SECOND);
            User4PostResponse user = new User4PostResponse();
            user.setId(p.getUser().getId());
            user.setName(p.getUser().getName());
            post.setUser(user);
            post.setTitle(p.getTitle());
            String text = Jsoup.parse(p.getText()).text();
            post.setAnnounce(text.substring(0, Math.min(MAX_TEXT_LENGTH, text.length())) + TEXT_END);
            post.setLikeCount((int) p.getPostVotes().stream().filter(postVote -> postVote.getValue() == LIKE).count());
            post.setDislikeCount((int) p.getPostVotes().stream().filter(postVote -> postVote.getValue() == DISLIKE).count());
            post.setCommentCount(p.getPostComments().size());
            post.setViewCount(p.getViewCount());
            post4PostResponseList.add(post);
        });
        Post4PostResponse[] posts4PostResponse = post4PostResponseList.toArray(new Post4PostResponse[0]);
        postsResponse.setPosts(posts4PostResponse);
        return postsResponse;

    }
}