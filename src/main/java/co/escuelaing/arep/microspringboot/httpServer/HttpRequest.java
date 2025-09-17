package co.escuelaing.arep.microspringboot.httpServer;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpRequest - Copia exacta del Taller 2
 * @author maria.sanchez-m
 */
public class HttpRequest {
    
    private String path;// Almacena la ruta de la solicitud (antes del signo '?')
    private String query; // Almacena la cadena de consulta de la solicitud (después del '?')
    private Map<String, String> queryParams; // Mapa que contiene los parámetros de la consulta clave-valor

    URI requri = null;

    HttpRequest(URI requri) {
        this.requri = requri;
    }

    /**
     * Constructor de la clase HttpRequest.
     * Toma una cadena `fullPath` que representa la URL completa, con la ruta y los parámetros de la consulta.
     * @param fullPath La URL completa de la solicitud, que puede contener ruta y parámetros de consulta.
     */
    public HttpRequest(String fullPath) {
        String[] parts = fullPath.split("\\?", 2);
        this.path = parts[0];
        this.query = (parts.length > 1) ? parts[1] : "";
        this.queryParams = parseQueryParams(this.query);
    }



    /**
     * Analiza los parámetros de la consulta en formato clave=valor.
     * @param query La cadena de consulta de la URL (después del '?')
     * @return Un mapa de los parámetros de la consulta donde la clave es el nombre del parámetro
     * y el valor es el valor del parámetro.
     */
    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }

    /**
     * Obtiene la ruta de la solicitud HTTP.
     * @return La ruta de la solicitud.
     */
    public String getPath() {
        return path;
    }

    /**
     * Obtiene el valor de un parámetro de la consulta dado su nombre (clave).
     * Si el parámetro no existe, retorna `null`.
     * @param key El nombre del parámetro de la consulta.
     * @return El valor del parámetro si existe, o `null` si no se encuentra.
     */
    public String getValues(String key) {
        return queryParams.getOrDefault(key, null);
    }

    // Método adicional para compatibilidad con el framework (alias de getValues)
    public String getQueryParam(String key) {
        return getValues(key);
    }

    public String getValue(String paramName) {
        
        //Extrae el valor de paramName desde el query.
        String paramValue = requri.getQuery().split("=")[1]; //Ejemplo: /app/hello?name=jhon
        return paramValue;
    }
}