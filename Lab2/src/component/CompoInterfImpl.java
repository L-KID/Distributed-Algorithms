package component;

import request.RequestQueue;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicInteger;

public class CompoInterfImpl extends UnicastRemoteObject implements CompoInterf {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4138486202875827055L;

	private RequestQueue reqQ;
	private CompoInterf[] requestSet;
	
	protected CompoInterfImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		this.reqQ=new RequestQueue();
	}
	
	public void sendRequest() throws RemoteException, InterruptedException{
		AtomicInteger sendCount=new AtomicInteger(requestSet.length);
		for(int i=0;i<requestSet.length;i++){
		CompoInterf reqSet_i=requestSet[i];
		new Thread(()->
		{
			try{
				reqSet_i.receiveRequest();
				sendCount.getAndDecrement();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
				).start();
		}
		while(sendCount.get()>0){
			Thread.sleep(10);
		}
		//??To add: determine whether it can enter CS
		
	}
	public void receiveRequest()throws RemoteException{
		
	}

}
