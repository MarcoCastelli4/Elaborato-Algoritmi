
public class Main {

	public static void main(String[] args) {
		
		Dimensioni dimensioni=new Dimensioni(10, 10);
		Griglia griglia= new Griglia(dimensioni, (float) 0.5, (float) 0.1);
		
		griglia.generatoreIstanze(8);
		griglia.printGrafo();
		
		
	}

}
