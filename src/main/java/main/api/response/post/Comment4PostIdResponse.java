package main.api.response.post;

public class Comment4PostIdResponse {
    private int id;
    private long timestamp;
    private String text;
    private User4Comment user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User4Comment getUser() {
        return user;
    }

    public void setUser(User4Comment user) {
        this.user = user;
    }
}
