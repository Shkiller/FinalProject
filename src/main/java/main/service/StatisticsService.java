package main.service;

import main.api.response.StatisticResponse;
import main.exception.UserUnauthorizedException;
import main.model.GlobalSetting;
import main.model.User;
import main.repository.PostRepository;
import main.repository.PostVoteRepository;
import main.repository.SettingsRepository;
import main.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;

@Service
public class StatisticsService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;
    private final SettingsRepository settingsRepository;

    private final int SECOND = 1000;
    private final int STATISTICS_IS_PUBLIC = 3;

    public StatisticsService(UserRepository userRepository,
                             PostRepository postRepository,
                             PostVoteRepository postVoteRepository,
                             SettingsRepository settingsRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
        this.settingsRepository = settingsRepository;
    }

    public StatisticResponse getMyStatistic(Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        StatisticResponse statisticResponse = new StatisticResponse();
        statisticResponse.setViewsCount(postRepository.findViewsCountByUser(currentUser.getId()).orElse(0));
        statisticResponse.setPostsCount(postRepository.findPostCountByUser(currentUser.getId()).orElse(0));
        statisticResponse.setLikesCount(postVoteRepository.findLikesByUser(currentUser.getId()).orElse(0));
        statisticResponse.setDislikesCount(postVoteRepository.findDislikesByUser(currentUser.getId()).orElse(0));
        statisticResponse.setFirstPublication(postRepository.findLatestPostByUser(currentUser.getId()).orElse(new Date(0)).getTime() / SECOND);
        return statisticResponse;
    }

    public StatisticResponse getAllStatistic(Principal principal) throws UserUnauthorizedException {
        if (principal == null)
            throw new UserUnauthorizedException();
        if (settingsRepository.findById(STATISTICS_IS_PUBLIC).orElse(new GlobalSetting()).getValue().equals("NO")) {
            User currentUser = userRepository.findByEmail(principal.getName())
                    .orElseThrow(UserUnauthorizedException::new);
            if (currentUser.getIsModerator() == (byte) 0)
                throw new UserUnauthorizedException();
        }
        StatisticResponse statisticResponse = new StatisticResponse();
        statisticResponse.setViewsCount(postRepository.findViewsCount().orElse(0));
        statisticResponse.setPostsCount(postRepository.findPostCount().orElse(0));
        statisticResponse.setLikesCount(postVoteRepository.findLikesCount().orElse(0));
        statisticResponse.setDislikesCount(postVoteRepository.findDislikesCount().orElse(0));
        statisticResponse.setFirstPublication(postRepository.findLatestPost().orElse(new Date(0)).getTime() / SECOND);
        return statisticResponse;
    }
}
