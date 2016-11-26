package server;

import interfac.RemoteInterfac;
import local.MessageQueue;
import local.Message;
import local.TieBreaker;

import java.util.concurrent.atomic.AtomicInteger;
import java.lang.String;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;


public class RemoteInterfImpl extends 
        UnicastRemoteObject implements RemoteInterfac {
	
	static final long serialVersionUID = 1L;
	
	private int id;
	private MessageQueue sendQ;
	private MessageQueue msggQ;
	private RemoteInterfac[] process;
	private AtomicInteger recNum;
	private AtomicInteger ackCount;
	private String name;
	
	protected RemoteInterfImpl() throws RemoteException{
		super();
		this.msggQ=new MessageQueue();
		
	}
	
	@Override
	public void broadcast() throws RemoteException, InterruptedException{
		if(sendQ.peek()!=null){
			recNum=new AtomicInteger(process.length);
			
			for(int i=0;i<process.length;i++){
			RemoteInterfac process_i=process[i];
			new Thread(()->{
					try{
						process_i.receive(sendQ.peek(),this);
						recNum.getAndDecrement();
					}catch(Exception e){
						e.printStackTrace();
					}
			}).start();
			}
			while(recNum.get()>0){
				Thread.sleep(10);
			}
			for(int i=0;i<process.length;i++){
				RemoteInterfac process_i=process[i];
				process_i.deliver();
			}
		    
		}
		
	    
	    
		
	}
	@Override
	public void receive(Message m2,RemoteInterfac sender)throws RemoteException{
	 if(this.msggQ.peek()!=null){
		if(m2.tieBreaker.olderthan(this.getHead())){
			//case of receiving a message later not included
			this.msggQ.add(m2);
			Message temp=this.msggQ.poll();
			this.msggQ.add(temp);
		}	
		}
	 else{
		    this.msggQ.add(m2);
	 }
	  for(int i=0;i<process.length;i++){
		  
		  RemoteInterfac process_ii=process[i];
		  process_ii.acknowledge(m2,this);
	  }
	  
	  
		  
		
	}
	@Override
	public TieBreaker getHead() throws RemoteException{
	
		return msggQ.peek().tieBreaker;
	 
	}
	
	@Override
	public void acknowledge(Message m3,RemoteInterfac p_wanttodeliver)throws RemoteException{
		if(this.id==p_wanttodeliver.getID()){
		this.ackCount.incrementAndGet();
		}
	}
	@Override
	public void deliver() throws RemoteException{
		if(this.ackCount.get()==process.length){
			  Message delivered=this.msggQ.poll();//deliver the head message
			  this.ackCount=new AtomicInteger(0);
			  System.out.println(delivered.msg+"has been delivered in process "+this.name+" .");
		  }
	}
	@Override
	public void setID(int i)throws RemoteException{
		this.id=i;
	}
	@Override
	public int getID()throws RemoteException{
		return this.id;
	}
	@Override
	public void addMessage(String n,int time)throws RemoteException{
		this.sendQ.add(new Message(n,new TieBreaker(time,this.id)));
		
	}
	@Override
	public void setRegister(RemoteInterfac[] ri)throws RemoteException{
		this.process=ri;
	}
	@Override
	public void setName(String name)throws RemoteException{
        this.name=name;		
	}
	
}
