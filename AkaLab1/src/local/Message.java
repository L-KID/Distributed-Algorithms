package local;

import java.lang.String;
import java.io.Serializable;

public class Message implements Serializable{
	

		private static final long serialVersionUID=42L;
		public String msg;  //private or public?
		public TieBreaker tieBreaker;
		
		public Message(String m,TieBreaker tb){
			this.msg=m;
			this.tieBreaker=tb;
			
	    }
		//@Override and a toString()?
	
}
