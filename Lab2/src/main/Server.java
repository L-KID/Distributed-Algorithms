package main;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
//import java.util.ArrayList;
import java.util.Scanner;
//import java.util.List;
import java.io.File;
//import java.util.concurrent.atomic.AtomicInteger;

import component.CompoInterfImpl;
import component.CompoInterf;

public class Server {
	public int clientNum;
	public CompoInterf[] RMI_IDS;
	public Registry registry;
	
	
	public void main() throws AlreadyBoundException, NotBoundException, IOException, FileNotFoundException{
		java.rmi.registry.LocateRegistry.createRegistry(1099);
		
		Scanner s = new Scanner(System.in);
		System.out.println("Please enter the number of clients:");
		clientNum =s.nextInt();
		System.out.println("Please choose request set size (between " + (int) Math.ceil(Math.sqrt(clientNum)) + " and " + clientNum +").");
		int setSize = s.nextInt();
		System.out.println("Please check the clientrequestset.txt to match client numbers " +clientNum+ " and request set size " + setSize + ").");
		
//		Integer[] times = new Integer[clientNum];
		
//		s.nextLine();
//		System.out.println("Please enter the time set for each client:");
//		String line1=s.nextLine();
//		String[] numbers1 = line1.split(",");
//		for(int m=0; m<numbers1.length;m++){
//			times[m] = Integer.parseInt(numbers1[m]);
//		}
		s.close();
		
		
		Scanner requestCFile = new Scanner(new File("test/clientrequestset.txt"));
		Integer[][] requestCSet = new Integer[clientNum][setSize];
		
		
		int i=0; 
		int j=0;
		do{
			
			String line = requestCFile.nextLine();
			
			
			Scanner scanner = new Scanner(line);
			scanner.useDelimiter(",");
			do{
				requestCSet[i][j] = scanner.nextInt();
				j++;
			}while(scanner.hasNextInt());
			j=0;
			i++;
			scanner.close();
		}while(requestCFile.hasNextLine());
		requestCFile.close();
		
		System.out.println(requestCSet[1][2]);
		
		
		Integer[] times = {1,10,8,3,9,6,5};
		
		
		
		for(int k=0;k<clientNum; k++){
			
			//CompoInterf test = new CompoInterfImpl(requestCSet[k],setSize,k,times[k]);
			//System.out.println(times[k]);
			registry.bind("client"+(k+1), new CompoInterfImpl(requestCSet[k],setSize,k,times[k]));
			
		}
		
		setRegistry();
		totalRequest();
		
		System.out.println("start");
		
	}
	
	
	
	
	
	public void setRegistry() throws RemoteException, NotBoundException{
		registry = LocateRegistry.getRegistry("localhost", 1099);
		RMI_IDS = new CompoInterf[registry.list().length];
		for(int i=0; i<registry.list().length; i++){
			RMI_IDS[i] = (CompoInterf) registry.lookup(registry.list()[i]);
		}
		for(int i=0; i<RMI_IDS.length; i++){
			RMI_IDS[i].setRegistrySet(RMI_IDS);
			RMI_IDS[i].setRequestSet(RMI_IDS);
		}
	}
	
	public  void totalRequest() throws RemoteException, NotBoundException{
		for(int i = 0; i<RMI_IDS.length; i++){
			CompoInterf RMI = RMI_IDS[i];
			new Thread ( () -> {					
				try {
					//Thread.sleep((int)(Math.random()*50));
					RMI.sendRequest();
				} catch (Exception e) {
					e.printStackTrace();
	 	 		}
			}).start();
		}
	}


}

