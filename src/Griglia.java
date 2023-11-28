import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.lang.Math;
import java.util.Queue;

public class Griglia {
	private Vertice[][] G;
	private Dimensioni dimensioni;
	// da input
	private int numero_ostacoli_MAX;
	
	//durante la creaizone dei cluster
	private int numero_ostacoli;
	
	private List<Vertice> listaOstacoli=new ArrayList<Vertice>();
	
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
	List<ReachGoal> reachGoals =new ArrayList<ReachGoal>();
	// Percorsi creati da dijkstra
	Map<Vertice, Percorso> allPath=new HashMap<Vertice, Percorso>();

	public double getPercentuale_celle_attraversabili() {
		return percentuale_celle_attraversabili;
	}

	public double getFattore_agglomerazione_ostacoli() {
		return fattore_agglomerazione_ostacoli;
	}

	private double percentuale_celle_attraversabili;
	private double fattore_agglomerazione_ostacoli;
	
	public Griglia(Dimensioni dimensioni, double percentuale_celle_attraversabili, double fattore_agglomerazione_ostacoli) {
		if(percentuale_celle_attraversabili<0.0 || percentuale_celle_attraversabili>1.0 || fattore_agglomerazione_ostacoli<0.0 || fattore_agglomerazione_ostacoli>1.0)
			throw new IllegalArgumentException();
		else {
			this.dimensioni= new Dimensioni(dimensioni.getRighe(),dimensioni.getColonne());
			this.numero_ostacoli_MAX=(int) (dimensioni.getRighe()*dimensioni.getColonne()*(1.0-percentuale_celle_attraversabili));
			this.numero_ostacoli=0;
			this.percentuale_celle_attraversabili=percentuale_celle_attraversabili;
			this.fattore_agglomerazione_ostacoli=fattore_agglomerazione_ostacoli;

			G=generatoreGriglia();
			
			// aggiorno la lista dei vertici validi
			for (int i = 0; i < dimensioni.getRighe(); i++) {
				for (int j = 0; j < dimensioni.getColonne(); j++) {
					if(!G[i][j].isOstacolo())
						listaVerticiValidi.add(G[i][j]);
				}
			}
			
		}
	}

	// inizializzo vertici come NON ostacoli
	private void inizializzaVerticiGriglia() {
		for (int i = 0; i < dimensioni.getRighe(); i++) {
			for (int j = 0; j < dimensioni.getColonne(); j++) {
				Vertice v =new Vertice(i,j, false);
				G[i][j]=v;
				listaOstacoli.add(v);
			}
		}
	}
	
