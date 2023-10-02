import java.util.Random;


public class Griglia {
	public static final int PRECEDENTE=-1; 
	public static final int SUCCESSIVA=1; 
	
	private Vertice[][] G;
	private Dimensioni dimensioni;
	
	public Griglia(Dimensioni dimensioni, int percentuale_celle_attraversabili, int fattore_agglomerazione_ostacoli) {
		if(percentuale_celle_attraversabili<0 || percentuale_celle_attraversabili>100 || fattore_agglomerazione_ostacoli<0 || fattore_agglomerazione_ostacoli>100)
			throw new IllegalArgumentException();
		else {
			this.dimensioni= new Dimensioni(dimensioni.getRighe(),dimensioni.getColonne());
			G=generatoreGriglia(dimensioni,percentuale_celle_attraversabili,fattore_agglomerazione_ostacoli);
		}
	}

	// inzializzo vertici
	private void inizializzaGriglia() {
		for (int i = 0; i < dimensioni.getRighe(); i++) {
			for (int j = 0; j < dimensioni.getColonne(); j++) {
				Vertice v =new Vertice(1, false);
				G[i][j]=v;
			}
		}
	}
	
	private boolean hasObstacleInAdjacentCells(int row, int col) {
        // Verifica le otto direzioni adiacenti (sopra, sotto, sinistra, destra e diagonali)
        int[][] directions = {
            {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            // Verifica se la nuova posizione è all'interno dei limiti della griglia
            if (newRow >= 0 && newRow < dimensioni.getRighe() && newCol >= 0 && newCol < dimensioni.getColonne()) {
                // Verifica se c'è un ostacolo nella cella adiacente
                if (G[newRow][newCol].isOstacolo()) {
                    return true; // Trovato un ostacolo nelle celle adiacenti
                }
            }
        }

        return false; // Nessun ostacolo nelle celle adiacenti
    }
	
	private Vertice[][] generatoreGriglia(Dimensioni dimensioni, int percentuale_celle_attraversabili, int fattore_agglomerazione_ostacoli) {
		
		G=new Vertice[dimensioni.getRighe()][dimensioni.getColonne()];
		
		// inizializzo vertici a 
		inizializzaGriglia();
		
		// numero di ostacoli che dobbiamo settare
		int numero_ostacoli= (int) ((dimensioni.getRighe()*dimensioni.getColonne())*((100-percentuale_celle_attraversabili)/100.0));
		Random random = new Random();
		
		while(numero_ostacoli>0) {
			
			//calcolo il primo valore dell'ostacolo
			int rY = random.nextInt(dimensioni.getColonne());
			int rX= random.nextInt(dimensioni.getRighe());
			
			
			// settiamo ostacolo e decremento
			if(G[rX][rY].isOstacolo()==false && !hasObstacleInAdjacentCells(rX,rY)) {
				G[rX][rY].setOstacolo(true);
				numero_ostacoli-=1;
				int ostacoli_agglomerati=fattore_agglomerazione_ostacoli-1;
				
				// per l'ultima iterazione se rimangono "fuori" degli ostacoli che non possono essere agglomerati nel fattore specifiato
				if(numero_ostacoli<ostacoli_agglomerati)
					ostacoli_agglomerati=numero_ostacoli;
				
				while(ostacoli_agglomerati>0) {
					// genero un numero da -1 a 1, dove -1 è la riga sopra e 1 la riga successiva; stesso per la colonna
					int oX = random.nextInt(SUCCESSIVA - PRECEDENTE + 1) + PRECEDENTE;
					int oY = random.nextInt(SUCCESSIVA - PRECEDENTE + 1) + PRECEDENTE;
					
					int x=rX+oX;
					int y=rY+oY;
					
					if((x>=0 && x<dimensioni.getRighe()) && (y>=0 && y<dimensioni.getColonne())) {
						if(G[x][y].isOstacolo()==false) {
							G[x][y].setOstacolo(true);
							numero_ostacoli-=1;
							ostacoli_agglomerati-=1;
						}		
					}
					
				}
			}
		}
		
		return G;
		
		
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
}
