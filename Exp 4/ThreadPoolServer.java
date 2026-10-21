import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolServer {
    private static final int PORT = 5000;
    private static final int THREAD_POOL_SIZE = 10; // Handle up to 10 concurrent clients smoothly

    public static void main(String[] args) {
        System.out.println("Starting thread-pooled server...");
        
        // Create a reusable pool of worker threads
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                // Blocks until a new client connects
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client accepted: " + clientSocket.getRemoteSocketAddress());

                // Submit the client task to the thread pool instead of spawning a raw Thread
                pool.submit(new PooledClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
        } finally {
            pool.shutdown(); // Gracefully shut down the pool when server stops
        }
    }
}

class PooledClientHandler implements Runnable {
    private final Socket socket;

    public PooledClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String clientMessage;
            while ((clientMessage = input.readLine()) != null) {
                if ("exit".equalsIgnoreCase(clientMessage.trim())) {
                    output.println("Connection closing...");
                    break;
                }
                System.out.println("[" + Thread.currentThread().getName() + "] Message: " + clientMessage);
                output.println("Echo [Processed]: " + clientMessage.toUpperCase());
            }
        } catch (IOException e) {
            System.err.println("Handler error: " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("Client socket closed safely.");
            } catch (IOException e) {
                System.err.println("Close exception: " + e.getMessage());
            }
        }
    }
}
