package main.controller;


import main.api.request.CommentRequest;
import main.api.request.ModerationRequest;
import main.api.request.SettingsRequest;
import main.api.request.profile.ProfileImageRequest;
import main.api.request.profile.ProfileRequest;
import main.api.response.*;
import main.api.response.calendar.CalendarResponse;
import main.api.response.tag.TagsResponse;
import main.exception.BadRequestException;
import main.exception.IncorrectFormatException;
import main.exception.UserUnauthorizedException;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/")
public class ApiGeneralController {
    private final ProfileService profileService;
    private final CommentService commentService;
    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final TagService tagService;
    private final CalendarService calendarService;
    private final ModerationService moderationService;
    private final StatisticsService statisticsService;

    public ApiGeneralController(ProfileService profileService,
                                CommentService commentService,
                                InitResponse initResponse,
                                SettingsService settingsService,
                                TagService tagService,
                                CalendarService calendarService,
                                ModerationService moderationService,
                                StatisticsService statisticsService) {
        this.profileService = profileService;
        this.commentService = commentService;
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.tagService = tagService;
        this.calendarService = calendarService;
        this.moderationService = moderationService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/init")
    public ResponseEntity<InitResponse> init() {
        return new ResponseEntity<>(initResponse, HttpStatus.OK);
    }

    @GetMapping("/settings")
    public ResponseEntity<SettingsResponse> getSettings() {
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/tag")
    public ResponseEntity<TagsResponse> tag(@RequestParam(name = "query", defaultValue = "") String query) {
        return new ResponseEntity<>(tagService.getTags(query), HttpStatus.OK);
    }

    @GetMapping("/calendar")
    public ResponseEntity<CalendarResponse> calendar(@RequestParam String year) {
        return new ResponseEntity<>(calendarService.getCalendar(year), HttpStatus.OK);
    }

    //Из сервиса возвращаю ResponseEntity, так как нужны разные коды ответа
    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<CommentResponse> comment(@RequestBody CommentRequest commentRequest, Principal principal) throws BadRequestException {

        return commentService.commentAdd(commentRequest, principal);
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ModerationResponse> moderation(@RequestBody ModerationRequest moderationRequest, Principal principal) {

        return new ResponseEntity<>(moderationService.moderation(moderationRequest, principal), HttpStatus.OK);
    }

    @GetMapping("/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<StatisticResponse> myStatistics(Principal principal) {
        return new ResponseEntity<>(statisticsService.getMyStatistic(principal), HttpStatus.OK);
    }

    @GetMapping("/statistics/all")
    public ResponseEntity<StatisticResponse> allStatistics(Principal principal) throws UserUnauthorizedException {
        return new ResponseEntity<>(statisticsService.getAllStatistic(principal), HttpStatus.OK);
    }

    @PostMapping(value = "/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultErrorsResponse> myProfile(@ModelAttribute ProfileImageRequest profileImageRequest, Principal principal) throws IOException, IncorrectFormatException {
        return new ResponseEntity<>(profileService.myProfile(profileImageRequest, principal), HttpStatus.OK);
    }

    @PostMapping(value = "/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultErrorsResponse> myProfile(@RequestBody ProfileRequest profileRequest, Principal principal) throws IOException, IncorrectFormatException {
        return new ResponseEntity<>(profileService.myProfile(profileRequest, principal), HttpStatus.OK);
    }
    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public void putSettings(@RequestBody SettingsRequest settingsRequest) {
        settingsService.putGlobalSettings(settingsRequest);
    }
}
