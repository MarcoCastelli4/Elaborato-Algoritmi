import java.util.ArrayList;
import java.util.List;

public class Percorso {

	private List<Stato> percorso=new ArrayList<>();

	public Percorso(List<Stato> percorso) {
		setPercorso(percorso);
	}
	
	public List<Stato> getPercorso() {
		return percorso;
	}

	public void setPercorso(List<Stato> percorso) {
		this.percorso = percorso;
	}
	
	
	
}
