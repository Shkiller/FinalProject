package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostVoteRequest {
    @JsonProperty("post_id")
    int postId;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
