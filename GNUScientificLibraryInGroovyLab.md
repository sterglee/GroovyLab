# Introduction #

`GroovyLab utilizes the project:`

https://github.com/bytedeco/javacpp-presets/tree/master/gsl

`in order to provide MATLAB-like access to the powerful GNU scientific library.`

`We provide some examples.`

## `Example 1` ##


```
  x = 5.6

  y = gsl_sf_bessel_J1(x)  // use the GSL library

```

## `Example 2 ` ##
```

lda = 3
  
 A = [0.11f, 0.12f, 0.13f, 0.21f, 0.22f, 0.23f ] as float []

  ldb = 2
  
  B = [1011, 1012, 1021, 1022,1031, 1032 ] as float []

  ldc = 2

  C = [ 0.00f, 0.00f, 0.00f, 0.00f ] as float []

  /* Compute C = A B */

  cblas_sgemm (CblasRowMajor, 
               CblasNoTrans, CblasNoTrans, 2, 2, 3,
               1.0f, A, lda, B, ldb, 0.0f, C, ldc)

println("multiplication results, C[0,0] = "+C[0]+" C[0,1] = "+C[1]+" C[1, 0] = "+C[2]+", C[1, 1] = "+C[3])
```


## `Example 3 ` ##
`Matrix Multiplication using CBLAS compared to the native GroovyLab multiplication.`

`GroovyLab here finishes much faster, since it combines Native BLAS with Java multithreading. `

```

 M = 2200
 N = 2400
 K = 2500
  
  
  A = Ones(M*N)
  lda = N
  
  B = Ones(N*K)
  ldb = K
  
  C = Ones(M*K)
  ldc = K
  /* Compute C = A B */

tic()
  cblas_dgemm (CblasRowMajor, 
               CblasNoTrans, CblasNoTrans, M, K, N,
               1.0, A, lda, B, ldb, 0.0, C, ldc)
tm=toc()   // time for CBLAS


// compare with native GroovyLab matrix multiplication
 Aj = Ones(M,N)
 Bj = Ones(N, K)

tic()
Cj = Aj*Bj
tmj = toc()

```


## `Example 4 - Wavelet transformation` ##

```
 N = 256
 nc = 20
 data = new double[N]

 
   w = gsl_wavelet_alloc (gsl_wavelet_daubechies(), 4)
   work = gsl_wavelet_workspace_alloc (N)

gg = new org.bytedeco.javacpp.gsl()


for (k in 0..N-1) 
    data[k]= sin(0.78*k)
    
    
    figure(1); subplot(2,1,1); plot(data, "Original Data")
    
  gsl_wavelet_transform_forward (w, data, 1, N, work)
 
 subplot(2,1,2); plot(data, "Wavelet Transformed Data")

```


## `Multiplication using GSL matrices` ##

```
import org.bytedeco.javacpp.DoublePointer

// dimensions of matrices
 M = 2000
 N = 1900
 K = 2100

// create sample arrays of 1s as matrices
  a = Ones(M*N)
  xa = new DoublePointer(a)

   b = Ones(N*K)
   xb = new DoublePointer(b)

   c = Ones(M*K)
   xc = new DoublePointer(c)
  
  
   A = gsl_matrix_view_array(xa, M, N)
   B = gsl_matrix_view_array(xb, N, K)
   C = gsl_matrix_view_array(xc, M, K)

  /* Compute C = A B */

  tic() 
  gsl_blas_dgemm (CblasNoTrans, CblasNoTrans,
                  1.0, A.matrix(), B.matrix(),
                  0.0, C.matrix())
   tmGLS = toc()                
      
      // get now the matrix
      
    Cs = new Matrix(M, K)
   
      for (rows  in 0..M-1 )
       for (cols  in  0..  K-1)
          Cs[rows, cols] =  C.matrix().data().get(rows*K+cols)
          

```

## `EigenValue Computation ` ##
```
import org.bytedeco.javacpp.DoublePointer

// dimensions of matrices
  M = 20

// create sample arrays of 1s as matrices
    a = Rand(M*M)

    xa = new DoublePointer(a)
  
  
   m = gsl_matrix_view_array(xa, M, M)
  
   eval = gsl_vector_complex_alloc(M)
   evec = gsl_matrix_complex_alloc(M, M)
  
   w = gsl_eigen_nonsymmv_alloc(M)
  
  gsl_eigen_nonsymmv(m.matrix(), eval, evec, w)
  
  gsl_eigen_nonsymmv_free(w)
  
  gsl_eigen_nonsymmv_sort(eval, evec, GSL_EIGEN_SORT_ABS_DESC)
  
   evals = new Matrix(M, 2)
   evecsReal = new Matrix(M, M)
   evecsImag = new Matrix(M, M)
  
  for (i in 0..M-1) {
       eval_i = gsl_vector_complex_get( eval, i)
      
       evec_i = gsl_matrix_complex_column(evec, i)
      
      // get computed eigenvalues i
      evals[i, 0] = eval_i.dat().get(0)
      evals[i, 1] = eval_i.dat().get(1)
      
      // get computed eigenvector i
      for (j in 0..M-1) {
           z = gsl_vector_complex_get(evec_i.vector(), j)
          evecsReal[i, j] = z.dat().get(0)
          evecsImag[i, j] = z.dat().get(1)
          }
        }
          
  
        gsl_vector_complex_free(eval)
        gsl_matrix_complex_free(evec)
        
```

## `Eigenvalue computation directly from GroovyLab's Matrix` ##
```
 N=800
 x = rand(N,N)

tic()
xgsl = x.gsleig()  // compute eigenvalues using GSL
tmgsl = toc()


tic()
xjava = x.eig()  // compute eigenvalues using Java
tmjava = toc()

```

## `Solution of a linear system with LU-decomposition` ##

```

 N = 2000

 A = rand(N,N)

 b = vrand(N).getv()

// solve the system with GSL LU solver
tic()
x = A.gsllusolve(b)
 tmsolveGSLLU = toc()

// solve the system with Java
tic()
x2 = solve(A, b)
tmsolveJava = toc()

max(max(abs(x2-x)))  // should be zero or a very small number

```