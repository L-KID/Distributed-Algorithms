package clock;

import java.io.Serializable;

public class ScalarClock implements Serializable{
	public static final long serialVersionUID = -2638475623163548223L;
	public int Time;//added public
	public int ProcessID;//added public
	
	public ScalarClock(int t, int id){
		Time = t;
		ProcessID = id;
		
	}
	
	public boolean olderThan(ScalarClock scOrigin){
		if(scOrigin == null){
			return true;
		}
		
		else if(this.Time < scOrigin.Time){
			return true;
		}
		else if(this.Time == scOrigin.Time && this.ProcessID <= scOrigin.ProcessID){
			return true;
		}
		else{
			return false;
		}
		
		
	}
	
	public int getID(){
		return ProcessID;
	}
	
	@Override
	public String toString(){
		return "Time:" +Time+ "Process:" +ProcessID;
	}

}