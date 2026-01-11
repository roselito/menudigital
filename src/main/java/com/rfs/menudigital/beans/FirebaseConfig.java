package com.rfs.menudigital.beans;

/**
 *
 * @author Roselito@RFS
 */
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        InputStream  serviceAccount = 
                new ClassPathResource("static/lu-mandalas-firebase-adminsdk-bv9k3-43a83c0932.json").getInputStream();
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            System.out.println("Firebase inicializado!");
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }
}
