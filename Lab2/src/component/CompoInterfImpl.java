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
	private AtomicInteger numGrant;
	private Integer[] setID;
	private Request localrequest;
	
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
	
	
	public CompoInterfImpl(Integer[] sets,int setSize,int i, int times) throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		this.reqQ=new RequestQueue();
		this.requestSet=new CompoInterf[sets.length];
		//this.requestSendQ=new RequestQueue();
		this.id=i+1;
		this.setID=sets;
		this.localrequest=new Request(new ScalarClock(times,i));
		
		this.granted = false;
		this.inquiring=false;
		this.postponed=false;
		this.numGrant=new AtomicInteger(0);
		
	}
	@Override
	public void sendRequest() throws RemoteException, InterruptedException{
		if(this.localrequest!=null){
		   AtomicInteger sendCount=new AtomicInteger(setID.length);
		   for(int i=0;i<setID.length;i++){
		   CompoInterf reqSet_i=requestSet[i];
		   new Thread(()->
		   {
		    	try{
				reqSet_i.receiveRequest(this.localrequest);
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
		   System.out.println("Request of"+this.id+"has been sent.");
		   /*
		   if(sendCount.get()==0){
			 requestSendQ.poll();  //??:finish sending the first request
		   }*/
		}
		//num_Grant=0
		
		
	}
	@Override
	public void receiveRequest(Request req)throws RemoteException{
		if(this.granted==false){
			this.current_grant=req.timestamp;
			try {
				registrySet[req.timestamp.ProcessID].receiveGrant();
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
				registrySet[req.timestamp.ProcessID].receivePostponed();
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
	public void receiveGrant()throws RemoteException, InterruptedException{
		this.numGrant.incrementAndGet();
		if(numGrant.get()==setID.length){
			this.postponed=false;
			/*
			 * entering Critical Section
			 */
			Thread.sleep((int)Math.random()*500);
			for(int i=0;i<setID.length;i++){
				CompoInterf reqSet_i=this.requestSet[i];
				reqSet_i.receiveRelease();
			}
		}
	}
	@Override
	public void receivePostponed()throws RemoteException{
		this.postponed=true;
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
		while((this.postponed==false)&&(this.numGrant.get()<setID.length)){
			Thread.sleep(5);
		}
		if(postponed==true){
			this.numGrant.decrementAndGet();
			this.requestSet[findPosInRS].receiveRelinquish();
		}
	}
	@Override
	public void receiveRelease()throws RemoteException{
		this.granted=false;
		this.inquiring=false;
		if(this.reqQ!=null){
			this.current_grant=this.reqQ.poll().timestamp;
			this.granted=true;
			try {
				registrySet[this.current_grant.ProcessID].receiveGrant();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public void receiveRelinquish() throws RemoteException{
		this.inquiring=false;
		this.granted=false;
		Request req=new Request(this.current_grant);
		this.reqQ.add(req);
		this.current_grant=this.reqQ.poll().timestamp;
		this.granted=true;
		try {
			registrySet[current_grant.ProcessID].receiveGrant();
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
	
	

}
