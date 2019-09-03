package gLabEdit;

import gExec.Interpreter.GlobalValues;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

// this action inspects a completion list for the type of an identifier, e.g.   jf = new javax.swing.JFrame(), jf is the identifier
// and the completion list is formed by discovering the contents of the javax.swing.JFrame class using Java reflection
class RSyntaxGroovyCompletionAction {

    
static   int numCompletionItems = 100; // initial size for the number of items retrieved for completion
static   Vector  scanMethods = new Vector(numCompletionItems);
static   String []   listOfAllMethods;

static public String methodSubString="";

    public void performCompletion() {

         RSyntaxTextArea  editor = gExec.Interpreter.GlobalValues.globalEditorPane;   // get RSyntaxArea based editor instance
         
         int  pos = editor.getCaretPosition()-1;      // position of the caret
         Document  doc = editor.getDocument();  // the document being edited
       
         GlobalValues.methodNameSpecified = false;
         GlobalValues.selectionStart = -1;
         
       boolean  exited = false;
        // take word part before cursor position
       String  wb = "";
       int  offset = pos;
       while (offset >= 0 && exited==false) {
         char  ch = 0;
            try {
                ch = doc.getText(offset, 1).charAt(0);
                if (ch == '.') {  // the user specified a method name
                    GlobalValues.methodNameSpecified = true;
                    GlobalValues.selectionStart = offset+1;
                }
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
         boolean  isalphaNumeric = ( ch >= 'a' && ch <='z')  || (ch >= 'A' && ch <='Z') || (ch >= '0' && ch <='9') || ch=='.' ||  ch=='_' ; 
         if (!isalphaNumeric)  exited = true;
          else {
           wb = wb + ch;
           offset--;
            }
          }
         
       if (GlobalValues.selectionStart == -1)  // a method name is not specified, thus set selection start to the beginning of the word
      GlobalValues.selectionStart = pos+1;
    
          // take word part after cursor position
       String  wa = "";
       int  docLen = doc.getLength();
       offset = pos+1;
       exited = false;
       while (offset < docLen && exited==false) {
         char  ch = 0;
            try {
                ch = doc.getText(offset, 1).charAt(0);
                if (ch == '.')  GlobalValues.methodNameSpecified = true;
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
         boolean  isalphaNumeric = ( ch >= 'a' && ch <='z')  || (ch >= 'A' && ch <='Z') || (ch >= '0' && ch <='9') || ch=='.' ||  ch=='_' ; 
         if (!isalphaNumeric)  exited = true;
           else {
         wa = wa + ch;
         offset++;
           }
         }
       GlobalValues.selectionEnd = offset;
  
      //   form total word that is under caret position
         // reverse string at the left of cursor
       StringBuffer sb = new StringBuffer(wb);
       StringBuffer rsb = new StringBuffer(wb);
       int p = 0;
       for (int k= wb.length()-1; k>=0; k--)
           rsb.setCharAt(p++, sb.charAt(k));
        
       // concatenate to form the whole word
       String  wordAtCursor = rsb.toString()+wa;

       wordAtCursor = wordAtCursor.trim();  // trim any spaces

       GlobalValues.textForCompletion = wordAtCursor; 
       
         String  className = wordAtCursor;
         
         if (wordAtCursor != null) {
             
         if (wordAtCursor.indexOf(".") > -1) {   // the user has typed a method after the period
           if (GlobalValues.performPackageCompletion == false) {
             String [] classNameSplitted = wordAtCursor.split("\\."); 
            className = classNameSplitted[0].trim();   // the class name
            if (classNameSplitted.length > 1)   // method has been specified
             groovySciCommands.Inspect.methodSubString = classNameSplitted[1];  // method name
           }
         }
          else
            groovySciCommands.Inspect.methodSubString = null;  // method not specified
          
         // get the object that the variable "className" refers to
          Object myVar = gExec.Interpreter.GlobalValues.GroovyShell.getVariable(className);
          
          collectInfoWithReflection(myVar);    // get completion info for our variable
            
          GCompletionProvider.installGroovyCompletion();
        
  
         }

     }
  
    
        public static void collectInfoWithReflection(Object object)
   {
      if (object !=null)  {
         Class cl = null;
         if (object instanceof Class)
             cl = (Class)object;
         else
             cl = object.getClass();
         String name = cl.getName();
         
         collectInfoWithReflection(name);
          }
        }

     
        
// collect information using Java reflection for the type with name: nameOfType
public static void collectInfoWithReflection(String nameOfType)
{
    GlobalValues.groovyResultsForCompletion.clear();
    scanMethods = new Vector(numCompletionItems);
     
    Package [] pkg = Package.getPackages();
    int pkgsNum = pkg.length;
    for (int pn = 0; pn<pkgsNum; pn++) {
        String pkgName = pkg[pn].getName();
        scanMethods.add(pkgName);
    }
    
         Class cl=null;
         try {
         cl = Class.forName(nameOfType);
         
    } catch (ClassNotFoundException ex) {
            System.out.println("Class not found for name: "+nameOfType);
            
        }
     
         // retrieve the methods of the class using Java reflection
    java.lang.reflect.Method []  classMethods = cl.getMethods();
for (int k=0; k<classMethods.length; k++) {   // all class methods
      Method currentMethod = classMethods[k];
      int  methodModifier = currentMethod.getModifiers();
      String methodName = currentMethod.getName();
      String longDescr = currentMethod.toString();
      String nameAndParamOnly = longDescr.substring(longDescr.indexOf(methodName), longDescr.length());
             // add only public methods
      if (Modifier.isPublic(methodModifier) ) {
          scanMethods.add(nameAndParamOnly);
              
      }  // isPublic
      
    }  // all class methods

 
 // retrieve the fields of the class using Java reflection
    java.lang.reflect.Field [] classFields  = cl.getFields();
    for (int k=0; k<classFields.length; k++) {  // all class fields
      Field  current = classFields[k];
      int fmodifiers = current.getModifiers();
         // add only public fields
      if (Modifier.isPublic(fmodifiers)) {
          String fieldName = current.getName();
           
          scanMethods.add(fieldName);
             
         }  // isPublic
        
      }   // all class fields

          // sort the vector of all the methods and fields
            // it is required since at the completion process we perform
            // binary search to detect all items beginning with a substring
       Collections.sort(scanMethods, new Comparator()  {
             public int compare(Object v1, Object v2) {
                 return ((String)v1).compareToIgnoreCase(((String)v2));
             }
        });
        
        // construct a view of the sorted Vector as an array of Strings
        int countMethods = scanMethods.size();
        listOfAllMethods = new String[countMethods];
        for (int k=0; k<countMethods; k++)
            listOfAllMethods[k] = (String) scanMethods.elementAt(k);

        //  filter the results if a substring after the dot is specified
        String [] filteredWithStringPattern =  listOfAllMethods;
            //  fix the array of Strings that start with methodSubString
        if (groovySciCommands.Inspect.methodSubString!=null)
               filteredWithStringPattern =   getMatched(groovySciCommands.Inspect.methodSubString);
       

        for (String filtElem: filteredWithStringPattern) {
         GlobalValues.groovyResultsForCompletion.add(filtElem);
         
        }
}  // for all filtered completion items add to the list's data model adjusting properly the font
     
    
  // returns the index of the first entry in the Vector scanMethods
// that matches the prefix using a binary search
// entries in scanMethods are assumed sorted
    private static  int firstIndexOfMatchedString(String prefix)
    {
        int up = 1;
        int low = 0;
        int ce;  // currently examined element
        int prefLen = prefix.length();
        int currentMethodCnt = scanMethods.size();
        do
        {
            low += up/2;
            up = 1;
            ce = up+low-1;
            while (ce < currentMethodCnt)
            {
                String currentDescription = listOfAllMethods[ce];
                int cLen = currentDescription.length();
                
                int k = (cLen < prefLen) ? cLen : prefLen;
                int m = currentDescription.substring (0, k).compareToIgnoreCase (prefix);
                if (cLen >= prefLen && m >= 0)
                {
                    low  += up/2;
                    if (up == 1) { break; }
                    up = 1;
                }
                else
                {
                    up *= 2;
                }
                ce = up+low-1;
            
            }
        }
        while (up != 1);

        return low;
    }


// returns the index of the last entry in the Vector scanMethods
// that matches the prefix using a binary search
// entries in scanMethods are assumed sorted
private static int lastIndexOfMatchedString(String prefix, int startingPoint)
    {
        int up = 1;
        int low = startingPoint;
        int ce;  // curerntly examined element
        int prefLen = prefix.length();
        int currentMethodCnt = scanMethods.size();
        do
        {
            low += up/2;
            up = 1;
            ce = up+low-1;
            while (ce < currentMethodCnt)
            {
                String currentDescription = listOfAllMethods[ce];
                int cLen = currentDescription.length();
                
                int k = (cLen < prefLen) ? cLen : prefLen;
                int m = currentDescription.substring (0, k).compareToIgnoreCase (prefix);
                if (cLen >= prefLen && m > 0)
                {
                    low  += up/2;
                    if (up == 1) { break; }
                    up = 1;
                }
                else
                {
                    up *= 2;
                }
                ce = up+low-1;
            
            }
        }
        while (up != 1);

        return low;
    }

    
    // returns an array of Strings that start with prefix
    public static String[] getMatched(String prefix)
    {
    
        if (prefix.equals(""))  // all methods match to a null string
        {
            return listOfAllMethods;
        }
        int i = firstIndexOfMatchedString(prefix);
        int j = lastIndexOfMatchedString(prefix, i);
        String[] matches = new String[j-i];

        for (int k = 0; k < matches.length; k++)        {    // collect all matches
            matches[k] = listOfAllMethods[i+k];
         }
        return matches;
    }

         
}