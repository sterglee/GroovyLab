# Introduction #

`LAPACK/JLAPACK are powerful linear algebra packages. However, their routines have a complex and difficult to use interface. GroovyLab offers a simplified interface to some of the most useful routines. We describe here some examples. `


## `Script for testing the performance of different solvers of linear equations` ##

```
 N=1200
 A  = Rand(N, N)  // create double [][]
 B = Rand(N,1) 
     
// using JLAPACK routine 
tic()
x = groovySci.directLAPACK.DLapack.dgesv(A, B)
tm = toc()
A*x-B  // verify that indeed the correction solution is found

// using JAMA's LU
tic()
lux = LU_solve(A, B)
tmLU = toc()
A*lux-B  // verify that indeed the correction solution is found

// using JAMA's QR
tic()
qrx = QR_solve(A, B)
tmQR = toc() 
A*qrx-B  // verify that indeed the correction solution is found

// the default of JAMA's solver, which uses LUDecomposition for square matrices
// and QRDecomposition otherwise
tic()
solvex = solve(A, B)
tmSolve = toc()
A*solvex-B

// native BLAS      
tic()
jblassolvex = jblas_solve(A, B)
tmjblassolve = toc()
      

```

## `Solving a linear system ` ##
```
     
A  = Rand(9, 9)
B = Rand(9,1)
     
x = groovySci.directLAPACK.DLapack.dgesv(A, B)
B

shouldBeZero = A*x-B  // verify

```

## Performing an SVD ##

```

N = 20;  M = 10
a = rand(N, M)
svdresults = groovySci.directLAPACK.DLapack.dgesvd(a)

U = svdresults[0]
S = svdresults[1]
V = svdresults[2]

Sd = diag(S)

shouldBeZero = U*Sd*t(V)-a

```

## `Working with the MTJ library in GroovyLab` ##

`The MTJ library is a well designed Java high-level object oriented wrapper around some of the functionality of JLAPACK. In this tutorial we present some examples of using MTJ from GroovyLab with a user-friendly scripting interface. `

`The MTJ library is integrated in the default libraries of GroovyLab, therefore any separate installation is unecessary.`

## Creating a lower-symmetrical Dense Matrix ##

```

n = 5  // size of the matrix

A = new no.uib.cipr.matrix.LowerSymmDenseMatrix(n)
Ad = no.uib.cipr.matrix.Utilities.populate(A)
no.uib.cipr.matrix.Utilities.lowerSymmetrice(Ad)   // create a lower symmetric matrix


```

## `Script for testing the Performance of JLAPACK solver with code adapted from NR` ##

`The following script uses both JLAPACK and code adapted from the Numerical Recipes book, to solve a linear system. We observe that JLAPACK outperforms for large systems.`

```

     N = 1500
     A  = Rand(N, N)
     B = Rand(N,1)

     tic()
     x = groovySci.directLAPACK.DLapack.dgesv(A, B)
     tmLAPACK = toc()
     B

     shouldBeZero = A*x-B 

	tic()
     nx = groovySci.NR.LU.solveNR(A, B)
     tmNR = toc()

     shouldBeZeroNR = A*x-B 

```