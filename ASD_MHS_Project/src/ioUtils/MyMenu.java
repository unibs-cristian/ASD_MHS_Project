package ioUtils;

public class MyMenu {
	  final private static String BORDER = "-";
	  final private static String EXIT_ITEM = "0\tEsci";
	  final private static String MSG_REQUEST_INPUT = "Inserisci il numero dell'opzione desiderata > ";
	
	  private String title;
	  private String [] items;
	
	
	  public MyMenu (String title, String [] items)
	  {
		  	this.title = title;
		  	this.items = items;
	  }
	
	  public int choose ()
	  { 
			printMenu();
			return UserInput.readInt(MSG_REQUEST_INPUT, 0, items.length);	 
	  }
		
	  public void printMenu ()
	  {
			System.out.println(stringBorder(BORDER,title.length()));
			System.out.println(title);
			System.out.println(stringBorder(BORDER,title.length()));
		    for (int i=0; i<items.length; i++)
		    {
		    	System.out.println( (i+1) + "\t" + items[i]);
		    }
		    System.out.println();
			System.out.println(EXIT_ITEM);
		    System.out.println();
	  }
	  
	  public String stringBorder(String character,int stringLength)
	  {
			StringBuffer result = new StringBuffer();
			for(int i = 0; i < stringLength; i++)
			{
				result.append(character);
			}
			return result.toString();
	  }
	  
	  
	  
	  //stampamenu ma senza lo zero per l'uscita
	  public void printMenuNZ()
	  {
		  	System.out.println(stringBorder(BORDER,title.length()));
			System.out.println(title);
			System.out.println(stringBorder(BORDER,title.length()));
		    for (int i=0; i<items.length; i++)
		    {
		    	System.out.println( (i+1) + "\t" + items[i]);
		    }
		    System.out.println();
	  }
	  
	  public int chooseNZ()
	  {
		  printMenuNZ();
		  return UserInput.readInt(MSG_REQUEST_INPUT,1,items.length);
	  }
}
