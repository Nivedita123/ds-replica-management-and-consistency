/** Name - Nivedita Gautam
 * Student ID = xxx
 * */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

// Client GUI class
public class ClientGUI extends Application {

	// Class variables
	final static int PORT_NUMBER = 4444;
	private Socket clientSocket = null;

	// GUI Elements
	private Text clientHeading = new Text("");
	private Text sharedValueLabel = new Text("");
	private Text clientNameLabel = new Text("\n\nEnter client name:   ");
	private TextField clientNameField = new TextField();
	private Button clientNameSubmit = new Button("    Okay    ");
	private Text inputInstructLabel = new Text("\n\nInput Instructions:\n\n");
	private Text inputInstruct = new Text(
			"1. Input Format: (operator)(space)(operand) ....\n" + "2. Example 1: + 2 - 3 * 5 / 15\n"
					+ "3. Example 2: / 2 * -2 + 60\n" + "4. Operators allowed: - + - * / \n\n\n");
	private Text inputLabel = new Text("Enter input String:    ");
	private TextField input = new TextField();
	private Button inputButton = new Button("    Submit    ");
	private Text operationLogLabel = new Text("Operation Log:\n");
	private TextArea operationLog = new TextArea("");
	private Button exitButton = new Button("    Exit    ");

	// Append text to the logs displayed on the GUI
	public void addtoOpLog(String text) {
		operationLog.appendText("\n" + text);
	}

	// Main function
	public static void main(String[] args) throws IOException {
		// launch the GUI thread
		launch(args);
	}

