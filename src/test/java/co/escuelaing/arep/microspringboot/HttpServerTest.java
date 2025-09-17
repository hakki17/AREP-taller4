package co.escuelaing.arep.microspringboot;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import co.escuelaing.arep.microspringboot.httpServer.HttpServer;

public class HttpServerTest {

    @Test
    public void testLoadServicesLoadsRestControllersWithGetMapping() {
        // Ejecutar el método que carga los servicios
        HttpServer.loadServices();

        // Obtener el mapa de servicios
        Map<String, Method> registeredServices = HttpServer.services;

        // Validar que el mapa no está vacío
        assertFalse(registeredServices.isEmpty(), "El mapa de servicios no debe estar vacío");

        // Verifica que contenga una ruta esperada (ajusta según lo que tengas en tu GreetingController)
        assertTrue(registeredServices.containsKey("/helloworld"),
        "Debería registrar el servicio '/helloworld' desde GreetingController");

        Method method = registeredServices.get("/helloworld");
        assertNotNull(method, "El método para '/helloworld' no debe ser null");

        assertEquals("helloworld", method.getName(), "El método registrado debería llamarse 'helloworld'");

    }
}
