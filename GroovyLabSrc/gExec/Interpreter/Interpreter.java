package gExec.Interpreter;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import gLabGlobals.JavaGlobals;
import groovySciCommands.BasicCommands;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

public class Interpreter 
{
    public  Interpreter() {
        gExec.Interpreter.GlobalValues.AutoCompletionGroovySci = new gExec.gui.AutoCompletionGroovySci();  // create the autocompletion object
        
    }
    // execute an expression with the Groovy shell
    public static String  execWithGroovyShell(String expression)
    {
     
            java.awt.Cursor  cr = new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR);
            
            gExec.Interpreter.GlobalValues.globalEditorPane.setCursor(cr);
            
        boolean displayAnswer = true;

            if (expression.endsWith(";"))
               displayAnswer = false;    // surpresses the display of the last script result
        
           // set the compiler configuration to have the list of the specified .jar files in its classpath
        if (GlobalValues.GroovyShell == null || GlobalValues.jarToolboxesClassPathUpdatedForGroovyShell==false)  {  // construct a properly inited GroovyShell
            GlobalValues.jarToolboxesClassPathUpdatedForGroovyShell = true;  // class path of GroovyShell updated
           
            gExec.gLab.groovySciToolboxes.warmUpGroovy();
            
        } // construct a properly inited GroovyShell
            

       expression = expression.trim();
        if (GlobalValues.useAlwaysDefaultImports)  // add GroovyLab's default imports 
        {
      expression  =  GlobalValues.bufferingImports+"\n"+ GlobalValues.bufferingCode+"\n"+expression;
    }

           Object  grResult=null;
       // evaluate the command with the Groovy shell
           GlobalValues.currentExpression = expression;
           try {
               grResult =  GlobalValues.GroovyShell.evaluate(expression);
               GlobalValues.groovyBinding = GlobalValues.GroovyShell.getContext();  // update Groovy context
            }
           catch (Exception e) {
                    cr = new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR);
                    gExec.Interpreter.GlobalValues.globalEditorPane.setCursor(cr);
            
