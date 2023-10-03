import java.lang.Math;
import java.util.HashMap;
import java.util.Map;

public class Vertice {

	private boolean ostacolo;
	private int x;
	private int y;
	
	// lista di adiacenza di ogni vertice
	private Map<Vertice,Float> lista_adiacenza = new HashMap<>();
	 
	public Vertice(int x,int y, boolean ostacolo) {
			this.setX(x);
			this.setY(y);
			this.ostacolo=false;
	
	}
	public boolean isOstacolo() {
		return ostacolo;
	}

	public void setOstacolo(boolean ostacolo) {
		this.ostacolo = ostacolo;
	}

	public String listaAdiacenza() {
		StringBuilder str = new StringBuilder();
		lista_adiacenza.forEach((key, value) -> str.append("r: ")
		        .append(key.getX())
		        .append(", c: ")
		        .append(key.getY())
		        .append(", peso: ")
		        .append(value)
		        .append(" --> "));
		
		return str.toString();

	}
	public void addVerticeAdiacente(Vertice vertice,float peso) {
		this.lista_adiacenza.put(vertice,peso);
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	
	
	
}
