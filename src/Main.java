
public class Main {

	public static void main(String[] args) {
		
		Dimensioni dimensioni=new Dimensioni(10, 10);
		Griglia griglia= new Griglia(dimensioni, (float) 0.2, (float) 0.5);
		
		
		griglia.generatoreIstanze(5);
		griglia.printGrafo();
		
		
	}

}
