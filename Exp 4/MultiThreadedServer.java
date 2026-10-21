import java.io.*;
import java.net.*;

public class MultiThreadedServer {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        System.out.println("Server is starting...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            // Continuously accept incoming client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());

                // Assign each client to an isolated handler thread
                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
        }
    }
}

// Handler class responsible for managing individual client communication
class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String clientMessage;
            // Read client input line by line until they disconnect
            while ((clientMessage = input.readLine()) != null) {
                if ("exit".equalsIgnoreCase(clientMessage.trim())) {
                    output.println("Goodbye!");
                    break;
                }
                System.out.println("Received from client: " + clientMessage);
                
                // Echo processed response back to client
                output.println("Server Echo: " + clientMessage.toUpperCase());
            }
        } catch (IOException e) {
            System.err.println("Handler exception: " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("Client disconnected cleanly.");
            } catch (IOException e) {
                System.err.println("Could not close socket: " + e.getMessage());
            }
        }
    }
}
