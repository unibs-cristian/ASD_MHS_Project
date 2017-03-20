package ioUtils;

public class MyMenu {
	  final private static String BORDO = "-";
	  final private static String VOCE_USCITA = "0\tEsci";
	  final private static String RICHIESTA_INSERIMENTO = "Inserisci il numero dell'opzione desiderata > ";
	
	  private String titolo;
	  private String [] voci;
	
	
	  public MyMenu (String titolo, String [] voci)
	  {
		  	this.titolo = titolo;
		  	this.voci = voci;
	  }
	
	  public int scegli ()
	  { 
			stampaMenu();
			return UserInput.leggiInt(RICHIESTA_INSERIMENTO, 0, voci.length);	 
	  }
		
	  public void stampaMenu ()
	  {
			System.out.println(bordoStringa(BORDO,titolo.length()));
			System.out.println(titolo);
			System.out.println(bordoStringa(BORDO,titolo.length()));
		    for (int i=0; i<voci.length; i++)
		    {
		    	System.out.println( (i+1) + "\t" + voci[i]);
		    }
		    System.out.println();
			System.out.println(VOCE_USCITA);
		    System.out.println();
	  }
	  
	  public String bordoStringa(String carattere,int lunghezzaStringa)
	  {
			StringBuffer risultato = new StringBuffer();
			for(int i = 0; i < lunghezzaStringa; i++)
			{
				risultato.append(carattere);
			}
			return risultato.toString();
	  }
	  
	  
	  
	  //stampamenu ma senza lo zero per l'uscita
	  public void stampaMenuNZ()
	  {
		  	System.out.println(bordoStringa(BORDO,titolo.length()));
			System.out.println(titolo);
			System.out.println(bordoStringa(BORDO,titolo.length()));
		    for (int i=0; i<voci.length; i++)
		    {
		    	System.out.println( (i+1) + "\t" + voci[i]);
		    }
		    System.out.println();
	  }
	  
	  public int scegliNZ()
	  {
		  stampaMenuNZ();
		  return UserInput.leggiInt(RICHIESTA_INSERIMENTO,1,voci.length);
	  }
}
