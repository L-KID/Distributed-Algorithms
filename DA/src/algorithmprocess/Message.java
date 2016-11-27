package algorithmprocess;

import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = -3075103411662207183L;
	private String msg;
	public ScalarClock timestamp;
	
	public Message(String m, ScalarClock ts){
		this.msg = m;
		this.timestamp = ts;
	}
	
	@Override
	public String toString() {
		return msg.toString();
	}

}
