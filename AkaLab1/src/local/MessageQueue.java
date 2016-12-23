package local;
import java.util.PriorityQueue;



public class MessageQueue {
   
	public PriorityQueue<Message> messageQueue;
		
	public MessageQueue(){
		
		messageQueue=new PriorityQueue<Message>(50,new MessageComparator());
	}
	public Message poll(){
		return messageQueue.poll();
	}
	public Message peek(){
		return messageQueue.peek();
	}
	public void add(Message msgg){
		messageQueue.add(msgg);
	}
		
	
}
