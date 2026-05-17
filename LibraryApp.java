import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class LibraryApp {
    public static void main(String[] args) throws Exception {
        // Creates a lightweight web server listening on port 8081
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); 
        System.out.println("Library Management System running on port 8081...");
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "Library Management System is running! CI/CD Pipeline deployment successful.";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}