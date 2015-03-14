# Introduction #

`The ` **`expandRunTime.expandGroovy.groovy`**  `module is very important: it offers much of the Matlab-like characteristics of GroovyLab.  It expands the functionality of the standard Java numeric types using MetaClass based Groovy programming. Clearly, these operations are performed using dynamic dispatch, therefore they have some performance overhead. For that reason, GroovyLab delegates to pure Java implementations to perform the numeric number crunching, therefore the overall speed of GroovyLab is kept very near the full Java speed. The operations offered by expandRunTime.expandGroovy.groovy are generally self-explanatory. At the time of writing these operations are listed below:`



# The expandRunTime.expandGroovy.groovy module #

```
package expandRunTime;

import static groovySci.math.array.BasicDSP.*; 
 import groovySci.math.array.Matrix;   // Matrix class
import static groovySci.math.array.Matrix.*;    
import groovySci.math.array.Vec;   // Vec class
import static groovySci.math.array.Vec.*;    
import numal.*;   // numerical analysis library routines
import static groovySci.math.plot.plot.*;     // plotting routines 
import static java.lang.Math.*;     // standard Java math routines, allows calling directly e.g sin(9.8) instead of Math.sin(9.8)
import java.awt.*; 
 import javax.swing.*;   // Java standard UI and graphics support
import static groovySci.math.io.MatIO.*;    // support for .mat Matlab files
import groovy.swing.SwingBuilder; 
import java.awt.event.*; 
import JSci.awt.*;
import org.antlr.runtime.*; 
import java.text.DecimalFormat; 
import static groovySciCommands.BasicCommands.*;   // support for GroovySci's console commands
import  static  groovySci.math.array.DoubleArray.*;
import  org.ejml.simple.SimpleMatrix;


// treat Number*String as concatenation of the String Number times
Number.metaClass.multiply = {
   String s ->
     scopy = s;
     for (k in 1..delegate)
       s = s+scopy;
   s
}

// this script expands the basic Groovy operators

//   Number  +  Matrix
Number.metaClass.plus = { 
   Matrix m ->   // the input Matrix
    ma = m.plus(delegate);   // perform Matrix + Number instead
   ma   // return value
}


//   Number  +  Vector
Number.metaClass.plus = { 
   Vec m ->   // the input Vector
    ma = m.plus(delegate);   // perform Vec + Number instead
   ma   // return value
}

//   Number - Matrix
Number.metaClass.minus = {
  Matrix m -> 
    mm = m.multiply(-1); 
    mm = mm.plus(delegate);  
  mm 
}


//   Number  -  Vector
Number.metaClass.minus= { 
   Vec m ->   // the input Vector
    mm = m.multiply(-1) 
    mm = mm.plus(delegate);   // perform Vec + Number instead
   mm   // return value
}

//   Number * Matrix
Number.metaClass.multiply = { 
  Matrix m -> 
    mm = m.multiply(delegate);
  mm 
}


//   Number * Vec
Number.metaClass.multiply = { 
  Vec m -> 
    mm = m.multiply(delegate);
  mm 
}

//   Number * Vec
Number.metaClass.div= { 
  Vec m -> 
    mm = m.multiply(1/delegate);
  mm 
}


//  Number / Matrix
Number.metaClass.div = 
   { 
  Matrix m -> 
      N = m.size()[0]; M = m.size()[1];  // get size of matrix m
      if (N != M) return null;
      mm = new Matrix(N, M, (double) delegate); // create a new Matrix with all its elements equal to the Number
      im = groovySci.math.array.Matrix.inv(m);    
      mm = im * delegate; 
   mm
   }




////////////////////////////////////////////////////////////////////////////////////
// double [] OPERATIONS

// multiply Number * double []
Number.metaClass.multiply = 
  {  double []  m -> 
  res = Jama.LinearAlgebra.times( delegate,  m)
  
        res 
}   

// multiply  double [] * Number
double [].metaClass.multiply = 
  {  Number  num -> 
  res = Jama.LinearAlgebra.times( delegate, num)

      res 
}   
        
        

//  Number + double []
Number.metaClass.plus = 
    {  double []  m -> 
  res = Jama.LinearAlgebra.plus( delegate,  m)
  
        res 
}   

// double [] + Number
double [].metaClass.plus = 
  {  Number  num -> 
  res = Jama.LinearAlgebra.plus( delegate, num)

      res 
}   
     

double [].metaClass.toString = 
 { ->
"double["+delegate.length+"]  =\n"+
 groovySci.math.array.DoubleArray.toString(delegate)
}

double [][].metaClass.toString = 
 { ->
"double["+delegate.length+"]["+delegate[0].length+"] =\n"+
 groovySci.math.array.DoubleArray.toString(delegate)
}

//  Number - double []
Number.metaClass.minus = 
{  double []  m -> 
  res = Jama.LinearAlgebra.minus( delegate,  m)
  
        res 
}   

// double [] - Number
double [].metaClass.minus = 
  {  Number  num -> 
  res = Jama.LinearAlgebra.minus( delegate, num)

      res 
}
// double [] + double []     
double[].metaClass.plus = 
{  double []  m -> 
  res = Jama.LinearAlgebra.plus( delegate,  m)
  
        res 
}   

// double [] - double[]
double[].metaClass.minus = 
{  double []  m -> 
  res = Jama.LinearAlgebra.minus( delegate,  m)
  
        res 
}   



// Number + double [][]
Number.metaClass.plus = 
  { 
 double [][]  m -> 
         res = Jama.LinearAlgebra.plus( delegate, m)
     res 
}   

double [][].metaClass.negative ={
   -> 
  double [][] a = delegate
  double [][] b = new double[a.length][a[0].length]
  for (int r=0; r<a.length; r++)
    for (int c=0; c<a[0].length; c++)
       b[r][c] = -a[r][c]

 b
}

// double [][] + Number
double[][].metaClass.plus = 
  {
  Number  num -> 
  res = Jama.LinearAlgebra.times(delegate, num)
     res 
}   

 // double [][] + Matrix
 double[][].metaClass.plus = 
  {
  groovySci.math.array.Matrix mat -> 
  res = groovySci.math.array.Matrix.plus(delegate, mat)
     res 
}

// double [][] - Matrix
 double[][].metaClass.minus = 
  {
  groovySci.math.array.Matrix mat -> 
  res = groovySci.math.array.Matrix.minus(delegate, mat)
     res 
}

// Number - double [][]
Number.metaClass.minus = 
  { 
 double [][]  m ->   
  res = Jama.LinearAlgebra.minus( delegate, m)
     res 
}   


// double [][] - Number
double[][].metaClass.minus = 
  { 
 Number  num -> 
  res = Jama.LinearAlgebra.minus( delegate, num)
      res 
}   

// double[][] + double[][]
double[][].metaClass.plus = 
  {
   double [][] m->
   res = Jama.LinearAlgebra.plus( delegate, m)
  res
}
   
// double [][] - double [][]
double[][].metaClass.minus = 
  { 
 double [][] m -> 
        res = Subtract(delegate, m);
      res 
}   

// double [] * double[] as inner product
double[].metaClass.multiply = {double [] m -> 
       res = Jama.LinearAlgebra.times( delegate,  m)
  
        res 
}   
   
// double [] + double[] 
double[].metaClass.plus = {double [] m -> 
       double [] res = new double[m.length]
       for (int k=0; k<m.length; k++)
          res[k] = m[k]+delegate[k]
        res 
}   
   
// double [] - double[] 
double[].metaClass.minus = {double [] m -> 
       double [] res = new double[m.length]
       for (int k=0; k<m.length; k++)
          res[k] = delegate[k]-m[k]
        res 
}   

// double [][] + double[]  : perform column-wise sum
double[][].metaClass.plus = {double [] m -> 
   groovySci.math.array.DoubleArray.Add(delegate, m)
}   
   
// double [][] - double[]  : perform column-wise subtraction
double[][].metaClass.minus = {double [] m -> 
       groovySci.math.array.DoubleArray.Subtract(delegate, m)
}   

  
// double [][] * double[]  : multiply each row by the input param column
double[][].metaClass.multiply = {double [] m -> 
      groovySci.math.array.DoubleArray.Multiply(delegate, m)
}   

// double [][] / double [] as element-wise division
double[][].metaClass.div = {
    double [] dm -> 
  res = Jama.LinearAlgebra.divide( delegate,  dm)
  
        res 
}   
	
// double [] / double [] as element-wise division
double[].metaClass.div = {
    double [] dm -> 
  res = Jama.LinearAlgebra.divide( delegate,  dm)
  
        res 
}   

//////////////////////////////////////////////////////////////////////////
// double [][] OPERATIONS

// multiply Number * double [][]
Number.metaClass.multiply = 
  { 
 double [][]  m -> 
   res = Jama.LinearAlgebra.times( delegate,  m)
  
        res 
}   

// multiply  double [][] * Number
double[][].metaClass.multiply = 
  {  
Number  num -> 
  res = Jama.LinearAlgebra.times( num, delegate)
     res 
}   

// multiply  double [][] * Matrix
double[][].metaClass.multiply = 
  {  
groovySci.math.array.Matrix   mat -> 
  res = Jama.LinearAlgebra.times( mat, delegate)
     res 
}   

// double [] / Number
double [].metaClass.div = 
  {
  Number  num -> 
  res = Jama.LinearAlgebra.times( delegate, 1.0/num)
     res 
}   

// divide double [][] / Number
double[][].metaClass.div = 
  { 
 Number  num -> 
  res = Jama.LinearAlgebra.times( delegate, 1.0/num)
     res 
}   


// double [][] * double [][]
double [][].metaClass.multiply = 
  { 
double [][] v2 ->
   res = Jama.LinearAlgebra.times( delegate,  v2)
     
 res
 }

// double [][] * Matrix
double [][].metaClass.multiply = 
  { 
groovySci.math.array.Matrix v2 ->
   res = Jama.LinearAlgebra.times( delegate,  v2.darray)
     
 res
 }

double [].metaClass.eachValue =
      {
 groovy.lang.Closure c ->
     rows = delegate.size();  
     for (int i = 0; i < rows; i++)
        delegate[i] =  c.call(delegate[i]);
	
     delegate
}

double [][].metaClass.eachValue =
      {
 groovy.lang.Closure c ->
     rows = delegate.size();  cols = delegate[0].size();     
     for (int i = 0; i < rows; i++)
      for (int j=0; j < cols; j++)
	   delegate[i][j] =  c.call(delegate[i][j]);
	
     delegate
}

double [][].metaClass.map =
      {
 groovy.lang.Closure c ->
     rows = delegate.size();  cols = delegate[0].size();     
     for (int i = 0; i < rows; i++)
      for (int j=0; j < cols; j++)
	   delegate[i][j] =  c.call(delegate[i][j]);
	
     delegate
}

double  [].metaClass.each =
      {
 groovy.lang.Closure c ->
     rows = delegate.size();  
     for (int i = 0; i < rows; i++)
        delegate[i] =  c.call(delegate[i]);
	
     delegate
}

double  [].metaClass.map =
      {
 groovy.lang.Closure c ->
     rows = delegate.size();  
     for (int i = 0; i < rows; i++)
        delegate[i] =  c.call(delegate[i]);
	
     delegate
}

double  [][].metaClass.each =
      {
 groovy.lang.Closure c ->
     rows = delegate.size();  cols = delegate[0].size();     
     for (int i = 0; i < rows; i++)
      for (int j=0; j < cols; j++)
	   delegate[i][j] =  c.call(delegate[i][j]);
	
     delegate
}


double [].metaClass.clone =  {
         ->
      n = delegate.length
      darray = new double[n];
      for (int i = 0; i < n; i++) 
            darray[i] = delegate[i]
         
    darray;
      }
   
double [].metaClass.copy =  {
         ->
      n = delegate.length
      darray = new double[n];
      for (int i = 0; i < n; i++) 
            darray[i] = delegate[i]
         
    darray;
      }



double [][].metaClass.clone =  {
         ->
      n = delegate.length
      m = delegate[0].length
      darray = new double[n][m];
      for (int i = 0; i < n; i++) 
        for (int j=0; j < m; j++)
            darray[i][j] = delegate[i][j]
         
    darray;
      }
   
double [][].metaClass.copy =  {
         ->
      n = delegate.length
      m = delegate[0].length
      darray = new double[n][m];
      for (int i = 0; i < n; i++) 
        for (int j=0; j < m; j++)
            darray[i][j] = delegate[i][j]
         
    darray;
      }
   
// product of the elements of a vector
double [].metaClass.prod = {
   -> 
  prCol = groovySci.math.array.DoubleArray.product(delegate);        
          
     prCol;
}



//  columnwise product of the columns of double[][] array
double [][].metaClass.prod = {
   -> 
  prCol = groovySci.math.array.DoubleArray.product(delegate);        
          
     prCol;
}

// sum of the elements of a vector
double [].metaClass.sum = {
   -> 
  prCol = groovySci.math.array.DoubleArray.sum(delegate);        
          
     prCol;
}

//  columnwise sum of the columns of double[][] array
double [][].metaClass.sum = {
   -> 
  smCols = groovySci.math.array.DoubleArray.sum(delegate);        
          
     smCols;
}


double[][].metaClass.cumProduct = {
   ->
  res = groovySci.math.array.DoubleArray.cumProduct(delegate)
  println "cumProduct called "
res
}

double [][].metaClass.sort  = { 
  int columnIdx  ->
   res =  groovySci.math.array.DoubleArray.sort(delegate, columnIdx);
 res
}
   
double [][].metaClass.t = {
   ->
   res = groovySci.math.array.DoubleArray.transpose(delegate)
 res
}

double [].metaClass.power = {
   double exponent -> 
        res = Jama.LinearAlgebra.raise( delegate, exponent)
  res  
}

double [][].metaClass.power = {
   double exponent -> 
res = Jama.LinearAlgebra.raise( delegate, exponent)
  res  
}
         
double [][].metaClass.inverseLU = {
    ->
    res = Jama.LinearAlgebra.inverseLU( delegate)
  res  
}

double [][].metaClass.inverseQR = {
    ->
    res = Jama.LinearAlgebra.inverseQR( delegate)
  res  
}

double [][].metaClass.inv = {
    ->
    res = Jama.LinearAlgebra.inverse( delegate)
  res  
}

double [][].metaClass.inverse = {
    ->
    res = Jama.LinearAlgebra.inverse( delegate)
  res  
}

double [][].metaClass.inverse = {
      double [][] B    ->
    res = Jama.LinearAlgebra.inverse( delegate, B)
  res  
}

double [][].metaClass.eigen = {
          ->
    res = Jama.LinearAlgebra.eigen( delegate)
  res  
}


double [][].metaClass.eig = {
          ->
    res = Jama.LinearAlgebra.eigen( delegate)
  res  
}

double [][].metaClass.svd = {
          ->
    return LinearAlgebra.singular(delegate).getSingularValues();
    
}

double [][].metaClass.QR = {
          ->
    res = Jama.LinearAlgebra.QR( delegate)
  res  
}	


double [][].metaClass.qr = {
          ->
    res = Jama.LinearAlgebra.QR( delegate)
  res  
}	

double [][].metaClass.LU = {
          ->
    res = Jama.LinearAlgebra.LU( delegate)
  res  
}	

double [][].metaClass.lu = {
          ->
    res = Jama.LinearAlgebra.LU( delegate)
  res  
}	

double [][].metaClass.cholesky = {
          ->
    res = Jama.LinearAlgebra.cholesky( delegate)
  res  
}	

double [][].metaClass.singular = {
          ->
    res = Jama.LinearAlgebra.singular( delegate)
  res  
}	

double [][].metaClass.cond = {
          ->
    res = Jama.LinearAlgebra.cond( delegate)
  res  
}	

double [][].metaClass.det = {
          ->
    res = Jama.LinearAlgebra.det( delegate)
  res  
}	


double [][].metaClass.rank = {
          ->
    res = Jama.LinearAlgebra.rank( delegate)
  res  
}	

double [][].metaClass.trace = {
          ->
    res = Jama.LinearAlgebra.trace( delegate)
  res  
}	

double [][].metaClass.norm1 = {
          ->
    res = Jama.LinearAlgebra.norm1( delegate)
  res  
}	

double [][].metaClass.norm2 = {
          ->
    res = Jama.LinearAlgebra.norm2( delegate)
  res  
}	

double [][].metaClass.normF = {
          ->
    res = Jama.LinearAlgebra.normF( delegate)
  res  
}	

double [][].metaClass.normInf = {
          ->
    res = Jama.LinearAlgebra.normInf( delegate)
  res  
}	

// determinant
double [][].metaClass.det = {
     ->
res =  Jama.LinearAlgebra.det(delegate)
   res
}

// eigenvalues
double [][].metaClass.eig = {
  ->
 res = groovySci.math.array.Matrix.eig(delegate)
  res
}


// linear system solve
double [][].metaClass.solve = {
  double [][] c ->
         linsys = new DhbMatrixAlgebra.LinearEquations(delegate, c)
         res = linsys.solutions
  res
}

/*
  a =  [ [3,2,4], [2, -5, -1], [1, -2, 2]] as double[][]
  c =  [ [16, 6, 10], [7, 10, 9]] as double[][]

  sols = a.solve(c)
 */
     
double [][].metaClass.d = {
  int ndigits ->
 dv = delegate
 s="0."
 for (k in 1..ndigits) s+="0";
 DecimalFormat digitfmt = new DecimalFormat(s);
 digitfmt.format( dv[0][0])
  
}
     


```