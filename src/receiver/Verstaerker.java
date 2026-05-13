package receiver;

public class Verstaerker {
	private String beschreibung;
	private Tuner tuner;
	private DvdSpieler dvd;
	private CdSpieler cd;
	
	public Verstaerker(String beschreibung, Tuner tuner, DvdSpieler dvd, CdSpieler cd) {
		this.tuner = tuner;
		this.dvd=dvd;
		this.cd = cd;
		this.beschreibung = beschreibung;
	}
	 
	public void ein() {
		System.out.println(beschreibung + " an");
	}
	 
	public void aus() {
		System.out.println(beschreibung + " aus");
	}
	 
	 
	public void setSurroundSound() {
		System.out.println(beschreibung + " aktiviere Surround-Sound (5 Boxen, 1 Subwoofer)");
	}
	 
	public void setLautstaerke(int stufe) {
		System.out.println(beschreibung + " setze Lautstaerke auf " + stufe);
	}

	public void setTuner(Tuner tuner) {
		System.out.println(beschreibung + " setze Tuner auf " + tuner);
		this.tuner = tuner;
	}
		
	public void setDvd(DvdSpieler dvd) {
		System.out.println(beschreibung + " setze DVD-Spieler auf " + dvd);
		this.dvd = dvd;
	}
	 
	public void setCd(CdSpieler cd) {
		System.out.println(beschreibung + " setze CD-Spieler auf " + cd);
		this.cd = cd;
	}
	 
	public String toString() {
		return beschreibung;
	}
}