                        String excText = e.getMessage();
                       if (excText != null) {
                      if (excText.isEmpty()==false) {
                           StackTraceElement []  st = e.getStackTrace();
                        StringBuilder exText = new StringBuilder();
                        for (StackTraceElement el: st) 
                            exText.append(el.toString()+"\n");
                        
                        
                        System.out.println("\nCompilation Error: \n"+excText.toString());
                        
                        return null;
                    }  // excText not empty
                  }   // excTest != null
              }   // exception in compile with GroovyShell   

           
            cr = new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR);
            gExec.Interpreter.GlobalValues.globalEditorPane.setCursor(cr);
            
       if (displayAnswer==true)  {
        if (grResult != null)  {
           if (grResult instanceof  double [])  {
if (groovySci.PrintFormatParams.verboseFlag == true)               
 return "double["+((double [])grResult).length+"] = \n "+groovySci.math.array.Matrix.printArray((double[])grResult);
else
return "";
           }
                   
else if  (grResult instanceof  double [][]) {
if (groovySci.PrintFormatParams.verboseFlag == true)               
    return "double["+((double [][])grResult).length+"] ["+((double [][])grResult)[0].length+ "] = \n"+
        groovySci.math.array.Matrix.printArray((double[][])grResult);
     else
    return "";
            }
           else
           
    if (grResult instanceof  int []) 
return "int["+((int [])grResult).length+"] = \n "+groovySci.math.array.DoubleArray.toString((int[])grResult);
else if  (grResult instanceof  int [][])
return "int["+((int [][])grResult).length+"] ["+((int [][])grResult)[0].length+ "] = \n"+
        groovySci.math.array.DoubleArray.toString((int[][])grResult);
                       else 
           if (grResult != null)
               {
          String typeOfResult = BasicCommands.typeOf(grResult);
           return typeOfResult+":\n"+grResult.toString();
           
         }
        
           else
               return "";
        }  // grResult != null
        else return ""; 
       }  // displayAnswer == true
       
       else return "";
    }
    
       // execute an expression with the Groovy Class Loader
    public Object  execWithGroovyClassLoader(String expression)
    {
        
            boolean displayAnswer = true;

            GlobalValues.gLabMainFrame.setTitle(GlobalValues.TITLE+ " indy = "+GlobalValues.CompileIndy);
            
            if (expression.endsWith(";"))
               displayAnswer = false;    // surpresses the display of the last script result
        
           // set the compiler configuration to have the list of the specified .jar files in its classpath
        if (GlobalValues.groovyClassLoader == null || GlobalValues.jarToolboxesClassPathUpdatedForGroovyShell==false)  {  // construct a properly inited GroovyShell
            GlobalValues.jarToolboxesClassPathUpdatedForGroovyShell = true;
            CompilerConfiguration cf = new CompilerConfiguration();
      
            GlobalValues.prepareImports();
            
            cf.addCompilationCustomizers(GlobalValues.globallmportCustomizer);
  
            if (GlobalValues.CompileDecimalsToDoubles)  {    // convert BigDecimalsToDoubles for efficiency
            
              cf.addCompilationCustomizers(new CompilationCustomizer(CompilePhase.CONVERSION) {
                
                    @Override
                    public void call(final SourceUnit source, final  GeneratorContext context, final ClassNode classNode)   {
                        new expandRunTime.ConstantTransformer(source).visitClass(classNode);
                    }
                });
            }
            
            cf.setTargetBytecode(GlobalValues.jdkTarget);
            if (GlobalValues.CompileIndy == true)  {
               cf.getOptimizationOptions().put("indy", true);
               cf.getOptimizationOptions().put("int", false);
            }
            else             {
               cf.getOptimizationOptions().put("indy", false);
               cf.getOptimizationOptions().put("int", true);
            }
            cf.setRecompileGroovySource(true);
            
            LinkedList <String> pathsList = new LinkedList<String>();
            pathsList.add("." );   // current directory
                    
            pathsList.add(JavaGlobals.jarFilePath);
            pathsList.add(JavaGlobals.groovyJarFile);
            pathsList.add(JavaGlobals.ApacheCommonsFile);
            pathsList.add(JavaGlobals.jsciFile);
            pathsList.add(JavaGlobals.mtjColtSGTFile);
            pathsList.add(JavaGlobals.ejmlFile);
            pathsList.add(JavaGlobals.jblasFile);
            pathsList.add(JavaGlobals.numalFile);
            
            pathsList.add(JavaGlobals.JASFile);
            pathsList.add(JavaGlobals.LAPACKFile);
            pathsList.add(JavaGlobals.ARPACKFile);
     
            for (int k=0; k<GlobalValues.jartoolboxesForGroovySci.size(); k++) {
               String pathToAdd = (String)GlobalValues.jartoolboxesForGroovySci.get(k);
               if (pathsList.contains(pathToAdd)==false)
                  pathsList.add(pathToAdd);
            }
         // append now the GroovySciClassPath components
            for (int k=0; k<GlobalValues.GroovySciClassPathComponents.size(); k++) {
                String pathToAdd =  (String) GlobalValues.GroovySciClassPathComponents.elementAt(k);
             if (pathsList.contains(pathToAdd)==false)
                pathsList.add(pathToAdd);
            }
         
            cf.setClasspathList(pathsList);
            GlobalValues.groovyClassLoader   = new GroovyClassLoader(this.getClass().getClassLoader(), cf );
            
            expandRunTime.expandGroovy expG = new expandRunTime.expandGroovy();
            expG.run();
           
        }
            

       expression = expression.trim();
        if (expression.startsWith("import")==false)  // add Glab's default imports 
        {
  expression  =  GlobalValues.bufferingImports+"\n"+expression;
    }


           Object  grResult=null;
       // evaluate the command with the Groovy shell
           GlobalValues.currentExpression = expression;
        
        
       Class  parsedClass = GlobalValues.groovyClassLoader.parseClass(expression);
       
        try {
       GroovyObject  groovyScriptMain = (GroovyObject) parsedClass.newInstance();
   //  pass the binding to the current Groovy object
       Map  bindedVars  = GlobalValues.groovyBinding.getVariables();
       Set bindedVarsSet  = bindedVars.keySet();
       Iterator varIter =  bindedVarsSet.iterator();
       HashMap <String, Object> propertyMap = new HashMap();
       while (varIter.hasNext())  {
           String  currentBindedVar = (String) varIter.next();  // get binded variable
           Object  currentBindedVarValue = bindedVars.get(currentBindedVar);
           propertyMap.put(currentBindedVar, currentBindedVarValue);
           groovyScriptMain.setProperty(currentBindedVar, currentBindedVarValue);
       }
       

       Object  [] args = {};
        grResult = groovyScriptMain.invokeMethod("main", args);
   
        //  update the global binding from the object's execution
       varIter =  bindedVarsSet.iterator();
       while (varIter.hasNext())  {
           String  currentBindedVar = (String) varIter.next();  // get binded variable
           Object newPropValue = groovyScriptMain.getProperty(currentBindedVar);  // updated property value
           GlobalValues.groovyBinding.setProperty(currentBindedVar, newPropValue);
       }
   
        }
        catch (java.lang.InstantiationException e) 
          { 
            System.out.println("Instantiation exception in trying to instantiate the main() method of the Script with the GroovyClassLoader");
            e.printStackTrace();
        }
       catch (java.lang.IllegalAccessException  e) 
          { 
            System.out.println("Illegal access exception in trying to instantiate the main() method of the Script with the GroovyClassLoader");
            e.printStackTrace();
        }
       
       if (displayAnswer==true)  {
           if (grResult instanceof  double [])
               return groovySci.math.array.Matrix.printArray((double[])grResult);
           else if (grResult instanceof  double [][])
               return groovySci.math.array.Matrix.printArray((double[][])grResult);
                       else 
           if (grResult != null)
               return grResult.toString(); 
           else
               return "";
       }
       else return "";
       
       
    }
    
 
    
    
    
}

      
   
