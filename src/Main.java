import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Main {

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		int righe=0,colonne=0,numero_agenti=0,max=0;
		double p_c_a=0.0,f_a_o=0.0;

		do {
			System.out.print("Inserisci numero righe della griglia: ");
			righe = scanner.nextInt();
		}while(righe<=0);

		do {
		System.out.print("Inserisci numero colonne della griglia: ");
		colonne=scanner.nextInt();
		}while(colonne<=0);

		do {
			System.out.print("Inserisci percentuale celle attraversabili <1: ");
			p_c_a = scanner.nextDouble();
		}while(p_c_a<=0.0 || p_c_a>1.0);

		do{
		System.out.print("Inserisci fattore di agglomerazione ostacoli <1: ");
		f_a_o=scanner.nextDouble();
		}while(f_a_o<=0.0 || f_a_o>=1.0);

		do{
		System.out.print("Inserisci numero di agenti preesistenti: ");
		numero_agenti=scanner.nextInt();
		}while(numero_agenti<0);

		do {
			System.out.print("Inserisci max lunghezza percorso: ");
			max = scanner.nextInt();
		}while(max<0 || max>=(righe*colonne)-(righe*colonne*(1-p_c_a)));

		int scelta=0;
		do{
		System.out.println("SCELTA GOAL: \n 1->goal generato casualmente \n 2->personalizzato:");
		scelta=scanner.nextInt();
		}while(scelta!=1 && scelta!=2);

		Dimensioni dimensioni=new Dimensioni(righe,colonne);
		Griglia griglia= new Griglia(dimensioni, p_c_a, f_a_o);
		List<Percorso> agenti= new ArrayList<Percorso>();

		System.out.println("Dimensione della griglia utilizzata: " + dimensioni.getRighe() + " x " +dimensioni.getColonne());
		System.out.println("La griglia ha " + griglia.getPercentuale_celle_attraversabili() +  " % di celle attraversabili con un fattore di agglomerazione di:  " + griglia.getFattore_agglomerazione_ostacoli());
		System.out.println("Andremo a creare " + numero_agenti + " agenti con un orizzonte temporale max di " + max);

		// Record start time
		long startTime = System.currentTimeMillis();

		agenti=griglia.generatoreIstanze(numero_agenti,max);
		// stampo i percorsi degli agenti
		griglia.printAgenti();

		Vertice[] v = new Vertice[2];
		if (scelta==1)
			v= griglia.generaInitGoal(max);
		else {
			int i_riga = 0,i_colonna=0;
			int g_riga = 0,g_colonna=0;
			do {
				System.out.print("INIT - Inserisci coordinata riga: ");
				i_riga= scanner.nextInt();
			}while(i_riga<0 || i_riga>=righe);
			do {
				System.out.print("INIT - Inserisci coordinata colonna: ");
				i_colonna= scanner.nextInt();
			}while(i_colonna<0 || i_colonna>=colonne);
			do {
				System.out.print("GOAL - Inserisci coordinata riga: ");
				g_riga= scanner.nextInt();
			}while(g_riga<0 || g_riga>=righe);
			do {
				System.out.print("GOAL - Inserisci coordinata colonna: ");
				g_colonna= scanner.nextInt();
			}while(g_colonna<0 || g_colonna>=colonne);
			v[0]=griglia.getVertice(i_riga,i_colonna) ;
			v[1]=griglia.getVertice(g_riga,g_colonna);
		}
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

		// Record end time
		long endTime = System.currentTimeMillis();

		// Calculate and print execution time
		long executionTime = endTime - startTime;
		System.out.println("Execution Time: " + executionTime + " milliseconds");

		// Get memory usage
		long totalMemory = Runtime.getRuntime().totalMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		long usedMemory = totalMemory - freeMemory;

		// Print memory usage
		System.out.println("Total Memory: " + totalMemory + " bytes");
		System.out.println("Free Memory: " + freeMemory + " bytes");
		System.out.println("Used Memory: " + usedMemory + " bytes");


	}

}
