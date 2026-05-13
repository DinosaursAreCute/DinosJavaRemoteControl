package receiver;

public class Beamer {
	String beschreibung;
	DvdSpieler dvdPlayer;
	
	public Beamer(String beschreibung, DvdSpieler dvdSpieler) {
		this.beschreibung = beschreibung;
		this.dvdPlayer = dvdSpieler;
	}
 
	public void ein() {
		System.out.println(beschreibung + " an");
	}
 
	public void aus() {
		System.out.println(beschreibung + " aus");
	}

	public void breitwandModus() {
		System.out.println(beschreibung + " in Breitwandmodus (16x9 Seitenverhaeltnis)");
	}

	public void tvModus() {
		System.out.println(beschreibung + " in TV-Modus (4x3 Seitenverhaeltnis)");
	}
	
	public String toString() {
		return beschreibung;
	}
}
