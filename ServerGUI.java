/** Name - Nivedita Gautam
 * Student ID = xxx
 * */
 
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

// The server's GUI class
public class ServerGUI extends Application {

	// ANd object of server class to access its methids and variables
	private final static Server server = new Server();

	// GUI elements
	private Text serverHeading = new Text("Server is online on port - " + Server.PORT_NUMBER + ". \n\n");
	private Text sharedValueLabel = new Text("Shared Value: " + Server.SHARED_VARIABLE);
	private Text statusMsgsLabel = new Text("\n\nStatus Messages: \n \n ");
	static TextArea statusMsgs = new TextArea("");
	private Button exitButton = new Button("   Exit   ");
	private Button pollButton = new Button("   Poll all clients   ");

	// To store the list of client names currently connected
	static private ObservableList<String> clientList = FXCollections
			.observableArrayList("List of clients currently connected:");

	// listview is used to represent the type of the objects stored in the
	// ObservableList
	static private ListView<String> clientListView = new ListView<String>();

	// getter method for private variable - clientList
	public static ObservableList<String> getClientList() {
		return clientList;
	}

	// Adds an element to clientList
	public static void addClient(String client) {
		clientList.add(client);
	}

	// removes an element from clientList
	public static void removeClient(String client) {
		clientList.remove(client);
	}

	// Appends messages to the TextArea
	public static void addToStatusMsgs(String statusMsg) {
		statusMsgs.appendText(statusMsg + "\n");
	}

	// Refreshes the shared value label on Server GUI
	public void refreshSharedValueLabel() {
		sharedValueLabel.setText("Shared Value server copy: " + Server.SHARED_VARIABLE);
	}

	// Asks all the servers threads to poll their respective clients and get the
	// operations list.
	public void pollClients() {

		String ops = "";

		// Get and append all the opLogs from the clients currents connected
		for (int i = 0; i < Server.clientList.size(); i++) {
			ops = ops + Server.clientList.get(i).getOpLog();
		}

		// Get the result of the operations and update the server copy and the Server's
		// GUI
		String val = executeOperations(ops);
		Server.SHARED_VARIABLE = val;
		refreshSharedValueLabel();

		// Ask all the ServerThreads to send the updated value to their respective
		// clients
		for (int i = 0; i < Server.clientList.size(); i++) {
			Server.clientList.get(i).sendValtoClient(val);
		}

	}

	// To get the result of the operations passed as input
	public String executeOperations(String input) {

		String output = "";

		// Append the server copy to the input operations
		input = Server.SHARED_VARIABLE + " " + input.trim();

		// SPlit the input string on space to get a list of operators and operands
		ArrayList<String> al = new ArrayList<>(Arrays.asList(input.split(" ")));

		// Keep evaluating the operations by taking two operands and replacing them with
		// the result until the list has just one value left
		while (al.size() > 1) {

			double op1;
			double op2;
			double res;

			// Perform all the divide operations first
			if (al.indexOf("/") > -1) {

				int d = al.indexOf("/");
				op1 = Double.parseDouble(al.get(d - 1));
				op2 = Double.parseDouble(al.get(d + 1));
				res = op1 / op2;
				al.remove(d + 1);
				al.remove(d);
				al.remove(d - 1);
				al.add(d - 1, res + "");

			}
			// Perform all the multiplications
			else if (al.indexOf("*") > -1) {

				int m = al.indexOf("*");
				op1 = Double.parseDouble(al.get(m - 1));
				op2 = Double.parseDouble(al.get(m + 1));
				res = op1 * op2;
				al.remove(m + 1);
				al.remove(m);
				al.remove(m - 1);
				al.add(m - 1, res + "");
			}
			// Perform all the subtractions
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
			// Perform all the additions
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

		// Round off the result upto 4 decimal places and return the result
		DecimalFormat df = new DecimalFormat("0.0000");
		df.setRoundingMode(RoundingMode.UP);
		output = df.format(Double.parseDouble(al.get(0)));
		output = String.format("%.4f", Double.parseDouble(output));
		return output;
	}

	// The start method for Server GUI
	@Override
	public void start(Stage stage) throws Exception {

		// creating textflow layout
		TextFlow textFlow = new TextFlow();
		textFlow.setLayoutX(40);
		textFlow.setLayoutY(40);

		// contains list of children to be rendered.
		Group group = new Group(textFlow);

		// setting window height, width and color
		Scene scene = new Scene(group, 1100, 700, Color.WHITE);
		// add the scene to the top level container - stage
		stage.setScene(scene);

		// Setting the title of the GUI window
		stage.setTitle("Multi-threaded Server");

		// setting font color, size, type and style
		serverHeading.setFill(Color.GREEN);
		serverHeading.setFont(Font.font("Helvetica", FontWeight.BOLD, 30));
		// add to textFlow to display on GUI
		textFlow.getChildren().add(serverHeading);

		sharedValueLabel.setFont(Font.font("Helvetica", FontWeight.NORMAL, 25));
		textFlow.getChildren().add(sharedValueLabel);

		// set fonts and add to GUI
		statusMsgsLabel.setFont(Font.font("Helvetica", FontWeight.NORMAL, 25));
		textFlow.getChildren().add(statusMsgsLabel);
		textFlow.getChildren().add(statusMsgs);

		// bind observable list to list view
		clientListView.setItems(clientList);
		clientListView.setStyle("-fx-font-size:17px;-fx-border-width:0");
		// set size of list displayed on GUI
		clientListView.setPrefSize(280, 185);
		// to add a bit of space before the client list
		textFlow.getChildren().add(new Text("                      "));
		// add to textFlow to display on GUI
		textFlow.getChildren().add(clientListView);

		// exit from the program when users clicks on exit button
		exitButton.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
		exitButton.setStyle("-fx-background-color: #ff3300;-fx-text-fill: #ffffff");
		exitButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				System.exit(0);
			}
		});

		// poll button
		pollButton.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
		pollButton.setStyle("-fx-background-color: #008ae6;-fx-text-fill: #ffffff");
		pollButton.setOnAction(new EventHandler<ActionEvent>() {

			// On click action event for pollButton
			@Override
			public void handle(ActionEvent arg0) {
				pollClients();
			}
		});

		// Add all the GUI elements on the textFlow
		textFlow.getChildren().add(new Text("\n\n   "));
		textFlow.getChildren().add(exitButton);
		textFlow.getChildren().add(new Text("       "));
		textFlow.getChildren().add(pollButton);

		// display the stage
		stage.show();
	}

	// main function
	public static void main(String[] args) throws IOException {

		// Start Server thread to listen for incoming connections
		server.start();

		// launch the GUI thread
		launch(args);
	}
}
