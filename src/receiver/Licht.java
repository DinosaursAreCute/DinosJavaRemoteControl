package receiver;


public class Licht {

	boolean licht = false;

	
	public void setLichtAn(){
		licht = true;
		System.out.println("Licht An!");
	}
	
	public void setLichtAus(){
		licht = false;
		System.out.println("Licht Aus!");
	}

}
