package server;

import files.FileManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@SpringBootApplication
public class  Application {

    @EventListener(ApplicationReadyEvent.class)
    public void parseTasksForUser() {
        FileManager.get();
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}