//package datastructure;

import java.util.PriorityQueue;

public class RequestQueue {
	public PriorityQueue<Request> msg_Q;
	
	public RequestQueue(){
		msg_Q = new PriorityQueue<Request>(500, new RequestCompare());

	}
	
	public Request peek(){     
		return msg_Q.peek();
	}
	
	public Request poll(){      //choose one from poll and remove?
		return msg_Q.poll();
	}
	
	public void add(Request msg){
		 msg_Q.add(msg);
	}
	
	public void remove(Request msg){
		msg_Q.remove(msg);
	}

}
