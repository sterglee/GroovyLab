
package gExec.gLab;

import gExec.Interpreter.GlobalValues;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;

public class commandHistory {

        
   // save commands of a session to a file
     public static void saveCommandHistory(String fileName, Vector commandHistory)
    {
        // fileName = gExec.Interpreter.GlobalValues.workingDir+File.separator+fileName;
        try
        {    	
            //create streams
            FileOutputStream output = new FileOutputStream(fileName);
            
            //create object stream
           OutputStreamWriter  commandWriter= new OutputStreamWriter(output);
            
            int vecLen = commandHistory.size();
            int commandCnt=vecLen;
            if (GlobalValues.numOfHistoryCommandsToKeep < commandCnt)
                commandCnt = gExec.Interpreter.GlobalValues.numOfHistoryCommandsToKeep;
            int numHistCommands = commandHistory.size();
            for (int k=0; k<commandCnt; k++)  {  // write commands in LIFO order
                String currentCommand = (String)commandHistory.elementAt(numHistCommands-1-k)+"\n";
                commandWriter.write(currentCommand, 0, currentCommand.length());
            }
            commandWriter.close();
            output.close();
          
        }
            
        catch(java.io.IOException except)
        {
            System.out.println("IO exception in saveCommandHistory");
            System.out.println(except.getMessage());
            //except.printStackTrace();
        }
   }
   
   //  load commands of a session from a file
     public static void loadCommandHistory(String fileName, Vector commandHistory)
    {
        // fileName = GlobalValues.workingDir+File.separator+fileName;
            //create streams
            try {
            FileInputStream input = new FileInputStream(fileName);
            
            //create object stream
           BufferedReader  commandReader= new BufferedReader(new InputStreamReader(input));
           commandHistory.clear(); 
           String currentLine;
           while ((currentLine = commandReader.readLine()) != null) 
               commandHistory.add(currentLine);
               
            commandReader.close();
            input.close();
            if (gExec.gLab.gLab.historyPanel != null)
                if (gExec.gLab.gLab.uiMainFrame != null)
                   gExec.gLab.gLab.uiMainFrame.updateHistoryWindow();
        }
        catch (java.io.FileNotFoundException e) {
            System.out.println("File "+ fileName+" cannot be opened for reading command history list");
        }    
        catch(java.io.IOException except)
        {
            System.out.println("IO exception in readCommandHistory");
            System.out.println(except.getMessage());
            //except.printStackTrace();
        }
   }

}
