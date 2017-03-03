package ioUtils;

import javax.swing.JFileChooser;
import java.io.File;       

public class UserInput {
	
	private final static String MSG_ERRORE_FORMATO_FILE = "Errore, formato file di input non corretto\n";
	private final static String MSG_FILE_SELEZIONATO = "File selezionato: ";

	public static File chooseInputFile() {
		JFileChooser fileChooser = new JFileChooser();
		int result;
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		do {
			result = fileChooser.showOpenDialog(null);
			System.out.println(MSG_FILE_SELEZIONATO + fileChooser.getSelectedFile().getName());
		} while(result != JFileChooser.APPROVE_OPTION);
		
		return fileChooser.getSelectedFile();
	}
	
	public static boolean check_extension(String fileName, String extension) {
		if(fileName.matches(".*" + extension))
			return true;
		else {
			System.out.println(MSG_ERRORE_FORMATO_FILE);
			return false;
		}
	}
}
