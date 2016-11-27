package server;


import interf.RemoteInterf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIserver {
	public static void main(String[] args) throws IOException, AlreadyBoundException, NotBoundException{
	//public static void main(String[] args) {	
	    //try{
			java.rmi.registry.LocateRegistry.createRegistry(1099);
			BufferedReader buffer = new BufferedReader(new FileReader("test/clients.txt"));
			String line = "";
			while ((line = buffer.readLine()) != null) {
				java.rmi.Naming.bind(line, new RemoteServer()); //bind the clients with remote object in RemoteServer
			}
			buffer.close(); //close the buffer
			
			conF();
			System.out.println("start");
			
		//}catch(Exception e){
			//e.getStackTrace();
		//}
	}
	
	public static void conF() throws RemoteException, NotBoundException{
	//public static void conF(){	
	    //try{
		    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
		    RemoteInterf[] RMI_IDS = new RemoteInterf[registry.list().length]; //if there are 10 clients, 10 RemoteInterface called
		    for(int i=0; i<registry.list().length; i++){
			    RMI_IDS[i] = (RemoteInterf) registry.lookup(registry.list()[i]);//return the remote reference for the clients
		    }
		    for(int i=0; i<RMI_IDS.length; i++){
			    RMI_IDS[i].conF(RMI_IDS); //?
			    RMI_IDS[i].setPName(registry.list()[i]);
			    RMI_IDS[i].setId(i+1);
		    }
		//}catch(Exception e){
			//e.getStackTrace();
		//}
	}

}
