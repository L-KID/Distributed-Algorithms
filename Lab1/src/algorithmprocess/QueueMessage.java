package algorithmprocess;

import java.util.PriorityQueue;

/*This is used to queue the messages in every process
 * 
 */

public class QueueMessage {
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
}
