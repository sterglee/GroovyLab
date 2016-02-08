package gLabEdit;


import gExec.Interpreter.GlobalValues;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.StringTokenizer;
import javax.swing.*;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

// this class defines the functionality to replace the abbreviations defined in the "Abbreviations.txt" file     
// with their long name with the F11 keystroke

public class Abbreviations {
    
    static boolean  abbreviationsMapInited = false;   // prevent reading abbreviations file multiple times
    
  // the abbreviations are kept with a Java Map 
    static Hashtable <String, String>   abbreviationsMap = new Hashtable<String, String> ();
    
    /*  build our abbreviations map from the contents of the file fileName.
      The abbreviations file is comma separated:
 we list first the abbreviation, then a comma (i.e. ",") and then the replacement, e.g.
      su,subplot(
 */
    static  Hashtable  <String, String>  buildAbbreviationsMap( String fileName )   {
        
         // Location of file to read
        File file = new File(fileName);
        StringBuilder sb = new StringBuilder();
        Hashtable <String, String> abbrTable =  new Hashtable<String, String>();
        
        try {

            Scanner scanner = new Scanner(file);
            
            while (scanner.hasNextLine()) {   // read and process each line from the abbreviations file
                String line = scanner.nextLine();
                // the format is:  <abbreviation> "," <replacement> \n
                StringTokenizer stk = new StringTokenizer(line, ",");  
                if (stk.countTokens() == 2) {  // a valid line
                String abbr  = stk.nextToken();   // the abbreviation
                String replacement = stk.nextToken(); // the replacement
                abbrTable.put(abbr, replacement);  // add the entry to the abbreviations table
              }
            }
            scanner.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }         
        
    return  abbrTable;   // the abbreviation's table
    }
    
    // initialize the map of the abbreviations if it is not already 
public static boolean initAbbreviationsMap()   {
         // build the abbreviations map at the first time
      if (abbreviationsMapInited == false) {
         abbreviationsMap =  buildAbbreviationsMap(gExec.Interpreter.GlobalValues.workingDir+java.io.File.separatorChar+"Abbreviations.txt");
         //println("Abbreviations map = "+abbreviationsMap.toString())
         abbreviationsMapInited = true;
       }
        return abbreviationsMapInited;    // should be true for successful init
      }

      
  public static  void  displayAbbreviations()   {
     initAbbreviationsMap();   // init the abbreviations map if not already inited
      
     JFrame   abbrFrame = new JFrame("Abbreviations -  F11 to replace ");

     StringBuilder  abbrevsAll = new StringBuilder();
     Enumeration<String> abbrevs = abbreviationsMap.keys();  // the abbreviations
     while (abbrevs.hasMoreElements()) {
         String currentAbbrev = abbrevs.nextElement();
         String  replacement = abbreviationsMap.get(currentAbbrev);
         abbrevsAll.append(currentAbbrev +" -> "+replacement+"\n");   
     }
     
     RSyntaxTextArea  jt = new RSyntaxTextArea();
     
      jt.setFont(new Font(GlobalValues.paneFontName, Font.PLAIN, GlobalValues.paneFontSize));
      jt.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
      jt.setCodeFoldingEnabled(true);
      jt.setText(GlobalValues.bufferingCode);
    
      jt.setText(abbrevsAll.toString());
     
     
     RTextScrollPane  jscr = new RTextScrollPane(jt);
     abbrFrame.add(jscr);
     abbrFrame.pack();
     abbrFrame.setVisible(true);
    }
  
  
// detects and returns the word at the current caret location
  public static  String  detectAndReplaceWordAtCaret()    {
     int caretPosition = gExec.Interpreter.GlobalValues.globalEditorPane.getCaretPosition()-1;
  
    String  txt = gExec.Interpreter.GlobalValues.globalEditorPane.getText();  // the whole editor's text
    
    boolean  exited = false;
    String  wb = "";
    int  offset = caretPosition;
    while (offset >= 0 && exited==false) {
         char  ch = txt.charAt(offset);
         boolean isalphaNumeric = ( ch >= 'a' && ch <='z')  || (ch >= 'A' && ch <='Z') || (ch >= '0' && ch <='9') || (ch=='_');
         if (!isalphaNumeric)  exited=true;
          else {
           wb = wb + ch;
           offset--;
             }
          }
          
    gExec.Interpreter.GlobalValues.globalEditorPane.setSelectionStart(offset+1);
          
       String  wa = "";
       int  docLen = txt.length();
       offset = caretPosition+1;
       exited = false;
       while (offset < docLen && exited==false) {
         char  ch = txt.charAt(offset);
         boolean  isalphaNumeric = ( ch >= 'a' && ch <='z')  || (ch >= 'A' && ch <='Z') || (ch >= '0' && ch <='9') || (ch=='_'); 
         if (!isalphaNumeric)  exited = true;
           else {
         wa = wa + ch;
         offset++;
           }
         }
       // reverse wb
        int length = wb.length();
        String reversewb="";
      for ( int i = length - 1 ; i >= 0 ; i-- )
         reversewb = reversewb + wb.charAt(i);
 
        // build the whole word at caret position
         String  wordAtCursor = reversewb+wa;         

    gExec.Interpreter.GlobalValues.globalEditorPane.setSelectionEnd(offset);
    
    initAbbreviationsMap();  // if not already inited, init the abbreviations map
  
    wordAtCursor = wordAtCursor.trim();
    
    if (abbreviationsMap.containsKey(wordAtCursor))  {
        String replacingText =  abbreviationsMap.get(wordAtCursor);
    
        gExec.Interpreter.GlobalValues.globalEditorPane.replaceSelection( replacingText);
    }
  
    return wordAtCursor;
   }
}
