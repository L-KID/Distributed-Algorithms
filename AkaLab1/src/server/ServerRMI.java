package server;

import interfac.RemoteInterfac;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ServerRMI {

	public static void main(String[] args)throws 
	RemoteException,FileNotFoundException,IOException,AlreadyBoundException{
		Registry registry=LocateRegistry.createRegistry(1099);//RMIport can be modified
		BufferedReader reader=
		    new BufferedReader(new InputStreamReader(new FileInputStream("Test/Processes.txt")));
		String noProc;
		while((noProc=reader.readLine())!=""){     //?empty string or null
			registry.bind(noProc, new RemoteInterfImpl());
		}
		reader.close();
		try {
			connecting();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Connected to server.");
	}
	public static void connecting()throws AccessException,RemoteException,NotBoundException{
		Registry registry=LocateRegistry.getRegistry("localhost",1099);
		RemoteInterfac[] processTemp=new RemoteInterfac[registry.list().length];
		
		for(int i=0;i<registry.list().length;i++){
		processTemp[i]=(RemoteInterfac)registry.lookup(registry.list()[i]);
		}
		for(int i=0;i<processTemp.length;i++){
			processTemp[i].setRegister(processTemp);
			processTemp[i].setID(i);
			processTemp[i].setName(registry.list()[i]);
		}
	}
}
