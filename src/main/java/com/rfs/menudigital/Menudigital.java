/**
 *
 * @author Roselito
 */
package com.rfs.menudigital;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.FileInputStream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Menudigital {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Menudigital.class);
        builder.headless(false); // Important: disable headless mode
        ConfigurableApplicationContext context = builder.run(args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {

            System.out.println("================");
            System.out.println("Inicializado!!!");
            System.out.println("================");

        };

    }
}
