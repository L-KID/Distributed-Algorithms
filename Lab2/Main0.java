//package createregistry;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;

public class Main0 {
	public static void main(String[] args) throws AlreadyBoundException,NotBoundException,IOException, InterruptedException{
		
	/*	
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
	*/	
		CreateRegistry0 server=new CreateRegistry0();
	
		server.main();
	}

}
