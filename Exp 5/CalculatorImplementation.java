import java.rmi.server.UnicastRemoteObject; 
import java.rmi.RemoteException; 

public class CalculatorImplementation extends UnicastRemoteObject implements Calculator { 
    
    public CalculatorImplementation() throws RemoteException { 
        super(); 
    } 
    
    @Override
    public int add(int a, int b) throws RemoteException { 
        return a + b; 
    } 
    
    @Override
    public int subtract(int a, int b) throws RemoteException { 
        return a - b; 
    } 
}
