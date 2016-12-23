//package createregistry;

//import datastructure.Request;
//import datastructure.RequestQueue;
//import datastructure.ScalarClock;

//import interf.CompoInterf;

import java.rmi.RemoteException;
//import java.rmi.registry.LocateRegistry;
//import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicInteger;



public class CompoInterfImpl extends UnicastRemoteObject implements CompoInterf {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4138486202875827055L;

	private int id;
	private AtomicInteger numGrant;
	private Integer[] setID;
	//private Request localrequest;
	
	private CompoInterf[] registrySet;
	private RequestQueue reqQ;   //request queue in the algorithm
	private CompoInterf[] requestSet;
	
	private ScalarClock current_grant;
	//private RequestQueue requestSendQ;//requests originated from ".txt"
	/*
	 * flags
	 */
	private Boolean granted;
	private Boolean inquiring;
	private Boolean postponed;
	private Boolean enteredCS;
	
	
	public CompoInterfImpl(Integer[] sets,int setSize,int i) throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		this.reqQ=new RequestQueue();
		this.requestSet=new CompoInterf[sets.length];
		//this.requestSendQ=new RequestQueue();
		this.id=i;
		this.setID=sets;
		//this.localrequest=new Request(new ScalarClock(times,i));
		
		this.granted = false;
		this.inquiring=false;
		this.postponed=false;
		this.enteredCS=false;
		this.numGrant=new AtomicInteger(0);
		
	}
	
	
	@Override
	public void sendRequest(Request localrequest) throws RemoteException, InterruptedException{
		   this.enteredCS=false;
		   
		   AtomicInteger sendCount=new AtomicInteger(setID.length);
		   
		   for(int i=0;i<setID.length;i++){
		   CompoInterf reqSet_i=requestSet[i];
		   new Thread(()->
		   {
		    	try{
//		    		Thread.sleep((int)(Math.random()*5));
				reqSet_i.receiveRequest(localrequest);
				sendCount.getAndDecrement();
			 }catch(Exception e){
				e.printStackTrace();
			 }
		   }
				 ).start();
		   }
		  
		   while(sendCount.get()>0){
			 Thread.sleep(10);
		   }
		   
		   
		   /*
		   if(sendCount.get()==0){
			 requestSendQ.poll();  //??:finish sending the first request
		   }*/
		
		//num_Grant=0
		
		
	}
	@Override
	public void receiveRequest(Request req)throws RemoteException{
		System.out.println("Client"+this.id+" has received request from client"+req.timestamp.ProcessID+".");
		if(this.granted==false){
			this.granted=true;
			this.current_grant=req.timestamp;
			try {
				registrySet[req.timestamp.ProcessID].receiveGrant(this.id);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		else{
			/*1.if "request" is added in Request, here req->req.timestamp
			 *
			 */
			this.reqQ.add(req);
			if((!req.timestamp.olderThan(this.reqQ.peek().timestamp))||
				(!req.timestamp.olderThan(this.current_grant))	){
				registrySet[req.timestamp.ProcessID].receivePostponed(this.id);
				
			}
			else{
				if(this.inquiring==false){
					this.inquiring=true;
					try {
						registrySet[current_grant.ProcessID].receiveInquire(this.id);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}
	}
	@Override
	public void receiveGrant(int fromID)throws RemoteException, InterruptedException{
		this.numGrant.incrementAndGet();
		System.out.println
		("Request of client"+this.id+" has been granted by client"+fromID);
		System.out.println
		("Request of client"+this.id+" has been granted by "+this.numGrant.get()+" client(s).");
		if(numGrant.get()==setID.length){
			this.postponed=false;
			
			this.numGrant=new AtomicInteger(0);
			System.out.println("Client"+this.id+" has entered the critical section.");
			
			/*
			 * entering Critical Section
			 */
			Thread.sleep((int)Math.random()*1000);
			
			this.enteredCS=true;
			for(int i=0;i<setID.length;i++){
				CompoInterf reqSet_i=this.requestSet[i];
				reqSet_i.receiveRelease(this.id);
			}
			
		//System.out.println("Client"+this.id+" has released the critical section.");
		}
	}
	@Override
	public void receivePostponed(int fromID)throws RemoteException{
		this.postponed=true;
		System.out.println("Client"+this.id+" has been postponed by client"+fromID);
	}
	@Override
	public void receiveInquire(int procSInquireID)throws RemoteException, InterruptedException{
		int findPosInRS=0;
		for(int i=0;i<this.setID.length;i++){
			if (this.requestSet[i].getID()==procSInquireID){
				findPosInRS=i;
                break;
			}
		}
		System.out.println("Client"+this.id+" has been inquired by client"+procSInquireID);
		while((this.postponed==false)&&(this.numGrant.get()<setID.length)){
			Thread.sleep(5);
		}
		if(postponed==true){
			this.numGrant.decrementAndGet();
			this.requestSet[findPosInRS].receiveRelinquish(this.id);
		}
	}
	@Override
	public void receiveRelease(int fromID)throws RemoteException{
		this.granted=false;
		this.inquiring=false;
		System.out.println("Client"+this.id+" received release from client"+fromID);
		if(this.reqQ.peek()!=null){
			this.current_grant=this.reqQ.poll().timestamp;
			this.granted=true;
			try {
				registrySet[this.current_grant.ProcessID].receiveGrant(this.id);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public void receiveRelinquish(int fromID) throws RemoteException{
		this.inquiring=false;
		this.granted=false;
		System.out.println("Client"+this.id+" received relingquish from client"+fromID);
		Request req=new Request(this.current_grant);
		this.reqQ.add(req);
		this.current_grant=this.reqQ.poll().timestamp;
		this.granted=true;
		try {
			registrySet[current_grant.ProcessID].receiveGrant(this.id);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void setRegistrySet(CompoInterf[] c)throws RemoteException{
		this.registrySet=c;
	}
	@Override
	public void setRequestSet(CompoInterf[] c)throws RemoteException{
		for(int i=0;i<this.setID.length;i++){
			requestSet[i]=c[setID[i]];
		}
	}
	/*public void setID(int i)throws RemoteException{
		this.id=i;
	}
	*/
	@Override
	public int getID()throws RemoteException{
		return this.id;
	}
	@Override
	public boolean getEnteredCS() throws RemoteException{
		return this.enteredCS;
	}

}

