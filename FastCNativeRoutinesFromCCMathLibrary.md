# Introduction #

`The CCMath library is a fast C library. Although Java code is generally very fast, some routines can run faster with native C code, if we perform an intense optimization of  that code. `

`We present some examples of optimized routines`

**`The interface of CCMath is in development`**

## SVD decomposition ##


```

 ccObj = gExec.Interpreter.NativeLibsObj.ccObj
 M = 300
 N = 300
 d = Zeros(N)
 MN=M*N
 a = Rand(MN)
 aa = a.clone()
 u = Zeros(M*M)
 v = Zeros(N*N)
 tic()
 ccObj.svduv(d, a, u, M, v, N)
 tm = toc()

```

`Using a higher level interface`


```
 N = 1100;  M = 1000

 xx = rand(N,M)

// use C svd
tic()
csvd = ccsvd(xx)
tmc = toc()

// take parameters from csvd
 U = csvd.U
 W = diag(csvd.W)  // the singular values
 V = csvd.V

 isIdentity = U* t(U)  // check orthogonality, should be the identity matrix

// use Java svd
tic()
jsvd = svd(xx)
tmj=toc()
```

`At the case above I obtained, 36.51 secs for the optimized C case, and 60.73 secs for the Java version, thus there is some benefit.`


## Inverse Operation ##
`Also, the matrix inverse operation, runs slightly faster in optimized C compared to Java. Without full optimization of C code, Java outperforms C.`

```
// test the C inverse operation
 N = 1200

// test for Matrix
 x = rand(N,N)
 xx = x.clone()

tic()
y = ccinv(x)
tmc = toc()



tic()
yj = inv(xx)
tmj = toc()


```

`Here, optimized C performs slightly faster than Java, e.g. 1.767 secs vs 2.086 secs on my computer. `

## Square Linear Systems Solution ##

`Here,optimized C performs faster from Java. `

```


N = 2000
A = Rand(N, N)
b = Rand(N)

// solve with C routine
tic()
x = ccsolv(A, b)
tmc = toc()

Aj = rand(N, N)
bj = rand(N)

tic()
xj = solve(Aj, bj)
tmj = toc()

```


## FFT ##

`FFT in GroovyLab perfoms faster with the Java based implementation. However, the Java and C implementations compared are different, therefore we cannot conclude from these results about the quality of code. `

```
 x = inc(0, 0.01, 500)
  N = x.length()

 y = 0.67*cos(0.123*x)+0.34*sin(0.0345*x)+Rand(1, N)


plot(x, y)

 xx = gExec.Interpreter.NativeLibsObj.ccObj

reCoeff = new double[N]
imCoeff = new double[N]

tic()
xx.ccfft(y[0],  reCoeff, imCoeff, N)
tmc = toc()

tic()
jfft = fft(y[0])
tmj = toc()


figure(1)
subplot(2,1,1); plot(reCoeff,"C FFT")
subplot(2,1,2); plot(jfft.realFFTs,"Java FFT")

```