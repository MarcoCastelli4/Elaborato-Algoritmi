import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		int max=30;
		
		Dimensioni dimensioni=new Dimensioni(20,20);
		Griglia griglia= new Griglia(dimensioni, 0.7, 0.1);
		List<Percorso> agenti= new ArrayList<Percorso>();

		agenti=griglia.generatoreIstanze(4,max);
		// stampo i percorsi degli agenti
		griglia.printAgenti();

		Vertice[] v= griglia.generaInitGoal(max);	
		System.out.println("init -> x:"+ v[0].getX()+",y: "+v[0].getY()); //init
		System.out.println("goal -> x:"+ v[1].getX()+",y: "+v[1].getY()); //goal 
		
		// REACH GOAL ORIGINALE
		ReachGoal pri=griglia.ReachGoal(griglia, agenti,v[0], v[1], max);
		if(pri == null || pri.getPercorso() == null ){
			System.err.println("ERRORE: Percorso dell'agente n+1 non trovato!!!");
		}else{
			System.out.println("\nPercorso n+1 trovato!");
			griglia.printPercorso(pri);
		}
		
		
		// eseguo il djkstra per il goal
		griglia.Dijkstra(griglia,v[1]);
		// REACH GOAL ALTERNATIVO
		ReachGoal alt=griglia.ReachGoalAlternativo(griglia, agenti,v[0], v[1], max);

		if(alt == null || alt.getPercorso() == null){
			System.err.println("Alternativa - ERRORE: Percorso dell'agente n+1 non trovato!!!");
			
		}else{
			System.out.println("\nAlternativa - Percorso n+1 trovato!");
			griglia.printPercorso(alt);
		}
		
	}

}
