
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;



public interface RemoteInterf extends Remote {

    public void broadcast() throws RemoteException, InterruptedException, FileNotFoundException, UnsupportedEncodingException, MalformedURLException, NotBoundException;
//	public void receiveJudge(Message msg, int procID) throws RemoteException, InterruptedException, MalformedURLException, NotBoundException;
 //   public void acknowledge(Message msg, int procID) throws RemoteException;
    
    public ScalarClock getHead() throws RemoteException;
   // public void setPName(String n) throws RemoteException;
    //public String getPName() throws RemoteException;
    public void setId(int i) throws RemoteException;
    public void addMessage(String msg, int time) throws RemoteException;
    //public void conF(RemoteInterf[] RIS) throws RemoteException;
    public Message Qlength() throws RemoteException;
    public void deliver()throws RemoteException;
    public void setRegistrySet(RemoteInterf[] c) throws RemoteException;
    public Boolean getFlag()throws RemoteException;
	public int getId() throws RemoteException;

	public void receiveAck(Message msg, RemoteInterf sender) throws RemoteException,InterruptedException;
	public void receiveMessage(Message msg, RemoteInterf sender)throws RemoteException, InterruptedException, MalformedURLException, NotBoundException;
	public void setNumMessages(int numM)throws RemoteException;
}
