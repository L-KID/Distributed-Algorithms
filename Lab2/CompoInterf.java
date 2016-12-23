//package interf;

import java.rmi.Remote;
import java.rmi.RemoteException;

//import datastructure.Request;

public interface CompoInterf extends Remote{

	public void sendRequest(Request localrequest) throws RemoteException,InterruptedException;
	public void receiveRequest(Request req)throws RemoteException;
	public void receiveGrant(int fromID)throws RemoteException,InterruptedException;
	public void receivePostponed(int fromID)throws RemoteException;
	public void receiveInquire(int procSInquireID)throws RemoteException,InterruptedException;
	public void receiveRelease(int fromID)throws RemoteException;
	public void receiveRelinquish(int fromID) throws RemoteException;
	//public void setID(int i)throws RemoteException;
	public int getID()throws RemoteException;
	public void setRegistrySet(CompoInterf[] c)throws RemoteException;
	public void setRequestSet(CompoInterf[] c)throws RemoteException;
	public boolean getEnteredCS() throws RemoteException;
}

