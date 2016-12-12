package client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import interf.RemoteInterf;
import server.RemoteServer;

//import java.rmi.registry.Registry;
//import java.util.concurrent.atomic.AtomicInteger;
import java.net.MalformedURLException;

/**
 * This is the client side
 * @author Xin Liu, Saiyi Wang
 * We used a messages.txt to provide the messages of all clients.
 *
 */

public class Client {
	private static int procID;
	private static int numProcess;
	
	public static void main(String[] args) throws NotBoundException, IOException, InterruptedException{
	         
//		procID = Integer.parseInt(args[0]);
        procID=0;
//		numProcess = Integer.parseInt(args[1]);
        numProcess=2;
//		int port = Integer.parseInt(args[0]) + 1098;
        System.setProperty("java.rmi.server.hostname", "localhost");
		     LocateRegistry.createRegistry(1099);
		     RemoteServer p = new RemoteServer(procID, numProcess) ;
		     Naming.rebind("rmi://localhost:1099/RemoteServer" + procID, p);
		     
		     Thread.sleep(3000);
		     startAlgorithm();
	}
		    
public static void startAlgorithm() throws NotBoundException, IOException, InterruptedException{
	Registry registry = LocateRegistry.getRegistry("localhost", 1099);
	RemoteInterf[] RMI_IDS = new RemoteInterf[registry.list().length]; 
	for(int i=0; i<registry.list().length; i++){
	    RMI_IDS[i] = (RemoteInterf) registry.lookup(registry.list()[i]);
    }
    for(int i=0; i<RMI_IDS.length; i++){
	    
	    RMI_IDS[i].setId(i);
    }
		BufferedReader buffer = new BufferedReader(new FileReader("test/messages.txt"));
		String line = "";
		String[] split_line;
		while ((line = buffer.readLine()) != null) {
			
			split_line = line.split(" ");
			//((RemoteInterf) Naming.lookup("rmi://localhost:1099/RemoteServer" + procID)).addMessage(split_line[0], Integer.parseInt(split_line[1]));
			((RemoteInterf) registry.lookup(registry.list()[0])).addMessage(split_line[0], Integer.parseInt(split_line[1]));
		}
		
		buffer.close();
		
		
		System.out.println("start");
		
		 while(RMI_IDS[0].Qlength() != null){
					new Thread ( () -> {					
						try {
							Thread.sleep((int)(Math.random()*4000)); // random delay
							RMI_IDS[0].broadcast();
							

						} catch (Exception e) {
							e.printStackTrace();
			 	 		}
					
					
					}).start();
				}

						
					
			
			
	}

}
