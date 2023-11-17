import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.lang.Math;


import java.util.LinkedList;
import java.util.Queue;

public class Griglia {
	private Vertice[][] G;
	private Dimensioni dimensioni;
	private int numero_ostacoli_MAX;
	private int numero_ostacoli;
	
	private List<Vertice> listaVerticiValidi=new ArrayList<Vertice>();
	
	// Liste di stati
	private List<Stato> open;
	private List<Stato> closed;
			
	// Strutture dati
	private Map<Stato, Double> g;
	private Map<Stato, Double> f;
			
	// Stato, Stato padre
	private Map<Stato, Stato> P;
	
	// Percorsi degli n agenti
	List<Percorso> percorsi =new ArrayList<Percorso>();
	
	// Percorsi creati da dijkstra
	Map<Vertice, Percorso> allPath=new HashMap<Vertice, Percorso>();
	
	
	public Griglia(Dimensioni dimensioni, float percentuale_celle_attraversabili, float fattore_agglomerazione_ostacoli) {
		if(percentuale_celle_attraversabili<0.0 || percentuale_celle_attraversabili>1.0 || fattore_agglomerazione_ostacoli<0.0 || fattore_agglomerazione_ostacoli>1.0)
			throw new IllegalArgumentException();
		else {
			this.dimensioni= new Dimensioni(dimensioni.getRighe(),dimensioni.getColonne());
			this.numero_ostacoli_MAX=(int) (dimensioni.getRighe()*dimensioni.getColonne()*(1.0-percentuale_celle_attraversabili));
			this.numero_ostacoli=0;
			G=generatoreGriglia(dimensioni,percentuale_celle_attraversabili,fattore_agglomerazione_ostacoli);
			
			for (int i = 0; i < dimensioni.getRighe(); i++) {
				for (int j = 0; j < dimensioni.getColonne(); j++) {
					if(!G[i][j].isOstacolo())
						listaVerticiValidi.add(G[i][j]);
				}
			}
			
		}
	}

	// inizializzo vertici come NON ostacoli
	private void inizializzaGriglia() {
		for (int i = 0; i < dimensioni.getRighe(); i++) {
			for (int j = 0; j < dimensioni.getColonne(); j++) {
				Vertice v =new Vertice(i,j, false);
				G[i][j]=v;
			}
		}
	}
	
