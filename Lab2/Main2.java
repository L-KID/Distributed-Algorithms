//package createregistry;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;

public class Main2 {
	public static void main(String[] args) throws AlreadyBoundException,NotBoundException,IOException, InterruptedException{
		
	/*	
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
	*/	
		CreateRegistry2 server=new CreateRegistry2();
	
		server.main();
	}

}
