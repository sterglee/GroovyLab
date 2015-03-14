# Introduction #

`EJML (http://code.google.com/p/efficient-java-matrix-library/) is a powerful, well designed Java library for linear algebra. Here, we extend the functionality of the GroovySci` _`Matrix`_ `class using EJML routines. These routines, sometimes replace the default routines, sometimes are new. `

`In some cases, we estimate and the obtained speed advantages. `


`The following script reprograms the metaclass of` _`Matrix`_ `to use EJML based implementations. `


```

// reimplement Matrix-Matrix  multiplication using EJML
groovySci.math.array.Matrix.metaClass.multiply = { 
   groovySci.math.array.Matrix m ->   // the input Matrix

 // transform the input matrix to the EJML representation
     dm =  Matrix2EJML(m)
 // transform the receiver to the EJML  representation
     dmthis = Matrix2EJML(delegate)
 // multiply using EJML
     mulRes = new org.ejml.data.DenseMatrix64F(dmthis.getNumRows(), dm.getNumCols())
    org.ejml.ops.CommonOps.mult(dmthis, dm, mulRes)

 // return back result as a Matrix
   EJML2Matrix(mulRes) 
}




/* Example: Comparing EJML and default multiplication: EJML is a little bit faster, even with the 
  conversions overhead!
 x = ones(1500)
tic()
y=x*x
tmEJML = toc()
xx = Ones(1500)
tic()
yy = xx*xx
tmDefault = toc()

*/

// reimplement Matrix+Matrix  addition using EJML. It does not gain a speed advantage
groovySci.math.array.Matrix.metaClass.plus = { 
   groovySci.math.array.Matrix m ->   // the input Matrix

 // transform the input matrix to the EJML DenseMatrix64F representation
     dm =  Matrix2EJML(m)
 // transform the receiver to the EJML  DenseMatrix64F  representation
     dmthis = Matrix2EJML(delegate)
 // multiply using EJML
     plusRes = new org.ejml.data.DenseMatrix64F(dmthis.getNumRows(), dmthis.getNumCols())
    org.ejml.ops.CommonOps.add(dmthis, dm, plusRes)

 // return back result as a Matrix
   EJML2Matrix(plusRes) 
}


// reimplement determinant using EJML - Similar execution time with JAMA
groovySci.math.array.Matrix.metaClass.det = {
	-> 
	// transform the input matrix to the EJML DenseMatrix64F representation
	dmthis = Matrix2EJML(delegate)  
	org.ejml.ops.CommonOps.det(dmthis)   // compute and return the determinant 
}


// reimplement matrix invert using EJML - Faster than JAMA, EJML_time is about 0.7-0.8 * JAMA_time

groovySci.math.array.Matrix.metaClass.inv = {
	-> 
	// transform the input matrix to the EJML DenseMatrix64F representation
	dmthis = Matrix2EJML(delegate)  
	// create a Matrix to store the inverse
	invMat = new org.ejml.data.DenseMatrix64F(dmthis.getNumRows(), dmthis.getNumCols())
	
	org.ejml.ops.CommonOps.invert(dmthis, invMat)   // compute and return the inverse
	EJML2Matrix(invMat)   // return the inverse as a Matrix
}


// computes the Moore-Penrose pseudo-inverse using EJML
groovySci.math.array.Matrix.metaClass.pinv = {
 ->
 	// transform the input matrix to the EJML DenseMatrix64F representation
	dmthis = Matrix2EJML(delegate)  
	// create a Matrix to store the pseudo-inverse
	pinvMat = new org.ejml.data.DenseMatrix64F(dmthis.getNumCols(), dmthis.getNumRows())

     // use EJML to compute the pseudo-inverse
     org.ejml.ops.CommonOps.pinv(dmthis, pinvMat)

     EJML2Matrix(pinvMat)   // return the pseudo-inverse as a Matrix
}


```