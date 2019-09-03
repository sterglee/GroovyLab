package gExec.gui;

import java.util.*;

import java.util.regex.*;



public class AutoCompletionGroovySci
{
    static public Vector  scanMethodsGroovySci = new Vector(10000);
    
    static String []   listOfAllMethodsGroovySci;
    
    public static Hashtable autoCompletionDetailsGroovySci = new Hashtable(3000);
    
    public AutoCompletionGroovySci()
    {

       Collections.sort(AutoCompletionGroovySci.scanMethodsGroovySci, new Comparator()  {
             public int compare(Object v1, Object v2) {
                 return ((String)v1).compareToIgnoreCase(((String)v2));
             }
        });
        
        
        // construct a view of the sorted AutoCompletionGroovy Vector as an array of Strings
        int countMethods = AutoCompletionGroovySci.scanMethodsGroovySci.size();
        listOfAllMethodsGroovySci = new String[countMethods];
        for (int k=0; k<countMethods; k++)
            listOfAllMethodsGroovySci[k] = (String) AutoCompletionGroovySci.scanMethodsGroovySci.elementAt(k);
  
        
        }
        
    
     
    /**
     * Return those functions starting with the prefix.

     * @param prefix Prefix of the function name.
     * @return An array of Strings with autocompletion information
     * returns null
     */
    public String[] getMatched(String prefix)
    {
          if (prefix.equals(""))  // all methods match to a null string
        {
            return listOfAllMethodsGroovySci;
        }
        int i = firstIndexOfMatchedString(prefix);
        int j = lastIndexOfMatchedString(prefix, i);
        String[] matches = new String[j-i];

        for (int k = 0; k < matches.length; k++)
        {
            matches[k] = listOfAllMethodsGroovySci[i+k];
         }
        return matches;
        
    }


    public String[] getMatchedRegEx(String prefix) 
     {
          if (prefix.equals(""))  {
              return listOfAllMethodsGroovySci;
          }
          Pattern commandPattern = Pattern.compile(prefix);
          Vector vmatches = new Vector();
          int cntMatches = 0;  // count of matches
          for (int k=0; k<listOfAllMethodsGroovySci.length; k++) {
              String currentDescription = listOfAllMethodsGroovySci[k];
              if (currentDescription != null) {
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



    private int firstIndexOfMatchedString(String prefix)
    {
        int up = 1;
        int low = 0;
        int ce;  // currently examined element
        int prefLen = prefix.length();
        int currentMethodCntGroovySci = AutoCompletionGroovySci.scanMethodsGroovySci.size();
        do
        {
            low += up/2;
            up = 1;
            ce = up+low-1;
            while (ce < currentMethodCntGroovySci)
            {
                String currentDescription = listOfAllMethodsGroovySci[ce];
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
        int currentMethodCntGroovySci = AutoCompletionGroovySci.scanMethodsGroovySci.size();
        do
        {
            low += up/2;
            up = 1;
            ce = up+low-1;
            while (ce < currentMethodCntGroovySci)
            {
                String currentDescription = listOfAllMethodsGroovySci[ce];
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


