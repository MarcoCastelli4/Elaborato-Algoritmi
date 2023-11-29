import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		int max=10;
		int numero_agenti=5;
		Dimensioni dimensioni=new Dimensioni(10,10);
		Griglia griglia= new Griglia(dimensioni, 0.5, 0.1);
		List<Percorso> agenti= new ArrayList<Percorso>();

		System.out.println("Dimensione della griglia utilizzata: " + dimensioni.getRighe() + " x " +dimensioni.getColonne());
		System.out.println("La griglia ha " + griglia.getPercentuale_celle_attraversabili() +  " % di celle attraversabili con un fattore di agglomerazione di:  " + griglia.getFattore_agglomerazione_ostacoli());
		System.out.println("Andremo a creare " + numero_agenti + " agenti con un orizzonte temporale max di " + max);
		agenti=griglia.generatoreIstanze(numero_agenti,max);
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
