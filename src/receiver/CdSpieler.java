package receiver;

public class CdSpieler {
	String beschreibung;
	int aktuellerTrack;
	Verstaerker verstaerker;
	String titel;
	
	public CdSpieler(String beschreibung, Verstaerker verstaerker) {
		this.beschreibung = beschreibung;
		this.verstaerker = verstaerker;
	}
 
	public void ein() {
		System.out.println(beschreibung + " an");
	}
 
	public void aus() {
		System.out.println(beschreibung + " aus");
	}

	public void auswerfen() {
		System.out.println(beschreibung + " wirft CD "+titel +" aus");
		titel = null;
	}
 
	public void spielen(String titel) {
		this.titel = titel;
		aktuellerTrack = 0;
		System.out.println(beschreibung + " spielt \"" + titel + "\"");
	}

	public void spielen(int track) {
		if (titel == null) {
			System.out.println(beschreibung + " kann Track " + aktuellerTrack + 
					"nicht abspielen, keine CD eingelegt");
		} else {
			aktuellerTrack = track;
			System.out.println(beschreibung + " spiele Track " + aktuellerTrack);
		}
	}

	public void beenden() {
		aktuellerTrack = 0;
		System.out.println(beschreibung + " beendet");
	}
 
	public void pause() {
		System.out.println(beschreibung + " angehalten \"" + titel + "\"");
	}
 
	public String toString() {
		return beschreibung;
	}
}