	// Verifica se una cella ï¿½ valida (all'interno dei limiti della griglia)
    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < dimensioni.getRighe() && col >= 0 && col < dimensioni.getColonne();
    }
    
    // Posiziona agglomerato di ostacoli nelle celle vicine
    private void placeObstacleCluster(int row, int col) {
    	Random random = new Random();
        int clusterSize = (int) (Math.ceil(0.02*dimensioni.getRighe()*dimensioni.getColonne()) + random.nextInt((int) Math.ceil(0.04*dimensioni.getRighe()*dimensioni.getColonne()))); // Dimensione del cluster casuale (da 2 a 5)

        // la dimesnione del cluster non puï¿½ sforarare il numero di celle attraversabili
        if (clusterSize+numero_ostacoli>numero_ostacoli_MAX)
        	clusterSize=numero_ostacoli_MAX-numero_ostacoli;
         
        for (int i = 0; i < clusterSize; i++) {
            int xOffset = random.nextInt(3) - 1; // Valori casuali tra -1, 0 e 1
            int yOffset = random.nextInt(3) - 1;

            int newRow = row + yOffset;
            int newCol = col + xOffset;

            // Verifica se la nuova cella ï¿½ valida e vuota
            if (isValidCell(newRow, newCol) && G[newRow][newCol].isOstacolo()==false) {
                G[newRow][newCol].setOstacolo(true);
                numero_ostacoli+=1;
            }
        }
    }

    private void setListaAdiacenzaVertice(int row, int col) {
        // Verifica le otto direzioni adiacenti (sopra, sotto, sinistra, destra e diagonali) aggiungo anche il nodo stesso
        int[][] directions = {
            {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1},{0, 0}
        };
        float peso;
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            // Verifica se la nuova posizione ï¿½ all'interno dei limiti della griglia
            if (newRow >= 0 && newRow < dimensioni.getRighe() && newCol >= 0 && newCol < dimensioni.getColonne()) {
                // Verifica se NON c'ï¿½ un ostacolo nella cella adiacente
                if (!G[newRow][newCol].isOstacolo()) {
                	if((dir[0] == -1 || dir[0] == 1) && (dir[1] == -1 || dir[1] == 1))
                		peso=(float) Math.sqrt(2);
                	else peso=(float) 1.0;
                    G[row][col].addVerticeAdiacente(G[newRow][newCol],peso);
                }
            }
        }

        
    }
    
	private Vertice[][] generatoreGriglia(Dimensioni dimensioni, float percentuale_celle_attraversabili, float fattore_agglomerazione_ostacoli) {
		
		G=new Vertice[dimensioni.getRighe()][dimensioni.getColonne()];
		Random random = new Random();
		float percentuale_ostacoli=(float) (1-percentuale_celle_attraversabili);
		// inizializzo vertici a 
		inizializzaGriglia();
		
		while(numero_ostacoli<numero_ostacoli_MAX) {
		for (int row = 0; row < dimensioni.getRighe(); row++) {
            for (int col = 0; col < dimensioni.getColonne(); col++) {
                // Genera un valore casuale tra 0 e 1
                double randomValue = random.nextDouble();

                // Verifica se la cella deve contenere un ostacolo in base alla densitï¿½ specificata
                boolean hasObstacle = randomValue <= percentuale_ostacoli;
                
                if (hasObstacle && numero_ostacoli<numero_ostacoli_MAX) {
                    // Posiziona l'ostacolo
                    G[row][col].setOstacolo(true);
                    numero_ostacoli+=1;
                    // Agglomerazione degli ostacoli
                    if (randomValue <= percentuale_ostacoli * fattore_agglomerazione_ostacoli) {
                        // Posiziona altri ostacoli nelle celle vicine
                        placeObstacleCluster(row, col);
                    }
                }
            }
        }
		}
		
		inizializza_matrice_W();
		return G;	
	}
	
	// aggiorno i pesi della matrice
	private void inizializza_matrice_W() {
		for (int i = 0; i < dimensioni.getRighe(); i++) {
			for (int j = 0; j < dimensioni.getColonne(); j++) {
				if (!G[i][j].isOstacolo()) {
					// aggiorno lista di adiacenza del vertice
					setListaAdiacenzaVertice(i,j);
				}
			}
		}
	}
	
	// stampe
	public void printGrafo() {
		int agente=0;
		for (Percorso p : percorsi) {
			System.out.println("Agente " + agente);
			System.out.println("Init si trova: x: "+p.getInit().getX()+ "y: "+p.getInit().getY());
        	System.out.println("Il goal si trova: x: "+p.getGoal().getX()+ "y: "+p.getGoal().getY());
			System.out.println("peso percorso: "+ p.getPeso());
			String[][] grafo =new  String[dimensioni.getRighe()][dimensioni.getColonne()];
			for (int i = 0; i < dimensioni.getRighe(); i++) {
				for (int j = 0; j < dimensioni.getColonne(); j++) {
					if (G[i][j].isOstacolo())
						grafo[i][j]="# | ";
					else grafo[i][j]="  | ";		
				}
			}
			
			for (int i = 0; i < p.getPercorso().size(); i++) {
				int x=p.getPercorso().get(i).getVertice().getX();
				int y=p.getPercorso().get(i).getVertice().getY();
				int t=p.getPercorso().get(i).getIstante_temporale();
				
				if(G[x][y].isOstacolo())
					System.out.println("Passo attraverso ostacolo");
				grafo[x][y]= String.format("%d | ",t);
				
			}
		
		
		for (int i = 0; i < dimensioni.getRighe(); i++) {
			for (int j = 0; j < dimensioni.getColonne(); j++) {
				System.out.print(grafo[i][j]);
			}
			System.out.println();
		}
		agente++;
		}
	}
	
	public void printPercorso(Percorso p) {
			System.out.println("Init si trova: x: "+p.getInit().getX()+ "y: "+p.getInit().getY());
        	System.out.println("Il goal si trova: x: "+p.getGoal().getX()+ "y: "+p.getGoal().getY());
			System.out.println("peso percorso: "+ p.getPeso());
			String[][] grafo =new  String[dimensioni.getRighe()][dimensioni.getColonne()];
			for (int i = 0; i < dimensioni.getRighe(); i++) {
				for (int j = 0; j < dimensioni.getColonne(); j++) {
					if (G[i][j].isOstacolo())
						grafo[i][j]="# | ";
					else grafo[i][j]="  | ";		
				}
			}
			
			for (int i = 0; i < p.getPercorso().size(); i++) {
				int x=p.getPercorso().get(i).getVertice().getX();
				int y=p.getPercorso().get(i).getVertice().getY();
				int t=p.getPercorso().get(i).getIstante_temporale();
				
				if(G[x][y].isOstacolo())
					System.out.println("Passo attraverso ostacolo");
				grafo[x][y]= String.format("%d | ",t);
				
			}
		
		
		for (int i = 0; i < dimensioni.getRighe(); i++) {
			for (int j = 0; j < dimensioni.getColonne(); j++) {
				System.out.print(grafo[i][j]);
			}
			System.out.println();
		}
		
	}
	
	public void printMatriceW() {
		for (int i = 0; i < dimensioni.getRighe(); i++) {
			for (int j = 0; j < dimensioni.getColonne(); j++) {
				if (!G[i][j].isOstacolo())
					System.out.println("Vertice r: "+ i + ", c: " + j + "-->" + G[i][j].PrintlistaAdiacenza());
			}
			
		}
	}
	
	// V[G]
	public List<Vertice> verticiG (){
		List<Vertice> res = new ArrayList<>();
		
		for (int i = 0; i < dimensioni.getRighe(); i++) {
			for (int j = 0; j < dimensioni.getColonne(); j++) {
				if(!G[i][j].isOstacolo())
					res.add(G[i][j]);
			}
		}
		return res;
	}
	
	
	public double h(Vertice vertice, Vertice goal) {
		
		// distanza diagonale
		double dx= Math.abs(vertice.getX()-goal.getX());
		double dy= Math.abs(vertice.getY()-goal.getY());
		
		return dx+dy + (Math.sqrt(2)-2)*Math.min(dx, dy);		
	}
	
	    		
    private List<Stato> ReconstructPath(Vertice init,Vertice goal,Map<Stato,Stato>P,int t) {
    	
    	List<Stato> res=new ArrayList<>();
    	
    	// controllo che ultimo elemento di array sia il goal
    	if(closed.get(closed.size()-1).equals(new Stato(goal, t))) {
    		
    		// mettiamo il goal che è all'ultima posizione
    		res.add(closed.get(closed.size()-1));
    		// mettiamo il padre di goal che è all'ultima posizione,  solo se init e goal sono diversi
    		if(!init.equals(goal))
    			res.add(P.get(closed.get(closed.size()-1)));
    	}

		if(res.contains(null)){
			System.err.println("ERRORE: il goal è isolato. Riprova!");
			return null;
		}else{
    	// ripeto fino a che non sono in init con t=0
    	while(!res.get(res.size()-1).equals(new Stato(init, 0))) {
    		//aggiungo il padre dell'ultimo elemento di res
    		res.add(P.get(res.get(res.size()-1)));
    	}
	}
    	Collections.reverse(res);
    	
    	return res;
    	
    }

    public List<Stato> ReachGoal(Griglia G, List<Percorso> agenti, Vertice init,Vertice goal, int max){
    	
    	// Liste di stati
    	open = new ArrayList<>();
    	closed=new ArrayList<>();
    			
    	// Strutture dati
    	g= new HashMap<>();
    	f= new HashMap<>();
    			
    	// Stato, Stato padre
    	P= new HashMap<>();
    	
    	if(init.getListaAdiacenza().size()==1 || goal.getListaAdiacenza().size()==1){
			System.err.println("Init o goal isolato");
			return null;
		}
		// controllo che i percorsi presistenti degli n agenti partano tutti da un vertice diverso e non uguale a init
    	for (Percorso a : agenti) {
			if(a.getPercorso().contains(new Stato(init,0))) {
					System.err.println("ERRORE: Posizione iniziale agente uguale a init. Riprova!");
					return null;
			}
    	}
    	
		open.add(new Stato(init,0));
		
		// g ï¿½ il costo per raggiungere il vertice (parametro 1) specificato all'istante parametro 2, con costo parametro (3)
		g.put(new Stato(init,0), 0.0);
		f.put(new Stato(init,0), h(init,goal));
		
		while(!open.isEmpty()) {
			
			Stato minStato=open.get(0);
		
			double min= Double.POSITIVE_INFINITY;
			
			// riga 13
			for (Stato stato : open) {
				Double gValue = g.get(stato);
			    double currentCost = (gValue != null) ? gValue : Double.POSITIVE_INFINITY;

			    if (currentCost + h(stato.getVertice(), goal) < min) {
			        min = currentCost + h(stato.getVertice(), goal);
			        minStato = stato;
			    }
			}
			
			open.remove(minStato);
			closed.add(minStato);
			
			if (minStato.getVertice().equals(goal)) {
				return ReconstructPath(init,goal,P,minStato.getIstante_temporale());
			}
			
			// parte 3
			int t=minStato.getIstante_temporale();
			Vertice v=minStato.getVertice();
			if (t<max) {
				for (Vertice n : v.getListaAdiacenza().keySet()) {
					if(!closed.contains(new Stato(n,t))) {
						boolean traversable=true;
						
						for (Percorso a : agenti) {
							if(a.getPercorso().contains(new Stato(n,t+1)) || 
									(a.getPercorso().contains(new Stato(v,t+1)) && a.getPercorso().contains(new Stato(n,t)))) {
								traversable=false;
							}
							
							// collisione con un agente preesistente fermo nella sua cella finale (il nodo percorso non deve passare nel goal di un altro percorso in un istante temporale succesivo a quello del goal)
							if(a.getPercorso().get(a.getPercorso().size()-1).getVertice().equals(n) && t>=a.getPercorso().get(a.getPercorso().size()-1).getIstante_temporale())
								traversable=false;
						}
						
						// parte 4
						if (traversable) {
						    Double minStatoGValue = g.get(minStato);
						    double currentCost = (minStatoGValue != null) ? minStatoGValue : Double.POSITIVE_INFINITY;

						    Double nT1GValue = g.get(new Stato(n, t + 1));
						    double newCost = (nT1GValue != null) ? nT1GValue : Double.POSITIVE_INFINITY;

						    if (currentCost + v.getListaAdiacenza().get(n) < newCost) {
						        P.put(new Stato(n, t + 1), minStato);
						        g.put(new Stato(n, t + 1), currentCost + v.getListaAdiacenza().get(n));
						        f.put(new Stato(n, t + 1), g.get(new Stato(n, t + 1)) + h(n, goal));
						    }
						}
						if (!open.contains(new Stato(n,t+1))) {
							open.add(new Stato(n,t+1));
						}
					}
				}
			}
		}
		System.err.println("ERRORE: impossibile generare il percorso ");
		return null;
	}
    
    public List<Stato> ReachGoalAlternativo(Griglia G, List<Percorso> agenti, Vertice init,Vertice goal, int max){
    	
    	// Liste di stati
    	open = new ArrayList<>();
    	closed=new ArrayList<>();
    			
    	// Strutture dati
    	g= new HashMap<>();
    	f= new HashMap<>();
    			
    	// Stato, Stato padre
    	P= new HashMap<>();
    	
    	if(init.getListaAdiacenza().size()==1 || goal.getListaAdiacenza().size()==1){
			System.err.println("Init o goal isolato");
			return null;
		}
		// controllo che i percorsi presistenti degli n agenti partano tutti da un vertice diverso e non uguale a init
    	for (Percorso a : agenti) {
			if(a.getPercorso().contains(new Stato(init,0))) {
					System.err.println("ERRORE: Posizione iniziale agente uguale a init. Riprova!");
					return null;
			}
    	}
    	
		open.add(new Stato(init,0));
		
		// g ï¿½ il costo per raggiungere il vertice (parametro 1) specificato all'istante parametro 2, con costo parametro (3)
		g.put(new Stato(init,0), 0.0);
		f.put(new Stato(init,0), h(init,goal));
		
		while(!open.isEmpty()) {
			
			Stato minStato=open.get(0);
		
			double min= Double.POSITIVE_INFINITY;
			
			// riga 13
			for (Stato stato : open) {
				Double gValue = g.get(stato);
			    double currentCost = (gValue != null) ? gValue : Double.POSITIVE_INFINITY;

			    if (currentCost + h(stato.getVertice(), goal) < min) {
			        min = currentCost + h(stato.getVertice(), goal);
			        minStato = stato;
			    }
			}
			
			open.remove(minStato);
			closed.add(minStato);
			
			if (minStato.getVertice().equals(goal)) {
				return ReconstructPath(init,goal,P,minStato.getIstante_temporale());
			}
			
			int t=minStato.getIstante_temporale();
			Vertice v=minStato.getVertice();
			
			// ALTERNATIVA -------------------------------------
			
			// ottengo percorso da v a goal sfruttando djikstra
			if(allPath.containsKey(v)) {
				Percorso p= allPath.get(v);
				// aggiorno l'istante temporale del percorso
				p=aggiornaIstantiTemporali(p,t);
		    	if(this.isConflitto(p, agenti)==false) {
		    		List<Stato> res= ReconstructPath(init, v, P, t);
		    		//inserisco i vertici dopo v
		    		for (int i = 1; i < p.getPercorso().size(); i++) {
						res.add(p.getPercorso().get(i));
					}
		    		return res;
		    	}
			}
			
			
	    	
	    	// --- FINE ALTERNATIVA ------------
			// parte 3
			
			if (t<max) {
				for (Vertice n : v.getListaAdiacenza().keySet()) {
					if(!closed.contains(new Stato(n,t))) {
						boolean traversable=true;
						
						for (Percorso a : agenti) {
							if(a.getPercorso().contains(new Stato(n,t+1)) || 
									(a.getPercorso().contains(new Stato(v,t+1)) && a.getPercorso().contains(new Stato(n,t)))) {
								traversable=false;
							}
							
							// collisione con un agente preesistente fermo nella sua cella finale (il nodo percorso non deve passare nel goal di un altro percorso in un istante temporale succesivo a quello del goal)
							if(a.getPercorso().get(a.getPercorso().size()-1).getVertice().equals(n) && t>=a.getPercorso().get(a.getPercorso().size()-1).getIstante_temporale())
								traversable=false;
						}
						
						// parte 4
						if (traversable) {
						    Double minStatoGValue = g.get(minStato);
						    double currentCost = (minStatoGValue != null) ? minStatoGValue : Double.POSITIVE_INFINITY;

						    Double nT1GValue = g.get(new Stato(n, t + 1));
						    double newCost = (nT1GValue != null) ? nT1GValue : Double.POSITIVE_INFINITY;

						    if (currentCost + v.getListaAdiacenza().get(n) < newCost) {
						        P.put(new Stato(n, t + 1), minStato);
						        g.put(new Stato(n, t + 1), currentCost + v.getListaAdiacenza().get(n));
						        f.put(new Stato(n, t + 1), g.get(new Stato(n, t + 1)) + h(n, goal));
						    }
						}
						if (!open.contains(new Stato(n,t+1))) {
							open.add(new Stato(n,t+1));
						}
					}
				}
			}
		}
		System.err.println("ERRORE: impossibile generare il percorso ");
		return null;
	}
    
    public Percorso aggiornaIstantiTemporali(Percorso p, int t) {
    	p.getPercorso().forEach((u) -> u.setIstante_temporale(u.getIstante_temporale()+t));
		return p;
    }
    // CHECK algoritmo percorso rilassato (Djkstra)
    
    public void Dijkstra(Griglia G, Vertice goal) {
	// inizialize single source => implicita quando creo un vertice
	
	List<Vertice> S=new ArrayList<>();
	Queue<Vertice> Q=new PriorityQueue<>();
	
	// setto vertice di partenza
	this.G[goal.getX()][goal.getY()].setPeso(0);
	
	// aggiugo tutti gli elementi della griglia
	for (int i = 0; i < dimensioni.getRighe(); i++) {
		for (int j = 0; j < dimensioni.getColonne(); j++) {
			if(!this.G[i][j].isOstacolo()) {
				Q.add(this.G[i][j]);
				
			}
		}
	}
	
	while (!Q.isEmpty()){
		Vertice u= Q.poll();
		
		S.add(u);
		
		for(Vertice v: u.getListaAdiacenza().keySet()){
			// effettuo il relax
			
			//d[v] > d[u] + w(u, v)
			if(v.getPeso()> u.getPeso()+ u.getListaAdiacenza().get(v)) {
				// elimino elemento prima di aggiornarlo
				Q.remove(v);
				v.setPeso(u.getPeso()+ u.getListaAdiacenza().get(v));
				v.setPadre(u);
				// aggiorno la struttura G per essere reperibile 
				this.G[v.getX()][v.getY()].setPeso(v.getPeso());
				this.G[v.getX()][v.getY()].setPadre(v.getPadre());
				// aggiorno con il nuovo elemento
				Q.add(v);
			}
		}	
	}
	
	// creo la struttura con i percorsi da goal a tutti i vertici del grafo
	
	for (int i = 0; i < dimensioni.getRighe(); i++) {
		 for (int j = 0; j < dimensioni.getColonne(); j++) {
			 
			 if(this.G[i][j].isOstacolo()==false) {
			List<Stato> p=new ArrayList<>();
			 // creo la lista di vertici
			 Vertice last=this.G[i][j];
			 Vertice init=this.G[i][j];
			 int t=0;
			 p.add(new Stato(last,t));
			
			// variabile per specificare se il percorso che creo è valido
			boolean valido=true;
			
			// continuo ad inserire finchè diverso da goal
			
			while(!last.equals(goal)) {
				// aggiungo padre dell'ultimo elemento
				p.add(new Stato(last.getPadre(),p.get(p.size()-1).getIstante_temporale()+1));
				last=last.getPadre();
				if(last==null) {
					valido=false;
					break;
				}
			}
			if(valido==true) {
				Percorso pr=new Percorso(p, init, goal);
				// inserisco il percorso nella variabile globale
				allPath.put(init, pr);
			}
		}
		 }
	}
	
	
	
}

