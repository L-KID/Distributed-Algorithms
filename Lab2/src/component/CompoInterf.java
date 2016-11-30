package component;

import java.rmi.RemoteException;

public interface CompoInterf {

	public void sendRequest() throws RemoteException,InterruptedException;
	public void receiveRequest()throws RemoteException;
}
