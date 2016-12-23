import java.io.Serializable;

public class Message implements Serializable,Comparable<Message>{
	private static final long serialVersionUID = -6632789450190262462L;
	public int Level;
	public int ID;
	
	public Message(int L, int id){
		this.Level = L;
		this.ID = id;
	}
	

    
    @Override
    public String toString(){
    	return "Level: " +Level+ " Process: " +ID;
    }



	@Override
	public int compareTo(Message m2) {
		
			if(this.Level < m2.Level){
				return -1;
			}
			else if(this.Level > m2.Level){
				return 1;
			}
			else{
				return (this.ID-m2.ID);
			}
		
	}

}
