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
        settingsRepository.findById(MULTIUSER_MODE).map(globalSetting -> {
            settingsResponse.setMultiuserMode(globalSetting.getValue().equals("YES"));
            return null;
        });
        settingsRepository.findById(POST_PREMODERATION).map(globalSetting -> {
            settingsResponse.setPostPremoderation(globalSetting.getValue().equals("YES"));
            return null;
        });
        settingsRepository.findById(STATISTICS_IS_PUBLIC).map(globalSetting -> {
            settingsResponse.setStatisticIsPublic(globalSetting.getValue().equals("YES"));
            return null;
        });
        return new ResponseEntity(settingsResponse, HttpStatus.OK);
    }
}
