/*
 * Distributed Algorithms Lab2
 * Author: Saiyi Wang, Xin Liu
 * 
 * Notes: 1.For different processes, modify processid, lookup localhost, "test/Process0RequestSet.txt"
 *          and "test/clientXrequests.txt";
 *        2.For different total number of processes, modify lookup(address and amount)
 *          and probably also waittime;
 *        3.For different request sets, modify "test/Process0RequestSet.txt";
 *        4.Implement in a single machine, set SMChoice to true; 
 *                         multiple machine, set it to false;
 */

//package createregistry;


import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
//import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

//import datastructure.Request;
//import datastructure.RequestQueue;
//import datastructure.ScalarClock;
//import interf.CompoInterf;


public class CreateRegistry1 {
	
	public boolean SMChoice=true;
	public static int processid=1;
	public int clientNum=4;
	public int setSize=3;
	public int port=1099;
	public int waittime=10;//in ms

	public void main() throws FileNotFoundException, RemoteException, NotBoundException, InterruptedException {
		// TODO Auto-generated method stub
/*		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
*/		
		
		
/*		
		Scanner s = new Scanner(System.in);
		System.out.println("Please enter the number of clients:");
		clientNum =s.nextInt();
		System.out.println("Please choose request set size (between " + (int) Math.ceil(Math.sqrt(clientNum)) + " and " + clientNum +").");
		int setSize = s.nextInt();
		System.out.println("Please check the clientrequestset.txt to match client numbers " +clientNum+ " and request set size " + setSize + ").");
		s.close();
*/		
		Scanner requestCFile = new Scanner(new File("test/Process1RequestSet.txt"));
//		Integer[][] requestCSet = new Integer[clientNum][setSize];
		Integer[] requestCSet=new Integer[setSize];
		
		int i=0;
		requestCFile.useDelimiter(",");
		do{
			requestCSet[i]=requestCFile.nextInt();
			i++;
		}while(requestCFile.hasNextInt());
		requestCFile.close();
	

		try{
			CompoInterf implement=new CompoInterfImpl(requestCSet,setSize,processid);
			//CompoInterf stub = (CompoInterf)UnicastRemoteObject.exportObject(implement, 1099);
          
			Registry registry=LocateRegistry.getRegistry(port);
           
           
				registry.bind("component"+processid, implement);
				System.out.println("component"+processid+" is bound.");
				
//			}
			}catch(Exception e){
				System.out.println("Error in binding name:");
				e.printStackTrace();
			}
		
		
		Thread.sleep(waittime);
			lookupRegistry();
	}
	public void lookupRegistry() throws RemoteException, NotBoundException, FileNotFoundException, InterruptedException{

		
			/*
			 * run on a single machine
			 */
			Registry registry = LocateRegistry.getRegistry("localhost", port);
		
		 CompoInterf[] RMI_IDS=new CompoInterf[clientNum];
		for(int k=0;k<clientNum;k++){ 
		 RMI_IDS[k] = (CompoInterf) registry.lookup(registry.list()[k]);
		}
		 
		 for(int i=0; i<RMI_IDS.length; i++){
				RMI_IDS[i].setRegistrySet(RMI_IDS);
				RMI_IDS[i].setRequestSet(RMI_IDS);
			}
		 
		 RequestQueue requestsToSend=new RequestQueue();
			
			Scanner requestReader = new Scanner(new File("test/client1requests.txt"));
			do{
	            String line = requestReader.nextLine();
				Scanner scanner = new Scanner(line);
				requestsToSend.add(new Request(new ScalarClock(scanner.nextInt(),processid)));
				scanner.close();
			}while(requestReader.hasNextLine());
			requestReader.close();
			
			
//			Thread.sleep((int)Math.random()*10000);//wait for all the processes to reach.
			new Thread(  ()->
			{
			 
			 try{
				
				CompoInterf thisProc=RMI_IDS[processid];
			
				
				while(requestsToSend.peek()!=null){
					 Thread.sleep((int)Math.random()*500);
				thisProc.sendRequest(requestsToSend.peek());
				
				  while(thisProc.getEnteredCS()!=true){
					 Thread.sleep(5);
				   }
				  
				  int sentRequest=requestsToSend.poll().timestamp.Time;
				  System.out.println("Attempt with timestamp"+sentRequest+" to enter CS succeeded.");
				  Thread.sleep((int)Math.random()*1000);
				
				}
			}catch(Exception e){
				System.out.println("Error in sending requests:");
				e.printStackTrace();
			}
			}
			 ).start();
			Thread.sleep(500);

	}


}

