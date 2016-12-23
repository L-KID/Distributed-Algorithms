//package datastructure;

import java.util.Comparator;


public class RequestCompare implements Comparator<Request>{
	public int compare(Request r1, Request r2){
		if(r1.timestamp.olderThan(r2.timestamp)){
			return -1;
		}
		else{
			return 1;
		}
	}

}
