package ioUtils;

import javax.swing.JFileChooser;

import java.io.File;       
import java.util.Scanner;

public class UserInput {
	
	private final static String MSG_FILE_SELECTED = "File selezionato: ";
	private final static String MSG_FILE_NOT_SELECTED = "Nessun file selezionato.";
	private final static String MSG_DIR_SELECTED = "Cartella selezionato: ";
	private final static String MSG_DIR_NOT_SELECTED = "Nessuna cartella selezionato.";
	private final static String POSITIVE_ANSWER="S";
	private final static String NEGATIVE_ANSWER="N";
	private final static String MSG_ILLEGAL_VALUE = "Errore! I valori ammissibili sono " + "'" +  POSITIVE_ANSWER + " e '" + NEGATIVE_ANSWER + "'";
	private static final String ERROR_INT = "Errore, il carattere inserito non è un intero.";
	private static final String ERROR_RANGE = "Errore, il numero inserito non appartiene al range richiesto.";
	private static Scanner reader = createScanner();
	 
 	/**
 	 * Crea scanner.
 	 *
 	 * @return the scanner
 	 */
	 private static Scanner createScanner ()
	  {
	   Scanner created = new Scanner(System.in);
	   created.useDelimiter(System.getProperty("line.separator"));
	   return created;
	  }
	
	 /**
 	 * Leggi string.
 	 *
 	 * @param message the messaggio
 	 * @return the string
 	 */
	public static String readString(String message)
	{
		System.out.print(message);
		return reader.next();
	}
	
	public static File chooseInputFile(boolean isDir) {
		JFileChooser fileChooser = new JFileChooser();
		int result;
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		
		if(isDir)
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//do {
		result = fileChooser.showOpenDialog(null);
		//} while(result != JFileChooser.APPROVE_OPTION);
		if(result == JFileChooser.APPROVE_OPTION)
			if(isDir)
				System.out.println(MSG_DIR_SELECTED + fileChooser.getSelectedFile().getName());
			else
				System.out.println(MSG_FILE_SELECTED + fileChooser.getSelectedFile().getName());
		else
			if(isDir)
				System.out.println(MSG_DIR_NOT_SELECTED);
			else
				System.out.println(MSG_FILE_NOT_SELECTED);
		return fileChooser.getSelectedFile();
	}
	
	public static boolean check_extension(String fileName, String extension) {
		if(fileName.matches(".*" + extension))
			return true;
		else 
			return false;
	}
	
	public static boolean yesOrNo(String message)
	{
		  String myMessage = message + "(" + POSITIVE_ANSWER + "/" + NEGATIVE_ANSWER + ")";
		  String inputData = "";
		  boolean legalValue = false;
		  do {
			  inputData = readString(myMessage);
			  if(inputData.equalsIgnoreCase(POSITIVE_ANSWER)) 
				  legalValue = true;
			  else if(inputData.equalsIgnoreCase(NEGATIVE_ANSWER))
				  return false;
			  else
				  System.out.println(MSG_ILLEGAL_VALUE);
		  } while(legalValue==false);
		  return legalValue;
	}
	
	public static int leggiInt(String message)
	{		
		boolean end = false;
		int number = 0;		
		while(!end)
		{
			System.out.print(message);

			if(reader.hasNextInt())
			{
				number = reader.nextInt();
				end = true;
			}
			else
			{
				System.out.println(ERROR_INT);
				@SuppressWarnings("unused")
				String daButtare = reader.next();
			}
		}
		return number;
	}
	
	public static int readInt(String message, int min, int max)
	{		
		boolean end = false;
		int number = 0;		
		while(!end)
		{
			System.out.print(message);

			if(reader.hasNextInt())
			{
				number = reader.nextInt();
				if(number>= min && number <= max)
					end = true;
				else
					System.out.println(ERROR_RANGE);
			}
			else
			{
				System.out.println(ERROR_INT);
				@SuppressWarnings("unused")
				String daButtare = reader.next();
			}
		}
		return number;
	}	
}
