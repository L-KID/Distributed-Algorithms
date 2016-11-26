package local;

import java.io.Serializable;

public class TieBreaker  implements Serializable {
	private static final long serialVersionUID=1000L;
	public int timeStamp;
	public int procID;
	
	public TieBreaker(int ts,int id){
		this.timeStamp=ts;
		this.procID=id;
	}
	
	public boolean olderthan(TieBreaker tb2){
		if(this.timeStamp<tb2.timeStamp){
			return true;
		}
		else if((this.timeStamp>=tb2.timeStamp)&&(this.procID<tb2.procID)){
			return true;
		}
		else {
			return false;
		}
	}

}
