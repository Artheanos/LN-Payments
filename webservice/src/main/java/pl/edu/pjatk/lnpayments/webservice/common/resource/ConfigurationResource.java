package pl.edu.pjatk.lnpayments.webservice.common.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjatk.lnpayments.webservice.common.resource.dto.PropertyValues;
import pl.edu.pjatk.lnpayments.webservice.common.service.SettingsService;

import javax.validation.Valid;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.SETTINGS_PATH;

@RestController
@RequestMapping(SETTINGS_PATH)
class ConfigurationResource {

    private final SettingsService settingsService;

    @Autowired
    public ConfigurationResource(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    ResponseEntity<PropertyValues> getSettings() {
        PropertyValues settings = settingsService.getSettings();
        return ResponseEntity.ok(settings);
    }

    @PutMapping
    ResponseEntity<?> updateSettings(@RequestBody @Valid PropertyValues propertyValues) {
        settingsService.saveSettings(propertyValues);
        return ResponseEntity.ok().build();
    }
}