	// Start method is executed when launch(args) is called
	@Override
	public void start(Stage stage) throws Exception {

		// Start the client thread
		Client client = new Client();

		// creating textflow layout
		TextFlow textFlow = new TextFlow();
		textFlow.setLayoutX(40);
		textFlow.setLayoutY(40);

		// contains list of children to be rendered.
		Group group = new Group(textFlow);

		// setting window height, width and color
		Scene scene = new Scene(group, 1000, 700, Color.WHITE);

		// add the scene to the top level container - stage
		stage.setScene(scene);

		// Setting style properties of the GUi elements and adding them to the textFlow
		// to be displayed
		clientHeading.setFill(Color.GREEN);
		clientHeading.setFont(Font.font("Helvetica", FontWeight.NORMAL, 25));
		textFlow.getChildren().add(clientHeading);

		sharedValueLabel.setFill(Color.BLUEVIOLET);
		sharedValueLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));
		textFlow.getChildren().add(sharedValueLabel);

		clientNameLabel.setFont(Font.font("Helvetica", FontWeight.NORMAL, 18));
		textFlow.getChildren().add(clientNameLabel);
		textFlow.getChildren().add(clientNameField);
		textFlow.getChildren().add(new Text("      "));

		clientNameSubmit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {

				client.setClientName(clientNameField.getText());

				client.start();

				clientHeading.setText("Client - " + client.getClientName() + " is connected to the server on PORT - "
						+ Server.PORT_NUMBER + ".\n\n");

				// Enable the input feild only after client provides a name
				clientNameField.setDisable(true);
				clientNameSubmit.setDisable(true);

				// Client name cannot be modified, hence disable the field
				input.setDisable(false);
				inputButton.setDisable(false);
			}
		});
		textFlow.getChildren().add(clientNameSubmit);

		inputInstructLabel.setFont(Font.font("Helvetica", FontWeight.NORMAL, 18));
		textFlow.getChildren().add(inputInstructLabel);
		inputInstruct.setFont(Font.font("Helvetica", FontWeight.NORMAL, 15));
		textFlow.getChildren().add(inputInstruct);
		inputLabel.setFont(Font.font("Helvetica", FontWeight.NORMAL, 18));
		textFlow.getChildren().add(inputLabel);
		input.setDisable(true);
		textFlow.getChildren().add(input);
		textFlow.getChildren().add(new Text("    "));

		inputButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {

				// Fetch the input provided by user
				String operation = input.getText();

				// Execute operations only if the input string is valid
				if (client.validateInput(operation)) {

					String result = client.executeOperation(operation);

					// Display a message in the log with the new value
					addtoOpLog("Value: " + client.sharedValue + ", Operation: " + operation + ", Result: " + result);

					// Log the operation in persistent storage
					client.logUserInput(operation);
					// and update the local copy
					client.updateLocalCopy(result);
				}
				// Notify the user if the input is invalid
				else {
					addtoOpLog(operation + " : Invalid input string!!");
				}
				input.clear();
			}
		});
		// Initially input button will be diabled, enable only after user provides a
		// name
		inputButton.setDisable(true);
		textFlow.getChildren().add(inputButton);
		textFlow.getChildren().add(new Text("\n\n"));

		operationLogLabel.setFont(Font.font("Helvetica", FontWeight.NORMAL, 20));
		textFlow.getChildren().add(operationLogLabel);
		textFlow.getChildren().add(operationLog);

		exitButton.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
		exitButton.setStyle("-fx-background-color: #ff0000;-fx-text-fill: #ffffff");
		exitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				System.out.println(client.clientName + "exiting");
				System.exit(0);
			}
		});
		// Add to the GUI
		textFlow.getChildren().add(new Text("    "));
		textFlow.getChildren().add(exitButton);

		// display the stage
		stage.show();
	}

	// Client thread to send receive HTTP messages
	public class Client extends Thread {

		// Class variables
		private String sharedValue = "";
		private String clientName = "";
		private String fileName = "";

		// Setter method for private variable clientName
		public void setClientName(String clientName) {
			this.clientName = clientName;
		}

		// Getter method for private variable clientName
		public String getClientName() {
			return this.clientName;
		}

		// Setter method for private variable sharedValue
		public void setSharedValue(double val) {
			this.sharedValue = "" + val;
		}

		// Getter method for private variable sharedValue
		public String getSharedValue() {
			return sharedValue;
		}

		// Refresh the value of shared variable on GUI
		public void refreshSharedValueLabel() {
			sharedValueLabel.setText("Shared value local copy : " + sharedValue);
		}

		// Method to update the local copy of the shared variable in the client's log
		// file
		public void updateLocalCopy(String value) {

			FileReader fileReader = null;
			FileWriter fileWriter = null;

			try {
				fileReader = new FileReader(fileName);
				BufferedReader br = new BufferedReader(fileReader);

				// Read the current value and opLog
				String currentValue = br.readLine();
				String opLog = br.readLine();
				br.close();
				fileReader.close();

				// Update the current value and overwrite in the file
				currentValue = "Value:" + value + "\n";
				fileWriter = new FileWriter(fileName, false);
				fileWriter.write(currentValue + opLog);

				// Close the file when done
				fileWriter.close();

				// Update the value on GUI as well
				sharedValue = value;
				refreshSharedValueLabel();

			} catch (Exception e) {
				try {
					// if any error occurs close the files
					fileReader.close();
					fileWriter.close();
				} catch (Exception e1) {

				}
				System.out.println("ERROR! Log file cannot be opened!");
			}
		}

		// To delete operations once they have been sent to the server
		public void deleteOpsFromLog() {

			FileReader fileReader = null;
			FileWriter fileWriter = null;
			try {
				fileReader = new FileReader(fileName);
				BufferedReader br = new BufferedReader(fileReader);
				// Fetch the current value from file
				String currentValue = br.readLine();
				br.close();
				fileReader.close();
				
				// Overwrite the file and write only the current value, hence deleting the opLog
				fileWriter = new FileWriter(fileName, false);
				fileWriter.write(currentValue + "\n");
				fileWriter.close();

			} catch (Exception e) {
				System.out.println("ERROR! Log file cannot be opened!");
				// Close the resources in case of an exception
				try {
					fileReader.close();
					fileWriter.close();
				} catch (Exception e1) {
				}
			}
		}

		// Initialize the local copy of shared variable in client's log file and display
		// it on client's GUI
		public void initsharedValue() {

			fileName = clientName + ".log";
			File clientFile;

			try {
				clientFile = new File(fileName);
				boolean newFileCreated = clientFile.createNewFile();

				// Client's file does not already exist i.e. client is connecting for the first
				// time
				if (newFileCreated) {

					FileWriter file = new FileWriter(clientFile);

					// Initialize the shared variable as 1 on GUi and in the log file
					sharedValue = "1";
					file.write("Value:" + sharedValue + "\n");
					file.close();

				} else {

					// If client's file already exists i.e client was gone temporarily or crashed
					// earlier
					try (BufferedReader br = new BufferedReader(new FileReader(clientFile))) {

						// Restore the client's session & Update the shared variable from the value in
						// the file
						String line = br.readLine();
						if (line != null) {
							sharedValue = line.split(":")[1];
						}
						String opLog = br.readLine();
						if (opLog == null)
							opLog = "";
						// If client had and operations which were not polled by the server, display on
						// the GUI
						if (opLog.length() > 0)
							addtoOpLog("Unsent Operations from previous session(s): " + opLog);
					}
				}
			} catch (Exception e) {
				System.out.println("ERROR! Log file cannot be created!");
			}
			// Refresh the value on GUI
			refreshSharedValueLabel();
		}

		// Method to validate the user's input
		public boolean validateInput(String input) {

			// Split the input by space
			String arr[] = input.split(" ");

			// odd positions should be operators
			for (int i = 0; i < arr.length; i += 2) {
				if (arr[i].equals("+") || arr[i].equals("*") || arr[i].equals("-") || arr[i].equals("/")) {
				} else {
					// Invalid input
					return false;
				}
			}
			// even positions should be operands
			for (int i = 1; i < arr.length; i += 2) {
				try {
					double d = Double.parseDouble(arr[i]);
				} catch (NumberFormatException nfe) {
					// Input is invalid
					return false;
				}
			}
			// If both checks were successful, input is valid
			return true;
		}

		// Method executes operation based on user's input and returns the result
		public String executeOperation(String input) {

			String output = "";
			// Append local value to the input string
			input = sharedValue + " " + input;
			// Split the input string on space
			ArrayList<String> al = new ArrayList<>(Arrays.asList(input.split(" ")));

			// Keep evaluating operations untill arraylist has only one value left i.e. the
			// result
			while (al.size() > 1) {

				double op1;
				double op2;
				double res;

				// Evaluate divisio operation first
				if (al.indexOf("/") > -1) {

					int d = al.indexOf("/");

					// Get the operands and peform operation
					op1 = Double.parseDouble(al.get(d - 1));
					op2 = Double.parseDouble(al.get(d + 1));
					res = op1 / op2;

					// Remove the current operands and the operations from the list
					al.remove(d + 1);
					al.remove(d);
					al.remove(d - 1);

					// add the result to the list at the same position
					al.add(d - 1, res + "");
				}
				// Next, Evaluate all the multiplications similarly
				else if (al.indexOf("*") > -1) {

					int m = al.indexOf("*");
					op1 = Double.parseDouble(al.get(m - 1));
					op2 = Double.parseDouble(al.get(m + 1));

					res = op1 * op2;
					al.remove(m + 1);
					al.remove(m);
					al.remove(m - 1);

					al.add(m - 1, res + "");

				} // Then perform subtractions
				else if (al.indexOf("-") > -1) {

					int s = al.indexOf("-");

					op1 = Double.parseDouble(al.get(s - 1));
					op2 = Double.parseDouble(al.get(s + 1));
					res = op1 - op2;
					al.remove(s + 1);
					al.remove(s);
					al.remove(s - 1);
					al.add(s - 1, res + "");
				}
				// evaluate the additions
				else if (al.indexOf("+") > -1) {

					int p = al.indexOf("+");
					op1 = Double.parseDouble(al.get(p - 1));
					op2 = Double.parseDouble(al.get(p + 1));
					res = op1 + op2;
					al.remove(p + 1);
					al.remove(p);
					al.remove(p - 1);
					al.add(p - 1, res + "");
				}
			}

			// Round off the input in desired format
			DecimalFormat df = new DecimalFormat("0.0000");
			df.setRoundingMode(RoundingMode.UP);
			output = df.format(Double.parseDouble(al.get(0)));
			output = String.format("%.4f", Double.parseDouble(output));
			return output;
		}

		// Method to log the operation executed in the client's log file
		public void logUserInput(String input) {

			FileWriter fileWriter = null;
			try {
				// open the file and append the operation
				fileWriter = new FileWriter(fileName, true);
				fileWriter.append(input + " ");
				fileWriter.close();
			} catch (Exception e) {
				System.out.println("ERROR! Log file cannot be opened!");
			}

		}

		// Fetch user operations from the client's log file (The log only contains the
		// operations which have not been sent to the server yet)
		public String fetchOpLog() {

			String opLog = "";
			FileReader fileReader = null;
			try {
				fileReader = new FileReader(fileName);
				BufferedReader br = new BufferedReader(fileReader);
				String val = br.readLine();
				// Read the op log from second line of the file
				opLog = br.readLine();
				br.close();
				fileReader.close();
			} catch (Exception e) {
				addtoOpLog("No Operation Log available for Client - " + clientName);
			}
			if (opLog == null)
				opLog = "";
			return opLog;
		}

		// Client thread's run method, execute when client is started after user enter's
		// client name
		public void run() {

			DataInputStream dis = null;
			DataOutputStream dos = null;
			try {

				// Fetch the socket
				clientSocket = new Socket(InetAddress.getByName("localhost"), PORT_NUMBER);
				// and initialize the shared variable 's value
				initsharedValue();

				// Get the I/O stream to send and receive I/O from server
				dis = new DataInputStream(clientSocket.getInputStream());
				dos = new DataOutputStream(clientSocket.getOutputStream());

				// Send client name to server
				dos.writeUTF(clientName);

				// keep performing user operations locally
				while (true) {
					if (!clientSocket.isClosed() && dis != null && dos != null) {

						// detect and execute poll command from server
						String command = dis.readUTF();
						addtoOpLog("POLL detected!!");
						
						// fetch and send Operation log to the server when it POLLS
						String opLog = fetchOpLog();
						dos.writeUTF(opLog);
						addtoOpLog("Sending operations: " + opLog);

						// Fetch the updated value recieved from server and update local copy
						String serverCopy = dis.readUTF();
						addtoOpLog("Value recieved from server: " + serverCopy);
						updateLocalCopy(serverCopy);
						refreshSharedValueLabel();
						
						// Delete the operations from Log after Poll operation is completed
						deleteOpsFromLog();
					}
				}
			}
			// Unable to connect to the server
			catch (Exception e) {
				// If server is offline, close sockets and i/o streams and exit
				clientNameField.setDisable(true);
				clientNameSubmit.setDisable(true);
				addtoOpLog("Server is offline. Press EXIT to close this window.");
				try {
					dis.close();
					dos.close();
					clientSocket.close();
				} catch (IOException e1) {
				}
			}
		}
	}
}
