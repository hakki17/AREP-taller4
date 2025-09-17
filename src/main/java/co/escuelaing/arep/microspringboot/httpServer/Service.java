package co.escuelaing.arep.microspringboot.httpServer;

/**
 *
 * @author maria.sanchez-m
 */
public interface Service {
    
    public String invoke(HttpRequest req, HttpResponse res);
    
}
