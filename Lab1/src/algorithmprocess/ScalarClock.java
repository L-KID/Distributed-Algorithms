package algorithmprocess;

import java.io.Serializable;

/*
 * create ScalarClock as the timestamp for each process
 */

public class ScalarClock implements Serializable{	
	private static final long serialVersionUID = -6632789450190262462L;

    int Time;
    int ProcessId;

    public ScalarClock(int t, int id){
	    Time = t;
	    ProcessId = id;
    }
    
    //if the local time of the message of this process is smaller (older) than that of another process, return true.
    //if the two local times are equal, then compare the two process' id. 
    public boolean olderThan(ScalarClock scNew){
    	if(scNew==null){
    		return true;
    	}
    	else if(this.Time < scNew.Time){
    		return true;
    	}
    	else if(this.Time == scNew.Time && this.ProcessId <= scNew.ProcessId){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    
    @Override
    public String toString(){
    	return "Time: " +Time+ " Process: " +ProcessId;
    }

}
