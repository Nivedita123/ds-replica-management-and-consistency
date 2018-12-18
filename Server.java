/** Name - Nivedita Gautam
 * Student ID = xxx
 * */
 
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

	// Port on which server is running
	final static int PORT_NUMBER = 4444;
	
	// Initialize value of shared variable on sever
	static String SHARED_VARIABLE = 1+"";

	private static ServerSocket server = null;
	
	// List of the ServerThreads handling the clients
	static List<ServerThread> clientList = new ArrayList<>();

	@Override
	public void run() {

		try {
			// Server is listening for connection on Port 4444
			server = new ServerSocket(PORT_NUMBER);

			// Keep accepting connections from clients
			while (true) {

				// Accept connection from client
				Socket clientSocket = server.accept();

				// Assign the client to a ServerThread
				Thread thread = new ServerThread(clientSocket);
				// Add the ServerThread object to the clientList
				clientList.add((ServerThread) thread);
				thread.start();
			}

		} catch (IOException e) {

			// If the port number is busy or not available, terminate the program
			System.out.println("Port " + PORT_NUMBER + " is busy!! Please use another port.");
			System.out.println("Terminating..");
			System.exit(0);
		}
	}
}
