package ioUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class IOFile {
	
	private final static String MSG_ERROR_FORMAT_FILE = "Errore, formato file di input non corretto.";
	/**
	 * Metodo che consente di scegliere il file di input.
	 * 
	 * @param extension : l'estensione che deve avere il file da selezionare
	 * @param isDir : booleano che ci dice se l'utente deve selezionare un file o una directory (per eseguirci tutti i file al suo interno)
	 * @return il percorso del file scelto
	 */
	public static String selectFile(String extension) {
		File f;
		// Lettura tramite File Chooser e controllo del formato del file scelto
		do {
			f = UserInput.chooseInputFile(false);
			if(f==null)
				return null;
			if(!UserInput.check_extension(f.getName(), extension))
				System.out.println(MSG_ERROR_FORMAT_FILE);
		} while(!UserInput.check_extension(f.getName(), extension));	
		return f.getPath();
	}
	
	public static String selectDir() {
		File f;
		f = UserInput.chooseInputFile(true);
		if(f==null)
			return null;
		return f.getPath();
	}
	
	/**
	 * Metodo per scrivere il file di output
	 * 
	 * @return void
	 */
	public static void writeOutputData(String output,String filename) {
		//File f;
		BufferedWriter bw = null;
		FileWriter fw = null;
		
		try {
			fw = new FileWriter(filename);
			bw = new BufferedWriter(fw);
			bw.write(output);
		} catch (IOException e) {
			//e.printStackTrace();
		} finally {
			try {
				if(bw!=null)
					bw.close();
				if(fw!=null)
					fw.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}
	
	public static void deleteFileInFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) //some JVMs return null for empty dirs
	        for(File f: files) 
	            f.delete();
	}
}
