
public class VerticeDijkstra extends Vertice {
	
	private double distanza;
	private Vertice padre;
	
	public VerticeDijkstra(Vertice vertice,double distanza, Vertice padre) {
		super(vertice.getX(),vertice.getY(),vertice.isOstacolo());
		setDistanza(distanza);
		setPadre(padre);
	}

	public double getDistanza() {
		return distanza;
	}

	public void setDistanza(double distanza) {
		this.distanza = distanza;
	}

	public Vertice getPadre() {
		return padre;
	}

	public void setPadre(Vertice padre) {
		this.padre = padre;
	}
}
