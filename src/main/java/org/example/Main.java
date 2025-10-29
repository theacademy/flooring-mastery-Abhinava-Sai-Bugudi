package org.example;

import org.example.controller.FlooringController;
import org.example.dao.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        // Initialize Spring application context using the AppConfig configuration
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // Retrieve the main controller bean from Spring
        FlooringController controller = context.getBean(FlooringController.class);

        // Start the main program loop
        controller.run();

        //final commit
    }
}
