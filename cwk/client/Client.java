import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Client {
	private Socket socket;
	private PrintWriter out;
	private static BufferedReader in;

	public static void main(String[] args) {
		if (args.length == 0) {
			// No arguments
			System.out.println("Usage: java Client list or put fname");
			System.exit(1);
		}

		// connect to the server
		Client client = new Client();
		client.connect();

		// list functionality
		if (args[0].equals("list") && args.length == 1) {
			client.list(client);

		// upload functionality
		} else if (args[0].equals("put") && args.length == 2) {
			client.put(client, args);

		} else {
			// incorrect arguments
			System.out.println("Usage: java Client list or put fname");
			System.exit(1);
		}
		// close the connection
		client.out.close();
	}

	public void list(Client client) {
		client.out.println("list");
		// create an array for the list of files
		List<String> list = new ArrayList<String>();
		String line;
		int i = 0;
		// read in all the files
		try {
			while ((line = in.readLine()) != null) {
				list.add(line);
				i++;
			}
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
		// return all the files
		System.out.println("Listing " + i + " file(s):");
		for (String s : list) {
			System.out.println(s);
		}
	}

	public void put(Client client, String args[]) {
		// open file to send over
		String fname = args[1];
		File file = new File(fname);

		// check if file exists
		if (!file.exists()) {
			System.out.println("Cannot open local file " + fname + " for reading.");
			System.exit(1);
		}

		// send the command and file name to the server
		String line;
		client.out.println("put " + fname);

		// send file
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(file));
			while ((line = fileReader.readLine()) != null) {
				client.out.println(line);
			}
			// denote end of file
			client.out.println("EOF");
			fileReader.close();
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
		// read in response
		try {
			line = in.readLine();
			System.out.println(line);
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
	}

	public void connect() {
		// connect to the server
		try {
			// initiate connection and output stream
			socket = new Socket("localhost", 9100);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			System.out.println("Error: Connecting to server");
		}
	}
}