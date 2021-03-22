package main.api.response.post;

import main.api.response.post.Post4PostResponse;

public class PostsResponse {
    private int count;
    private Post4PostResponse[] posts;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Post4PostResponse[] getPosts() {
        return posts;
    }

    public void setPosts(Post4PostResponse[] posts) {
        this.posts = posts;
    }
}
