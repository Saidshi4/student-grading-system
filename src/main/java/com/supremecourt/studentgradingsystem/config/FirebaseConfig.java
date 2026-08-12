package com.supremecourt.studentgradingsystem.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.config-path:}")
    private String configPath;

    @Value("${firebase.bucket-name:}")
    private String storageBucket;

    @Bean
    @ConditionalOnProperty(name = "firebase.enabled", havingValue = "true")
    FirebaseApp firebaseApp() throws IOException {
        if (!StringUtils.hasText(configPath)) {
            throw new IllegalStateException(
                    "firebase.enabled=true but firebase.config-path is empty. " +
                    "Set FIREBASE_CONFIG_PATH to a valid Firebase service-account JSON file."
            );
        }

        Resource resource = resolveConfigResource();
        if (!resource.exists() || !resource.isReadable()) {
            throw new IllegalStateException(
                    "Firebase credentials file not found or not readable: " + configPath
            );
        }

        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        try (InputStream credentialsStream = resource.getInputStream()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
            FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                    .setCredentials(credentials);
            if (StringUtils.hasText(storageBucket)) {
                optionsBuilder.setStorageBucket(storageBucket);
            }
            FirebaseApp app = FirebaseApp.initializeApp(optionsBuilder.build());
            log.info("Firebase initialized from {}", configPath);
            return app;
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load Firebase credentials from '" + configPath +
                    "'. File must be a valid service-account JSON.", e
            );
        }
    }

    private Resource resolveConfigResource() {
        Resource classpathResource = new ClassPathResource(configPath);
        if (classpathResource.exists()) {
            return classpathResource;
        }
        return new FileSystemResource(configPath);
    }
}
