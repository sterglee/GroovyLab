package gLabEdit;

// this class imports the contents of the DefaultImports.txt file

import gExec.Interpreter.GlobalValues;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DefaultImports {
    static boolean defaultImportsInited = false;
    
    static  void scanDefaultImports( String fileName )   {
        
         // Location of file to read
        File file = new File(fileName);
        StringBuilder sb = new StringBuilder();
        
        try {

            Scanner scanner = new Scanner(file);
            
            while (scanner.hasNextLine()) {   // read and process each line from the defaultImports file
                String line = scanner.nextLine();
                sb.append(line+"\n");
             }
             scanner.close();
            }
           
            
         catch (FileNotFoundException e) {
            e.printStackTrace();
        }         
         GlobalValues.bufferingImports += "\n"+ sb.toString();
    
         defaultImportsInited = true;
    
    }
    
}
