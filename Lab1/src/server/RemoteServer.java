package server;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

//import java.util.ArrayList;

import algorithmprocess.*;
//import algorithmprocess.ScalarClock;
//import algorithmprocess.QueueMessage;
import interf.RemoteInterf;





import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicInteger;

//import java.util.ArrayList;


public class RemoteServer extends UnicastRemoteObject implements RemoteInterf{
	private static final long serialVersionUID = 1L;
	private int numProcess;
	private int id;
	private int ack =0;
	
	private QueueMessage msg_q;
	
	
	
	public RemoteServer(int procID,int numProcess) throws RemoteException{
		//super();
		this.id = procID;
		this.numProcess = numProcess;
		this.msg_q = new QueueMessage();  //sequentialize this process message
		
		
		
	}
	
		
	
	
	public void broadcast() throws FileNotFoundException, UnsupportedEncodingException, RemoteException, InterruptedException, MalformedURLException, NotBoundException{
		
	       
		if(msg_q.peek()!=null){ 	
			     
			    try {
					for(int i=0;i<numProcess;i++){
					
					
					        	receiveJudge(msg_q.peek(),i);
					        	
					        
                              }
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			
			//receive acknowledgement from all processes
			if(ack == numProcess){
				Message deliver = msg_q.poll();
				System.out.println("Process "+this.id+" delivered message '"+deliver+"' at "+LocalDateTime.now()+".");
				
				//record the messages every process received from every broadcasting into *.txt file
				for(int i=0;i<this.id;i++){
					RemoteInterf rcM = (RemoteInterf) Naming.lookup("rmi://localhost:"+(1099-i)+"/RemoteServer" + i);
					rcM.receiveMessage(deliver,i);
					
					
				}
				for(int j=(this.id)+1;j<numProcess;j++){ 
					RemoteInterf rcM = (RemoteInterf) Naming.lookup("rmi://localhost:"+(1099-j)+"/RemoteServer" + j);
					rcM.receiveMessage(deliver,j);
			
			}
				
				ack = 0;
			}
			else{
				ack = 0; //delivery not succeed, set ack for a new round
			}
			
		
		}
	}
	
	public void receiveMessage(Message message, int procid) throws RemoteException {
		System.out.println("Process " + procid+ " received " +message+ " with timestamp " +message.timestamp.toString());
		
		File f = new File("receivedmeaasge_p"+procid+".txt");
		try{
			if(f.exists() == false){
		        f.createNewFile();
		        PrintWriter out = new PrintWriter(new FileWriter(f,true)) ;
				out.append(message.toString()+"\n");
				out.close();
			}
			else{
				FileWriter fw = new FileWriter(f, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw);
				out.append(message.toString()+"\n");
				out.close();
			}
	
	
		}catch(IOException e){
			e.getStackTrace();
		}
	}
	
	//check if this message is older than the first message of the compared process queue 
	public void receiveJudge(Message msg, int procID) throws RemoteException, InterruptedException, MalformedURLException, NotBoundException{
		RemoteInterf rcP = (RemoteInterf) Naming.lookup("rmi://localhost:"+(1099-procID)+"/RemoteServer" + procID);
		if(msg.timestamp.olderThan(rcP.getHead())){
			
			ack++;
				
			}
		
		
	}
	
	//get the timestamp of the first "message" of this message queue
	public ScalarClock getHead() throws RemoteException{
		if(msg_q.peek()!=null){
			return msg_q.peek().timestamp;
		}
		return null;
	}
	
	//"send" acknowledgement
	/*@Override
	public void acknowledge(Message msg, RemoteInterf verifyP) throws RemoteException {
		if(this.getPName().equals(verifyP.getPName())){ 
			this.ack++;
		}
	}*/
	
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
	
	//add message to this message queue
	@Override
	public void addMessage(String msg, int time) throws RemoteException{
		//System.out.println(this.id);
		this.msg_q.add(new Message(msg, new ScalarClock(time, this.id)));
		
	}
	
	public Message Qlength() throws RemoteException{
		
			return this.msg_q.peek();
		
	}
    
	
	

}
