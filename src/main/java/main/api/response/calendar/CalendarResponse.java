package main.api.response.calendar;

import java.util.Map;

public class CalendarResponse {
    private Integer [] years;
    private Map<String,Integer> posts;

    public Integer[] getYears() {
        return years;
    }

    public void setYears(Integer[] years) {
        this.years = years;
    }

    public Map<String, Integer> getPosts() {
        return posts;
    }

    public void setPosts(Map<String, Integer> posts) {
        this.posts = posts;
    }
}
