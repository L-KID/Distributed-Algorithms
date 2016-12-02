package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
//import java.io.FileReader;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
//import java.util.concurrent.atomic.AtomicInteger;

import component.CompoInterf;


public class Client {
	public static void main(String[] args) throws RemoteException,NotBoundException, IOException, InterruptedException{
		
		Registry registry = LocateRegistry.getRegistry("localhost",1099);
		
		
		
		CompoInterf[] RMI_IDS = new CompoInterf[registry.list().length];
		for(int k=0;k<registry.list().length;k++){
			RMI_IDS[k]=(CompoInterf)registry.lookup(registry.list()[k]);
		}
		for(int i = 0; i<RMI_IDS.length; i++){
			CompoInterf RMI = RMI_IDS[i];
			new Thread ( () -> {					
				try {
					Thread.sleep((int)(Math.random()*50));
					RMI.sendRequest();
				} catch (Exception e) {
					e.printStackTrace();
	 	 		}
			}).start();
		}
	}
	
	
	
	
	
	
	/*
	public  static void totalRequest() throws RemoteException, NotBoundException{
		Registry registry = LocateRegistry.getRegistry("localhost",1099);
		
	}
*/
}
