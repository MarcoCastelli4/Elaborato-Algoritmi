
public class Dimensioni {
	
	private int righe;
	private int colonne;
	
	public Dimensioni(int righe, int colonne) {
		if(righe<=0 || colonne<=0 )
			throw new IllegalArgumentException();
		else {
			this.righe=righe;
			this.colonne=colonne;
		}
	}
	
	public int getRighe() {
		return righe;
	}
	public void setRighe(int righe) {
		this.righe = righe;
	}
	public int getColonne() {
		return colonne;
	}
	public void setColonne(int colonne) {
		this.colonne = colonne;
	}
	
	

}
