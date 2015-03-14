# Introduction #

`JBLAS is a very fast Java library with support from native code. It is included in Glab for cases in which we need to perform very fast matrix operations. We present some examples`


# Examples #

`A simple example of using JBLAS`

```
data =  [[1,  2,  3,   4,   5 ] , [6,  7,  8,   9,  10 ], [11, 12, 13, 14, 15]] as double[][]
                    
matrix = new DoubleMatrix(data)
 
vector = new DoubleMatrix([ 3, 3, 3, 3,3 ] as double [])
result = matrix.mmul(vector)
	
println(result.rows+"x"+result.columns+": "+result)
```

`Benchmarking matrix multiplications`

```
// sizes of matrices
N = 2000
M = 2500

// create JBLAS matrices of all ones
dm1 = DoubleMatrix.ones(N,M)
dm2 = DoubleMatrix.ones(M,N)

// perform and time JBLAS multiplication
tic()
dm3 = dm1.mmul(dm2)
tmJBLAS = toc()    // time for native multiplication

// create Glab matrices of all ones
m1 = ones(N, M)
m2 = ones(M, N)

// perform and time Java based multiplication for default Glab matrices
tic()
m3 = m1*m2  // Glab default Java multiplication
tmJava = toc()
```