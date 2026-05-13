package receiver;

public class Leinwand {
	String beschreibung;
	
	public Leinwand(String beschreibung) {
		this.beschreibung = beschreibung;
	}
 
	public void hoch() {
		System.out.println(beschreibung + " faehrt hoch");
	}
 
	public void runter() {
		System.out.println(beschreibung + " faehrt runter");
	}
 
	
	public String toString() {
		return beschreibung;
	}
}
