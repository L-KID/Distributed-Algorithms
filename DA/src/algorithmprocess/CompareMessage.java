package algorithmprocess;

import java.util.Comparator;

public class CompareMessage implements Comparator<Message>{
	public int compare(Message m1, Message m2){
		if(m1.timestamp.olderThan(m2.timestamp)){
			return -1;
		}
		else {
			return 1;
		}
	}
	

}
