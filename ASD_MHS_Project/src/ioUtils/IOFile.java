package ioUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class IOFile {
	
	/**
	 * Metodo che consente di scegliere il file di input.
	 * 
	 * @return il percorso del file scelto
	 */
	public static String selectFile(String extension) {
		File f;
		// Lettura tramite File Chooser e controllo del formato del file scelto
		do {
			f = UserInput.chooseInputFile();
			if(f==null)
				return null;
		} while(!UserInput.check_extension(f.getName(), extension));	
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
			e.printStackTrace();
		} finally {
			try {
				if(bw!=null)
					bw.close();
				if(fw!=null)
					fw.close();
			} catch (IOException e) {
				e.printStackTrace();
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
