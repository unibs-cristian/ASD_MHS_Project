package ioUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import objects.Instance;

public class Reader {
	private final static String COMMENT_DELIMITER = ";;;";
	private final static String ROW_DELIMITER = "-";
	private final static String SEPARATOR = " ";
	
	private String path;
	private FileReader fr;
	private BufferedReader br;
	
	public Reader(String path){
		this.path = path;
	}
	
	public void read() {
		try {
			fr = new FileReader(path);
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		}
		br = new BufferedReader(fr);

		String sCurrentLine;
		Instance instance;
		int numRows = 0;

		try {
			while ((sCurrentLine = br.readLine()) != null) {
				if(!sCurrentLine.startsWith(COMMENT_DELIMITER)) {
					sCurrentLine = cleanString(sCurrentLine);
					break;
				}
			}
			instance = new Instance(sCurrentLine.length());
			
			do {
				numRows ++;
				sCurrentLine = cleanString(sCurrentLine);
				for(int i=0; i<sCurrentLine.length(); i++) {
					if(sCurrentLine.charAt(i) == '1')
						instance.setUsefulColumn(i);
				}
			} while((sCurrentLine = br.readLine()) != null);		

			instance.createMatrix(numRows);
			try {
				fr = new FileReader(path);
			} catch (FileNotFoundException e) {
				System.out.println("File not found.");
			}
			br = new BufferedReader(fr);
					
			int i = 0;
			
			while ((sCurrentLine = br.readLine()) != null) {
				if(!sCurrentLine.startsWith(COMMENT_DELIMITER)) {
					sCurrentLine = cleanString(sCurrentLine);
					int j = 0, k = 0;
					
					while(j<instance.getNumUsefulColumns() && k<sCurrentLine.length()) {												
						if(instance.isUseful(k)) {							
							if(sCurrentLine.charAt(k) == '1') {															
								instance.setElement(i, j);								
							}							
							j++;
						}						
						k++;
					}					
					i++;
				}				
			}
			instance.printMatrix();
			
			if (br != null)
				br.close();

			if (fr != null)
				fr.close();	
			
		} catch (IOException e) {
			System.out.println("I/O error");
		}		
		
	}
	
	private String cleanString(String str) {
		str = str.replace(SEPARATOR, "");		
		return str.replace(ROW_DELIMITER, "");
	}
	
	
}
