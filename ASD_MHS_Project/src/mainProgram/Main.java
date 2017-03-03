package mainProgram;
import java.util.BitSet;

import ioUtils.*;

public class Main {
	
	private final static String MSG_NOME_FILE = "Inserire il nome del file (.matrix) contenente i dati di ingresso: \n";
	private final static String EXTENSION = "matrix";
	
	public static void main(String[] args) {
		readInputData();	
	}
	
	private static void readInputData() {
		String fileName;
		do {
			fileName = UserInput.leggiStringPiena(MSG_NOME_FILE);
		} while(!UserInput.check_extension(fileName, EXTENSION));
		//String fileName = "74L85.000.matrix";
		Reader r = new Reader(".", fileName);
		r.read();
	}

}
