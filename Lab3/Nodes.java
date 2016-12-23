import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;



public class Nodes extends UnicastRemoteObject implements NodesInterf{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1136974069944651871L;
	//public variables
	private boolean someElected;
//	private NodesInterf[] proc;
	private List<Integer> untraversed;
	private int mmD;
	private int procNum;
	//candidate variables
	private AtomicInteger rcvKill;
	private AtomicInteger KILLedSta;
	private AtomicInteger sendCapNum;
	private AtomicInteger candircvAck;
	private AtomicInteger candisendAck;
	
	private Message candiMsg;
	private int candiLevel;
	private int candiID;
	private boolean killed;
	private boolean elected;
	
	//ordinary variables
	private AtomicInteger ordircvAck;
	private AtomicInteger ordisendAck;
	private AtomicInteger timesCaptured;
	private AtomicInteger sendKill;
	private AtomicInteger rcvCapNum;
	
	private int potentialOwnerLink;  //potential_owner link
	private int ownerLink;      //owner link
	private Message currKeepMsg;//(level,owner_id)
	private Message potentialKeepMsg;

	
	
	public Nodes(int ascandiID, int numOfProcs,int m) throws RemoteException, AlreadyBoundException, MalformedURLException{
		super();
		//public variables
		this.KILLedSta=new AtomicInteger(0);
		this.ordircvAck=new AtomicInteger(0);
		this.ordisendAck=new AtomicInteger(0);
		this.candircvAck=new AtomicInteger(0);
		this.candisendAck=new AtomicInteger(0);
		this.rcvCapNum=new AtomicInteger(0);
		this.sendCapNum=new AtomicInteger(0);
		this.rcvKill=new AtomicInteger(0);
		this.sendKill=new AtomicInteger(0);
		this.someElected=false;
//		this.proc=new NodesInterf[numOfProcs];
		this.untraversed=new ArrayList<Integer>();
		this.mmD=m;
		this.procNum=numOfProcs;
		//candidate variables
		this.elected=false;
		this.killed=false;
		this.candiID=ascandiID;
		this.candiLevel=0;
		this.candiMsg=new Message(candiLevel,candiID);
		//ordinary variables
		this.timesCaptured=new AtomicInteger(0);
		
		this.ownerLink=0;   
		this.currKeepMsg = new Message(0, 0);//initialize
		this.potentialKeepMsg=new Message(0,0);
//		this.oldOwnerID= 0;   //initialize: owner=null
		
		
		
	}
	/**********************************************************************************
	 * 	Ordinary Process
	 *******************************************************************************/
	
