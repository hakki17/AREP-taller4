package co.escuelaing.arep.microspringboot.httpServer;

import co.escuelaing.arep.microspringboot.annotations.GetMapping;
import co.escuelaing.arep.microspringboot.annotations.RequestParam;
import co.escuelaing.arep.microspringboot.annotations.RestController;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.annotation.Annotation;

/**
 * HttpServer que combina la base del Taller 2 con elementos específicos de la clase
 * @author maria.sanchez-m
 */
public class HttpServer {
    
    // ELEMENTO DE CLASE: Map services copiado exacto
    public static Map<String, Method> services = new HashMap<>();
    
    // Elementos del Taller 2
    private static final int port = 8080;
    private static String directory = "webroot";
    private static final Map<String, String> dataStore = new HashMap<>();

    // MÉTODO DE CLASE: loadServices copiado exacto
    public static void loadServices() {
        HttpServer server = new HttpServer();

        // Configurar carpeta de archivos estáticos
        server.staticfiles("webroot");
        
        String packageName = "co.escuelaing.arep.microspringboot.examples"; // Ajusta según tu estructura
        String path = packageName.replace('.', '/');
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL packageURL = classLoader.getResource(path);
            
            if (packageURL == null) {
                System.err.println("No se pudo encontrar el paquete: " + packageName);
                return;
            }

            File directory = new File(packageURL.toURI());
            File[] files = directory.listFiles();

            if (files == null) {
                System.err.println("No se encontraron archivos en el paquete: " + packageName);
                return;
            }

            for (File file : files) {
                if (file.getName().endsWith(".class")) {
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    String fullClassName = packageName + "." + className;

                    Class<?> clazz = Class.forName(fullClassName);

                    if (clazz.isAnnotationPresent(RestController.class)) {
                        for (Method m : clazz.getDeclaredMethods()) {
                            if (m.isAnnotationPresent(GetMapping.class)) {
                                String mapping = m.getAnnotation(GetMapping.class).value();
                                services.put(mapping, m);
                                System.out.println("Servicio cargado automáticamente: " + mapping + " -> " + m.getName());
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // MÉTODO DE CLASE: invokeService copiado y adaptado
    private static String invokeService(URI requri) {
        String header = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n";
        try {
            HttpRequest req = new HttpRequest(requri);
            String key = requri.getPath().substring(4); // Quita "/app"
            Method m = services.get(key);
            
            if (m == null) {
                return header + "Servicio no encontrado: " + key;
            }
            
            String[] argsValues = null;
            
            // Verificar si el método tiene parámetros
            if (m.getParameterCount() > 0) {
                RequestParam rp = (RequestParam) m.getParameterAnnotations()[0][0];
                
                if (requri.getQuery() == null) {
                    argsValues = new String[]{rp.defaultValue()};
                } else {
                    String queryParamName = rp.value();
                    argsValues = new String[]{req.getValue(queryParamName)};
                }
            } else {
                argsValues = new String[0];
            }
            
            return header + m.invoke(null, argsValues);
            
        } catch (IllegalAccessException ex) {
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return header + "ERROR!";
    }

    public static void startServer() throws IOException, URISyntaxException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado en http://localhost:" + port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            handleRequestClient(clientSocket);
        }
    }

    public static void handleRequestClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); 
            BufferedOutputStream dataOut = new BufferedOutputStream(clientSocket.getOutputStream())) {

            String requestLine = in.readLine();
            if (requestLine != null) {
                String[] tokens = requestLine.split(" ");
                String method = tokens[0];
                String fileRequested = tokens[1];

                if (fileRequested.equals("/")) {
                    fileRequested = "/index.html";
                }

                if (method.equals("GET")) {
                    handleGetRequest(fileRequested, dataOut, out);
                } else if (method.equals("POST")) {
                    handlePostRequest(in, fileRequested, out);
                } else {
                    out.println("HTTP/1.1 501 Not Implemented");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleGetRequest(String fileRequested, BufferedOutputStream dataOut, PrintWriter out) {
        String basePath = fileRequested.split("\\?")[0];

        // 1. PRIORIDAD: Verificar servicios cargados con loadServices (Map services)
        try {
            URI requri = new URI("http://localhost" + fileRequested);
            if (requri.getPath().startsWith("/app") && services.containsKey(requri.getPath().substring(4))) {
                String response = invokeService(requri);
                out.print(response);
                out.flush();
                return;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // // 2. Verificar servicios dinámicos (BiFunction)
        // if (services.containsKey(basePath)) {
        //     System.out.println("Manejando ruta dinámica: " + basePath);

        //     HttpRequest req = new HttpRequest(fileRequested);
        //     HttpResponse res = new HttpResponse(out);

        //     String responseBody = services.get(basePath).apply(req, res);

        //     out.println("HTTP/1.1 200 OK");
        //     out.println("Content-Type: text/html");
        //     out.println();
        //     out.println(responseBody);
        //     return;
        // }
        
        // 3. Endpoints específicos del Taller 2
        if (fileRequested.startsWith("/app/greeting")) {
            String savedName = dataStore.getOrDefault("name", "usuario");
            String savedApellido = dataStore.getOrDefault("apellido", "");
            String queryParams = fileRequested.split("\\?").length > 1 ? fileRequested.split("\\?")[1] : "";
            String[] params = queryParams.split("&");

            for (String param : params) {
                if (param.startsWith("name=")) {
                    String name = param.split("=")[1];
                    try {
                        name = URLDecoder.decode(name, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                    }
                    dataStore.put("name", name);
                    savedName = name;
                }
            }

            String jsonResponse;
            if (!savedApellido.isEmpty() && !savedApellido.equals("Apellido")) {
                jsonResponse = "{\"name\": \"" + savedName + "\", \"apellido\": \"" + savedApellido + "\"}";
            } else {
                jsonResponse = "{\"name\": \"" + savedName + "\"}";
            }

            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: application/json");
            out.println("Content-Length: " + jsonResponse.length());
            out.println();
            out.print(jsonResponse);
            out.flush();
            return;
        }

        // 4. Servir archivos estáticos (del Taller 2)
        File file = new File(directory, fileRequested);

        if (file.exists() && !file.isDirectory()) {
            try {
                String contentType = getType(fileRequested);
                long fileLength = file.length();

                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: " + contentType);
                out.println("Content-Length: " + fileLength);
                out.println();
                out.flush();

                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    dataOut.write(buffer, 0, bytesRead);
                }
                dataOut.flush();
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            out.println("HTTP/1.1 404 Not Found");
            out.println("Content-Type: text/html");
            out.println();
            out.println("<html><body><h1>404 - File not found</h1></body></html>");
        }
    }

    private static void handlePostRequest(BufferedReader in, String fileRequested, PrintWriter out) {
        if (fileRequested.equals("/app/updateName")) {
            try {
                String line;
                int contentLength = 0;

                while (!(line = in.readLine()).isEmpty()) {
                    if (line.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(line.split(":")[1].trim());
                    }
                }

                String apellido = "Apellido";

                if (contentLength > 0) {
                    char[] body = new char[contentLength];
                    in.read(body, 0, contentLength);
                    String requestBody = new String(body);

                    if (requestBody.contains("\"apellido\"")) {
                        String[] parts = requestBody.split("\"apellido\"\\s*:\\s*\"");
                        if (parts.length > 1) {
                            String namePart = parts[1];
                            int endQuote = namePart.indexOf("\"");
                            if (endQuote > 0) {
                                apellido = namePart.substring(0, endQuote);
                            }
                        }
                    }
                    dataStore.put("apellido", apellido);
                }

                String responseMessage = "{\"mensaje\": \"Apellido actualizado: " + apellido + "\"}";

                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: application/json");
                out.println("Content-Length: " + responseMessage.length());
                out.println();
                out.print(responseMessage);
                out.flush();

            } catch (IOException e) {
                out.println("HTTP/1.1 500 Internal Server Error");
            }
        } else {
            out.println("HTTP/1.1 404 Not Found");
        }
    }

    private static String getType(String fileRequested) {
        String extension = getFileExtension(fileRequested);

        if (extension.equals("html")) {
            return "text/html; charset=utf-8";
        }
        if (extension.equals("css")) {
            return "text/css; charset=utf-8";
        }
        if (extension.equals("js")) {
            return "application/javascript; charset=utf-8";
        }
        if (extension.equals("png")) {
            return "image/png";
        }
        if (extension.equals("jpg") || extension.equals("jpeg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }

    private static String getFileExtension(String fileRequested) {
        int dotIndex = fileRequested.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }
        return fileRequested.substring(dotIndex + 1).toLowerCase();
    }

    // // Métodos del Taller 2 para compatibilidad
    // public static void get(String path, BiFunction<HttpRequest, HttpResponse, String> handler) {
    //     services.put(path, handler);
    // }
    
    public static void staticfiles(String path) {
        directory = path;
    }
    
    public static Map<String, String> getDataStore() {
        return dataStore;
    }
    
    // public static Map<String, BiFunction<HttpRequest, HttpResponse, String>> getServices() {
    //     return services;
    // }

    // MÉTODO PRINCIPAL que integra todo
    public static void runServer() throws IOException, URISyntaxException {
    // Solución aquí
        loadServices();
        System.out.println("Servicios registrados:");
        services.forEach((k, v) -> System.out.println(k + " -> " + v.getName()));

        startServer();
    }

}