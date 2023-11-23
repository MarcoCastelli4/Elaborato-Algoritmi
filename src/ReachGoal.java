import java.util.List;

public class ReachGoal {
	
	private List<Stato> percorso;
	private int open;
	private int closed;
	private int lunghezza;
	private float peso;
	private int wait;
	
	public ReachGoal (List<Stato> percorso, int open, int closed, int lunghezza, float peso, int wait) {
		setPercorso(percorso);
		setOpen(open);
		setClosed(closed);
		setLunghezza(lunghezza);
		setPeso(peso);
		setWait(wait);
		
	}
	public List<Stato> getPercorso() {
		return percorso;
	}
	public void setPercorso(List<Stato> percorso) {
		this.percorso = percorso;
	}
	public int getOpen() {
		return open;
	}
	public void setOpen(int open) {
		this.open = open;
	}
	public int getClosed() {
		return closed;
	}
	public void setClosed(int closed) {
		this.closed = closed;
	}
	public int getLunghezza() {
		return lunghezza;
	}
	public void setLunghezza(int lunghezza) {
		this.lunghezza = lunghezza;
	}
	public float getPeso() {
		return peso;
	}
	public void setPeso(float peso) {
		this.peso = peso;
	}
	public int getWait() {
		return wait;
	}
	public void setWait(int wait) {
		this.wait = wait;
	}
	
	public String toString() {
		return "Lunghezza: "+ lunghezza + ", Peso: "+ peso+ ", Wait: "+wait+ ", Open: "+open+" ,Closed: "+closed;
	}
	
}