	@Override
	public void ordinaryRcvCap(Message msg,int senderID) throws RemoteException{
		this.rcvCapNum.incrementAndGet();
		//msg: attempt to capture this
		if (this.currKeepMsg.compareTo(msg)<0){
						
			this.potentialKeepMsg = new Message(msg.Level, msg.ID);//id in current keep message may not be the same as the owner link
			this.potentialOwnerLink=msg.ID;
			
			if(this.ownerLink == 0){   //this doesn't have a current owner
				System.out.println("Current owner is null and set owner to node"+potentialOwnerLink+" directly.");
				this.timesCaptured.incrementAndGet();
				this.ownerLink = this.potentialOwnerLink;  //send ack back directly
				this.currKeepMsg=this.potentialKeepMsg;
				
			}
			if(this.ownerLink==this.potentialOwnerLink){
				this.ordisendAck.incrementAndGet();
			}
			else{
				this.sendKill.incrementAndGet();
			}
			this.ordinarySend(this.ownerLink, this.potentialKeepMsg);
			
			
		}
		
	}
	@Override
	public void ordinaryRcvAck(Message msg,int senderID)throws RemoteException{
            if((this.potentialKeepMsg.compareTo(msg)==0)&&(senderID==this.ownerLink)){   //receive ack from current owner
			this.ordircvAck.incrementAndGet();
			System.out.println("Previous owner is node"+this.ownerLink+" and the new owner is node"+potentialOwnerLink+".");
			this.ordisendAck.incrementAndGet();
			this.timesCaptured.incrementAndGet();
			this.ownerLink = this.potentialOwnerLink; 
			this.ordinarySend(this.ownerLink,this.currKeepMsg);
		}
	}
	@Override
	public void ordinarySend(int owner_Link, Message currentKeepMsg)throws RemoteException{
		try {
			NodesInterf RMI_ID = (NodesInterf)Naming.lookup("rmi://localhost:1099/RemoteObject"+owner_Link);
			RMI_ID.candidateReceive(currentKeepMsg,this.candiID);//"candiID" is used as the identifier of the link.
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	

/**********************************************************************************
 * 	Candidate Process
 *******************************************************************************/
	@Override
	public void candidateSend()throws RemoteException{
		Collections.shuffle(this.untraversed);
		this.candiMsg=new Message(this.candiLevel,this.candiID);
		this.sendCapNum.incrementAndGet();
		System.out.println("Node"+this.candiID+" attempts to capture node"+this.untraversed.get(0)+" with msg:"+this.candiMsg.toString());
		
		try {
			 
			NodesInterf RMI_ID = (NodesInterf)Naming.lookup("rmi://localhost:1099/RemoteObject"+untraversed.get(0));
			RMI_ID.ordinaryRcvCap(this.candiMsg,this.candiID);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void candidateReceive(Message msgKillAttempt,Integer ordiLink)throws RemoteException {
		// TODO Auto-generated method stub
		if((this.candiID==msgKillAttempt.ID)&&(this.killed==false)){
			this.candircvAck.incrementAndGet();
			System.out.println("Node"+this.candiID+" captures node"+ordiLink+" sucessfully.");
			this.candiLevel=candiLevel+1;
			this.untraversed.remove(ordiLink);//It's the element being removed in the brackets.
			if(this.untraversed.isEmpty()&&this.candiID==this.mmD){
				this.elected=true;
				System.out.println("Node"+this.candiID+" has been elected.");
				for(int i=0;i<procNum;i++){
					
					try {
						NodesInterf	RMI_ID = (NodesInterf)Naming.lookup("rmi://localhost:1099/RemoteObject"+(i+1));
						RMI_ID.receiveElected(this.candiID);
					} catch (MalformedURLException | NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
		}
		else{
			if((this.candiID>msgKillAttempt.ID)){
				//doing nothing;
				this.rcvKill.incrementAndGet();
			}
			else if(this.candiID<msgKillAttempt.ID){
				this.rcvKill.incrementAndGet();
				this.KILLedSta.incrementAndGet();
				this.candisendAck.incrementAndGet();
				this.killed=true;
				System.out.println("Node"+this.candiID+" has been killed by node"+msgKillAttempt.ID+".");
				
				try {
					NodesInterf  RMI_ID = (NodesInterf)Naming.lookup("rmi://localhost:1099/RemoteObject"+(ordiLink));
					RMI_ID.ordinaryRcvAck(msgKillAttempt,this.candiID);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	
/**********************************************************************************
 * 	Public Methods
 *******************************************************************************/
	@Override
	public void receiveElected(int electedID)throws RemoteException{
		this.someElected=true;
		System.out.println("(Other): node"+electedID+" has been elected.");
	}
//	@Override
//	public void setRegistry(NodesInterf[] all) throws RemoteException{
//		this.proc=all;
//	}
	
	@Override
	public void initUntraSet(Integer[] allID)throws RemoteException{
		for(int i:allID){
			if(i!=this.candiID){
		       this.untraversed.add(i);
			  }
		}
		System.out.println("Initially the number of untraversed nodes is "+this.untraversed.size()+".");
	}
	@Override
	public boolean anyUntraversed()throws RemoteException{
		return !this.untraversed.isEmpty();  //return true if there is any untraversed nodes.
	}
	@Override
	public boolean isKilled()throws RemoteException{
		return this.killed;
	}
	@Override
	public boolean isElected()throws RemoteException{
		return this.elected;
	}
	@Override
	public boolean someisElected()throws RemoteException{
		return this.someElected;
	}
	/**********************************************************************************
	 * 	Print for candidate processes
	 *******************************************************************************/
	@Override
	public int getLevel()throws RemoteException{
		return this.candiLevel;
	}
	@Override
	public int getsendCapNum()throws RemoteException{
		return this.sendCapNum.get();
	}
	@Override
	public int getrcvKill()throws RemoteException{
		return this.rcvKill.get();
	}
	@Override
	public int getcandircvAck()throws RemoteException{
		return this.candircvAck.get();
	}
	@Override
	public int getcandisendAck()throws RemoteException{
		return this.candisendAck.get();
	}
	@Override
	public int getKILLedSta()throws RemoteException{
		return this.KILLedSta.get();
		}
	
	/**********************************************************************************
	 * 	Print for ordinary processes
	 *******************************************************************************/
	@Override
	public int getordircvAck()throws RemoteException{
		return this.ordircvAck.get();
	}
	@Override
	public int getordisendAck()throws RemoteException{
		return this.ordisendAck.get();
	}
	@Override   //for both candidate and ordinary
	public int gettimesCaptured()throws RemoteException{
		return this.timesCaptured.get();
	}
	@Override
	public int getsendKill()throws RemoteException{
		return this.sendKill.get();
	}
	@Override
	public int getrcvCapNum()throws RemoteException{
		return this.rcvCapNum.get();
	}
	

}
