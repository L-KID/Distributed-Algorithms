package distributed.algorithms;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Algorithms extends Remote {
    String sayHello() throws RemoteException;
}