package main.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.JoinTable;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "is_moderator", nullable = false, columnDefinition = "TINYINT(1)")
    private byte isModerator;
    @Column(name = "reg_time", nullable = false)
    private Date registrationTime;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    private String code;
    @Column(columnDefinition = "Text")
    private String photo;
    @OneToMany
    @JoinColumn(name = "user_id")
    private Set<Post> posts = new HashSet<>();
    @OneToMany
    @JoinColumn(name = "user_id")
    private Set<PostComment> postComments = new HashSet<>();
    @ManyToMany()
    @JoinTable(name = "post_votes",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "post_id")})
    private List<Post> postVotes;

    public List<Post> getPostVotes() {
        return postVotes;
    }

    public Set<PostComment> getPostComments() {
        return postComments;
    }

    public void setPostComments(Set<PostComment> postComments) {
        this.postComments = postComments;
    }

    public void addPostComments(PostComment postComment) {
        postComment.setUser(this);
        postComments.add(postComment);

    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public void addPost(Post post) {
        post.setUser(this);
        posts.add(post);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Date getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Date registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public byte getIsModerator() {
        return isModerator;
    }

    public void setIsModerator(byte isModerator) {
        this.isModerator = isModerator;
    }
}
