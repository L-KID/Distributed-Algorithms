//import java.io.BufferedWriter;
//import java.io.File;
import java.io.FileNotFoundException;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
//import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

//import java.util.ArrayList;



public class RemoteServer extends UnicastRemoteObject implements RemoteInterf{
	private static final long serialVersionUID = 1L;

	private int id;
    private QueueMessage msg_q;
	private Boolean deliveredM;
    private AtomicInteger recNum;
	private RemoteInterf[] proc;
	private QueueMessage messageQ;
	private TreeMap<ScalarClock,AtomicInteger> ackk;
	private int numMsg;
	private AtomicInteger numReceivedMsg;
//	private QueueAck ackQueue;
	
	public RemoteServer(int procID,int numProcess) throws RemoteException{
		super();
		this.id = procID;
		this.deliveredM=false;
		this.msg_q = new QueueMessage();  //messages being broadcast
		this.proc=new RemoteInterf[numProcess];
		this.messageQ=new QueueMessage(); //use to store the received messages
	//	this.ackQueue=new QueueAck();
		this.ackk=new TreeMap<ScalarClock,AtomicInteger>();
		this.numReceivedMsg=new AtomicInteger(0);
	}
	
	public void broadcast() throws FileNotFoundException, UnsupportedEncodingException, RemoteException, InterruptedException, MalformedURLException, NotBoundException{
		
	       
		if(msg_q.peek()!=null){ 	
			     
			        recNum=new AtomicInteger(proc.length);
					for(int i=0;i<proc.length;i++){
					
							RemoteInterf proc_i=proc[i];
//						new Thread(()->{
								try {
							proc_i.receiveMessage(msg_q.peek(),this);
							recNum.decrementAndGet();
						} catch (Exception e) {
							
							e.printStackTrace();
						}
						
//						}).start();  
					} 
					/*
					while(this.recNum.get()>0){
						Thread.sleep(5);
					}*/
			        if(this.recNum.get()==0){
			        	this.msg_q.poll();
			        }
		
		
		}
	}
	
	
	@Override
	public void receiveMessage(Message msg, RemoteInterf sender) throws RemoteException, InterruptedException, MalformedURLException, NotBoundException{
		    this.messageQ.add(msg);  
	//	    this.ackQueue.add(new AckCounter(msg,0));
		    this.numReceivedMsg.incrementAndGet();
		    System.out.println("Process"+this.id+" receives "+msg.msg+".");
		   
			    for(int i=0;i<this.proc.length;i++){
				      proc[i].receiveAck(msg, sender);
			
		    }	
			
		
		
	}
	
	//get the timestamp of the first "message" of this message queue
	@Override
	public void receiveAck(Message msg, RemoteInterf sender) throws RemoteException, InterruptedException{
	//	System.out.println(this.getId()+"receive ack.");
		 if(!this.ackk.containsKey(msg.timestamp)){
			this.ackk.put(msg.timestamp, new AtomicInteger(1));
			System.out.println("Process"+this.getId()+" receives "+this.ackk.get(msg.timestamp).get()+" acknowledge(s) of "+msg.msg+".");
		       }
		 else if(this.ackk.containsKey(msg.timestamp)){
		
		   this.ackk.get(msg.timestamp).incrementAndGet();
		   System.out.println("Process"+this.getId()+" receives "+this.ackk.get(msg.timestamp).get()+" acknowledge(s) of "+msg.msg+".");
			   }
	     if((this.messageQ.peek()!=null)&&(this.ackk.containsKey(this.messageQ.peek().timestamp))){
		    if((this.ackk.get(this.messageQ.peek().timestamp).get()==proc.length)){
		      System.out.println("debug: deliver attempt "+this.messageQ.peek().msg);
		      if((this.numReceivedMsg.get()==numMsg)){
              this.deliver();
		      }
		   }	
	      }
		
	}
	@Override
	public synchronized void deliver()throws RemoteException{
		if(this.messageQ.peek()==null){
			System.out.println("******");
			return;
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       if((this.messageQ.peek()!=null)&&(this.ackk.get(this.messageQ.peek().timestamp).get()==proc.length)){
		Message delivered=this.messageQ.poll();	
		System.out.println("Process "+this.getId()+" delivers message "+delivered.msg+" "+delivered.timestamp.toString()+" at "+LocalDateTime.now()+".");
    
		if(this.messageQ.peek()!=null){
			
			Message temp=this.messageQ.peek();
			System.out.println("try to deliver one more: "+temp.msg);
			if(this.ackk.containsKey(temp.timestamp)&&this.ackk.get(temp.timestamp).get()==proc.length){
				this.deliver();
			}
		}
    }
       else{
    	   return;
    	   
       }
		
	}

	
	@Override
	public ScalarClock getHead() throws RemoteException{
	ScalarClock temp=new ScalarClock(10000,this.id);
		if(msg_q.peek()!=null){
			return msg_q.peek().timestamp;
		}
		
		
			return temp;
		
	}
	
	
	//set process name
	/*@Override
	public void setPName(String n) throws RemoteException{
		this.name = n;
	}*/
	
	//get the name of process
	/*@Override
	public String getPName() throws RemoteException{
		return name;
	}*/
	
	//set process ID
	@Override
	public void setId(int i) throws RemoteException{
		this.id = i;
		//System.out.println(this.id);
	}
	
	@Override 
	public int getId() throws RemoteException{
		return id;
	}
	
	//add message to this message queue
	@Override
	public void addMessage(String msg, int time) throws RemoteException{
		//System.out.println(this.id);
		this.msg_q.add(new Message(msg, new ScalarClock(time, this.id)));
		
	}
	
	public Message Qlength() throws RemoteException{
		
			return this.msg_q.peek();
		
	}
	@Override
	public void setRegistrySet(RemoteInterf[] c)throws RemoteException{
		this.proc=c;
	}
    
	@Override
	public Boolean getFlag()throws RemoteException{
		return this.deliveredM;
	}
	@Override
	public void setNumMessages(int numM)throws RemoteException{
		this.numMsg=numM;
	}
	

}