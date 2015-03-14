# Introduction #

_`JBLAS`_ `is the fastest Java library (as far as I can know) for linear algebra. To obtain speed it utilizes native BLAS routines. The project page is: http://mikiobraun.github.io/jblas/`

`GroovyLab automatically installs the relevant staff, therefore it is easy to perform fast computations using JBLAS. We give some examples. `

## JBLAS operations supported with the Matrix class ##

`The ` **`Matrix`** ` class supports some JBLAS native operations, that are significantly faster than Java based implementations (e.g. about 3 to 7 times, depending on the operation). `

`Therefore we can use the fast JBLAS eigendecomposition for a ` **`Matrix`** ` object as:`

```
A = rand(20)
eigdecomp = A.jblas_eigenvectors()

```

The JBLAS routines are displayed below (is the relevant part of the ` *`Matrix`* `implementation:`


```

    //  Computes the eigenvalues of the matrix.using JBLAS
public ComplexDoubleMatrix jblas_eigenvalues() {
    DoubleMatrix dM = new DoubleMatrix(this.getArray());
    return org.jblas.Eigen.eigenvalues(dM);
}

 
   //   Computes the eigenvalues and eigenvectors of a general matrix.
   //   returns an array of ComplexDoubleMatrix objects containing the eigenvectors
   //          stored as the columns of the first matrix, and the eigenvalues as the
   //         diagonal elements of the second matrix.
public  ComplexDoubleMatrix[]  jblas_eigenvectors() {
    DoubleMatrix dM = new DoubleMatrix(this.getArray());
    return org.jblas.Eigen.eigenvectors(dM);
}
     
//  Compute the eigenvalues for a symmetric matrix.
public  DoubleMatrix  jblas_symmetricEigenvalues() {
    DoubleMatrix dM = new DoubleMatrix(this.getArray());
    return org.jblas.Eigen.symmetricEigenvalues(dM);
}


//  Computes the eigenvalues and eigenvectors for a symmetric matrix.
//  returns an array of DoubleMatrix objects containing the eigenvectors
//         stored as the columns of the first matrix, and the eigenvalues as
//         diagonal elements of the second matrix.
public  DoubleMatrix []  jblas_symmetricEigenvectors() {
    DoubleMatrix dM = new DoubleMatrix(this.getArray());
    return org.jblas.Eigen.symmetricEigenvectors(dM);
}

//  Computes generalized eigenvalues of the problem A x = L B x.
// @param A symmetric Matrix A (A is this). Only the upper triangle will be considered.
//  @param B symmetric Matrix B. Only the upper triangle will be considered.
//  @return a vector of eigenvalues L.
    
public  DoubleMatrix jblas_symmetricGeneralizedEigenvalues( double [][] B) {
    return org.jblas.Eigen.symmetricGeneralizedEigenvalues(new DoubleMatrix(this.getArray()), new DoubleMatrix(B));
}

    /**
     * Solve a general problem A x = L B x.
     *
     * @param A symmetric matrix A ( A is this )
     * @param B symmetric matrix B
     * @return an array of matrices of length two. The first one is an array of the eigenvectors X
     *         The second one is A vector containing the corresponding eigenvalues L.
     */
public DoubleMatrix [] jblas_symmetricGeneralizedEigenvectors( double [][] B) {
    return org.jblas.Eigen.symmetricGeneralizedEigenvectors(new DoubleMatrix(this.getArray()), new DoubleMatrix(B));
}

    /**
     * Compute Cholesky decomposition of A
     *
     * @param A symmetric, positive definite matrix (only upper half is used)
     * @return upper triangular matrix U such that  A = U' * U
     */
public DoubleMatrix  jblas_cholesky() {
  return org.jblas.Decompose.cholesky(new DoubleMatrix(this.getArray()));
}

/** Solves the linear equation A*X = B.  ( A is this) */
public DoubleMatrix jblas_solve( double [][] B) {
    return org.jblas.Solve.solve(new DoubleMatrix(this.getArray()),  new DoubleMatrix(B));
}

/** Solves the linear equation A*X = B for symmetric A.  ( A is this) */
public DoubleMatrix jblas_solveSymmetric(double [][] B) {
    return org.jblas.Solve.solveSymmetric(new DoubleMatrix(this.getArray()),  new DoubleMatrix(B));
}

/** Solves the linear equation A*X = B for symmetric and positive definite A. ( A is this ) */
public DoubleMatrix jblas_solvePositive(double [][] B) {
    return org.jblas.Solve.solvePositive(new DoubleMatrix(this.getArray()),  new DoubleMatrix(B));
}

 /**
     * Compute a singular-value decomposition of A. ( A is this )
     *
     * @return A DoubleMatrix[3] array of U, S, V such that A = U * diag(S) * V'
     */

public DoubleMatrix []  jblas_fullSVD( ) {
    return org.jblas.Singular.fullSVD(new DoubleMatrix(this.getArray()));
}


    /**
     * Compute a singular-value decomposition of A (sparse variant) (A is this)
     * Sparse means that the matrices U and V are not square but
     * only have as many columns (or rows) as possible.
     * 
     * @param A
     * @return A DoubleMatrix[3] array of U, S, V such that A = U * diag(S) * V'
     */

public  DoubleMatrix []  jblas_sparseSVD( ) {
    return org.jblas.Singular.sparseSVD(new DoubleMatrix(this.getArray()));
}


public ComplexDoubleMatrix []  jblas_sparseSVD( double [][] Aimag) {
    return org.jblas.Singular.sparseSVD(
            new ComplexDoubleMatrix(new DoubleMatrix(this.getArray()),  new DoubleMatrix(Aimag)));
}


  /**
     * Compute the singular values of a matrix.
     *
     * @param A DoubleMatrix of dimension m * n
     * @return A min(m, n) vector of singular values.
     */

public  DoubleMatrix jblas_SPDValues() {
    return  org.jblas.Singular.SVDValues(new DoubleMatrix(this.getArray()));
}

    /**
     * Compute the singular values of a complex matrix.
     *
     * @param Areal, Aimag : the real and imaginary components of a  ComplexDoubleMatrix of dimension m * n
     * @return A real-valued (!) min(m, n) vector of singular values.
     */

public  DoubleMatrix  jblas_SPDValues( double [][]Aimag) {
    return  org.jblas.Singular.SVDValues(
            new ComplexDoubleMatrix(new DoubleMatrix(this.getArray()), new DoubleMatrix(Aimag)));
}


  
```


## Matrix multiplication ##

```


// Demonstrate the difference in performance beteen Native BLAS and Java
// for matrix multiplication

 import  org.jblas.*

 n = 1000
 
 x = DoubleMatrix.randn(n, n)
 y = DoubleMatrix.randn(n, n)
 z = DoubleMatrix.randn(n, n)        

 println("Multiplying DOUBLE matrices of size "+ n)

 tic()
 SimpleBlas.gemm(1.0, x, y, 0.0, z)
 tm = toc()

// test with Java multiplication
 xm = rand(n)
 tic()
 xmxm =xm*xm
 tmJ = toc()
        
 println("Time Native = "+tm+", time Java = "+tmJ)

```

## `Switching of the GroovySci Matrix class to use JBLAS` ##

`We can explore the metaprogramming facilities of the Groovy language, in order to dynamically bind the code of many important operations of the` **`Matrix`** `class, with the JBLAS implementations.`

`Therefore, using native implementations offered by JBLAS, we can obtain significant speedup relative to the Java implementations (about 4 to 8 times speedup). `


`For example, here is how we can reprogram the ` _`matrix multiplication`_ `routine of the ` _`Matrix`_ `class, in order to use JBLAS .`

```
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

```

`After executing the former script, we can test the speedup. Here, we note that JBLAS obtains significant speed for Linux and Win32 platforms (and MacOS I suppose but I can't test) but not for Win64, where the speed is similar to the Java implementation. Here is a test code: `

```

x = rand(2000, 2000) // a large 2000X2000 matrix  

tic()
y = x*x  // multiply with JBLAS Native BLAS
tmJBLAS = toc()

xx=Rand(2000, 2000)  // a large 2000X2000 double[][] array
tic()
yy=xx*xx  // multiply with Java
tmJ = toc()

println("time for matrix multiplication using Native BLAS = "+tmJBLAS+", time with Java = "+tmJ)
```

`The results for my Linux based PC is: `
```
time for matrix multiplication using Native BLAS = 1.956, time with Java = 10.575
```

## Eigendecomposition with JBLAS ##

`Similarly we can utilize JBLAS for more complex tasks as eigendecomposition:`

```

// compute the eigenvalues of a general matrix using JBLAS
groovySci.math.array.Matrix.metaClass.jblasEigenValues = {
	// transform the receiver to the JBLAS representation
	dmthis = new org.jblas.DoubleMatrix(delegate.toDoubleArray())
	org.jblas.Eigen.eigenvalues(dmthis)
 }

```

`To test our routine: `
```
xs = rand(4,4)
xeigs = xs.jblasEigenValues()
```

`Similarly for eigenvectors: `

```

// compute the eigenvectors of a general matrix using JBLAS
//   returns an array of ComplexDoubleMatrix objects containing the eigenvectors
//          stored as the columns of the first matrix, and the eigenvalues as the
//         diagonal elements of the second matrix.
groovySci.math.array.Matrix.metaClass.jblasEigenVectors = {
	// transform the receiver to the JBLAS representation
	dmthis = new org.jblas.DoubleMatrix(delegate.toDoubleArray())
	org.jblas.Eigen.eigenvectors(dmthis)
}

```

`And, we can test: `
```
xs2 = rand(3,3)
xeigvecs = xs2.jblasEigenVectors()

```

`Similarly for symmetric matrices. `

```

     
//  Compute the eigenvalues for a symmetric matrix.
groovySci.math.array.Matrix.metaClass.jblas_symmetricEigenvalues = {
	// transform the receiver to the JBLAS representation
	dmthis = new org.jblas.DoubleMatrix(delegate.toDoubleArray())
	org.jblas.Eigen.symmetricEigenvalues(dmthis)
}

//  Computes the eigenvalues and eigenvectors for a symmetric matrix.
//  returns an array of DoubleMatrix objects containing the eigenvectors
//         stored as the columns of the first matrix, and the eigenvalues as
//         diagonal elements of the second matrix.
groovySci.math.array.Matrix.metaClass.jblas_symmetricEigenvectors = {
	// transform the receiver to the JBLAS representation
	dmthis = new org.jblas.DoubleMatrix(delegate.toDoubleArray())
	org.jblas.Eigen.symmetricEigenvectors(dmthis)
}

```

`JBLAS also offers a routine for fast Cholesky Decomposition, which can be wrapped as: `
```

//  Compute Cholesky decomposition of A
//      @param A symmetric, positive definite matrix (only upper half is used)
//      @return upper triangular matrix U such that  A = U' * U
 groovySci.math.array.Matrix.metaClass.jblas_cholesky = {
 	// transform the receiver to the JBLAS representation
	dmthis = new org.jblas.DoubleMatrix(delegate.toDoubleArray())
	org.jblas.Decompose.cholesky(dmthis)    
 }
```

## Solution of Linear Systems with JBLAS ##

`Similarly we can use the fast LAPACK based routines for solving linear systems: `

```

/** Solves the linear equation A*X = B. */
groovySci.math.array.Matrix.metaClass.jblas_solve = {
  groovySci.math.array.Matrix B ->   // input is the B Matrix, the receiver is A

	// transform the input Matrix B to the JBLAS representation
	Bm =  new org.jblas.DoubleMatrix(B.toDoubleArray())
	// transform the receiver to the JBLAS representation
	dmthis = new org.jblas.DoubleMatrix(delegate.toDoubleArray())
	// return the solution as a convenient GroovySci Matrix
	groovySci.math.array.JBLASUtils.JBLASDoubleMatrixToDouble2D(org.jblas.Solve.solve(dmthis, Bm))
	// groovySci.math.array.JBLASUtils.JBLASDoubleMatrixToMatrix(org.jblas.Solve.solve(dmthis, Bm))  // similar
}

```
`and an example: `

```

 A = rand(3,3)
 B = rand(3,3)
 x = A.jblas_solve(B)
 shouldBeZero = A*x-B

```