//package createregistry;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;

public class Main3 {
	public static void main(String[] args) throws AlreadyBoundException,NotBoundException,IOException, InterruptedException{
		
	/*	
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
	*/	
		CreateRegistry3 server=new CreateRegistry3();
	
		server.main();
	}

}
