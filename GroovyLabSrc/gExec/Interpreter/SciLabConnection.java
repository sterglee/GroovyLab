
package gExec.Interpreter;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scilab.modules.javasci.JavasciException;
import org.scilab.modules.javasci.Scilab;
import org.scilab.modules.types.ScilabType;
import org.scilab.modules.types.ScilabDouble;

public class SciLabConnection {
  
    // initializes a SciLab instance
    static public  void initSciLabConnection() {
        if (GlobalValues.sciLabInitedFlag == false) {
        try {
            GlobalValues.scilabObj = new Scilab();
            GlobalValues.scilabObj.open();
        } 
        catch (org.scilab.modules.javasci.JavasciException e) {
            System.err.println("An exception occurred: initializing SciLab " + e.getLocalizedMessage());
      }
        
        GlobalValues.sciLabInitedFlag = true;
        }
    }


        // "general" type evaluation. Each input and output parameter can be either real (i.e. double [][])  or complex (i.e. MatlabComplex [][])
    // evaluate a SciLab script that has input the vInNames list of GroovyLab's variables and it computes 
    // the vOutNames variables. 
    
    static public void scieval(String expr, ArrayList <String>  vInNames,  ArrayList  <String> vOutNames) {
      Scilab sciobj = GlobalValues.scilabObj; 
     // transfer the variables of the vInNames list,  from GroovyLab to MATLAB's workspace 
     if (vInNames != null) {  // list of variables to transfer from GroovyLab to MATLAB are specified
       int numInVars = vInNames.size();
      for (int v = 0; v<numInVars; v++) {   // for all GroovyLab's variables
         String vname = vInNames.get(v);
         Object cvar = GlobalValues.groovyBinding.getVariable(vname);  // currently examined input variable
       if (cvar instanceof  double [][])  {  // double [][] array
           double [][] xin = (double [][]) cvar;
           ScilabDouble sd = new ScilabDouble(xin);
             try {
                 sciobj.put(vname, sd);
             } catch (JavasciException ex) {
                 System.out.println("JavaSci exception "+ex.getMessage());
             }
         }    
       else if (cvar instanceof  groovySci.math.array.Matrix )  {  // groovySci.math.array.Matrix  array
           double [][] xin = ((groovySci.math.array.Matrix ) cvar).getArray();
           ScilabDouble sd = new ScilabDouble(xin);
           try {
           sciobj.put(vname, sd);
           }
            catch (JavasciException ex) {
                 System.out.println("JavaSci exception "+ex.getMessage());
             }
      }   // groovySci.math.array.Matrix 
         else if (cvar instanceof  groovySci.math.array.Vec )  {  // groovySci.math.array.Vec
           double [][] xin = new double[1][1];
           xin[0] = ((groovySci.math.array.Vec ) cvar).getv();
           ScilabDouble sd = new ScilabDouble(xin);
           try {
           sciobj.put(vname, sd);
             }
           catch (JavasciException ex) {
                 System.out.println("JavaSci exception "+ex.getMessage());
             }
      }   // groovySci.math.array.Vec
         else if (cvar instanceof  double [] )  {  // double []
           double [][] xin = new double[1][1];
           xin[0] = (double [] ) cvar;
           ScilabDouble sd = new ScilabDouble(xin);
           try {
           sciobj.put(vname, sd);
           } 
           catch (JavasciException ex) {
                 System.out.println("JavaSci exception "+ex.getMessage());
             }
      }   // double []
         else if (cvar instanceof  Double)  {  // double
           ScilabDouble sd = new ScilabDouble((double)cvar);
            try {
           sciobj.put(vname, sd);
            }
            catch (JavasciException ex) {
                 System.out.println("JavaSci exception "+ex.getMessage());
             }
         }   // Double 
         else if (cvar instanceof  Integer)  {  // int
           ScilabDouble sd = new ScilabDouble((int)cvar);
           try {
           sciobj.put(vname, sd);
           } 
           catch (JavasciException ex) {
                 System.out.println("JavaSci exception "+ex.getMessage());
             }
             
         }   // Integer
       }    // for all GroovyLab's variables
     } // list of variables to transfer from GroovyLab to SciLab are specified
     
     sciobj.exec(expr);  // evaluate the script text with SciLab
    
      // transfer from SciLab's workspace to GroovyLab
      if (vOutNames != null)  {   // list of variables to transfer from MATLAB to GroovyLab
      int numOutVars = vOutNames.size();
     for (int v = 0; v<numOutVars; v++) {  
          try {
              String vname = vOutNames.get(v);
              // get the computed SciLab matrix as Java2D array
              ScilabType scitype = sciobj.get(vname);
              GlobalValues.groovyBinding.setVariable(vname, scitype);
          } catch (JavasciException ex) {
              Logger.getLogger(SciLabConnection.class.getName()).log(Level.SEVERE, null, ex);
          }
            }
          
        }
      }
    
}

