package interfac;

import local.Message;
import local.TieBreaker;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterfac extends Remote{
   
	
	public void broadcast() throws RemoteException,InterruptedException;
	public void receive(Message m2, RemoteInterfac sender) throws RemoteException;
	public void acknowledge(Message m3,RemoteInterfac sender)throws RemoteException;
	public void addMessage(String n,int time)throws RemoteException;
	public int getID()throws RemoteException;
	public void deliver() throws RemoteException;
	public TieBreaker getHead() throws RemoteException;
	public void setID(int i)throws RemoteException;
	public void setRegister(RemoteInterfac[] ri)throws RemoteException;
	public void setName(String name)throws RemoteException;
}
