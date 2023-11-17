import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		
		Dimensioni dimensioni=new Dimensioni(10, 10);
		Griglia griglia= new Griglia(dimensioni, (float) 0.5, (float) 0.1);
		List<Percorso> agenti= new ArrayList<Percorso>();

		agenti=griglia.generatoreIstanze(3);
		Vertice[] v= griglia.generaInitGoal(agenti);
		// stampo i percorsi degli agenti
		griglia.printGrafo();
				
		
		System.out.println("x:"+ v[0].getX()+",y: "+v[0].getY());
		System.out.println("x:"+ v[1].getX()+",y: "+v[1].getY());
		
		// eseguo il djkstra per il goal
		griglia.Dijkstra(griglia,v[1]);
		
		Percorso pri=new Percorso(griglia.ReachGoal(griglia, agenti,v[0], v[1], 20),v[0],v[1]);
		
		
		// REACH ORIGINALE
		if(pri.getPercorso() != null){
			System.out.println("Percorso n+1 trovato!");
			griglia.printPercorso(pri);
		}else{
			System.err.println("ERRORE: Percorso dell'agente n+1 non trovato!!!");
		}
		
		Percorso alt=new Percorso(griglia.ReachGoalAlternativo(griglia, agenti,v[0], v[1], 20),v[0],v[1]);
		
		//REACH GOAL ALT
		if(alt.getPercorso() != null){
			System.out.println("Alternativa - Percorso n+1 trovato!");
			griglia.printPercorso(alt);
		}else{
			System.err.println("Alternativa - ERRORE: Percorso dell'agente n+1 non trovato!!!");
		}
		
		
		
		
	}

}
