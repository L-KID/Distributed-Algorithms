package main;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import component.CompoInterfImpl;
import component.CompoInterf;

public class Server {
	public int clientNum;
	public CompoInterf[] RMI_IDS;
	public Registry registry;
	
	
	public void main() throws AlreadyBoundException, NotBoundException, IOException, FileNotFoundException{
		java.rmi.registry.LocateRegistry.createRegistry(1099);
		
		Scanner s = new Scanner(System.in);
		clientNum =s.nextInt();
		System.out.println("Please choose request set size (between " + (int) Math.ceil(Math.sqrt(clientNum)) + " and " + clientNum +").");
		int setSize = s.nextInt();
		s.close();
		
		Scanner requestCFile = new Scanner(new File("clientrequestset.txt"));
		
		
		while(requestCFile.hasNextLine()){
			String line = requestCFile.nextLine();
			ArrayList<Integer> requestCSet = new ArrayList<Integer>();
			
			Scanner scanner = new Scanner(line);
			scanner.useDelimiter(",");
			while(scanner.hasNextInt()){
				requestCSet.add(scanner.nextInt());
			}
			scanner.close();
		}
		requestCFile.close();
		
		System.out.println(requestCSet);
		
		Integer[] times = {1,10,8,3,9,6,5};
		
		for(int i=0;i<clientNum; i++){
			
			registry.bind("client"+(i+1), new CompoInterf(requestCSet[i],i,times[i]));
			
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
