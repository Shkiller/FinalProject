package main.service;

import main.api.request.PostRequest;
import main.api.response.post.*;
import main.exception.EntityNotFoundException;
import main.model.*;
import main.repository.*;
import org.jsoup.Jsoup;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostSearchRepository postSearchRepository;
    private final UsersPostRepository usersPostRepository;
    private final PostSortingRepository postSortingRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final Tag2PostRepository tag2PostRepository;
    private final SettingsRepository settingsRepository;

    private final int MAX_TEXT_LENGTH = 150;
    private final int SECOND = 1000;
    private final byte LIKE = 1;
    private final byte DISLIKE = 0;
    private final String TEXT_END = "...";
    private final int MIN_TEXT_LENGTH = 50;
    private final int MIN_TITLE_LENGTH = 3;
    private final int POST_PREMODERATION = 2;

    public PostService(PostSearchRepository postSearchService,
                       UsersPostRepository usersPostRepository,
                       PostSortingRepository postSortingRepository,
                       PostRepository postRepository,
                       UserRepository userRepository,
                       TagRepository tagRepository,
                       Tag2PostRepository tag2PostRepository,
                       SettingsRepository settingsRepository) {
        this.postSearchRepository = postSearchService;
        this.usersPostRepository = usersPostRepository;
        this.postSortingRepository = postSortingRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.tag2PostRepository = tag2PostRepository;

        this.settingsRepository = settingsRepository;
    }

    public PostsResponse getPosts(int offset, int limit, String mode) {
        PostsResponse postsResponse = new PostsResponse();
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> postsList = new ArrayList<>();
        switch (mode) {
            case "recent":
                postsList = postSortingRepository.findPostsOrderRecentTime(pageable).toList();
                break;
            case "popular":
                postsList = postSortingRepository.findPostsOrderByComments(pageable).toList();
                break;
            case "best":
                postsList = postSortingRepository.findPostsOrderByLikes(pageable).toList();
                break;
            case "early":
                postsList = postSortingRepository.findPostsOrderByEarlyTime(pageable).toList();
                break;
        }
        Post4PostResponse[] posts4PostResponse = getPost4PostResponse(postsList).toArray(new Post4PostResponse[0]);
        postsResponse.setCount((int) postSortingRepository.findPostsOrderByLikes(pageable).getTotalElements());
        postsResponse.setPosts(posts4PostResponse);
        return postsResponse;
    }

    public PostsResponse getSearch(int offset, int limit, String query) {
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
        Post4PostResponse[] posts4PostResponse = getPost4PostResponse(postsList).toArray(new Post4PostResponse[0]);
        postsResponse.setPosts(posts4PostResponse);
        return postsResponse;
    }

    public PostsResponse getPostsByDate(int offset, int limit, String date) {

        PostsResponse postsResponse = new PostsResponse();
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> postsList;
        postsResponse.setCount((int) postSearchRepository.findPostsByDate(date, pageable).getTotalElements());
        postsList = postSearchRepository.findPostsByDate(date, pageable).toList();
        Post4PostResponse[] posts4PostResponse = getPost4PostResponse(postsList).toArray(new Post4PostResponse[0]);
        postsResponse.setPosts(posts4PostResponse);
        return postsResponse;
    }

    public PostsResponse getPostsByTag(int offset, int limit, String tag) {
        PostsResponse postsResponse = new PostsResponse();
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> postsList;
        postsResponse.setCount((int) postSearchRepository.findPostsByTagName(tag, pageable).getTotalElements());
        postsList = postSearchRepository.findPostsByTagName(tag, pageable).toList();

        Post4PostResponse[] posts4PostResponse = getPost4PostResponse(postsList).toArray(new Post4PostResponse[0]);
        postsResponse.setPosts(posts4PostResponse);
        return postsResponse;
    }

    public PostIdResponse getPostId(int id, Principal principal) throws EntityNotFoundException {
        List<Comment4PostIdResponse> commentList = new ArrayList<>();
        List<String> tagList = new ArrayList<>();
        Post post = postRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        PostIdResponse postIdResponse = new PostIdResponse();
        if (principal == null)
            post.setViewCount(post.getViewCount() + 1);
        else {
            User currentUser = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
            if (currentUser.getIsModerator() == (byte) 1 || currentUser.getId() == post.getUser().getId()) {
                post.setViewCount(post.getViewCount());
            } else {
                post.setViewCount(post.getViewCount() + 1);
            }
        }
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

        switch (status) {
            case "new":
                postsList = usersPostRepository.findPostsByModerationStatus(pageable).toList();
                postsResponse.setCount((int) usersPostRepository.findPostsByModerationStatus(pageable).getTotalElements());
                break;
            case "declined":
                postsList = usersPostRepository.findPostsByModeratorDeclined(currentUser.getId(), pageable).toList();
                postsResponse.setCount((int) usersPostRepository.findPostsByModeratorDeclined(currentUser.getId(), pageable).getTotalElements());
                break;
            case "accepted":
                postsList = usersPostRepository.findPostsByModeratorAccepted(currentUser.getId(), pageable).toList();
                postsResponse.setCount((int) usersPostRepository.findPostsByModeratorAccepted(currentUser.getId(), pageable).getTotalElements());
                break;
        }

        Post4PostResponse[] posts4PostResponse = getPost4PostResponse(postsList).toArray(new Post4PostResponse[0]);
        postsResponse.setPosts(posts4PostResponse);
        return postsResponse;
    }

    public PostsResponse getMy(Principal principal, int offset, int limit, String status) {
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        PostsResponse postsResponse = new PostsResponse();
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> postsList = new ArrayList<>();

        switch (status) {
            case "inactive":
                postsList = usersPostRepository.findPostsByInactive(currentUser.getId(), pageable).toList();
                postsResponse.setCount((int) usersPostRepository.findPostsByInactive(currentUser.getId(), pageable).getTotalElements());
                break;
            case "pending":
                postsList = usersPostRepository.findPostsByPending(currentUser.getId(), pageable).toList();
                postsResponse.setCount((int) usersPostRepository.findPostsByPending(currentUser.getId(), pageable).getTotalElements());
                break;
            case "declined":
                postsList = usersPostRepository.findPostsByDeclined(currentUser.getId(), pageable).toList();
                postsResponse.setCount((int) usersPostRepository.findPostsByDeclined(currentUser.getId(), pageable).getTotalElements());
                break;
            case "published":
                postsList = usersPostRepository.findPostsByAccepted(currentUser.getId(), pageable).toList();
                postsResponse.setCount((int) usersPostRepository.findPostsByAccepted(currentUser.getId(), pageable).getTotalElements());
                break;
        }

        Post4PostResponse[] posts4PostResponse = getPost4PostResponse(postsList).toArray(new Post4PostResponse[0]);
        postsResponse.setPosts(posts4PostResponse);
        return postsResponse;
    }

    public PostAddResponse postAdd(PostRequest postRequest, Principal principal) {
        Map<String, String> errors = new HashMap<>();
        PostAddResponse postAddResponse = new PostAddResponse();
        if (postRequest.getTitle().length() < MIN_TITLE_LENGTH && postRequest.getTitle() != null) {
            errors.put("title", "Заголовок слишком короткий");
        }
        if (postRequest.getText().length() < MIN_TEXT_LENGTH && postRequest.getText() != null) {
            errors.put("text", "Текст публикации слишком короткий");
        }
        if (errors.size() == 0) {
            postRequest.setTimestamp(Math.max(postRequest.getTimestamp() * SECOND, new Date().getTime()));
            Post post = new Post();
            User currentUser = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
            post.setUser(currentUser);
            post.setTitle(postRequest.getTitle());
            post.setViewCount(0);
            post.setIsActive(postRequest.getActive());
            if (currentUser.getIsModerator() == (byte) 0)
                post.setModerationStatus(settingsRepository.findById(POST_PREMODERATION).orElse(new GlobalSetting()).getValue()
                        .equals("YES") ? ModerationStatusType.NEW : ModerationStatusType.ACCEPTED);
            else post.setModerationStatus(ModerationStatusType.ACCEPTED);
            post.setText(postRequest.getText());
            post.setTime(new Date(postRequest.getTimestamp()));
            postRepository.save(post);
            for (String tagString : postRequest.getTags()) {
                Optional<Tag> tag = tagRepository.findTagByName(tagString);
                if (tag.isEmpty())
                    continue;
                Tag2Post tag2Post = new Tag2Post();
                tag2Post.setPost(post);
                tag2Post.setTag(tag.get());
                tag2PostRepository.save(tag2Post);
            }
            postAddResponse.setResult(true);
        } else {
            postAddResponse.setErrors(errors);
        }
        return postAddResponse;
    }

    public PostAddResponse postUpdate(int id, PostRequest postRequest, Principal principal) throws EntityNotFoundException {
        Map<String, String> errors = new HashMap<>();
        PostAddResponse postAddResponse = new PostAddResponse();
        if (postRequest.getTitle().length() < MIN_TITLE_LENGTH && postRequest.getTitle() != null) {
            errors.put("title", "Заголовок слишком короткий");
        }
        if (postRequest.getText().length() < MIN_TEXT_LENGTH && postRequest.getText() != null) {
            errors.put("text", "Текст публикации слишком короткий");
        }
        if (errors.size() == 0) {
            User currentUser = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
            postRequest.setTimestamp(Math.max(postRequest.getTimestamp() * SECOND, new Date().getTime()));
            Post post = postRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            post.setTitle(postRequest.getTitle());
            post.setIsActive(postRequest.getActive());
            if (post.getUser().getId() == currentUser.getId())
                    post.setModerationStatus(settingsRepository.findById(POST_PREMODERATION).orElse(new GlobalSetting()).getValue()
                            .equals("YES") ? ModerationStatusType.NEW : ModerationStatusType.ACCEPTED);
            post.setText(postRequest.getText());
            post.setTime(new Date(postRequest.getTimestamp()));
            postRepository.save(post);
            Set<String> newTagSet = Arrays.stream(postRequest.getTags()).collect(Collectors.toSet());
            tag2PostRepository.findByPost(id).forEach(tag2Post -> {
                if (newTagSet.contains(tag2Post.getTag().getName())) {
                    newTagSet.remove(tag2Post.getTag().getName());
                } else {
                    tag2PostRepository.delete(tag2Post);
                }
            });

            for (String tagString : newTagSet) {
                Optional<Tag> tag = tagRepository.findTagByName(tagString);
                if (tag.isEmpty())
                    continue;
                Tag2Post tag2Post = new Tag2Post();
                tag2Post.setPost(post);
                tag2Post.setTag(tag.get());
                tag2PostRepository.save(tag2Post);
            }
            postAddResponse.setResult(true);
        } else {
            postAddResponse.setErrors(errors);
        }
        return postAddResponse;
    }

    private List<Post4PostResponse> getPost4PostResponse(List<Post> postsList) {
        List<Post4PostResponse> post4PostResponseList = new ArrayList<>();
        postsList.forEach(p ->
        {
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
        return post4PostResponseList;
    }
}