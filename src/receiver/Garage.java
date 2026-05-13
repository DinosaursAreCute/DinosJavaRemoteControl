package receiver;

public class Garage {

	private boolean hoch;

	public boolean isHoch() {
		return hoch;
	}

	public void hoch() {
		System.out.println("Garagentor faehrt hoch");
		this.hoch = true;
	}
	
	public void runter(){
		System.out.println("Garagentor faehrt runter");
		this.hoch = false;
	}
	
}
