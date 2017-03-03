package mainProgram;
import java.io.File;
import java.util.BitSet;

import objects.Instance;
import ioUtils.*;

/**
 * Classe contenente il metodo Main e i metodi corrispondenti ai vari moduli che costituiscono l'elaborato
 *
 */
public class Main {
	
	private final static String EXTENSION = "matrix";
	
	/**
	 * Main program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Lettura file di input
		Instance in = readInputData();
		// Modulo per il calcolo monolitico dei MHS
		calcoloMonoliticoMhs(in);
	}
	
	/**
	 * Modulo per la lettura del file di input
	 * 
	 * @return l'oggetto Instance che rappresenta i dati in input
	 */
	private static Instance readInputData() {
		File f;
		// Lettura tramite File Chooser e controllo del formato del file scelto
		do {
			f = UserInput.chooseInputFile();
		} while(!UserInput.check_extension(f.getName(), EXTENSION));	
		Reader r = new Reader(f.getPath());
		return r.read();
	}
	
	/**
	 * Modulo per effettuare il calcolo monolitico dei MHS sui dati in ingresso 
	 * 
	 * @param in : l'oggetto Instance che rappresenta i dati in input
	 */
	private static void calcoloMonoliticoMhs(Instance in) {
		
	}

}
