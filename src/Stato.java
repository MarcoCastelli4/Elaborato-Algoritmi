
public class Stato {

	private int istante_temporale;
	private Vertice vertice;
	
	public Stato(int istante_temporale,Vertice vertice) {
		setIstante_temporale(istante_temporale);
		setVertice(vertice);
	}
	
	
	public int getIstante_temporale() {
		return istante_temporale;
	}
	public void setIstante_temporale(int istante_temporale) {
		this.istante_temporale = istante_temporale;
	}
	public Vertice getVertice() {
		return vertice;
	}
	public void setVertice(Vertice vertice) {
		this.vertice = vertice;
	}
	
}