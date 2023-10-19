import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		
		Dimensioni dimensioni=new Dimensioni(10, 10);
		Griglia griglia= new Griglia(dimensioni, (float) 0.5, (float) 0.1);
		List<Percorso> agenti= new ArrayList<Percorso>();

		agenti=griglia.generatoreIstanze(4);
		Vertice[] v= griglia.generaInitGoal(agenti);
		griglia.ReachGoal(griglia, agenti,v[0], v[1], 10);
		System.out.println("x:"+ v[0].getX()+",y: "+v[0].getY());
		System.out.println("x:"+ v[1].getX()+",y: "+v[1].getY());
		Percorso percorso= new Percorso(griglia.ReachGoal(griglia, agenti,v[0], v[1], 10),v[0],v[1]);
		if(percorso.getPercorso() != null){
			griglia.getPercorsi().add(percorso);
			System.out.println("Percorso trovato!");
		}else{
			System.err.println("ERRORE: Percorso dell'agente n+1 non trovato!!!");
		}
		
		griglia.printGrafo();
		
		
	}

}
