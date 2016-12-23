

import java.util.Comparator;
import java.io.Serializable;

//compare two message, if the timestamp of message1 smaller than that of message2, return -1.
//If the timestamp of message1 larger than that of message2, return -1.
public class CompareMessage implements Comparator<Message>, Serializable{
	private static final long serialVersionUID = -307510341183L;
	public int compare(Message m1, Message m2){
		if(m1.timestamp.olderThan(m2.timestamp)){
			return -1;
		}
		else {
			return 1;
		}
	}
	

}
