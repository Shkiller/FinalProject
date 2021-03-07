package main.service;


import main.api.response.Tag4TagsResponse;
import main.api.response.TagsResponse;
import main.model.ModerationStatusType;
import main.model.Post;
import main.model.Tag;
import main.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TagService {
    private final PostRepository postRepository;

    public TagService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public ResponseEntity getTags(String query) {
        Map<Tag, Double> tagMap = new HashMap<>();
        List<Tag4TagsResponse> tag4TagsResponseList = new ArrayList<>();
        TagsResponse tagsResponse = new TagsResponse();
        List<Post> postList = new ArrayList<>();
        postRepository.findAll().forEach(p -> {
            if (p.getIsActive() == 1 && p.getModerationStatus().equals(ModerationStatusType.ACCEPTED) && p.getTime().compareTo(new Date()) < 1)
                postList.add(p);
            p.getTags().forEach(tag -> tagMap.compute(tag, (k, v) -> (v == null) ? v = 1.0 : v + 1.0));
        });
        double maxWeight = tagMap.values().stream().max(Double::compare).orElse(0.0) / (double) postList.size();
        if (maxWeight == 0.0)
            return new ResponseEntity(null, HttpStatus.OK);
        else {
            double k = 1.0 / maxWeight;
            tagMap.forEach((tag, value) -> {
                if (tag.getName().contains(query)) {
                    Tag4TagsResponse tag4TagsResponse = new Tag4TagsResponse();
                    tag4TagsResponse.setName(tag.getName());
                    tag4TagsResponse.setWeight(value / postList.size() * k);
                    tag4TagsResponseList.add(tag4TagsResponse);
                }
            });
            Tag4TagsResponse[] tags4PostResponse = tag4TagsResponseList.toArray(new Tag4TagsResponse[0]);
            tagsResponse.setTags(tags4PostResponse);
            return new ResponseEntity(tagsResponse, HttpStatus.OK);
        }
    }
}
