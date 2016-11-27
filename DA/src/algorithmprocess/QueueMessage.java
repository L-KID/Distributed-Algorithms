package algorithmprocess;

import java.util.PriorityQueue;

public class QueueMessage {
	public PriorityQueue<Message> messageQ;
	
	public QueueMessage(){
		messageQ = new PriorityQueue<Message>(500, new CompareMessage());
	}
	public Message peek(){
		return messageQ.peek();
	}

	public Message poll(){
		return messageQ.poll();
	}
	
	public void add(Message msg){
		messageQ.add(msg);
	}
}
