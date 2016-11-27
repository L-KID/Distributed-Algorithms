package client;

import interf.RemoteInterf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
	static int totalMessages = 0;
	public static void main(String[] args) throws NotBoundException, IOException, InterruptedException{
	//public static void main(String[] args){
		//try{
			Registry registry = LocateRegistry.getRegistry("localhost",1099);
			BufferedReader buffer = new BufferedReader(new FileReader("test/messages.txt"));
			String line = "";
			String[] split_line;
			while ((line = buffer.readLine()) != null) {
				totalMessages++;
				split_line = line.split(" ");
				((RemoteInterf) registry.lookup(registry.list()[Integer.parseInt(split_line[0])])).addMessage(split_line[1], Integer.parseInt(split_line[2]));
			}
			buffer.close();
		
			
			startAlgorithm();
			
		//}catch(Exception e){
			//e.getStackTrace();
		//}
		
	}
	
	public static void startAlgorithm() throws NotBoundException, IOException, InterruptedException{
	//public static void startAlgorithm(){	
	    //try{
			Registry registry = LocateRegistry.getRegistry("localhost",1099);
			RemoteInterf[] RMI_IDS = new RemoteInterf[registry.list().length];
			for(int i=0; i<registry.list().length; i++){
				RMI_IDS[i] = (RemoteInterf) registry.lookup(registry.list()[i]);
			}
			
			for(int j=0; j<totalMessages; j++){
				AtomicInteger rounds = new AtomicInteger(RMI_IDS.length);
				for(int i=0; i<RMI_IDS.length; i++){
					RemoteInterf RINi = RMI_IDS[i];
					new Thread ( () -> {					
						try {
							Thread.sleep((int)(Math.random()*1000)); // random delay
							RINi.broadcast();
							rounds.getAndDecrement();

						} catch (Exception e) {
							e.printStackTrace();
			 	 		}
					
					
					}).start();
				}
				while(rounds.get()!=0){
					Thread.sleep(1);
				}
			}
		
			
		//}catch(Exception e){
			//e.printStackTrace();
		//}
	}

}
