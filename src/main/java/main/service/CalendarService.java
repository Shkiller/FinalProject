package main.service;

import main.api.response.calendar.CalendarResponse;
import main.model.Post;
import main.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class CalendarService {
    private final PostRepository postRepository;
    private final SimpleDateFormat dayMonthYearFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

    public CalendarService(PostRepository postRepository) {
        this.postRepository = postRepository;

    }
    public CalendarResponse getCalendar(String year) {
        Set<Integer> yearSet = new HashSet<>();
        Map<String, Integer> dateMap = new HashMap<>();
        List<Post> postList = postRepository.findPosts();
        if (year.equals(""))
            year = String.valueOf(LocalDate.now().getYear());
        String finalYear = year;
        postList.forEach(post -> {

            yearSet.add(Integer.valueOf(yearFormat.format(post.getTime())));
            if(finalYear.equals(yearFormat.format(post.getTime())))
            dateMap.compute(dayMonthYearFormat.format(post.getTime()), (k, v) -> (v == null) ? v = 1 : v + 1);

        });
        CalendarResponse calendarResponse = new CalendarResponse();
        calendarResponse.setPosts(dateMap);
        calendarResponse.setYears(yearSet.toArray(new Integer[0]));
        return calendarResponse;
    }
}