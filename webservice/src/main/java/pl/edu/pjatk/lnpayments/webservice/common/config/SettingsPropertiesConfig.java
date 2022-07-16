package pl.edu.pjatk.lnpayments.webservice.common.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.USER_HOME;

@Slf4j
@Configuration
public class SettingsPropertiesConfig {

    @Value("${lnp.config.workingDirectory}")
    private String walletDirectory;

    @Value("${lnp.settings.fileName}")
    private String fileName;

    @Bean
    public PropertiesConfiguration propertiesConfig() throws ConfigurationException, IOException {
        File propertiesFile = new File(USER_HOME + walletDirectory, fileName);
        if (!propertiesFile.exists()) {
            initializeProperties(propertiesFile.getPath());
        }
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
                new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                        .configure(new Parameters().properties()
                                .setFile(propertiesFile)
                                .setThrowExceptionOnMissing(true)
                                .setIncludesAllowed(false));
        builder.setAutoSave(true);
        return builder.getConfiguration();
    }

    private void initializeProperties(String path) throws IOException {
        log.info("Creating new settings file in " + path);
        InputStream defaultConfig = new ClassPathResource("/default/" + fileName).getInputStream();
        Path settingsFilePath = Path.of(path);
        if (!settingsFilePath.getParent().toFile().exists()) {
            Files.createDirectory(settingsFilePath.getParent());
        }
        Files.copy(defaultConfig, settingsFilePath);
    }

}
