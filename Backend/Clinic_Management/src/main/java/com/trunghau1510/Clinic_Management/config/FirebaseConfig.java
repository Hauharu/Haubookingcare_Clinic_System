package com.trunghau1510.Clinic_Management.config;

import lombok.AccessLevel;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import com.google.firebase.FirebaseApp;
import lombok.experimental.FieldDefaults;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FirebaseConfig {

    @Value("${firebase.service.account.path}")
    String serviceAccountPath;

    @PostConstruct
    public void initialize() throws IOException {
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream(serviceAccountPath);
        if (serviceAccount == null) {
            throw new java.io.FileNotFoundException(serviceAccountPath + " not found in classpath");
        }
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
