package ioUtils;

import javax.swing.JFileChooser;

import java.io.File;       
import java.util.Scanner;

public class UserInput {
	
	private final static String MSG_ERRORE_FORMATO_FILE = "Errore, formato file di input non corretto.";
	private final static String MSG_FILE_SELEZIONATO = "File selezionato: ";
	private final static String MSG_FILE_NON_SELEZIONATO = "Esecuzione annullata.";
	private final static String RISPOSTA_SI="S";
	private final static String RISPOSTA_NO="N";
	private final static String MSG_AMMISSIBILI1 = "Errore! I valori ammissibili sono " + "'" +  RISPOSTA_SI + " e '" + RISPOSTA_NO + "'";
	private static final String ERRORE_INTERO = "Errore, il carattere inserito non è un intero.";
	private static final String ERRORE_RANGE = "Errore, il numero inserito non appartiene al range richiesto.";
	private static Scanner lettore = creaScanner();
	 
 	/**
 	 * Crea scanner.
 	 *
 	 * @return the scanner
 	 */
	 private static Scanner creaScanner ()
	  {
	   Scanner creato = new Scanner(System.in);
	   creato.useDelimiter(System.getProperty("line.separator"));
	   return creato;
	  }
	
	 /**
 	 * Leggi string.
 	 *
 	 * @param messaggio the messaggio
 	 * @return the string
 	 */
	public static String leggiString(String messaggio)
	{
		System.out.print(messaggio);
		return lettore.next();
	}
	
	public static File chooseInputFile() {
		JFileChooser fileChooser = new JFileChooser();
		int result;
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		//do {
			result = fileChooser.showOpenDialog(null);
		//} while(result != JFileChooser.APPROVE_OPTION);
		if(result == JFileChooser.APPROVE_OPTION)
			System.out.println(MSG_FILE_SELEZIONATO + fileChooser.getSelectedFile().getName());
		else
			System.out.println(MSG_FILE_NON_SELEZIONATO);
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
	
	public static boolean yesOrNo(String messaggio)
	{
		  String mioMessaggio = messaggio + "(" + RISPOSTA_SI + "/" + RISPOSTA_NO + ")";
		  String inputDati = "";
		  boolean valoreCorretto = false;
		  do {
			  inputDati = leggiString(mioMessaggio);
			  if(inputDati.equalsIgnoreCase(RISPOSTA_SI)) 
				  valoreCorretto = true;
			  else if(inputDati.equalsIgnoreCase(RISPOSTA_NO))
				  return false;
			  else
				  System.out.println(MSG_AMMISSIBILI1);
		  } while(valoreCorretto==false);
		  return valoreCorretto;
	}
	
	public static int leggiInt(String messaggio)
	{		
		boolean fine = false;
		int numero = 0;		
		while(!fine)
		{
			System.out.print(messaggio);

			if(lettore.hasNextInt())
			{
				numero = lettore.nextInt();
				fine = true;
			}
			else
			{
				System.out.println(ERRORE_INTERO);
				@SuppressWarnings("unused")
				String daButtare = lettore.next();
			}
		}
		return numero;
	}
	
	public static int leggiInt(String messaggio, int min, int max)
	{		
		boolean fine = false;
		int numero = 0;		
		while(!fine)
		{
			System.out.print(messaggio);

			if(lettore.hasNextInt())
			{
				numero = lettore.nextInt();
				if(numero>= min && numero <= max)
					fine = true;
				else
					System.out.println(ERRORE_RANGE);
			}
			else
			{
				System.out.println(ERRORE_INTERO);
				@SuppressWarnings("unused")
				String daButtare = lettore.next();
			}
		}
		return numero;
	}	
}
