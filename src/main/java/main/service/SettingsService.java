package main.service;

import main.api.response.SettingsResponse;
import main.model.GlobalSetting;
import main.repository.SettingsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    private final int MULTIUSER_MODE = 1;
    private final int POST_PREMODERATION = 2;
    private final int STATISTICS_IS_PUBLIC = 3;
    private final SettingsRepository settingsRepository;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public ResponseEntity getGlobalSettings() {
        SettingsResponse settingsResponse = new SettingsResponse();
        settingsRepository.findAll().forEach(gS -> {
            switch (gS.getId()) {
                case MULTIUSER_MODE:
                    settingsResponse.setMultiuserMode(gS.getValue().equals("YES"));
                    break;
                case POST_PREMODERATION:
                    settingsResponse.setPostPremoderation(gS.getValue().equals("YES"));
                    break;
                case STATISTICS_IS_PUBLIC:
                    settingsResponse.setStatisticIsPublic(gS.getValue().equals("YES"));
                    break;
            }
        });
        return new ResponseEntity(settingsResponse, HttpStatus.OK);
    }
}
