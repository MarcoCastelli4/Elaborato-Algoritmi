
public class Stato {

	private int istante_temporale;
	private Vertice vertice;
	
	public Stato(Vertice vertice,int istante_temporale) {
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
	
	public boolean equals(Object obj) {
	    return (getIstante_temporale() == ((Stato)obj).getIstante_temporale() && getVertice().equals(((Stato)obj).getVertice()));
	    		
	}
	
	public int hashCode() {
        return getIstante_temporale()*getVertice().hashCode();
    }

	public String toString(){
		return "vertice "+ this.getVertice().toString()+", t:" + this.getIstante_temporale();
	}
	}
