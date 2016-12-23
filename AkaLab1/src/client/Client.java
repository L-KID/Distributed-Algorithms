package client;

import interfac.RemoteInterfac;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
	
     public static void main(String[] args) 
    		 throws NotBoundException, NumberFormatException, IOException,FileNotFoundException{
	    Registry registry=LocateRegistry.getRegistry("localhost",1099);
	    BufferedReader reader=
	    		new BufferedReader(new InputStreamReader(new FileInputStream("Test/Messages.txt")));
	    RemoteInterfac[] processTemp=new RemoteInterfac[registry.list().length];
	    for(int i=0;i<processTemp.length;i++){
	    processTemp[i]=(RemoteInterfac)registry.lookup(registry.list()[i]);
	    }
	    String line;
	    String[] split_line;
	    while((line=reader.readLine())!=""){
	    	split_line=line.split(" ");
	    	processTemp[Integer.parseInt(split_line[2])].addMessage(split_line[0], Integer.parseInt(split_line[1]));
	    }
	    reader.close();
	    try {
			invoking();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
     public static void invoking()throws RemoteException, NotBoundException,InterruptedException{
    	 Registry registry=LocateRegistry.getRegistry("localhost",1099);
    	 RemoteInterfac[] processTemp=new RemoteInterfac[registry.list().length];
    	 for(int i=0;i<processTemp.length;i++){
    		    processTemp[i]=(RemoteInterfac)registry.lookup(registry.list()[i]);
    		    }
    	 AtomicInteger broNum=new AtomicInteger(processTemp.length);
    	 for(int i=0;i<processTemp.length;i++){
    		 RemoteInterfac process_i=processTemp[i];
    		 new Thread(()->{
    			 try{
    		         Thread.sleep((int)Math.random()*500);
    				 process_i.broadcast();
    				 broNum.getAndDecrement();
    			 }catch(Exception e){
    				 e.printStackTrace();
    			    }
    		 }).start();
    	 }
    	 while(broNum.get()>0){
    		 Thread.sleep(10);
    	 }
     }
}
