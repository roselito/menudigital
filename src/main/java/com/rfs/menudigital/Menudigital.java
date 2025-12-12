/**
 *
 * @author Roselito
 */
package com.rfs.menudigital;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Menudigital {

    public static void main(String[] args) {
//        System.out.println("Hello World!");
        SpringApplication.run(Menudigital.class, args);
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
