package algorithmprocess;

import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = -3075103411662207183L;
	private String msg;
	public ScalarClock timestamp;
	
	//every message includes a message and a timestamp.
	//the timestamp contains local message time and process id.
	public Message(String m, ScalarClock ts){
		this.msg = m;
		this.timestamp = ts;
	}
	
	@Override
	public String toString() {
		return msg.toString();
	}

}
