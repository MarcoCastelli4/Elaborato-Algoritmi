import java.util.Random;
import java.lang.Math;

public class Griglia {
	private Vertice[][] G;
	private Dimensioni dimensioni;
	
	
	public Griglia(Dimensioni dimensioni, float percentuale_celle_attraversabili, float fattore_agglomerazione_ostacoli) {
		if(percentuale_celle_attraversabili<0.0 || percentuale_celle_attraversabili>1.0 || fattore_agglomerazione_ostacoli<0.0 || fattore_agglomerazione_ostacoli>1.0)
			throw new IllegalArgumentException();
		else {
			this.dimensioni= new Dimensioni(dimensioni.getRighe(),dimensioni.getColonne());
			G=generatoreGriglia(dimensioni,percentuale_celle_attraversabili,fattore_agglomerazione_ostacoli);
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
        int clusterSize = 2 + random.nextInt(4); // Dimensione del cluster casuale (da 2 a 5)

        for (int i = 0; i < clusterSize; i++) {
            int xOffset = random.nextInt(3) - 1; // Valori casuali tra -1, 0 e 1
            int yOffset = random.nextInt(3) - 1;

            int newRow = row + yOffset;
            int newCol = col + xOffset;

            // Verifica se la nuova cella è valida e vuota
            if (isValidCell(newRow, newCol) && G[newRow][newCol].isOstacolo()==false) {
                G[newRow][newCol].setOstacolo(true);
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
		
		for (int row = 0; row < dimensioni.getRighe(); row++) {
            for (int col = 0; col < dimensioni.getColonne(); col++) {
                // Genera un valore casuale tra 0 e 1
                double randomValue = random.nextDouble();

                // Verifica se la cella deve contenere un ostacolo in base alla densità specificata
                boolean hasObstacle = randomValue <= percentuale_ostacoli;

                if (hasObstacle) {
                    // Posiziona l'ostacolo
                    G[row][col].setOstacolo(true);

                    // Agglomerazione degli ostacoli
                    if (randomValue <= percentuale_ostacoli * fattore_agglomerazione_ostacoli) {
                        // Posiziona altri ostacoli nelle celle vicine
                        placeObstacleCluster(row, col);
                    }
                }
            }
        }
		return G;	
	}

	// aggiorno i pesi della matrice
	private void matriceW() {
		for (int i = 0; i < dimensioni.getRighe(); i++) {
			for (int j = 0; j < dimensioni.getColonne(); j++) {
				if (!G[i][j].isOstacolo()) {
					// aggiorno lista di adiacenza del vertice
					setListaAdiacenzaVertice(i,j);
				}
			}
		}
	}
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
	
	/*
	public void printW() {
		for (int i = 0; i < dimensioni.getRighe(); i++) {
			for (int j = 0; j < dimensioni.getColonne(); j++) {
				if (!G[i][j].isOstacolo())
					System.out.println("Vertice "+ i + j + "-->" + G[i][j].printListaAdiacenza());
					
			}
			
		}
	}*/
	
	public Vertice[][] getG(){
		return G;
	}
	
	
}