	// Verifica se una cella è valida (all'interno dei limiti della griglia)
    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < dimensioni.getRighe() && col >= 0 && col < dimensioni.getColonne();
    }
    
    // Posiziona agglomerato di ostacoli nelle celle vicine
    private void placeObstacleCluster(int row, int col, double fattore_agglomerazione_ostacoli) {
    	Random random = new Random();
        int clusterSize = (int) 1+random.nextInt((int) Math.ceil((fattore_agglomerazione_ostacoli/10)*dimensioni.getRighe()*dimensioni.getColonne())); // Dimensione del cluster casuale (da 2 a fattore di agglomerazione)

        // la dimesnione del cluster non puï¿½ sforarare il numero di celle attraversabili
        if (clusterSize+numero_ostacoli>numero_ostacoli_MAX)
        	clusterSize=numero_ostacoli_MAX-numero_ostacoli;
         
        while(clusterSize>0) {
            int xOffset = random.nextInt(3) - 1; // Valori casuali tra -1, 0 e 1
            int yOffset = random.nextInt(3) - 1;

            int newRow = row + yOffset;
            int newCol = col + xOffset;
            
            // Verifica se la nuova cella è valida e vuota
            if (isValidCell(newRow, newCol) ) {
            	if(G[newRow][newCol].isOstacolo()==false) {
            		G[newRow][newCol].setOstacolo(true);
            		numero_ostacoli+=1;
            		listaOstacoli.remove(G[newRow][newCol]);
            		
            	}
            	clusterSize--;
            }
            
        }
    }

    // Crea la lista di adiacenza per il vertice specificato dalle coordinate come parametri
    private void setListaAdiacenzaVertice(int row, int col) {
        // Verifica le otto direzioni adiacenti (sopra, sotto, sinistra, destra e diagonali) aggiungo anche il nodo stesso
        int[][] directions = {
            {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1},{0, 0}
        };
        float peso;
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            // Verifica se la nuova posizione è all'interno dei limiti della griglia
            if (newRow >= 0 && newRow < dimensioni.getRighe() && newCol >= 0 && newCol < dimensioni.getColonne()) {
                // Verifica se NON c'è un ostacolo nella cella adiacente
                if (!G[newRow][newCol].isOstacolo()) {
                	if((dir[0] == -1 || dir[0] == 1) && (dir[1] == -1 || dir[1] == 1))
                		peso=(float) Math.sqrt(2);
                	else peso=(float) 1.0;
                    G[row][col].addVerticeAdiacente(G[newRow][newCol],peso);
                }
            }
        }

        
    }
    
	private Vertice[][] generatoreGriglia() {
		
		G=new Vertice[dimensioni.getRighe()][dimensioni.getColonne()];
		Random random = new Random();
		double percentuale_ostacoli= 1-percentuale_celle_attraversabili;
		
		// inizializzo vertici a 
		inizializzaVerticiGriglia();
		
		
		while(numero_ostacoli<numero_ostacoli_MAX) {
		Vertice r= listaOstacoli.get(random.nextInt(listaOstacoli.size()));
                // Genera un valore casuale tra 0 e 1
                double rv = random.nextDouble();

                // Verifica se la cella deve contenere un ostacolo in base alla densità specificata
                boolean hasObstacle = rv <= percentuale_ostacoli;
                
                if (hasObstacle && numero_ostacoli<numero_ostacoli_MAX) {
                    // Posiziona l'ostacolo
                	if(!G[r.getX()][r.getY()].isOstacolo()) {
                    // Agglomerazione ostacolo
                    if (rv <= fattore_agglomerazione_ostacoli) {
                        // Posiziona altri ostacoli nelle celle vicine
                        placeObstacleCluster(r.getX(), r.getY(), fattore_agglomerazione_ostacoli);
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
	
	// Funzione untilizzata per la stampa degli agenti
	public void printAgenti() {
		
		System.out.println("NUMERO OSTACOLI: "+ numero_ostacoli);
		int agente=0;
		for (ReachGoal r : reachGoals) {
			System.out.println("Agente " + agente);
			System.out.println("Init si trova: x: "+ r.getPercorso().get(0).getVertice().getX()+ " ; y: "+ r.getPercorso().get(0).getVertice().getY());
        	System.out.println("Il goal si trova: x: "+r.getPercorso().get(r.getPercorso().size()-1).getVertice().getX()+ " ; y: "+r.getPercorso().get(r.getPercorso().size()-1).getVertice().getY());
			System.out.println(r.toString());
			String[][] grafo =new  String[dimensioni.getRighe()][dimensioni.getColonne()];
			for (int i = 0; i < dimensioni.getRighe(); i++) {
				for (int j = 0; j < dimensioni.getColonne(); j++) {
					if (G[i][j].isOstacolo())
						grafo[i][j]="# | ";
					else grafo[i][j]="  | ";		
				}
			}
			
			for (int i = 0; i < r.getPercorso().size(); i++) {
				int x=r.getPercorso().get(i).getVertice().getX();
				int y=r.getPercorso().get(i).getVertice().getY();
				int t=r.getPercorso().get(i).getIstante_temporale();
				
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
	
	// Funzione utilizzata per la stampa del percorso cercato
	public void printPercorso(ReachGoal r) {
			System.out.println("Init si trova: x: "+ r.getPercorso().get(0).getVertice().getX()+ " ; y: "+ r.getPercorso().get(0).getVertice().getY());
        	System.out.println("Il goal si trova: x: "+r.getPercorso().get(r.getPercorso().size()-1).getVertice().getX()+ " ; y: "+r.getPercorso().get(r.getPercorso().size()-1).getVertice().getY());
			System.out.println(r.toString());
			String[][] grafo =new  String[dimensioni.getRighe()][dimensioni.getColonne()];
			for (int i = 0; i < dimensioni.getRighe(); i++) {
				for (int j = 0; j < dimensioni.getColonne(); j++) {
					if (G[i][j].isOstacolo())
						grafo[i][j]="# | ";
					else grafo[i][j]="  | ";		
				}
			}
			
			for (int i = 0; i < r.getPercorso().size(); i++) {
				int x=r.getPercorso().get(i).getVertice().getX();
				int y=r.getPercorso().get(i).getVertice().getY();
				int t=r.getPercorso().get(i).getIstante_temporale();
				
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
		System.out.println("Vertici toccati");
		for (int i = 0; i < r.getPercorso().size(); i++) {
			System.out.print(r.getPercorso().get(i).getVertice().toString() + " --> ");
		}
		System.out.println();
		
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
    	Stato tmp=new Stato(goal,t);
    	Stato i=new Stato(init,0);

    	while(!tmp.equals(i)) {
    		if(P.containsKey(tmp)) {
    			res.add(tmp);
    			tmp=P.get(tmp);
    		}
    	}
    	//aggiungo init
    	res.add(tmp);
    	Collections.reverse(res);
    	return res;
    	
    }

    public ReachGoal ReachGoal(Griglia G, List<Percorso> agenti, Vertice init,Vertice goal, int max){
    	
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
					System.err.println("ERRORE: Posizione iniziale di un agente uguale a init. Riprova!");
					return null;
			}
    	}
    	
		open.add(new Stato(init,0));
		
		// g è il costo per raggiungere il vertice (parametro 1) specificato all'istante parametro 2, con costo parametro (3)
		g.put(new Stato(init,0), 0.0);
		f.put(new Stato(init,0), h(init,goal));
		P.put(new Stato(init,0),null);
		
		
		while(!open.isEmpty()) {
			// Prendo lo stato in open con il più piccolo valore di f
			Stato minStato=open.get(0);
			double min= Double.POSITIVE_INFINITY;
			// riga 13
			for (Stato stato : open) {
			    double currentCost = f.get(stato);
			    if (currentCost< min) {
			        min = currentCost;
			        minStato = stato;
			    }
			}
			
			open.remove(minStato);
			closed.add(minStato);
			
			if (minStato.getVertice().equals(goal)) {
				Percorso r= new Percorso(ReconstructPath(init,goal,P,minStato.getIstante_temporale()), init, goal);
				int wait=0;
				for (int i = 0; i < r.getPercorso().size()-1; i++) {
					// wait
					if(r.getPercorso().get(i).getVertice().equals(r.getPercorso().get(i+1).getVertice()))
						wait++;
				}
				return new ReachGoal(r.getPercorso(), P.size(),closed.size(), r.getPercorso().size()-1,r.getPeso(), wait);
			}
			
			// parte 3
			int t=minStato.getIstante_temporale();
			Vertice v=minStato.getVertice();
			if (t<max) {
				for (Vertice n : v.getListaAdiacenza().keySet()) {
					if(!closed.contains(new Stato(n,t+1))) {
						boolean traversable=true;
						
						for (Percorso a : agenti) {
							if(a.getPercorso().contains(new Stato(n,t+1)) || 
									(a.getPercorso().contains(new Stato(v,t+1)) && a.getPercorso().contains(new Stato(n,t)))) {
								traversable=false;
							}
							
							// collisione con un agente preesistente fermo nella sua cella finale (il percorso non deve passare nel goal di un altro agente in un istante temporale successivo a quello del goal dell'agente)
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
							if (!open.contains(new Stato(n,t+1))) {
								open.add(new Stato(n,t+1));
							}
						}

					}
				}
			}
		}
		System.err.println("ReachGoal: impossibile generare il percorso");
		return null;
	}
    
    public ReachGoal ReachGoalAlternativo(Griglia G, List<Percorso> agenti, Vertice init,Vertice goal, int max){
    	
    	// Liste di stati
    	open = new ArrayList<>();
    	closed=new ArrayList<>();
    			
    	// Strutture dati
    	g= new HashMap<>();
    	f= new HashMap<>();
    			
    	// Stato, Stato padre
    	P= new HashMap<>();
    	int wait=0;

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
			// Prendo lo stato in open con il più piccolo valore di f
			Stato minStato=open.get(0);
			double min= Double.POSITIVE_INFINITY;
			// riga 13
			for (Stato stato : open) {
				double currentCost = f.get(stato);
				if (currentCost< min) {
					min = currentCost;
					minStato = stato;
				}
			}
			
			open.remove(minStato);
			closed.add(minStato);
			
			if (minStato.getVertice().equals(goal)) {

				Percorso r= new Percorso(ReconstructPath(init,goal,P,minStato.getIstante_temporale()), init, goal);
					for (int i = 0; i < r.getPercorso().size()-1; i++) {
						// wait
						if(r.getPercorso().get(i).getVertice().equals(r.getPercorso().get(i+1).getVertice()))
							wait++;
					}
					return new ReachGoal(r.getPercorso(), P.size(),closed.size(), r.getPercorso().size()-1,r.getPeso(), wait);
			}
			
			int t=minStato.getIstante_temporale();
			Vertice v=minStato.getVertice();
			
			// ALTERNATIVA -------------------------------------
			
			// ottengo percorso da v a goal sfruttando djikstra
			if(allPath.containsKey(v)) {
				Percorso p= new Percorso(allPath.get(v).getPercorso(),v,goal);
				
		    	if(this.isConflitto(p, agenti,t)==false) {
		    		List<Stato> res= ReconstructPath(init, v, P, t);
		    		// aggiorno l'istante temporale del percorso
					p=aggiornaIstantiTemporali(p,t);
					//inserisco i vertici dopo v
					if(v.equals(goal) == false && res != null) {
		    		for (int i = 1; i < p.getPercorso().size(); i++) {
						res.add(p.getPercorso().get(i));
					}
					Percorso l= new Percorso(res,init,goal);
					// ho creato un percorso il cui istante temporale > max
					if (l.getPercorso().get(l.getPercorso().size()-1).getIstante_temporale()>max) {
						System.err.println("Percorso Rilassato - Errore superato l'orizzonte temporale max!");
						return null;
					}
		    		return new ReachGoal(res, P.size(), closed.size(), res.size()-1, l.getPeso(), wait);
					}
		    	}
		    	
			}
	    	// --- FINE ALTERNATIVA ------------
			// parte 3
			
			if (t<max) {
				for (Vertice n : v.getListaAdiacenza().keySet()) {
					if(!closed.contains(new Stato(n,t+1))) {
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
							if (!open.contains(new Stato(n,t+1))) {
								open.add(new Stato(n,t+1));
							}
						}

					}
				}
			}
		}
		System.err.println("ReachGoalAlternativa: impossibile generare il percorso ");
		return null;
	}
    
    public Percorso aggiornaIstantiTemporali(Percorso p, int t) {
    	p.getPercorso().forEach((u) -> u.setIstante_temporale(u.getIstante_temporale()+t));
		return p;
    }
    
    //  algoritmo percorso rilassato (Djkstra)
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
			
			// variabile per specificare se il percorso che creo ï¿½ valido
			boolean valido=true;
			
			// continuo ad inserire finchï¿½ diverso da goal
			
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

    // algoritmo per il controllo dei conflitti cammini preesistenti, restituisce true se trova un conflitto
    
    private boolean isConflitto(Percorso p, List<Percorso> agenti,int t) {
	for (int i = 0; i < p.getPercorso().size(); i++) {
		for (Percorso a:agenti) {
			// controllo di non passare in uno stato finale di un agente in un istante successivo
			if ((p.getPercorso().get(i).getVertice().equals(a.getPercorso().get(a.getPercorso().size()-1).getVertice())) &&
					(((p.getPercorso().get(i).getIstante_temporale())+t) >= a.getPercorso().get(a.getPercorso().size()-1).getIstante_temporale()))
			return true;
						
			if(((p.getPercorso().get(i).getIstante_temporale())+t) < a.getPercorso().get(a.getPercorso().size()-1).getIstante_temporale()) {
				// stato già presente in un percorso, potrebbe essere anche l'init
				if (p.getPercorso().get(i).getVertice().equals(a.getPercorso().get(i + t).getVertice()))
					return true;
				//scambio di posizione SCONTRO
				if (i < p.getPercorso().size() - 1) {
					if ((p.getPercorso().get(i + 1).getVertice().equals(a.getPercorso().get(i + t).getVertice()))
							&& (p.getPercorso().get(i).getVertice().equals(a.getPercorso().get(i + 1 + t).getVertice())))
						return true;
				}
			}
		}
		
}

	
	return false;
}
    
    public Vertice[] generaInitGoal(int max) {
	   
	   // indici init
       int i;
       //indici goal
       int g;
       do {
       
    	  // INIT - controllo che non sia un ostacolo
    	Random random = new Random();
    	i = random.nextInt(listaVerticiValidi.size());
       // GOAL - controllo che non sia un ostacolo       
    	g = random.nextInt(listaVerticiValidi.size());
       }while(listaVerticiValidi.get(i).equals(listaVerticiValidi.get(g)));
       
       Vertice[] result = new Vertice[2];
       result[0] = listaVerticiValidi.get(i);
       result[1] = listaVerticiValidi.get(g);
       return result;
   }
   
   public List<Percorso> generatoreIstanze(int numero_agenti,int max){
    	int maxAss=(dimensioni.getRighe()*dimensioni.getColonne()) -numero_ostacoli;
    	int istanti_max = 0;
    	int i=0;
		int j=1;

		
    	while(i<numero_agenti && max< (maxAss + istanti_max)) {
    		for (Percorso p : percorsi) {
    			if(istanti_max<p.getPercorso().size())
    				istanti_max=p.getPercorso().size();
    		}
    		
    		if(j>10){
				System.err.println("Numero massimo iterazioni per ricerca agenti raggiunto. Non è possibile generare "+ numero_agenti + " agenti, sono stati generati solo "+i+" agenti");
				break;
			} else{
			Vertice init,goal;
    		
    		Vertice[] res=generaInitGoal(max);
    		init=res[0];
    		goal=res[1];
    		
    		ReachGoal t=null;
    		if(j<=10) {
    			t=ReachGoal(this, percorsi, init, goal, max);
				if(t==null || t.getPercorso()==null){
					j++;
				}
    		}
    		
    		if(t!= null && t.getPercorso()!=null && !t.getPercorso().contains(null)) {
    			System.out.println("Trovato il percorso dell'agente "+i + " con un numero "+ j + " di invocazioni di ReachGoal");
				percorsi.add(new Percorso(t.getPercorso(),init,goal));
				reachGoals.add(t);
    			i++;
				j=1;
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

