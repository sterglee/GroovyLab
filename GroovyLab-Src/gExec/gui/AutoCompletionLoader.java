
package gExec.gui;


import java.io.*;
import java.util.TreeSet;
import gExec.ClassLoaders.JarClassLoader;
import gExec.Interpreter.GlobalValues;
import gLabGlobals.JavaGlobals;
import javax.swing.JOptionPane;

public class AutoCompletionLoader 
{ 

    
   public void getAutoCompletionInfo() {
               try {
         JarClassLoader  myJarLoader = new JarClassLoader();
               
         int groovyClasses;
        
         if (GlobalValues.startingFromNetbeans)
              groovyClasses = myJarLoader.scanBuiltInGroovySciClasses(GlobalValues.jarFilePath);   
         else
              groovyClasses = myJarLoader.scanBuiltInGroovySciClasses(JavaGlobals.jarFilePath);   
   
         int numAlClasses = myJarLoader.scanBuiltInLibraryClasses( GlobalValues.LibNumAlDir, JavaGlobals.numalFile);   // scan the NUMAL numerical analysis functions
         System.out.println("NUMAL  Classes = "+numAlClasses);
         int ejmlClasses = myJarLoader.scanBuiltInLibraryClasses("org/ejml/", JavaGlobals.ejmlFile);
         System.out.println("EJML  Classes = "+ejmlClasses);
         int jSciWaveletClasses = myJarLoader.scanBuiltInLibraryClasses("JSci/maths/wavelet",  JavaGlobals.jsciFile);
         System.out.println("Wavelet Classes = "+jSciWaveletClasses);
         int cernColtClasses = myJarLoader.scanBuiltInLibraryClasses("cern/colt", JavaGlobals.jsciFile);
         System.out.println("COLT  Classes = "+cernColtClasses);
         int mtjClasses = myJarLoader.scanBuiltInLibraryClasses("no/uib", JavaGlobals.mtjColtSGTFile);
         System.out.println("MTJ  Classes = "+mtjClasses);
         
         
       }
         
             catch (IOException ioEx) {
                 System.out.println("I/O error in JarClassLoader");
                 ioEx.printStackTrace();
                 
             }
        } 
}
