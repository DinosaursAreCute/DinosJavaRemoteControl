package receiver;

public class Stereoanlage {

	private String cd;
	private boolean an;
	private boolean spielt;
	private int lautstaerke;

	private enum Mode {
		CD, RADIO
	};

	private Mode currentMode = Mode.RADIO;

	public Stereoanlage() {
		this.an = false;
		this.cd = "NO CD";
		this.lautstaerke = 4;
		this.spielt = false;
	}

	public void spieleCDAb() {
		if (this.an) {
			this.spielt = true;
			this.setCD();
			if (this.cd.equals("NO CD"))
				System.out.println("Lege zuerst eine CD ein.");
			else
				System.out.println("Starte das playback von " + this.cd);
		} else
			System.out.println("Erst muss die Anlage angestellt werden.");
	}

	public Mode getCurrentMode() {
		return currentMode;
	}

	public void spieleRadio() {
		if (this.an) {
			this.spielt = true;
			this.setRadio();
			System.out.println("100,1 Das Hit-Radio! ...");
		} else
			System.out.println("Erst muss die Anlage angestellt werden.");
	}

	public void stoppePlayback() {
		if (this.an) {
			System.out.println("Stoppe das Playback");
		}
		this.spielt = false;
	}

	public void anschalten() {
		this.an = true;
		System.out.println("Die Stereoanlage ist jetzt AN");
	}

	public void ausschalten() {
		this.an = false;
		this.spielt = false;
		System.out.println("Die Stereoanlage ist jetzt aus");
	}

	public void setLautstaerke(int lautst) {
		this.lautstaerke = lautst;
		System.out.println("Aendere Lautstaerke auf " + lautst);
	}

	public int getLautstaerke() {
		return this.lautstaerke;
	}

	public void legeCDEin(String cdName) {
		this.cd = cdName;
	}

	public void setCD() {
		System.out.println("Der aktuelle Modus ist CD.");
		this.currentMode = Mode.CD;
	}

	public void setRadio() {
		System.out.println("Der aktuelle Modus ist Radio");
		this.currentMode = Mode.RADIO;
	}

	public String getAktuelleCD() {
		return this.cd;
	}
}
