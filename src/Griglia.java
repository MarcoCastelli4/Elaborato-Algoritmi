import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.lang.Math;

public class Griglia {
	private Vertice[][] G;
	private Dimensioni dimensioni;
	private int numero_ostacoli_MAX;
	private int numero_ostacoli;
	
	public Griglia(Dimensioni dimensioni, float percentuale_celle_attraversabili, float fattore_agglomerazione_ostacoli) {
		if(percentuale_celle_attraversabili<0.0 || percentuale_celle_attraversabili>1.0 || fattore_agglomerazione_ostacoli<0.0 || fattore_agglomerazione_ostacoli>1.0)
			throw new IllegalArgumentException();
		else {
			this.dimensioni= new Dimensioni(dimensioni.getRighe(),dimensioni.getColonne());
			this.numero_ostacoli_MAX=(int) (dimensioni.getRighe()*dimensioni.getColonne()*(1.0-percentuale_celle_attraversabili));
			G=generatoreGriglia(dimensioni,percentuale_celle_attraversabili,fattore_agglomerazione_ostacoli);
			this.numero_ostacoli=0;
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
        // Verifica le otto direzioni adiacenti (sopra, sotto, sinistra, destra e diagonali)
        int[][] directions = {
            {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}
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
		for (int i = 0; i < dimensioni.getRighe(); i++) {
			for (int j = 0; j < dimensioni.getColonne(); j++) {
				if (G[i][j].isOstacolo())
					System.out.print("O | ");
				else System.out.print("  | ");
					
			}
			System.out.println();
		}
	}
	
	
	public void printMatriceW() {
		for (int i = 0; i < dimensioni.getRighe(); i++) {
			for (int j = 0; j < dimensioni.getColonne(); j++) {
				if (!G[i][j].isOstacolo())
					System.out.println("Vertice r: "+ i + ", c: " + j + "-->" + G[i][j].listaAdiacenza());
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
	public List<Vertice> ReachGoal(Griglia G, List<Vertice>[] percorsi_presistenti, Vertice init,Vertice goal, int max){
		// Liste di stati
		List<Stato> open = new ArrayList<>();
		List<Stato> closed=new ArrayList<>();
		
		// Strutture dati
		Map<Stato, Double> g= new HashMap<>();
		
		// Stato, Stato padre
		Map<Stato, Stato> P= new HashMap<>();
		
		
		open.add(new Stato(0,init));
		
		for (int t = 0; t < max; t++) {
			for (Vertice v : G.verticiG()) {
				g.put(new Stato(t,v), Double.POSITIVE_INFINITY);
				P.put(new Stato(t,v), null);
			}
		}
		g.put(new Stato(0,init), 0.0);
		
		// riga 11 TODO
		
	}
	
}
