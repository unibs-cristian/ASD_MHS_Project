package mainProgram;
import ioUtils.*;

public class Main {

	public static void main(String[] args) {
		readInputData();	
	}
	
	private static void readInputData() {
		String fileName = "74L85.000.matrix";
		Reader r = new Reader(".", fileName);
		r.read();
	}

}
