import java.rmi.server;

public class Server {
    
    public static void main(String[] args) {
        try {
            CalculatorImplementation obj = new CalculatorImplementation();

            Naming.rebind(name: "rmi://localhost:5000/Calculator", obj);

            System.out.println("Calculator Service is running...");
        }
         catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}