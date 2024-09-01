import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;

public class ClientHandler extends Thread {
    private Socket socket = null;

    public ClientHandler(Socket socket) {
        super("ClientHandler");
        this.socket = socket;

    }

    public void run() {
        try {

            // Initialise the input and output streams.
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            FileWriter log = new FileWriter("log.txt", true);

            // Read the command from the client.
            String line;
            line = in.readLine();

            // Handle the command.
            
            if (line.equals("list")) {
                HandleList(out, log);

            } else if (line.startsWith("put")) {
                String filename = line.substring(4);
                HandlePut(in, out, filename, log);

            } else {
                out.println("Error: Invalid command");
                out.close();
                in.close();
                socket.close();
                log.close();
                return;
            }

            // Close the streams and the socket
            out.close();
            in.close();
            socket.close();
            log.close();

        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void HandleList(PrintWriter out, FileWriter log) throws IOException {
        // send over list of files
        File dir = new File("serverFiles");
        File[] files = dir.listFiles();
        for (File file : files) {
            out.println(file.getName());
        }

        logcommand("list", log);
    }

    public void HandlePut(BufferedReader in, PrintWriter out, String filename, FileWriter logcommand)
            throws IOException {
        File dir = new File("serverFiles");
        File file = new File(dir, filename);

        // Check if the file already exists
        if (file.exists()) {
            out.println("Error: Cannot upload file " + "'" + filename + "'" + " as it already exists on server.");
            logcommand("put", logcommand);
            return; // Exit the method if file exists
        }

        // Write to the file
        try (BufferedWriter fileWrite = new BufferedWriter(new FileWriter(file))) {
            String line = in.readLine();
            fileWrite.write(line);
            
            while ((line = in.readLine()) != null) {
                if ("EOF".equals(line)) {
                    break; // Exit the loop when EOF marker is found
                }
                fileWrite.newLine();
                fileWrite.write(line);   
            }

            out.println("Uploaded file " + filename);
            out.flush();
            logcommand("put", logcommand);

        } catch (IOException e) {
            throw new IOException("Error writing to file: " + filename, e);
        }

    }

    public void logcommand(String command, FileWriter log) throws IOException {
        // log the command with the appropriate format
        InetAddress inet = socket.getInetAddress();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd | HH:mm:ss");
        String formattedDate = sdf.format(date);

        log.write(formattedDate + " | " + inet.getHostAddress() + " | " + command + "\n");
        log.flush();
    }
}
