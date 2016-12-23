import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;


public class Main5 {
	
	public static void main(String[] args) throws AlreadyBoundException, NotBoundException, IOException, InterruptedException{
		int candidateID=Integer.parseInt(args[0]);
		int procNum=Integer.parseInt(args[1]);
		boolean isCandidate=Boolean.parseBoolean(args[2]);
		int delay=Integer.parseInt(args[3]);
		int mm=Integer.parseInt(args[4]);
		
		System.setProperty("java.rmi.server.hostname", "localhost");
		Thread.sleep(1000);
		LocateRegistry.createRegistry(1099);
//		Registry registry=LocateRegistry.getRegistry(1099);
		
		RemoteObject p = new Nodes(candidateID, procNum,mm);
		Naming.bind("rmi://localhost:1099/RemoteObject"+candidateID, p); 
		Thread.sleep(3000);
		startAlgorithm(candidateID,procNum,isCandidate,delay);
		
	}
	
	public static void startAlgorithm(int candidateID,int procNum,boolean isCandidate,int delayCap) throws NotBoundException, InterruptedException, IOException{
		
		Registry registry = LocateRegistry.getRegistry("localhost", 1099);
//		NodesInterf[] RMI_IDS = new NodesInterf[registry.list().length]; 
	    Integer[] allID=new Integer[registry.list().length];
	    for(int i=0;i<registry.list().length;i++){
	    	allID[i]=i+1;
	    }
	    NodesInterf RMI_ID=(NodesInterf)Naming.lookup("rmi://localhost:1099/RemoteObject"+candidateID);
	    RMI_ID.initUntraSet(allID);
	    
	    
	    
	    System.out.println("start");
	  
	    if(isCandidate){
//			new Thread ( () -> {
				try {
					
						while(RMI_ID.anyUntraversed()){
							if(RMI_ID.isKilled()||RMI_ID.isElected()){
								break;
							}
							Thread.sleep((int)Math.random()*delayCap); // random delay
							RMI_ID.candidateSend();
				            if((RMI_ID.someisElected())&&RMI_ID.getsendCapNum()>(procNum/2)){
				            	break;
				            }
		                }
					
					} catch (Exception e) {
						e.printStackTrace();
						}	
	//			}).start();
			
	    }
	    while(!RMI_ID.someisElected()){
			Thread.sleep(10);
		}
	    if(isCandidate){
		if(RMI_ID.isKilled()){
			System.out.println("Print in main: Node"+candidateID+" is killed.");
		}
		else if(RMI_ID.isElected()){
			System.out.println("Print in main: Node"+candidateID+" is elected.");
		}
		else if(RMI_ID.someisElected()){
			System.out.println("Print in main: Some other node is elected.");
		}
		System.out.println("====================statistics=======================");
		System.out.println("Node"+candidateID+" reached the highest level: "+RMI_ID.getLevel());
		System.out.println("Node"+candidateID+" received kills messages: "+RMI_ID.getrcvKill());
		System.out.println("Node"+candidateID+" is killed:"+RMI_ID.getKILLedSta()+" times.");
		System.out.println("Node"+candidateID+" sent capture messages: "+RMI_ID.getsendCapNum()); 
		System.out.println("Node"+candidateID+" received acknowledgements as candidate: "+RMI_ID.getcandircvAck());
		System.out.println("Node"+candidateID+" sent acknowledgements as candidate: "+RMI_ID.getcandisendAck());
	    }
	    System.out.println("====================statistics=======================");
	    System.out.println("Node"+candidateID+" receives acknowledgements as ordinary: "+RMI_ID.getordircvAck());
	    System.out.println("Node"+candidateID+" sends acknowledgements as ordinary: "+RMI_ID.getordisendAck());
	    System.out.println("Node"+candidateID+" is captured: "+RMI_ID.gettimesCaptured()+" times.");
	    System.out.println("Node"+candidateID+" sent kills: "+RMI_ID.getsendKill());
	    System.out.println("Node"+candidateID+" receives capture attempts: "+RMI_ID.getrcvCapNum());
		}
	}
