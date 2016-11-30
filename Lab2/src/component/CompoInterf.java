package component;

import java.rmi.Remote;
import java.rmi.RemoteException;

import request.Request;

public interface CompoInterf extends Remote{

	public void sendRequest() throws RemoteException,InterruptedException;
	public void receiveRequest(Request req)throws RemoteException;
	public void receiveGrant()throws RemoteException,InterruptedException;
	public void receivePostponed()throws RemoteException;
	public void receiveInquire(int procSInquireID)throws RemoteException,InterruptedException;
	public void receiveRelease()throws RemoteException;
	public void receiveRelinquish() throws RemoteException;
	public void setID(int i)throws RemoteException;
	public int getID()throws RemoteException;
}
