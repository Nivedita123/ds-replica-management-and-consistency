# Replica management and Consistency

This  system consists of a server and multiple client processes. Each client process will connect to the server over a socket connection and register a user name at the server.

The server and the client are managed with a simple GUI. The messages exchanged between server and client use HTTP format.

Each client implements a simple four-function calculator to handle below operations:
- Addition
- Subtraction
- Multiplication
- Division
- Negative numbers
- Decimals to four digits, rounding up

The required actions are summarized as follows:

### Client
The client will execute the following sequence of steps:

1. Initialize a local copy of the shared value.
2. Connect to the server via a socket.
3. Provide the server with a unique user name.
4. Wait to be polled by server. While waiting:
   - Allow users to input and execute operations on the four-function
calculator.
   - Log user-input in persistent storage.
5. When polled by the server, upload all user-input (sequence of operations) logged since previous poll.
6. Overwrite local copy of shared value with server copy.
7. Notify user local copy has been updated.
8. Repeat at step 4 until the process is killed by the user.

### Server
The server will execute the following sequence of steps:
1. Initialize server copy of shared value.
2. Startup and listen for incoming connections.
3. Print that a client has connected and fork a thread to handle that client.
4. When instructed by the user, poll clients for user-input sequence.
5. Display received input sequences from clients on GUI.
6. Apply user-input sequence to server copy of shared value.
7. Push updated copy of shared value to clients.
8. Begin at step 4 until server is closed by the user.



### Classes

1. `ServerGUI` extends `javafx.application.Application`
2. `HTTPServer` extends `java.lang.Thread`
3. `ServerThread` extends `java.lang.Thread`
4. `ClientGUI` extends `javafx.application.Application`
5. `Client` extends `java.lang.Thread`


### Compilation  

- `javac HTTPServer.java`   
- `javac ServerGUI.java`   
- `javac ClientGUI.java`

### Execution
- `java ServerGUI`
- `java ClientGUI`

### References

1. JavaFX: Working with the JavaFX Scene Graph – https://docs.oracle.com/javase/8/javafx/scene-graph-tutorial/scenegraph.htm
2. Interface `ObservableList<E>` – https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableList.html
3. Introducing Threads in Socket Programming in Java – https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/


### Note
This code was submitted as part of Distributed Systems course assignment at The University of Texas at Arlington to Prof. Chance Eary (https://mentis.uta.edu/explore/profile/chance-eary)
