
package gExec.Interpreter;

import groovy.lang.Binding;
import groovySci.math.array.Matrix;
import groovySci.math.array.Vec;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

// the MatlabConnection class allows to execute MATLAB scripts within the GroovyLab environment

public class MatlabConnection {

// initializes a MATLAB instance
    static public void initMatlabConnection() {
        if (GlobalValues.matlabInitedFlag == false) {
        GlobalValues.factory = new MatlabProxyFactory();
        try {
            GlobalValues.proxy = GlobalValues.factory.getProxy();
        } catch (MatlabConnectionException ex) {
            System.out.println("MATLAB Invocation exception: "+ex.getMessage());
        }
        
        GlobalValues.matlabInitedFlag = true;
        }
        
    }
    
    // evaluate a MATLAB script that has no input from GroovyLab and it produces no output also
    static public void meval(String expr)  {
        try {
            GlobalValues.proxy.eval(expr);
        } catch (MatlabInvocationException ex) {
            System.out.println("MATLAB Invocation exception: "+ex.getMessage());
        }
    }
    
    // "general" type evaluation. Each input and output parameter can be either real (i.e. double [][])  or complex (i.e. MatlabComplex [][])
    // evaluate a MATLAB script that has input the vInNames list of GroovyLab's variables and it computes 
    // the vOutNames variables. 
    
