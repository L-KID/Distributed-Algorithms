

import java.util.PriorityQueue;
import java.io.Serializable;

/*This is used to queue the messages in every process
 * 
 */

public class QueueMessage implements Serializable{
	private static final long serialVersionUID = -11662207183L;
	public PriorityQueue<Message> messageQ;
	
	public QueueMessage(){
		messageQ = new PriorityQueue<Message>(500, new CompareMessage());
	}
	//get the first message of the message queue
	public Message peek(){
		return messageQ.peek();
	}

	//get the first message of the message queue and remove it from the queue
	public Message poll(){
		return messageQ.poll();
	}
	
	//add message to the message queue
	public void add(Message msg){
		messageQ.add(msg);
	}
	public int size(){
		return messageQ.size();
	}
}