// TODO algoritmo per il controllo dei conflitti cammini preesistenti, restituisce true se trova un conflitto
    
    private boolean isConflitto(Percorso p, List<Percorso> agenti) {

    	int start=p.getPercorso().get(0).getIstante_temporale();
    	int end=p.getPercorso().get(p.getPercorso().size()-1).getIstante_temporale();
	for (int i = start; i < end; i++) {
		for (Percorso a:agenti) {
			// controllo di non passare in uno stato finale di un agente in un istante successivo
			if ((p.getPercorso().get(i).getVertice().equals(a.getPercorso().get(a.getPercorso().size()-1).getVertice())) &&
					(p.getPercorso().get(i).getIstante_temporale()>= a.getPercorso().get(a.getPercorso().size()-1).getIstante_temporale()))
			return true;
						
			if(i < a.getPercorso().get(a.getPercorso().size()-1).getIstante_temporale()){
				// stato già presente in un percorso, potrebbe essere anche l'init
				if(p.getPercorso().get(i).equals(a.getPercorso().get(i)))
					return true;
				//scambio di posizione SCONTRO
				if((p.getPercorso().get(i+1).getVertice().equals(a.getPercorso().get(i).getVertice()))
						&& (p.getPercorso().get(i).getVertice().equals(a.getPercorso().get(i+1).getVertice())))
					return true;
			
		}
	}
}
	return false;
}
    


    public Vertice[] generaInitGoal(List <Percorso> percorsi) {
	   
	   // indici init
       int i;
       //indici goal
       int g;
       do {
       
    	   // INIT - controllo che non sia un ostacolo
    	   Random random = new Random();
    	   i = random.nextInt(listaVerticiValidi.size());
    	  
       // GOAL - controllo che non sia un ostacolo
       
       boolean valido;
       do {
    	   valido=true;
    	   g = random.nextInt(listaVerticiValidi.size());
    	   
    	// se genero un goal che ï¿½ all'interno delle celle visitate da un altro percorso, non ï¿½ valido
    	   for (Percorso percorso : percorsi) {
				for (Vertice vertice : percorso.getAllVertici()) {
			        if (vertice.equals(listaVerticiValidi.get(g))) 
			        	valido=false;
			    }
    	   }
       }while(!valido);
       }while(listaVerticiValidi.get(i).equals(listaVerticiValidi.get(g)));
       
       Vertice[] result = new Vertice[2];
       result[0] = listaVerticiValidi.get(i);
       result[1] = listaVerticiValidi.get(g);
       return result;
   }
   
   public List<Percorso> generatoreIstanze(int numero_agenti){
    	int max=(dimensioni.getRighe()*dimensioni.getColonne()) -numero_ostacoli;
    	int istanti_max = 0;
    	int i=0;
		int j=0;

    	while(i<numero_agenti) {
    		if(j>=10){
				System.err.println("Non Ã¨ possibile generare "+ numero_agenti + " agenti, sono stati generati solo "+i+" agenti");
				break;
			} else{
			Vertice init,goal;
    		
    		Vertice[] res=generaInitGoal(percorsi);
    		init=res[0];
    		goal=res[1];
    		
    		Percorso t=null;
    		if(i==0 && j<10) {
    			t=new Percorso(ReachGoal(this, percorsi, init, goal, max),init,goal);
				if(t.getPercorso()==null){
					j++;
				} else {
					j=0;
				}
		}
				if(i!=0 && j<10){
        		t=new Percorso(ReachGoal(this, percorsi, init, goal, max+istanti_max),init,goal);
				if(t.getPercorso()==null || t ==null){
				j++;
			} else {
				j=0;
			}
		}
    		
    		if(t!= null && t.getPercorso()!=null && !t.getPercorso().contains(null)) {
    			System.out.println("Trovato il percorso dell'agente "+i);
				percorsi.add(t);
    			istanti_max= percorsi.get(i).getPercorso().size()-1;
    			i++;
    		}
    		
    	
    }
	
		}
		return percorsi;
}
   public List<Percorso> getPercorsi(){
	return percorsi;
}

	public Vertice getVertice(int x, int y) {
		return this.G[x][y];
	}
}

