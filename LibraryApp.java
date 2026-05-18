import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

// Create a Book Object
class Book {
    String title, author, status;
    public Book(String title, String author, String status) {
        this.title = title;
        this.author = author;
        this.status = status;
    }
}

public class LibraryApp {
    // In-memory Database
    static List<Book> libraryDB = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        // Start with one default book
        libraryDB.add(new Book("DevOps Handbook", "Gene Kim", "Available"));

        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/", new DisplayHandler());
        server.createContext("/upload", new UploadHandler()); // New endpoint for files
        server.setExecutor(null); 
        System.out.println("Library System with File Upload running on port 8081...");
        server.start();
    }

    // Handles displaying the website
    static class DisplayHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if ("GET".equals(t.getRequestMethod())) {
                StringBuilder html = new StringBuilder();
                html.append("<html><head><title>Library Records</title>");
                html.append("<style>");
                html.append("body { font-family: Arial; margin: 40px; background-color: #f4f4f9;}");
                html.append("table { width: 80%; border-collapse: collapse; margin-top: 20px; background-color: white;}");
                html.append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left;}");
                html.append("th { background-color: #0056b3; color: white;}");
                html.append(".upload-box { background: white; padding: 20px; border: 1px dashed #0056b3; width: 50%;}");
                html.append("</style></head><body>");
                
                html.append("<h1>Central Library Management System</h1>");
                
                // File Upload Section
                html.append("<div class='upload-box'>");
                html.append("<h3>Upload Book Records (.txt or .csv)</h3>");
                html.append("<p>Format: <i>Title, Author, Status</i></p>");
                html.append("<input type='file' id='fileInput' accept='.txt,.csv'> ");
                html.append("<button onclick='uploadFile()'>Upload File</button>");
                html.append("</div>");

                // JavaScript to read the file and send it to Java backend
                html.append("<script>");
                html.append("function uploadFile() {");
                html.append("  var file = document.getElementById('fileInput').files[0];");
                html.append("  if(!file) { alert('Please select a file first!'); return; }");
                html.append("  var reader = new FileReader();");
                html.append("  reader.onload = function(e) {");
                html.append("    fetch('/upload', { method: 'POST', body: e.target.result })");
                html.append("    .then(() => window.location.reload());"); // Reload page after upload
                html.append("  };");
                html.append("  reader.readAsText(file);");
                html.append("}");
                html.append("</script>");

                // Display Table
                html.append("<h3>Current Database</h3>");
                html.append("<table><tr><th>Book Title</th><th>Author</th><th>Status</th></tr>");
                for(Book book : libraryDB) {
                    html.append("<tr><td>").append(book.title).append("</td><td>")
                        .append(book.author).append("</td><td>")
                        .append(book.status).append("</td></tr>");
                }
                html.append("</table></body></html>");

                byte[] response = html.toString().getBytes();
                t.sendResponseHeaders(200, response.length);
                OutputStream os = t.getResponseBody();
                os.write(response);
                os.close();
            }
        }
    }

    // Handles receiving the uploaded file data
    static class UploadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if ("POST".equals(t.getRequestMethod())) {
                InputStream is = t.getRequestBody();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                
                // Read the uploaded file line by line
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(",");
                    if(parts.length >= 3) {
                        libraryDB.add(new Book(parts[0].trim(), parts[1].trim(), parts[2].trim()));
                    }
                }
                
                String response = "Upload Successful";
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}