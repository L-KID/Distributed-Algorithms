package request;

import java.io.Serializable;
import clock.ScalarClock;

public class Request implements Serializable{
	
	private static final long serialVersionUID = -6234657468992657463L;
	
	public ScalarClock timestamp;
	
	public Request (ScalarClock t){
		this.timestamp = t;
	}
	

}
