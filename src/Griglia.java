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
	
	// Verifica se una cella è valida (all'interno dei limiti della griglia)
    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < dimensioni.getRighe() && col >= 0 && col < dimensioni.getColonne();
    }
    
    // Posiziona agglomerato di ostacoli nelle celle vicine
    private void placeObstacleCluster(int row, int col) {
    	Random random = new Random();
        int clusterSize = (int) (Math.ceil(0.02*dimensioni.getRighe()*dimensioni.getColonne()) + random.nextInt((int) Math.ceil(0.04*dimensioni.getRighe()*dimensioni.getColonne()))); // Dimensione del cluster casuale (da 2 a 5)

        // la dimesnione del cluster non può sforarare il numero di celle attraversabili
        if (clusterSize+numero_ostacoli>numero_ostacoli_MAX)
        	clusterSize=numero_ostacoli_MAX-numero_ostacoli;
         
        for (int i = 0; i < clusterSize; i++) {
            int xOffset = random.nextInt(3) - 1; // Valori casuali tra -1, 0 e 1
            int yOffset = random.nextInt(3) - 1;

            int newRow = row + yOffset;
            int newCol = col + xOffset;

            // Verifica se la nuova cella è valida e vuota
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

                // Verifica se la cella deve contenere un ostacolo in base alla densità specificata
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
    		// mettiamo il padre di goal che è all'ultima posizione
    		res.add(P.get(closed.get(closed.size()-1)));
    	}
    	// ripeto fino a che non sono in init con t=0
    	while(!res.get(res.size()-1).equals(new Stato(init, 0))) {
    		//aggiungo il padre dell'ultimo elemento di res
    		res.add(P.get(res.get(res.size()-1)));
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
    	
    	   	
		// controllo che i percorsi presistenti degli n agenti partano tutti da un vertice diverso e non uguale a init
    	for (Percorso a : agenti) {
			if(a.getPercorso().contains(new Stato(init,0))) {
					System.err.println("Posizione iniziale agenti uguale a init");
					return null;
			}
    	}
    	
		open.add(new Stato(init,0));
		
		/*
		for (int t = 0; t <= max; t++) {
			for (Vertice v : G.verticiG()) {
				g.put(new Stato(v,t), Double.POSITIVE_INFINITY);
				P.put(new Stato(v,t), null);
			}
		}*/
		
		// g è il costo per raggiungere il vertice (parametro 1) specificato all'istante parametro 2, con costo parametro (3)
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
							
							// collisione con un agente preesistente fermo nella sua cella finale
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
		
		return null;
	}
    
   private Vertice[] generaInitGoal(List <Percorso> percorsi) {
	   
	   // indici init
       int i;
       //indici goal
       int g;
       do {
       
    	   // INIT - controllo che non sia un ostacolo
    	   Random random = new Random();
    	   i = random.nextInt(listaVerticiValidi.size()-1);
    	  
       // GOAL - controllo che non sia un ostacolo
       
       boolean valido;
       do {
    	   valido=true;
    	   g = random.nextInt(listaVerticiValidi.size()-1);
    	   
    	// se genero un goal che è all'interno delle celle visitate da un altro percorso, non è valido
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

    	while(i<numero_agenti) {
    		Vertice init,goal;
    		
    		Vertice[] res=generaInitGoal(percorsi);
    		init=res[0];
    		goal=res[1];
    		
    		Percorso t;
    		if(i==0) 
    			t=new Percorso(ReachGoal(this, percorsi, init, goal, max),init,goal);
    		else {
    			
        		t=new Percorso(ReachGoal(this, percorsi, init, goal, max+istanti_max),init,goal);
    		}
    		
    		if(t.getPercorso()!=null) {
    			percorsi.add(t);
    			istanti_max= percorsi.get(i).getPercorso().size()-1;
    			i++;
    		}
    			
    	}
    	
    	return percorsi;
    }
	 
}
