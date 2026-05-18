import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

// Create a Book Object
class Book {
    String title;
    String author;
    String status;

    public Book(String title, String author, String status) {
        this.title = title;
        this.author = author;
        this.status = status;
    }
}

public class LibraryApp {
    // Create an in-memory "Database"
    static List<Book> libraryDB = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        // Populate the database with library records
        libraryDB.add(new Book("Introduction to Algorithms", "Thomas H. Cormen", "Available"));
        libraryDB.add(new Book("Clean Code", "Robert C. Martin", "Checked Out"));
        libraryDB.add(new Book("Design Patterns", "Erich Gamma", "Available"));
        libraryDB.add(new Book("The Pragmatic Programmer", "Andrew Hunt", "Available"));
        libraryDB.add(new Book("Database System Concepts", "Abraham Silberschatz", "Checked Out"));

        // Start the server
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); 
        System.out.println("Library Management System with Book Records running on port 8081...");
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Build an actual HTML webpage to display the books
            StringBuilder html = new StringBuilder();
            html.append("<html><head><title>Library Records</title>");
            html.append("<style>");
            html.append("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f4f4f9;}");
            html.append("table { width: 80%; border-collapse: collapse; margin-top: 20px; background-color: white;}");
            html.append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left;}");
            html.append("th { background-color: #0056b3; color: white;}");
            html.append("</style></head><body>");
            
            html.append("<h1>Central Library Management System</h1>");
            html.append("<h3>Current Book Records</h3>");
            html.append("<table><tr><th>Book Title</th><th>Author</th><th>Status</th></tr>");
            
            // Loop through the database and print each book into the table
            for(Book book : libraryDB) {
                html.append("<tr><td>").append(book.title).append("</td><td>")
                    .append(book.author).append("</td><td>")
                    .append(book.status).append("</td></tr>");
            }
            
            html.append("</table>");
            html.append("<br><p><i>Server running in Docker container. Deployed automatically via Jenkins CI/CD.</i></p>");
            html.append("</body></html>");

            String response = html.toString();
            t.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}