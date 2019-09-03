package gExec.gui;

import java.util.*;

import java.util.regex.*;
import gExec.Interpreter.GlobalValues;
import groovy.lang.Binding;


/**  class AutoCompletionWorkspace implements autocompletion support for the variables in global workspace
 *  this support is triggered with the TAB key
 */
public class AutoCompletionWorkspace
{
    static public int mxCntOfVariables = 100;  // init time load of variables  for autocompletion
    static public Vector  scanVariables = new Vector(mxCntOfVariables);
    static String []  listOfAllVariables;
    
    public AutoCompletionWorkspace()
    {
      Binding groovyBinding = GlobalValues.groovyBinding;    // get the reference to the Groovy variable binding
      Map variables = groovyBinding.getVariables();  // get a Map of the binded variables at the Groovy's context
      Set  bindElemsSet = variables.keySet();  // return a set view of the variables in the Map
        
            Iterator bindedElemsIter  =  bindElemsSet.iterator();   // iterate through the Groovy's variables
            while (bindedElemsIter.hasNext())  { 
    String currentVarName = (String) bindedElemsIter.next();  // get the name of the Groovy's variable
    AutoCompletionWorkspace.scanVariables.add(currentVarName);
            }
            
       
        Collections.sort(AutoCompletionWorkspace.scanVariables, new Comparator()  {
             public int compare(Object v1, Object v2) {
                 return ((String)v1).compareToIgnoreCase(((String)v2));
             }
        });
        
        // copy vector to list
        int countMethods = AutoCompletionWorkspace.scanVariables.size();
        listOfAllVariables = new String[mxCntOfVariables];
        for (int k=0; k<countMethods; k++)
            listOfAllVariables[k] = (String) AutoCompletionWorkspace.scanVariables.elementAt(k);
             
        }
        
       
       


   public String[] getMatchedRegEx(String prefix) 
     {
          if (prefix.equals(""))  {
              return listOfAllVariables;
          }
          Pattern commandPattern = Pattern.compile(prefix);
          Vector vmatches = new Vector();
          int cntMatches = 0;  // count of matches
          for (int k=0; k<listOfAllVariables.length; k++) {
              String currentDescription = listOfAllVariables[k];
              if (currentDescription!=null) {
               Matcher exprMatcher = commandPattern.matcher(currentDescription);
               if (exprMatcher.matches()) {  // one more match
                  vmatches.add(currentDescription);
                  cntMatches++;
               }
              }
          }
          String [] matches;
          if (cntMatches > 0) {    
            matches = new String [cntMatches];
          for (int k=0; k<cntMatches;k++)
              matches[k] =  vmatches.elementAt(k).toString();
          }
          else {
              matches = new String[1];
              matches[0] = "";
          }
          
          return matches;
   }
   
   /**
     * Return those functions starting with the prefix.

     * @param prefix Prefix of the function name.
     * @return An array of function (full) names. If nothing can be matched, it
     * returns null
     */
    public String[] getMatched(String prefix)
    {
        if (prefix.equals(""))    {  // all methods match to a null string
            return listOfAllVariables;
         }
        int i = firstIndexOfMatchedString(prefix);
        int j = lastIndexOfMatchedString(prefix, i);
        String[] matches = new String[j-i];

        for (int k = 0; k < matches.length; k++)
        {
            matches[k] = listOfAllVariables[i+k];
        }

        return matches;
    }



    private int firstIndexOfMatchedString(String prefix)
    {
        int up = 1;
        int low = 0;
        int ce;  // curerntly examined element
        int prefLen = prefix.length();
        do
        {
            low += up/2;
            up = 1;
            ce = up+low-1;
            while (ce < AutoCompletionWorkspace.scanVariables.size())
            {
                String currentDescription = listOfAllVariables[ce];
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



    private int lastIndexOfMatchedString(String prefix, int startingPoint)
    {
     
        int up = 1;
        int low = startingPoint;
        int ce;  // curerntly examined element
        int prefLen = prefix.length();
        int currentMethodCnt = AutoCompletionWorkspace.scanVariables.size();
            
        do
        {
            low += up/2;
            up = 1;
            ce = up+low-1;
            while (ce < currentMethodCnt)
            {
                String currentDescription = listOfAllVariables[ce];
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
}


