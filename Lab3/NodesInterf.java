import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NodesInterf extends Remote{
	//Ordinary Process
	public void ordinarySend(int owner_Link, Message currentKeepMsg)throws RemoteException;
	public void ordinaryRcvAck(Message msg,int senderID)throws RemoteException;
	public void ordinaryRcvCap(Message msg,int senderID) throws RemoteException;
	public int getordircvAck()throws RemoteException;
	public int getordisendAck()throws RemoteException;
	public int gettimesCaptured()throws RemoteException;
	public int getsendKill()throws RemoteException;
	public int getrcvCapNum()throws RemoteException;
	//Candidate Process
	public void candidateReceive(Message msg,Integer ordiLink)throws RemoteException;
	public void candidateSend()throws RemoteException;
	public int getrcvKill()throws RemoteException;
	public int getcandircvAck()throws RemoteException;
	public int getcandisendAck()throws RemoteException;
	public int getKILLedSta()throws RemoteException;
	//Public Methods
	public void receiveElected(int electedID)throws RemoteException;
//	public void setRegistry(NodesInterf[] all) throws RemoteException;
	public void initUntraSet(Integer[] allIDs)throws RemoteException;
	public boolean anyUntraversed()throws RemoteException;
	public boolean isKilled()throws RemoteException;
	public boolean isElected()throws RemoteException;
	public boolean someisElected()throws RemoteException;
	public int getLevel()throws RemoteException;
	public int getsendCapNum()throws RemoteException;
	
}
