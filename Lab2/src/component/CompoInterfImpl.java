package component;

import request.RequestQueue;
import request.Request;
import clock.ScalarClock;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicInteger;



public class CompoInterfImpl extends UnicastRemoteObject implements CompoInterf {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4138486202875827055L;

	private int id;
	private int numGrant;
	
	private RequestQueue reqQ;   //request queue in the algorithm
	private RequestQueue requestSendQ;//requests originated from ".txt"
	private CompoInterf[] requestSet;
	private ScalarClock current_grant;
	/*
	 * flags
	 */
	private Boolean granted;
	private Boolean inquiring;
	private Boolean postponed;
	
	
	protected CompoInterfImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		this.reqQ=new RequestQueue();
	}
	
	public void sendRequest() throws RemoteException, InterruptedException{
		if(requestSendQ!=null){
		   AtomicInteger sendCount=new AtomicInteger(requestSet.length);
		   for(int i=0;i<requestSet.length;i++){
		   CompoInterf reqSet_i=requestSet[i];
		   new Thread(()->
		   {
		    	try{
				reqSet_i.receiveRequest(this.requestSendQ.peek());
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
		   if(sendCount.get()==0){
			 requestSendQ.poll();  //??:finish sending the first request
		   }
		}
		//num_Grant=0
		
		
	}
	public void receiveRequest(Request req)throws RemoteException{
		if(this.granted==false){
			this.current_grant=req.timestamp;
			try {
				requestSet[req.timestamp.ProcessID].receiveGrant();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.granted=true;
		}
		else{
			/*1.if "request" is added in Request, here req->req.timestamp
			 *
			 */
			this.reqQ.add(req);
			if((this.reqQ.peek().timestamp.olderThan(req.timestamp))||
				(this.current_grant.olderThan(req.timestamp))	){
				requestSet[req.timestamp.ProcessID].receivePostponed();
			}
			else{
				if(this.inquiring==false){
					this.inquiring=true;
					try {
						requestSet[current_grant.ProcessID].receiveInquire(this.id);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}
	}
	public void receiveGrant()throws RemoteException, InterruptedException{
		this.numGrant=numGrant+1;
		if(numGrant==requestSet.length){
			this.postponed=false;
			/*
			 * entering Critical Section
			 */
			Thread.sleep((int)Math.random()*500);
			for(int i=0;i<requestSet.length;i++){
				CompoInterf reqSet_i=requestSet[i];
				reqSet_i.receiveRelease();
			}
		}
	}
	public void receivePostponed()throws RemoteException{
		this.postponed=true;
	}
	public void receiveInquire(int procSInquireID)throws RemoteException, InterruptedException{
		while((this.postponed==false)&&(this.numGrant<requestSet.length)){
			Thread.sleep(5);
		}
		if(postponed==true){
			this.numGrant=numGrant-1;
			requestSet[procSInquireID].receiveRelinquish();
		}
	}
	public void receiveRelease()throws RemoteException{
		this.granted=false;
		this.inquiring=false;
		if(this.reqQ!=null){
			this.current_grant=this.reqQ.poll().timestamp;
			this.granted=true;
			try {
				requestSet[this.current_grant.ProcessID].receiveGrant();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void receiveRelinquish() throws RemoteException{
		this.inquiring=false;
		this.granted=false;
		Request req=new Request(this.current_grant);
		this.reqQ.add(req);
		this.current_grant=this.reqQ.poll().timestamp;
		this.granted=true;
		try {
			requestSet[current_grant.ProcessID].receiveGrant();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void setID(int i)throws RemoteException{
		this.id=i;
	}
	public int getID()throws RemoteException{
		return this.id;
	}
	

}
