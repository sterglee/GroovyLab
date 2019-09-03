package expandRunTime;

import static groovySci.math.array.BasicDSP.*; 
import groovySci.math.array.Matrix;   // Matrix class
import static groovySci.math.array.Matrix.*;    
import groovySci.math.array.Vec;   // Vec class
import static groovySci.math.array.Vec.*;    
import groovySci.math.array.Sparse;   // Sparse Matrix class
import static groovySci.math.array.Sparse.*;    
import numal.*;   // numerical analysis library routines
import static groovySci.math.plot.plot.*;     // plotting routines 
import static java.lang.Math.*;     // standard Java math routines, allows calling directly e.g sin(9.8) instead of Math.sin(9.8)
import java.awt.*; 
 import javax.swing.*;   // Java standard UI and graphics support
import static groovySci.math.io.MatIO.*;    // support for .mat Matlab files
import groovy.swing.SwingBuilder; 
import java.awt.event.*; 
import java.text.DecimalFormat; 
import static groovySciCommands.BasicCommands.*;   // support for GroovySci's console commands
import  static  groovySci.math.array.DoubleArray.*;
import  org.ejml.simple.SimpleMatrix;
import static gExec.gui.watchMatrix.*; 

// reimplement Matrix-Matrix  multiplication using JBLAS
groovySci.math.array.Matrix.metaClass.multiply = { 
   groovySci.math.array.Matrix m ->   // the input Matrix

 // transform the input matrix to the JBLAS representation
     dm =  new org.jblas.DoubleMatrix(m.toDoubleArray())
 // transform the receiver to the JBLAS representation
     dmthis = new org.jblas.DoubleMatrix(delegate.toDoubleArray())
 // fast multiply using JBLAS Native BLAS
     mulRes = dmthis.mmul(dm)

 // return back result as a double [][] array
    groovySci.math.array.JBLASUtils.JBLASDoubleMatrixToDouble2D(mulRes)
  }



   
