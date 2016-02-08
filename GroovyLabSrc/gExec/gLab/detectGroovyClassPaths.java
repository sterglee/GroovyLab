
package gExec.gLab;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import gExec.Interpreter.GlobalValues;
import gLabGlobals.JavaGlobals;
import java.util.LinkedList;

public class detectGroovyClassPaths {
     

    public static URL  jarPathOfClass(String className) {
        try {
            return Class.forName(className).getProtectionDomain().getCodeSource().getLocation();
        } catch (ClassNotFoundException ex) {
           System.out.println("error in jarPathOfClass"+className+")");
           ex.printStackTrace();
           return null;
        }
}
    
      public static String decodeFileName(String fileName) {
        String decodedFile = fileName;

        try {
            decodedFile = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println("Encounted an invalid encoding scheme when trying to use URLDecoder.decode().");
            System.err.println("Please note that if you encounter this error and you have spaces in your directory you will run into issues. ");
        }

        return decodedFile;
    }
      
    // detects the .jar files of all the main GroovyLab's libraries
      public static void detectClassPaths()
    {
        boolean      hostIsUnix = true;
         if (File.pathSeparatorChar==';')
              hostIsUnix = false;  // Windows host
          
             if (hostIsUnix) {    
        JavaGlobals.jarFilePath = jarPathOfClass("gExec.gLab.gLab").toString().replace("file:/", "/");   // GroovyLab's main .jar
        JavaGlobals.groovyJarFile = jarPathOfClass("groovy.lang.GroovyClassLoader").toString().replace("file:/", "/");   // Groovy's embeddable jar file
        JavaGlobals.LAPACKFile = jarPathOfClass("org.netlib.lapack.LAPACK").toString().replace("file:/",  "/");   // JLAPACK 
        JavaGlobals.ARPACKFile = jarPathOfClass("org.netlib.lapack.Dgels").toString().replace("file:/",  "/");    // ARPACK
        JavaGlobals.javacFile = jarPathOfClass("com.sun.tools.javac.Main").toString().replace("file:/", "/");    // Java compiler
        JavaGlobals.JASFile = jarPathOfClass("org.matheclipse.core.eval.EvalEngine").toString().replace("file:/", "/");  // Java Algebra System
        JavaGlobals.jsciFile = jarPathOfClass("JSci.maths.wavelet.Cascades").toString().replace("file:/", "/");   // JSci file
        JavaGlobals.mtjColtSGTFile = jarPathOfClass("no.uib.cipr.matrix.AbstractMatrix").toString().replace("file:/", "/");  // Matrix Toolkits for Java, Colt and Scientific Graphics Toolbox
        JavaGlobals.ejmlFile = jarPathOfClass("org.ejml.EjmlParameters").toString().replace("file:/", "/");   // EJML
        JavaGlobals.jblasFile = jarPathOfClass("org.jblas.NativeBlas").toString().replace("file:/", "/");   // JBLAS
        JavaGlobals.numalFile = jarPathOfClass("numal.Linear_algebra").toString().replace("file:/","/");   // NUMAL and Numerical Recipes routines
        JavaGlobals.ApacheCommonsFile = jarPathOfClass("org.apache.commons.math3.ode.nonstiff.ThreeEighthesIntegrator").toString().replace("file:/", "/");  // Apache Common Maths
   
                    } 
         else {
        JavaGlobals.jarFilePath = jarPathOfClass("gExec.gLab.gLab").toString().replace("file:/", "");
        JavaGlobals.groovyJarFile = jarPathOfClass("groovy.lang.GroovyClassLoader").toString().replace("file:/", "");
        JavaGlobals.LAPACKFile = jarPathOfClass("org.netlib.lapack.LAPACK").toString().replace("file:/",  "");
        JavaGlobals.ARPACKFile = jarPathOfClass("org.netlib.lapack.Dgels").toString().replace("file:/",  "");
        JavaGlobals.javacFile = jarPathOfClass("com.sun.tools.javac.Main").toString().replace("file:/", "");
        JavaGlobals.JASFile = jarPathOfClass("org.matheclipse.core.eval.EvalEngine").toString().replace("file:/", "");
        JavaGlobals.jsciFile = jarPathOfClass("JSci.maths.wavelet.Cascades").toString().replace("file:/", "");
        JavaGlobals.mtjColtSGTFile = jarPathOfClass("no.uib.cipr.matrix.AbstractMatrix").toString().replace("file:/", "");
        JavaGlobals.ejmlFile = jarPathOfClass("org.ejml.EjmlParameters").toString().replace("file:/", "");
        JavaGlobals.jblasFile = jarPathOfClass("org.jblas.NativeBlas").toString().replace("file:/", "");
        JavaGlobals.numalFile = jarPathOfClass("numal.Linear_algebra").toString().replace("file:/","");
        JavaGlobals.ApacheCommonsFile = jarPathOfClass("org.apache.commons.math3.ode.nonstiff.ThreeEighthesIntegrator").toString().replace("file:/", "");
   
         }   

        
             
             
    }
                  
}
