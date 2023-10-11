
public class Main {

	public static void main(String[] args) {
		
		Dimensioni dimensioni=new Dimensioni(10, 10);
		Griglia griglia= new Griglia(dimensioni, (float) 0.99, (float) 0.2);
		
		griglia.generatoreIstanze(2);
		
		griglia.printGrafo();
		
		
		
		
	}

}
