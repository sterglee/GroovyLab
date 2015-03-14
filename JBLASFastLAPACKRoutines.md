# JBLAS fast routines #

`JBLAS offers some useful LAPACK routines that are highly optimized and therefore for large matrices can run significantly faster than Java ones. This routines can be utilized from GroovyLab either with double[][] arrays or with the Matrix type. These routines are provided with the class: ` **`groovySci.math.array.Matrix:`**

`We list below the relevant part.`


```


    //  Computes the eigenvalues of a general matrix.
public static ComplexDoubleMatrix jblas_eigenvalues(double [][]dM) {
    return org.jblas.Eigen.eigenvalues(new DoubleMatrix(dM));
}



public static ComplexDoubleMatrix jblas_eigenvalues(Matrix dM) {
     return jblas_eigenvalues(dM.getArray());
}


   //   Computes the eigenvalues and eigenvectors of a general matrix.
   //   returns an array of ComplexDoubleMatrix objects containing the eigenvectors
   //          stored as the columns of the first matrix, and the eigenvalues as the
   //         diagonal elements of the second matrix.
public static ComplexDoubleMatrix [] jblas_eigenvectors(double [][]dM) {
    return org.jblas.Eigen.eigenvectors(new DoubleMatrix(dM));
}

   
public static ComplexDoubleMatrix [] jblas_eigenvectors(Matrix dM) {
     return jblas_eigenvectors(dM.getArray());
}


//  Compute the eigenvalues for a symmetric matrix.
public static DoubleMatrix  jblas_symmetricEigenvalues(double [][]dM) {
    return org.jblas.Eigen.symmetricEigenvalues(new DoubleMatrix(dM));
}

public static DoubleMatrix  jblas_symmetricEigenvalues(Matrix dM) {
    return jblas_symmetricEigenvalues(dM.getArray());
}


//  Computes the eigenvalues and eigenvectors for a symmetric matrix.
//  returns an array of DoubleMatrix objects containing the eigenvectors
//         stored as the columns of the first matrix, and the eigenvalues as
//         diagonal elements of the second matrix.
public static DoubleMatrix []  jblas_symmetricEigenvectors(double [][]dM) {
    return org.jblas.Eigen.symmetricEigenvectors(new DoubleMatrix(dM));
}

public static DoubleMatrix  [] jblas_symmetricEigenvectors(Matrix dM) {
    return  jblas_symmetricEigenvectors(dM.getArray());
}

//  Computes generalized eigenvalues of the problem A x = L B x.
// @param A symmetric Matrix A. Only the upper triangle will be considered.
//  @param B symmetric Matrix B. Only the upper triangle will be considered.
//  @return a vector of eigenvalues L.
public static DoubleMatrix jblas_symmetricGeneralizedEigenvalues( double [][] A, double [][] B) {
    return org.jblas.Eigen.symmetricGeneralizedEigenvalues(new DoubleMatrix(A), new DoubleMatrix(B));
}

public static DoubleMatrix jblas_symmetricGeneralizedEigenvalues( Matrix A, Matrix B) {
    return jblas_symmetricGeneralizedEigenvalues(A.getArray(), B.getArray());
}


public static DoubleMatrix [] jblas_symmetricGeneralizedEigenvectors( double [][] A, double [][] B) {
    return org.jblas.Eigen.symmetricGeneralizedEigenvectors(new DoubleMatrix(A), new DoubleMatrix(B));
}

public static DoubleMatrix [] jblas_symmetricGeneralizedEigenvectors( Matrix A, Matrix B) {
    return jblas_symmetricGeneralizedEigenvectors(A.getArray(), B.getArray());
}

 /**
     * Compute Cholesky decomposition of A
     *
     * @param A symmetric, positive definite matrix (only upper half is used)
     * @return upper triangular matrix U such that  A = U' * U
     */
public static  DoubleMatrix  jblas_cholesky(double [][]A) {
  return org.jblas.Decompose.cholesky(new DoubleMatrix(A));
}

public static  DoubleMatrix  jblas_cholesky(Matrix A) {
  return jblas_cholesky(A.getArray());
}


    /**
     * Solve a general problem A x = L B x.
     *
     * @param A symmetric matrix A
     * @param B symmetric matrix B
     * @return an array of matrices of length two. The first one is an array of the eigenvectors X
     *         The second one is A vector containing the corresponding eigenvalues L.
     */

public static DoubleMatrix jblas_solve(double [][]A, double [][] B) {
    return org.jblas.Solve.solve(new DoubleMatrix(A),  new DoubleMatrix(B));
}

public static DoubleMatrix jblas_solve(Matrix A, Matrix B) {
    return jblas_solve(A.getArray(),  B.getArray());
}

public static DoubleMatrix jblas_solveSymmetric(double [][]A, double [][] B) {
    return org.jblas.Solve.solveSymmetric(new DoubleMatrix(A),  new DoubleMatrix(B));
}

public static DoubleMatrix jblas_solveSymmetric(Matrix A, Matrix B) {
    return jblas_solveSymmetric(A.getArray(),  B.getArray());
}


public static DoubleMatrix jblas_solvePositive(double [][]A, double [][] B) {
    return org.jblas.Solve.solvePositive(new DoubleMatrix(A),  new DoubleMatrix(B));
}

public static DoubleMatrix jblas_solvePositive(Matrix A, Matrix B) {
    return jblas_solvePositive(A.getArray(),  B.getArray());
}

/**
     * Compute a singular-value decomposition of A.
     *
     * @return A DoubleMatrix[3] array of U, S, V such that A = U * diag(S) * V'
     */
public static DoubleMatrix []  jblas_fullSVD( double [][]A) {
    return org.jblas.Singular.fullSVD(new DoubleMatrix(A));
}

public static DoubleMatrix []  jblas_fullSVD( Matrix  A) {
    return  jblas_fullSVD(A.getArray());
}


    /**
     * Compute a singular-value decomposition of A (sparse variant).
     * Sparse means that the matrices U and V are not square but
     * only have as many columns (or rows) as possible.
     * 
     * @param A
     * @return A DoubleMatrix[3] array of U, S, V such that A = U * diag(S) * V'
     */

public static DoubleMatrix []  jblas_sparseSVD( double [][]A) {
    return org.jblas.Singular.sparseSVD(new DoubleMatrix(A));
}

public static DoubleMatrix []  jblas_sparseSVD( Matrix  A) {
    return  jblas_sparseSVD(A.getArray());
}


public static ComplexDoubleMatrix []  jblas_sparseSVD( double [][]Areal, double [][] Aimag) {
    return org.jblas.Singular.sparseSVD(
            new ComplexDoubleMatrix(new DoubleMatrix(Areal),  new DoubleMatrix(Aimag)));
}

public static ComplexDoubleMatrix []  jblas_sparseSVD( Matrix Areal, Matrix Aimag) {
    return jblas_sparseSVD(Areal.getArray(), Aimag.getArray());
}


  /**
     * Compute the singular values of a matrix.
     *
     * @param A DoubleMatrix of dimension m * n
     * @return A min(m, n) vector of singular values.
     */
public static DoubleMatrix jblas_SPDValues(double [][]A) {
    return  org.jblas.Singular.SVDValues(new DoubleMatrix(A));
}

public static DoubleMatrix jblas_SPDValues(Matrix A) {
    return jblas_SPDValues(A.getArray());
}

public static DoubleMatrix jblas_SPDValues(double [][]Areal, double [][]Aimag) {
    return  org.jblas.Singular.SVDValues(
            new ComplexDoubleMatrix(new DoubleMatrix(Areal), new DoubleMatrix(Aimag)));
}

   /**
     * Compute the singular values of a complex matrix.
     *
     * @param Areal, Aimag : the real and imaginary components of a  ComplexDoubleMatrix of dimension m * n
     * @return A real-valued (!) min(m, n) vector of singular values.
     */

public static DoubleMatrix jblas_SPDValues(Matrix Areal, Matrix Aimag) {
    return jblas_SPDValues(Areal.getArray(), Aimag.getArray());
}
```

`An example of using them follows: `

```


A = M("3.4 5.6  -3.4; 0.45 0.545 -1.3; 5.3 5.9 -2.3")  // create a Matrix

// compute the eigenvalues using JBLAS 
JBLASeigs = jblas_eigenvalues(A)  

// compute the eigenvectors using JBLAS
JBLASeigsevecs = jblas_eigenvectors(A)

// create a (large) symmetric matrix
N = 200
Mdd = new double[N][N]
for (r in 0..N-1)
 for (c in 0..N-1) {
   denom = 1.0+r*c
   Mdd[r][c] = 1.0/ denom
}

// compute with LAPACK specific routine for symmetric matrices the eigenvalues
tic();  JBLASsym = jblas_symmetricEigenvectors(Mdd);  tmJBLASsym = toc()

// compute with LAPACK general routine
tic();  JBLASgen = jblas_eigenvectors(Mdd); tmJBLASgen = toc()



```