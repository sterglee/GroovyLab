package test
import static groovySci.math.array.BasicDSP.*; 
import groovySci.math.array.Matrix; 
import static groovySci.math.array.Matrix.*; 
import static groovySci.math.array.Matrix.*; 
import numal.*;
import static groovySci.math.plot.plot.*;
import static java.lang.Math.*;  
import java.awt.*; 
import javax.swing.*; 
import static groovySci.math.io.MatIO.*;
import java.awt.event.*; 
import groovy.swing.SwingBuilder; 
import java.text.DecimalFormat; 
import static groovySciCommands.BasicCommands.*;
import  static  groovySci.math.array.DoubleArray.*;
import  static  groovySci.math.LinearAlgebra.LinearAlgebra.*; 
 import JSci.maths.*; 
import JSci.maths.wavelet.*; 
import JSci.maths.wavelet.daubechies2.*; 

 class testc {
  static void c() {
tic();
double sm = 0.0;
int N=100;
int M=100;
int K=100;
for (k in 1..N) {
   for (m in 1..M) {
     for (r in 1..K) {
   sm +=  k*m+8.9*k*m*r;
 sm /= 0.12*m*k*r*sm;
  }
 }
}
 def tmm = toc();  
 println(sm)
println(tmm)
 }

public static void main(String [] args) {
  c()
 }
}