    static public void meval(String expr, ArrayList <String>  vInNames,  ArrayList  <String> vOutNames) {
      MatlabTypeConverter  processor = new MatlabTypeConverter(GlobalValues.proxy);
       
     // transfer the variables of the vInNames list,  from GroovyLab to MATLAB's workspace 
     if (vInNames != null) {  // list of variables to transfer from GroovyLab to MATLAB are specified
       int numInVars = vInNames.size();
      for (int v = 0; v<numInVars; v++) {   // for all GroovyLab's variables
         String vname = vInNames.get(v);
         Object cvar = GlobalValues.groovyBinding.getVariable(vname);  // currently examined input variable
         if (cvar instanceof  MatlabComplex) {  // MatlabComplex array
           MatlabComplex xin = (MatlabComplex ) cvar;
          try {      
              processor.setNumericArray(vInNames.get(v), new MatlabNumericArray(xin.re, xin.im));
          } catch (MatlabInvocationException ex) {
              System.out.println("MATLAB Invocation exception in getting parameters of type MatlabComplex: "+ex.getMessage());
          }
        }  //  MatlabComplex  array
         else if (cvar instanceof  double [][])  {  // double [][] array
           double [][] xin = (double [][]) cvar;
          try {      
              processor.setNumericArray(vInNames.get(v), new MatlabNumericArray(xin, null));
          } catch (MatlabInvocationException ex) {
              System.out.println("MATLAB Invocation exception in getting parameters of type double [][] from GroovyLab: "+ex.getMessage());
          }  
         }   // double [][] array
              else if (cvar instanceof  groovySci.math.array.Matrix )  {  // groovySci.math.array.Matrix  array
           double [][] xin = ((groovySci.math.array.Matrix ) cvar).getArray();
          try {      
              processor.setNumericArray(vInNames.get(v), new MatlabNumericArray(xin, null));
          } catch (MatlabInvocationException ex) {
              System.out.println("MATLAB Invocation exception in getting parameters of type double [][] from GroovyLab: "+ex.getMessage());
          }  
         }   // groovySci.math.array.Matrix 
         else if (cvar instanceof  groovySci.math.array.Vec )  {  // groovySci.math.array.Vec
           double [][] xin = new double[1][1];
           xin[0] = ((groovySci.math.array.Vec ) cvar).getv();
          try {      
              processor.setNumericArray(vInNames.get(v), new MatlabNumericArray(xin, null));
          } catch (MatlabInvocationException ex) {
              System.out.println("MATLAB Invocation exception in getting parameters of type double [][] from GroovyLab: "+ex.getMessage());
          }  
         }   // groovySci.math.array.Vec
         else if (cvar instanceof  double [] )  {  // double []
           double [][] xin = new double[1][1];
           xin[0] = (double [] ) cvar;
          try {      
              processor.setNumericArray(vInNames.get(v), new MatlabNumericArray(xin, null));
          } catch (MatlabInvocationException ex) {
              System.out.println("MATLAB Invocation exception in getting parameters of type double [][] from GroovyLab: "+ex.getMessage());
          }  
         }   // double []
         else if (cvar instanceof  Double)  {  // double
           double [][] xin = new double[1][1];
           xin[0][0] = (double ) cvar;
          try {      
              processor.setNumericArray(vInNames.get(v), new MatlabNumericArray(xin, null));
          } catch (MatlabInvocationException ex) {
              System.out.println("MATLAB Invocation exception in getting parameters of type double [][] from GroovyLab: "+ex.getMessage());
          }  
         }   // Double 
         else if (cvar instanceof  Integer)  {  // int
           double [][] xin = new double[1][1];
           xin[0][0] = (double ) cvar;
          try {      
              processor.setNumericArray(vInNames.get(v), new MatlabNumericArray(xin, null));
          } catch (MatlabInvocationException ex) {
              System.out.println("MATLAB Invocation exception in getting parameters of type double [][] from GroovyLab: "+ex.getMessage());
          }  
         }   // Integer
       }    // for all GroovyLab's variables
     } // list of variables to transfer from GroovyLab to MATLAB are specified
     
     
      meval(expr);   // evaluate the script text with MATLAB
    
      // transfer from MATLAB's workspace to GroovyLab
      if (vOutNames != null)  {   // list of variables to transfer from MATLAB to GroovyLab
      int numOutVars = vOutNames.size();
     for (int v = 0; v<numOutVars; v++) {  
          try {      
         String vname = vOutNames.get(v);
         // get the computed MATLAB matrix as Java2D array
                if (processor.getNumericArray(vname).isReal()) {
          double [][]xoutRe = processor.getNumericArray(vname).getRealArray2D();  
          GlobalValues.groovyBinding.setVariable(vname, xoutRe);
                }
                else  // imaginary
                    { 
                double [][]xoutRe = processor.getNumericArray(vname).getRealArray2D();   
                double [][]xoutIm = processor.getNumericArray(vname).getImaginaryArray2D();
                MatlabComplex mtcmplx = new MatlabComplex();
                mtcmplx.re = xoutRe;
                mtcmplx.im = xoutIm;
                GlobalValues.groovyBinding.setVariable(vname, mtcmplx);
                
            }
                
          } catch (MatlabInvocationException ex) {
              System.out.println("MATLAB Invocation exception at returning results to GroovyLab: "+ex.getMessage());
          }
        }
      }
    }
    
     
// evaluate a MATLAB script that has input the vInNames list of GroovyLab's variables and it computes 
    // the vOutNames variables. All variables are double [][] 
    static public void meval(String expr, ArrayList  <String> vOutNames) {
      MatlabTypeConverter  processor = new MatlabTypeConverter(GlobalValues.proxy);
             
     // transfer to MATLAB's workspace from GroovyLab
      Map  cvars  = GlobalValues.groovyBinding.getVariables();
      Set keys = cvars.keySet();
      
      int numInVars = cvars.size();
      double [][] xin = null;
      
      Binding groovyBinding = GlobalValues.groovyBinding;    // get the reference to the Groovy variable binding
      Map variables = groovyBinding.getVariables();  // get a Map of the binded variables at the Groovy's context
      Set  bindElemsSet = variables.keySet();  // return a set view of the variables in the Map
      
      Iterator bindedElemsIter  =  bindElemsSet.iterator();   // iterate through the Groovy's variables
      boolean passCurrentValueToMatlab;
    
         while (bindedElemsIter.hasNext())  {  // for all binded variables of GroovyShell
               passCurrentValueToMatlab = true;
    String varName = (String) bindedElemsIter.next();  // get the name of the Groovy's variable
    
    Object currentVarValue = groovyBinding.getVariable(varName);
    
    if (currentVarValue != null) {  // value of variable not null
     String varValue="";
     //  System.out.println("passing : "+varName+" value= "+currentVarValue.toString());
 
     if ( currentVarValue instanceof  double [])   // a double []
            {
     double [] vv = (double[]) currentVarValue;
     xin = new double[1][1];
     xin[0] = vv;
            }
  else
  if (( currentVarValue instanceof  double [][]))
       {
    xin = (double [][]) currentVarValue;
       }
  else   if  (currentVarValue instanceof  Double) 
             {
    xin = new double [1][1];
    xin[0][0] = (double) currentVarValue;
             }
  else if  (currentVarValue instanceof  BigDecimal) 
             {
    xin = new double [1][1];
    xin[0][0] = (double) currentVarValue;
             }
  else     if  (currentVarValue instanceof  Integer || currentVarValue instanceof  Short || currentVarValue instanceof  Long ) 
             {
    xin = new double [1][1];
    double tmpVal = Double.valueOf(currentVarValue.toString());
    xin[0][0] = tmpVal;
             }
  else if ( currentVarValue instanceof groovySci.math.array.Matrix)  {  // a GroovySci matrix
    xin = ((Matrix)currentVarValue).getArray();
    }
  else if ( currentVarValue instanceof groovySci.math.array.Vec)  {  // a GroovySci vector
    Vec xvec  = (Vec)currentVarValue;
    double [] xvecvals = xvec.getv();
    xin = new double [1][1];
    xin[0]= xvecvals;
        }  
  
  else {
      passCurrentValueToMatlab = false;
      break;
  }
  
  if (passCurrentValueToMatlab==true)   
          try {      
              processor.setNumericArray(varName, new MatlabNumericArray(xin, null));
          } catch (MatlabInvocationException ex) {
              System.out.println("MATLAB Invocation exception: "+ex.getMessage());
          }
    }  // value of variable not null
  }  // for all binded elements of GroovyShell
       
      meval(expr);   // evaluate the script text with MATLAB
    
      // transfer from MATLAB's workspace to GroovyLab
      if (vOutNames != null)  {   // list of variables to transfer from MATLAB to GroovyLab
      int numOutVars = vOutNames.size();
     for (int v = 0; v<numOutVars; v++) {  
          try {      
         String vname = vOutNames.get(v);
         // get the computed MATLAB matrix as Java2D array
                if (processor.getNumericArray(vname).isReal()) {
          double [][]xoutRe = processor.getNumericArray(vname).getRealArray2D();  
          GlobalValues.groovyBinding.setVariable(vname, xoutRe);
                }
                else  // imaginary
                    { 
                double [][]xoutRe = processor.getNumericArray(vname).getRealArray2D();   
                double [][]xoutIm = processor.getNumericArray(vname).getImaginaryArray2D();
                MatlabComplex mtcmplx = new MatlabComplex();
                mtcmplx.re = xoutRe;
                mtcmplx.im = xoutIm;
                GlobalValues.groovyBinding.setVariable(vname, mtcmplx);
                
            }
                
          } catch (MatlabInvocationException ex) {
              System.out.println("MATLAB Invocation exception: "+ex.getMessage());
          }
        }
      
    }
  }
    
    // parses the svdc string that contains a MATLAB svd command 
    // and then calls MATLAB to execute it
static public void msvd(String  svdc) {
 String invar = svdc.substring(svdc.indexOf("(")+1, svdc.indexOf(")")).trim();  // get the input variable
 int  firstCommaIndex = svdc.indexOf(",");
 String uvar = svdc.substring(svdc.indexOf("[")+1, firstCommaIndex ).trim();  // get the first output variable
 String restCommand = svdc.substring(firstCommaIndex+1, svdc.length()).trim();
 int secondCommaIndex = restCommand.indexOf(",");
 String svar = restCommand.substring(0, secondCommaIndex).trim();  // get the second output variable
 restCommand = restCommand.substring(secondCommaIndex+1, restCommand.length()).trim();
 String vvar = restCommand.substring(0, restCommand.indexOf("]")).trim();  // get the third output variable

 ArrayList<String> inList = new ArrayList<>();
 inList.add(invar);   // input variable
 ArrayList<String> outList = new ArrayList<>();
 outList.add(uvar); outList.add(svar); outList.add(vvar); 
  meval(svdc,  inList,  outList);
}

   }

