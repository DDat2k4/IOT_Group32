package org.example.web;

import org.example.web.config.DotenvInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WebApplication.class);
        app.addInitializers(new DotenvInitializer());
        app.run(args);
    }
}
