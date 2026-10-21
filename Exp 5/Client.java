import java.rmi.Naming;
public class Client {
    
    public static void main(String[] args) {
        try {
            Calculator obj = (Calculator) Naming.lookup(name: "rmi://localhost:5000/Calculator");

            System.out.println("Addition: " + obj.add(a:5, b:3));

            System.out.println("Subtraction: " + obj.subtract(a:5, b:3));
}
         catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}