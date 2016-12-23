

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;



//import java.rmi.registry.Registry;
//import java.util.concurrent.atomic.AtomicInteger;
import java.net.MalformedURLException;

/**
 * This is the client side
 * @author Xin Liu, Saiyi Wang
 * We used a messages.txt to provide the messages of all clients.
 *
 */

public class Client3 {
	private static int procID=3;
	private static int numProcess=5;
	private static int numMessages=6;
	
	public static void main(String[] args) throws AlreadyBoundException, NotBoundException, IOException, InterruptedException{
	         
//		procID = Integer.parseInt(args[0]);
//        procID=0;
//		numProcess = Integer.parseInt(args[1]);
//        numProcess=3;
//		int port = Integer.parseInt(args[0]) + 1098;
        System.setProperty("java.rmi.server.hostname", "localhost");
		     LocateRegistry.createRegistry(1096);
			 Registry registry=LocateRegistry.getRegistry(1096);
		     RemoteServer p = new RemoteServer(procID, numProcess) ;
		     registry.bind("rmi://localhost:1096/RemoteServer3", p);
		     
		     Thread.sleep(2000);
		     startAlgorithm();
	}
		    
public static void startAlgorithm() throws NotBoundException, IOException, InterruptedException{
	Registry registry0 = LocateRegistry.getRegistry("localhost", 1099);
	Registry registry1 = LocateRegistry.getRegistry("localhost", 1098);
	Registry registry2 = LocateRegistry.getRegistry("localhost", 1097);
	Registry registry3 = LocateRegistry.getRegistry("localhost", 1096);
	Registry registry4 = LocateRegistry.getRegistry("localhost", 1095);
	RemoteInterf[] RMI_IDS = new RemoteInterf[numProcess]; 
	
	    RMI_IDS[0] = (RemoteInterf) registry0.lookup("rmi://localhost:1099/RemoteServer0");
		RMI_IDS[1] = (RemoteInterf) registry1.lookup("rmi://localhost:1098/RemoteServer1");
		RMI_IDS[2] = (RemoteInterf) registry2.lookup("rmi://localhost:1097/RemoteServer2");
        RMI_IDS[3] = (RemoteInterf) registry3.lookup("rmi://localhost:1096/RemoteServer3");
        RMI_IDS[4] = (RemoteInterf) registry4.lookup("rmi://localhost:1095/RemoteServer4");
	    
	    RMI_IDS[procID].setId(procID);
		RMI_IDS[procID].setRegistrySet(RMI_IDS);
		RMI_IDS[procID].setNumMessages(numMessages);
    
		BufferedReader buffer = new BufferedReader(new FileReader("messages3.txt"));
		String line = "";
		String[] split_line;
		while ((line = buffer.readLine()) != null) {
			
			split_line = line.split(" ");
			//((RemoteInterf) Naming.lookup("rmi://localhost:1099/RemoteServer" + procID)).addMessage(split_line[0], Integer.parseInt(split_line[1]));
			RMI_IDS[procID].addMessage(split_line[0], Integer.parseInt(split_line[1]));
		}
		
		buffer.close();
		
		Thread.sleep(2000);
		System.out.println("start");
				new Thread ( () -> {
					try {
		while(RMI_IDS[procID].Qlength() != null){
								
				
					Thread.sleep((int)Math.random()*60); // random delay
					RMI_IDS[procID].broadcast();
				/*	while(RMI_IDS[0].getFlag()==false){
						Thread.sleep(5);
					}
*/
                        }
			
			
			} catch (Exception e) {
					e.printStackTrace();
			}	
			}).start();
		
						
					
			
			
	}

}
