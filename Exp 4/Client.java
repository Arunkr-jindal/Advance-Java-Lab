import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_HOST = "127.0.0.1"; // Localhost
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        System.out.println("Connecting to server...");
        try (
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            System.out.println("Connected successfully! Type your messages (type 'exit' to quit):");

            String message;
            while (true) {
                System.out.print("> ");
                message = userInput.readLine();
                if (message == null) break;

                // Send data to server
                output.println(message);

                // Read server's reply
                String response = serverInput.readLine();
                System.out.println(response);

                if ("exit".equalsIgnoreCase(message.trim())) {
                    break;
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
        }
    }
}
