
public class Main {

	public static void main(String[] args) {
		
		Dimensioni dimensioni=new Dimensioni(10, 10);
		Griglia griglia= new Griglia(dimensioni, (float) 0.9, (float) 0.8);
		
		//griglia.printGrafo();
		
		griglia.getG()[0][0].printListaAdiacenza();
	}

}
