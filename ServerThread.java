/** Name - Nivedita Gautam
 * Student ID = xxx
 * */
 
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javafx.application.Platform;

// Server thread to handle a particular client
public class ServerThread extends Thread {

	// Class variables
	private Socket clientSocket = null;
	private String clientName = "";
	private DataInputStream dis = null;
	private DataOutputStream dos = null;

	// Constructor to initialize class variables
	public ServerThread(Socket clientSocket) {

		this.clientSocket = clientSocket;
		try {
			dis = new DataInputStream(clientSocket.getInputStream());
			dos = new DataOutputStream(clientSocket.getOutputStream());
		} catch (Exception e) {
			ServerGUI.addToStatusMsgs("Cannot obtain I/O streams for client - " + clientName);
		}
	}

	// Gets the Op Log from the client this thread is handling
	public String getOpLog() {

		String op = "";
		try {
			if (clientSocket != null && !clientSocket.isClosed() && dos != null) {
				// Notify the client of the POLL Operation
				dos.writeUTF("POLL");
				ServerGUI.addToStatusMsgs("Polling client - " + clientName + "!!");
			}
			if (clientSocket != null && !clientSocket.isClosed() && dis != null) {
				// get the OpLog sent by the client
				op = dis.readUTF();
				ServerGUI.addToStatusMsgs("Operations recieved from Client - " + clientName + " : " + op);
			}
		} catch (Exception e) {

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					ServerGUI.removeClient(clientName);
					ServerGUI.addToStatusMsgs("Client - " + clientName + " got disconnected!!");
				}
			});
			Server.clientList.remove(this);
		}
		// If client didn't send anything , send empty string
		if (op == null)
			op = "";
		return op;
	}

	// Send the value to the client this thread is handling
	public void sendValtoClient(String val) {

		try {
			if (clientSocket != null && !clientSocket.isClosed() && dos != null) {
				dos.writeUTF(val);
				ServerGUI.addToStatusMsgs("Value sent to Client - " + clientName + " : " + val);
			}
		} catch (Exception e) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					ServerGUI.removeClient(clientName);
					ServerGUI.addToStatusMsgs("Client - " + clientName + " got disconnected!!");
				}
			});
			Server.clientList.remove(this);
		}
	}

	// Run method is executed when the Server thread is started
	@Override
	public void run() {
		try {
			if (!clientSocket.isClosed() && clientSocket != null && dos != null) {
				// Read the client's name
				clientName = dis.readUTF();
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						// Add the name of the client to the list to be displayed on the GUI
						ServerGUI.addClient(clientName);
						// Display a message on the Server's GUI
						ServerGUI.addToStatusMsgs("Client - " + clientName + " got connected!!");
					}
				});
			}
		} catch (Exception e) {
			System.out.println("Client's name not recieved.");
		}
	}
}
