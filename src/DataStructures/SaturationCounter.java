package DataStructures;

public class SaturationCounter {
	private int value;
	public SaturationCounter(){
		value=2;
	}
	SaturationCounter(int value){
		this.value=value;
	}
	public int getValue(){
		return this.value;
	}
	public void update(int dir){
		if(dir>0){
			if(this.value<3)
				this.value++;
		}
		else{
			if(this.value>0)
				this.value--;
		}
	}
}