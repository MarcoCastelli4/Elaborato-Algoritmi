import java.lang.Math;

public class Vertice {

	private float peso;
	private boolean ostacolo;
	
	public Vertice(float peso, boolean ostacolo) {
		if(peso!= 1 && peso!=Math.sqrt(2) )
			throw new IllegalArgumentException();
		else {
			this.peso=peso;
			this.ostacolo=false;
		}
		
	}
	public boolean isOstacolo() {
		return ostacolo;
	}

	public void setOstacolo(boolean ostacolo) {
		this.ostacolo = ostacolo;
	}

	public float getPeso() {
		return peso;
	}

	public void setPeso(float peso) {
		this.peso = peso;
	}
	
	
}
