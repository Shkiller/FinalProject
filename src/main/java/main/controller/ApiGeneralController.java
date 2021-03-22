package main.controller;


import main.api.response.InitResponse;
import main.service.CalendarService;
import main.service.SettingsService;
import main.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/")
public class ApiGeneralController {
    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final TagService tagService;
    private final CalendarService calendarService;

    public ApiGeneralController(InitResponse initResponse, SettingsService settingsService, TagService tagService, CalendarService calendarService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.tagService = tagService;
        this.calendarService = calendarService;
    }

    @GetMapping("/init")
    private ResponseEntity<InitResponse> init() {
        return new ResponseEntity<>(initResponse, HttpStatus.OK);
    }

    @GetMapping("/settings")
    private ResponseEntity settings() {
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/tag")
    private ResponseEntity tag(@RequestParam(name = "query", defaultValue = "") String query) {
        return new ResponseEntity(tagService.getTags(query),HttpStatus.OK);
    }
    @GetMapping("/calendar")
    private ResponseEntity calendar(@RequestParam String year) {
        return new ResponseEntity(calendarService.getCalendar(year),HttpStatus.OK);
    }
}
