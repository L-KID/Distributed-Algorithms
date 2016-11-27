package server;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicInteger;

//import java.util.ArrayList;

import algorithmprocess.*;
//import algorithmprocess.ScalarClock;
//import algorithmprocess.QueueMessage;
import interf.RemoteInterf;


public class RemoteServer extends UnicastRemoteObject implements RemoteInterf {
	private static final long serialVersionUID = 1L;
	private int id;
	private int ack;
	private String name;
	private QueueMessage msg_q;
	private RemoteInterf[] RIN;
	private AtomicInteger rounds;
	//private ArrayList<String[]> receiveQ = new ArrayList<String[]>();
	//private String[] receiveM;
	
	public RemoteServer() throws RemoteException{
		super();
		this.msg_q = new QueueMessage();  //sequentialize this process message
		
	}
	
	@Override
	public void broadcast() throws FileNotFoundException, UnsupportedEncodingException, RemoteException, InterruptedException{
	//public void broadcast(){
		//try{
	        if(msg_q.peek()!=null){
			     rounds = new AtomicInteger(RIN.length);
			    for(int i=0;i<RIN.length;i++){
				RemoteInterf RINi = RIN[i];
				new Thread ( () -> { 
				        try{
				        	RINi.receiveJudge(msg_q.peek(),this);//ensure "this" process will not receive message older than ack_message
				        	rounds.getAndDecrement();
				        }catch(Exception e){
							e.printStackTrace();
						}
				}).start();
			}
			while(this.rounds.get() > 0){
				Thread.sleep(1);
			}
			
			if(this.ack == RIN.length){
				Message deliver = msg_q.poll();
				System.out.println("Process "+this.id+" delivered message '"+deliver+"'.");
				for(int i=0;i<(this.id)-1;i++){
					File f = new File("receivedmeaasge_p"+i+".txt");
					try{
						if(f.exists() == false){
					        f.createNewFile();
					        PrintWriter out = new PrintWriter(new FileWriter(f,true)) ;
							out.append(deliver.toString()+"\n");
							out.close();
						}
						else{
							FileWriter fw = new FileWriter(f, true);
							BufferedWriter bw = new BufferedWriter(fw);
							PrintWriter out = new PrintWriter(bw);
							out.append(deliver.toString()+"\n");
							out.close();
						}
				
				
					}catch(IOException e){
						e.getStackTrace();
					}
					//receiveM[i] = deliver.toString();
				}
				for(int j=(this.id)+1;j<RIN.length;j++){ // TODO print out all the received message for each process 
					File f = new File("receivedmeaasge_p"+j+".txt");
					try{
						if(f.exists() == false){
					        f.createNewFile();
					        PrintWriter out = new PrintWriter(new FileWriter(f,true)) ;
							out.append(deliver.toString()+"\n");
							out.close();
						}
						else{
							FileWriter fw = new FileWriter(f, true);
							BufferedWriter bw = new BufferedWriter(fw);
							PrintWriter out = new PrintWriter(bw);
							out.append(deliver.toString()+"\n");
							out.close();
						}
				
				
					}catch(IOException e){
						e.getStackTrace();
				}
			}
				this.ack = 0;
			}
			else{
				this.ack = 0; //delivery not succeed, set ack for a new round
			}
			
		}
		//}catch(Exception e){
			//e.getStackTrace();
		//}
	}
	
	//check if this message is older than the first message of the compared process queue 
	public void receiveJudge(Message msg, RemoteInterf verifyP) throws RemoteException, InterruptedException{
		if(msg.timestamp.olderThan(this.getHead())){
			for(int i=0;i<RIN.length;i++){
				RIN[i].acknowledge(msg,verifyP);
				
			}
		}
		
	}
	
	
	public ScalarClock getHead() throws RemoteException{
		if(msg_q.peek()!=null){
			return msg_q.peek().timestamp;
		}
		return null;
	}
	
	@Override
	public void acknowledge(Message msg, RemoteInterf verifyP) throws RemoteException {
		if(this.getPName().equals(verifyP.getPName())){ 
			this.ack++;
		}
	}
	
	
	@Override
	public void setPName(String n) throws RemoteException{
		this.name = n;
	}
	
	@Override
	public String getPName() throws RemoteException{
		return name;
	}
	
	@Override
	public void setId(int i) throws RemoteException{
		this.id = i;
	}
	
	@Override
	public void addMessage(String msg, int time) throws RemoteException{
		this.msg_q.add(new Message(msg, new ScalarClock(time, this.id)));
	}
    
	public void conF(RemoteInterf[] RIS) throws RemoteException{
		this.RIN = RIS;
	}
	

}
