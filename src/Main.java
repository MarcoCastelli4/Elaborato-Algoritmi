
public class Main {

	public static void main(String[] args) {
		
		Dimensioni dimensioni=new Dimensioni(10, 10);
		Griglia griglia= new Griglia(dimensioni, 50, 7);
		
		griglia.printGrafo();
	}

}
