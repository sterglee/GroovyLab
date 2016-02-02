// GroovySci routines for operating on Matlab's .mat files
package groovySci.math.io;

import gExec.Interpreter.GlobalValues;
import com.jmatio.types.MLDouble;
import java.util.ArrayList;
import com.jmatio.io.*;
import com.jmatio.types.MLArray;
import groovy.lang.Binding;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class MatIO {

    // writes to the Matlab .mat file the contents of the variable variableNameToSave of the GroovySci workspace
    public static  boolean  save(String fileName, String variableNameToSave) {
        
        fileName = fileName.trim();
        String matFileName = fileName;
      if (fileName.endsWith(".mat")==false)
          matFileName = matFileName+".mat";  //  append the default extension
        // check if absolute file name path is specified
      boolean absoluteFileName = false;
      if (GlobalValues.hostIsUnix)
      {
          if  (fileName.startsWith("/"))
              absoluteFileName = true;
      }
      else  // Windows host
          if (fileName.charAt(1)==':')
              absoluteFileName = true;
    
    if (absoluteFileName == false)
        matFileName = GlobalValues.workingDir+File.separator+matFileName;
      
        boolean success= false;
      
         // get the specified variable from the GroovyShell
      Object valueOfVariable = GlobalValues.GroovyShell.getProperty(variableNameToSave);
      if (valueOfVariable != null)  {  // variable exist in GroovySci's workspace
        ArrayList <MLArray> list = new ArrayList<MLArray>();  // list of variables to save
      
        // depending on the type of the variable construct the compatible .mat file object
      if (valueOfVariable instanceof double[])  { 
        double [] valuesOfVariable = (double[])valueOfVariable;
        MLDouble mlDouble = new MLDouble(variableNameToSave, valuesOfVariable, 1);
        list.add(mlDouble);   // add to the list of variables to save
      }
      else if (valueOfVariable instanceof double[][])  {
          double [][] valuesOfVariable = (double [][]) valueOfVariable;
          MLDouble mlDouble = new MLDouble(variableNameToSave, valuesOfVariable);
          list.add(mlDouble);   // add to the list of variables to save
       }
      else if (valueOfVariable instanceof groovySci.math.array.Vec)  {
          double [] valuesOfVariable = ((groovySci.math.array.Vec) valueOfVariable).getv();
          MLDouble mlDouble = new MLDouble(variableNameToSave, valuesOfVariable, 1);
          list.add(mlDouble);   // add to the list of variables to save
       }
      else if (valueOfVariable instanceof groovySci.math.array.Matrix)  {
          double [][]valuesOfMatrix = ((groovySci.math.array.Matrix)valueOfVariable).getArray();
          MLDouble mlDouble = new MLDouble(variableNameToSave, valuesOfMatrix);
          list.add(mlDouble);   // add to the list of variables to save
       }
            
        try {
        new MatFileWriter(matFileName, list);   // save the variable to the .mat file
        }
        catch (IOException ex)  {
            System.out.println("IO Exception in MatFileWriter");
            return false;
        }
        success = true;
       }
      return success;
      }
         
     /*  GroovySci sample code
  //    save a Matrix    
       t = inc(1,1, 1000)
       x = sin(0.12*t)
       matFileName = "testMatrixX"
       save(matFileName, "x")
      
 //     save  a Vector
      vt = vinc(0, 0.01, 10)
      vx = sin(2.3*vt)
      vecFileName = "testVecX"
      save(vecFileName, "vx")
          
      */
      


    // writes to the Matlab .mat file the contents of the GroovySci workspace
    public static  boolean  save(String fileName) {
        
        fileName=fileName.trim();
      String matFileName = fileName;
      if (fileName.endsWith(".mat")==false)
          matFileName = matFileName+".mat";  //  append the default extension
      boolean absoluteFileName = false;
      if (GlobalValues.hostIsUnix)
      {
          if  (fileName.startsWith("/"))
              absoluteFileName = true;
      }
      else
          if (fileName.charAt(1)==':')
              absoluteFileName = true;
    
    if (absoluteFileName == false)
        matFileName = GlobalValues.workingDir+File.separator+matFileName;
      
      boolean success= false;
      
      Binding  currentVarBinding = GlobalValues.GroovyShell.getContext();    // current binding of GroovySci's variables
      Map varsMap = currentVarBinding.getVariables();
      Set varsSet = varsMap.keySet();
      Iterator <String> currentVar = varsSet.iterator();
      ArrayList <MLArray> list = new ArrayList<MLArray>();  // list of variables to save
      while (currentVar.hasNext()) {  // for all variables
          String variableNameToSave = currentVar.next();
          Object valueOfVariable = GlobalValues.GroovyShell.getProperty(variableNameToSave);
      if (valueOfVariable != null)  {  // variable exist in GroovySci's workspace
        // depending on the type of the variable construct the compatible .mat file object
      if (valueOfVariable instanceof double[])  {
        double [] valuesOfVariable = (double[])valueOfVariable;
        MLDouble mlDouble = new MLDouble(variableNameToSave, valuesOfVariable, 1);
        list.add(mlDouble);   // add to the list of variables to save
      }
      else if (valueOfVariable instanceof double[][])  {
          double [][] valuesOfVariable = (double [][]) valueOfVariable;
          MLDouble mlDouble = new MLDouble(variableNameToSave, valuesOfVariable);
          list.add(mlDouble);   // add to the list of variables to save
       }
      else if (valueOfVariable instanceof groovySci.math.array.Vec)  {
          double [] valuesOfVariable = ((groovySci.math.array.Vec) valueOfVariable).getv();
          MLDouble mlDouble = new MLDouble(variableNameToSave, valuesOfVariable, 1);
          list.add(mlDouble);   // add to the list of variables to save
       }
      else if (valueOfVariable instanceof groovySci.math.array.Matrix)  {
          double [][]valuesOfMatrix = ((groovySci.math.array.Matrix)valueOfVariable).getArray();
          MLDouble mlDouble = new MLDouble(variableNameToSave, valuesOfMatrix);
          list.add(mlDouble);   // add to the list of variables to save
          }
       }     // for all variables   
      }  // variable exist in GroovySci's workspace
          try {
        new MatFileWriter(matFileName, list);   // save the variable to the .mat file
        }
        catch (IOException ex)  {
            System.out.println("IO Exception in MatFileWriter");
            return false;
        }
        success = true;
       
      return success;
      }
         
     /*  GroovySci sample code
       t = inc(1,1, 1000);
       x = sin(0.12*t);
       matFileName = "testX";
       save(matFileName, "x");
      */
      

    
    /* GroovySci
      matFileName =  "quake";
     load(matFileName);
      */
    
    // loads the Matlab .mat file contents to GroovySci workspace
    public static  int  load(String fileName) {
        
        fileName=fileName.trim();
      String matFileName = fileName;
      if (fileName.endsWith(".mat")==false)
          matFileName = matFileName+".mat";  //  append the default extension
      boolean absoluteFileName = false;
      if (GlobalValues.hostIsUnix)
      {
          if  (fileName.startsWith("/"))
              absoluteFileName = true;
      }
      else
          if (fileName.charAt(1)==':')
              absoluteFileName = true;
    
    if (absoluteFileName == false)
        matFileName = GlobalValues.workingDir+File.separator+matFileName;
      
      int numVarsReaded = 0;  
                 
    try {
        //read in the file
//   MatFileReader mfr = new MatFileReader( "c:\\testMatlabM\\quake.y.mat" );
  MatFileReader mfr = new MatFileReader( matFileName );
  //  a map of MLArray objects that were inside MAT-file. MLArrays are mapped with MLArrays' names
  Map  matFileVars = mfr.getContent(); 
  
  Set varsSet = matFileVars.keySet();
  Iterator <String> varsIter = varsSet.iterator();
  boolean isRoot = true;
  while (varsIter.hasNext())  {  // for all .mat file variables
        String currentVariable = varsIter.next();  // get the String of the Matlab variable
            //get array of a name "my_array" from file\
        MLDouble mlArrayRetrived = (MLDouble)mfr.getMLArray(currentVariable);
        String arrayName = mlArrayRetrived.getName();
        int NSize = mlArrayRetrived.getN();
        int MSize = mlArrayRetrived.getM();
        double [][] data  = mlArrayRetrived.getArray();   // get data 
       if (NSize == 1 && MSize == 1)  // get as single double
        {
            double scalarData = data[0][0];
            GlobalValues.GroovyShell.setProperty(arrayName, scalarData);
        }
       else 
        GlobalValues.GroovyShell.setProperty(arrayName, data);

        numVarsReaded++;  // one more variable readed
     }   // for all .mat file variables
  
    return numVarsReaded;
    }     
       
    catch (Exception e) 
    {
        System.out.println("Exception ");
        e.printStackTrace();
        return 0;
    }
  
    }
  
}
