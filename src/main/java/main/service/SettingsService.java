package main.service;

import main.api.request.SettingsRequest;
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

    public ResponseEntity<SettingsResponse> getGlobalSettings() {
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
        return new ResponseEntity<>(settingsResponse, HttpStatus.OK);
    }

    public void putGlobalSettings(SettingsRequest settingsRequest) {
        settingsRepository.findAll().forEach(gS -> {
            switch (gS.getId()) {
                case MULTIUSER_MODE:
                    gS.setValue(settingsRequest.isMultiuserMode() ? "YES" : "NO");
                    settingsRepository.save(gS);
                    break;
                case POST_PREMODERATION:
                    gS.setValue(settingsRequest.isPostPremoderation() ? "YES" : "NO");
                    settingsRepository.save(gS);
                    break;

                case STATISTICS_IS_PUBLIC:
                    gS.setValue(settingsRequest.isStatisticsIsPublic() ? "YES" : "NO");
                    settingsRepository.save(gS);
                    break;
            }
        });
    }
}
