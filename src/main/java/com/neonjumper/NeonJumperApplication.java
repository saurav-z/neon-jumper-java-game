package com.neonjumper;

import com.neonjumper.ui.JavaFxApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NeonJumperApplication {

    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }
}
