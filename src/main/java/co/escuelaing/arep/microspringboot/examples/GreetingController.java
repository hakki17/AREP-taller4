/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.escuelaing.arep.microspringboot.examples;

import co.escuelaing.arep.microspringboot.annotations.GetMapping;
import co.escuelaing.arep.microspringboot.annotations.RequestParam;
import co.escuelaing.arep.microspringboot.annotations.RestController;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author maria.sanchez-m
 */
@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public static String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }

    @GetMapping("/pi")
    public static String pi() {
        return String.valueOf(Math.PI);
    }

    @GetMapping("/helloworld")
    public static String helloworld() {
        return "Hello World";
    }

    @GetMapping("/shutdown")
    public static String shutdown() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("Apagando servidor por petición web...");
                System.exit(0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        return "Servidor apagándose en 1 segundo...";
    }
}
