package ioUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.BitSet;

import objects.Instance;

public class Reader {
	private final static String EXTENSION = "matrix";
	private final static String COMMENT_DELIMITER = ";;;";
	private final static String ROW_DELIMITER = "-";
	private final static String SEPARATOR = " ";
	
	private String path;
	private String fileName;
	private FileReader fr, fr2;
	private BufferedReader br, br2;
	
	public Reader(String path, String fileName){
		this.fileName = fileName;
		this.path = path;
	}
	
	public void read() {
		try {
			fr = new FileReader(fileName);
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

			if (br != null)
				br.close();

			if (fr != null)
				fr.close();

			instance.createMatrix(numRows);
			try {
				fr2 = new FileReader(fileName);
			} catch (FileNotFoundException e) {
				System.out.println("File not found.");
			}
			br2 = new BufferedReader(fr2);
					
			int i = 0;
			
			while ((sCurrentLine = br2.readLine()) != null) {
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
		} catch (IOException e) {
			System.out.println("I/O error");
		}
		
	}
	
	private String cleanString(String str) {
		str = str.replace(SEPARATOR, "");		
		return str.replace(ROW_DELIMITER, "");
	}
	
	
}
