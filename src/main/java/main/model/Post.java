package main.model;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1)")
    private byte isActive;
    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status",columnDefinition = "enum('NEW','ACCEPTED','DECLINED')",nullable = false)
    private ModerationStatusType moderationStatus;
    @ManyToOne
    @JoinColumn(name = "moderator_id")
    private User moderator;
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @Column(nullable = false)
    private Date time;
    @Column(nullable = false)
    private String title;
    @Column(columnDefinition="Text",nullable = false)
    private String text;
    @Column(name = "view_count",nullable = false)
    private int viewCount;
    @OneToMany(mappedBy = "id")
    private Set<PostComment> postComments = new HashSet<>();

    public Set<PostComment> getPostComments() {
        return postComments;
    }

    public void setPostComments(Set<PostComment> postComments) {
        this.postComments = postComments;
    }

    public void addPostComments(PostComment postComment) {
        postComment.setPost(this);
        postComments.add(postComment);

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getIsActive() {
        return isActive;
    }

    public void setIsActive(byte isActive) {
        this.isActive = isActive;
    }

    public ModerationStatusType getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(ModerationStatusType moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
