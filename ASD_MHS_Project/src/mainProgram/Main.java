package mainProgram;
import java.io.File;
import ioUtils.*;

public class Main {
	
	private final static String EXTENSION = "matrix";
	
	public static void main(String[] args) {
		readInputData();	
	}
	
	private static void readInputData() {
		File f;
		do {
			f = UserInput.chooseInputFile();
		} while(!UserInput.check_extension(f.getName(), EXTENSION));	
		Reader r = new Reader(f.getPath());
		r.read();
	}

}
