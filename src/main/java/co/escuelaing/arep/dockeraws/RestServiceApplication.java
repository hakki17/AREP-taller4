/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.escuelaing.arep.dockeraws;

import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author maria.sanchez-m
 */
@SpringBootApplication
public class RestServiceApplication {

    public static void main(String[] args) {
    SpringApplication app = new SpringApplication(RestServiceApplication.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", getPort()));
    app.run(args);
    }

    private static int getPort() {
    if (System.getenv("PORT") != null) {
    return Integer.parseInt(System.getenv("PORT"));
    }
    return 9000;
    }

}