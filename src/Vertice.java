import java.lang.Math;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Vertice implements Comparable<Vertice>{

	private boolean ostacolo;
	private int x;
	private int y;
	private float peso;
	private Vertice padre;
	
	// lista di adiacenza di ogni vertice
	private Map<Vertice,Float> lista_adiacenza = new HashMap<>();
	 
	public Vertice(int x,int y, boolean ostacolo) {
			this.setX(x);
			this.setY(y);
			this.ostacolo=false;
			// per dijkstra
			setPeso(Float.POSITIVE_INFINITY);
			setPadre(null);
	
	}
	
	public Vertice(Vertice v) {
		setOstacolo(v.isOstacolo());
		setPadre(v.getPadre());
		setPeso(v.getPeso());
		setX(v.getX());
		setY(v.getY());
		
		// copio la lista di adiacenza
		setListaAdiacenza(v.getListaAdiacenza());

}
	public boolean isOstacolo() {
		return ostacolo;
	}

	public void setOstacolo(boolean ostacolo) {
		this.ostacolo = ostacolo;
	}

	public String PrintlistaAdiacenza() {
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
	
	public Map<Vertice,Float> getListaAdiacenza(){
		return lista_adiacenza;
	}
	
	public void setListaAdiacenza(Map<Vertice,Float> lista){
		this.lista_adiacenza.putAll(lista);
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

	
	public boolean equals(Object obj) {
	    return (getX() == ((Vertice)obj).getX() && getY() == ((Vertice)obj).getY()  && isOstacolo() == ((Vertice)obj).isOstacolo());
	}
	
	public int hashCode() {
		int intValue = isOstacolo() ? 1 : 0;
        return getX()*getY()*intValue;
    }

	public Vertice getPadre() {
		return padre;
	}

	public void setPadre(Vertice padre) {
		this.padre = padre;
	}

	public float getPeso() {
		return peso;
	}

	public void setPeso(float peso) {
		this.peso = peso;
	}

	@Override
	public int compareTo(Vertice o) {
		float d= getPeso()-o.getPeso();
		
		if(d<0)
			return -1;
		if (d>0)
			return 1;
		return 0;
	}	
	
}
