package co.escuelaing.arep.microspringboot.httpServer;

import java.io.PrintWriter;

/**
 * HttpResponse - Copia exacta del Taller 2
 * @author maria.sanchez-m
 */
public class HttpResponse {
    private final PrintWriter out;

    public HttpResponse(PrintWriter out) {
        this.out = out;
    }

    /**
     * Envía una respuesta al cliente.
     * @param response La respuesta a enviar.
     */
    public void send(String response) {
        out.println(response);
    }

    // Método adicional para compatibilidad con el framework 
    public String getType() {
        return "text/html"; // Valor por defecto
    }
}