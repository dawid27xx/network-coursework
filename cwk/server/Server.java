import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class Server {

    private ServerSocket server = null;
    private ExecutorService service = null;

    public Server() {
        try {
            server = new ServerSocket(9100);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void runServer() {
        try {
            // Initialise log.txt file
            File logfile = new File("log.txt");

            // Initialise the executor.
            service = Executors.newFixedThreadPool(20);

            // For each new client, submit a new handler to the thread pool.
            while (true) {
                Socket client = server.accept();
                service.submit(new ClientHandler(client));
            }
            // check for any errors
        } catch (IOException e) {
            System.out.println("Error: " + e);
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    System.out.println("Error closing server: " + e);
                }
            }
            if (service != null) {
                service.shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.runServer();
        } catch (Exception e) {
            System.out.println("Error running server: " + e);
        }
    }
}
