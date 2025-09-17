/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package co.escuelaing.arep.microspringboot;

import java.io.IOException;
import java.net.URISyntaxException;

import co.escuelaing.arep.microspringboot.httpServer.HttpServer;

/**
 *
 * @author maria.sanchez-m
 */
public class MicroSpringBoot {

    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println("Starting MicroSpringBoot:");
        HttpServer.runServer();
    }
}
