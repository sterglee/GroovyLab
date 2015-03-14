# Introduction #

**`Numerical Recipes`** `is a classic text on Numerical Computation containing a lot of superb quality routines covering wide fields of numerical computing. Perhaps even more important,is that the book explains very well the algorithms on which the routines are based. Therefore, Numerical Recipes (NR) is excellent for students and researchers. Furthermore, generally NR routines are efficient, therefore they also fit for "production" applications. `

`Thanks to Huang Wen Hui that performed a translation to Java, and to the authors of the Numerical Recipes book we can utilize that routines as a standard GroovyLab library and explore them in a Matlab-like way from GroovyLab. This page aims to provide some examples in order to practice the NR routines. We provide the page numbers of the Numerical Recipes book, Edition 3, where the routines are described.`


## Laplace Interpolation (NR3, p. 150) ##

```

import static com.nr.NRUtil.*
import static com.nr.test.NRTestUtil.*
import static java.lang.Math.*

import com.nr.interp.Laplace_interp
import com.nr.ran.Ran


    N=100;     M=100;  NBAD=1000
    sbeps=0.01
    actual = new double[N][M]
    globalflag=false
    
    // Test Laplace_interp
    println("Laplace_interp")
    myran = new Ran(17)
    
    for (int i=0;i<N;i++)
      for (int j=0;j<M;j++)
        actual[i][j]=cos((double)(i)/20.0)*cos((double)(j)/20.0)
        
    mat = buildMatrix(actual)
    for (int i = 0; i < NBAD; i++)  {  // insert "missing" data
      p=myran.int32p()%N
      q=myran.int32p()%M
      mat[p][q]=1.0e99
    }
    printf("     Initial discrepancy: %g\n", maxel(matsub(actual,mat)))
    
    mylaplace = new Laplace_interp(mat)
    mylaplace.solve()
    
    printf("     Final discrepancy: %g\n", maxel(matsub(actual,mat)))
    localflag = maxel(matsub(actual,mat)) > sbeps
    globalflag = globalflag || localflag
    if (localflag) {
      println("*** Laplace_interp: Inaccurate Laplace interpolation of missing matrix data.")
    }

    if (globalflag)   println("Failed\n")
    else  println("Passed\n")



```



## 1-D FFT, four1 p. 612 ##

```

import static com.nr.NRUtil.buildVector
import static com.nr.fft.FFT.four1
import static com.nr.test.NRTestUtil.maxel   
import static com.nr.test.NRTestUtil.ranvec
import static com.nr.test.NRTestUtil.vecsub
import static java.lang.Math.acos
import static java.lang.Math.cos
import static java.lang.Math.sin

import com.nr.ran.Ran

    N=256
    sbeps=1.0e-14
    pi=acos(-1.0)
    data1 = new double[2*N]
    localflag=false
    globalflag=false
    

    // Test four1
    println("Testing four1")
    myran = new Ran(17)

    // Round-trip test for reals
    for (i=0;i<N;i++) {
      data1[2*i]=myran.doub()
      data1[2*i+1]=0.0
    }
    
    data2=buildVector(data1)
    for (i=0;i<2*N;i++) data2[i] /= N
    four1(data2,1)
    four1(data2,-1)
    localflag = localflag || maxel(vecsub(data1,data2)) > sbeps
    globalflag = globalflag || localflag
    if (localflag) {
      fail("*** four1: Round-trip test for random real values failed");
      
    }

    // Round-trip test for imaginaries
    for (i=0;i<N;i++) {
      data1[2*i]=0.0
      data1[2*i+1]=myran.doub()
    }
    for (i=0;i<2*N;i++) data2[i]=data1[i]/N
    four1(data2,1)
    four1(data2,-1)
    localflag = localflag || maxel(vecsub(data1,data2)) > sbeps
    globalflag = globalflag || localflag
    if (localflag) {
      fail("*** four1: Round-trip test for random imaginary values failed");
      
    }

    // Round-trip test for complex numbers
    ranvec(data1)
    for (i=0;i<2*N;i++) data2[i]=data1[i]/N
    four1(data2,1)
    four1(data2,-1)
    localflag = localflag || maxel(vecsub(data1,data2)) > sbeps
    globalflag = globalflag || localflag
    if (localflag) {
      fail("*** four1: Round-trip test for random complex values failed");
      
    }

    // Test delta-function in to sine-wave out, forward transform
    for (i=0;i<2*N;i++) data1[i]=0.0;
    data1[2*5]=1.0;
    four1(data1,1);
    for (i=0;i<N;i++) {
      data2[2*i]=cos(2.0*pi*5*i/N);
      data2[2*i+1]=sin(2.0*pi*5*i/N);
    }
    localflag = localflag || maxel(vecsub(data1,data2)) > sbeps;
    globalflag = globalflag || localflag;
    if (localflag) {
      fail("*** four1: Forward transform of a chosen delta function did not give expected result");
      
    }

    // Test delta-function in to sine-wave out, backward transform
    for (i=0;i<2*N;i++) data1[i]=0.0
    data1[2*7]=1.0
    four1(data1,-1)
    for (i=0;i<N;i++) {
      data2[2*i]=cos(2.0*pi*7*i/N)
      data2[2*i+1]=-sin(2.0*pi*7*i/N)

    }
    localflag = localflag || maxel(vecsub(data1,data2)) > sbeps
    globalflag = globalflag || localflag;
    if (localflag) {
      fail("*** four1: Backward transform of a chosen delta function did not give expected result")
      
    }

    if (globalflag) System.out.println("Failed\n")
    else System.out.println("Passed\n")
  


```



## FFT of Real Functions, NR3, p.617 ##
```

import static com.nr.test.NRTestUtil.*
import static java.lang.Math.*
import static com.nr.fft.FFT.*

 N=256
 sbeps=1.0e-14
 pi=acos(-1.0)
    
    data1=new double[N]
    data2=new double[N]
    localflag=false
    globalflag=false

    println("Testing realft")

    // Round-trip test for random numbers
    ranvec(data1)
    data2=data1
    for (i=0;i<N;i++) data2[i] *= (2.0/N)
    figure(1); subplot(3,1,1); plot(data2, "Data before FFT")
    realft(data2,1)
    subplot(3,1,2); plot(data2, "FFT of data")
    realft(data2,-1)
    subplot(3,1,3); plot(data2, "Inverse FFT")
    localflag = localflag || maxel(vecsub(data1,data2)) > sbeps
    globalflag = globalflag || localflag
    if (localflag) {
      fail("*** realft: Round-trip test for random real values failed")
      
    }

    // Test delta-function in to sine-wave out, forward transform
    for (i=0;i<N;i++) data1[i]=0.0
    data1[5]=1.0
    figure(2); subplot(3,1,1); plot(data1, "delta-function")
    realft(data1, 1)
    subplot(3,1,2); plot(data1, "Computed Real FFT of delta-function")
    data2[0]=1.0
    data2[1]=cos(pi*5)
    for (i=1;i<N/2;i++) {
      data2[2*i]=cos(2.0*pi*5*i/N)
      data2[2*i+1]=sin(2.0*pi*5*i/N)
    }
   subplot(3,1,3); plot(data2, "Expected Real FFT of delta-function")
    
    localflag = localflag || maxel(vecsub(data1,data2)) > sbeps
    globalflag = globalflag || localflag;
    if (localflag) {
      fail("*** realft: Forward transform of a chosen delta function did not give expected result");
      
    }

    if (globalflag) println("Failed\n")
    else println("Passed\n")
  



```



## RBF Interpolation NR3 p. 139 ##


```


import static com.nr.test.NRTestUtil.maxel
import static com.nr.test.NRTestUtil.vecsub
import static java.lang.Math.cos

import com.nr.interp.RBF_gauss
import com.nr.interp.RBF_interp
import com.nr.interp.RBF_inversemultiquadric
import com.nr.interp.RBF_multiquadric
import com.nr.interp.RBF_thinplate
import com.nr.ran.Ran


    NPTS=100; NDIM=2; N=10; M=10;
    sbeps=0.05
    pts =new double[NPTS][NDIM]
    y = new double[NPTS]
    
    actual = new double[M]
    
    estim = new double[M]
    ppt = new double[2]
    globalflag=false

    
    // Test RBF_interp
    myran = new Ran(17)
    pt = new double[M][2]
    for (i=0;i<M;i++) {
      pt[i][0]=(double)(N)*myran.doub()
      pt[i][1]=(double)(N)*myran.doub()
      actual[i]=cos(pt[i][0]/20.0)*cos(pt[i][1]/20.0)
    }
    for (i=0;i<N;i++) {
      for (j=0;j<N;j++) {
        k=N*i+j
        pts[k][0]=(double)(j)
        pts[k][1]=(double)(i)
        y[k]=cos(pts[k][0]/20.0)*cos(pts[k][1]/20.0)
      }
    }

    println("Testing RBF_interp with multiquadric function")
    scale=3.0
    multiquadric = new RBF_multiquadric(scale)
    myRBFmqf = new RBF_interp(pts,y,multiquadric,false)
    
    for (i=0;i<M;i++) {
      ppt[0]=pt[i][0]
      ppt[1]=pt[i][1]
      estim[i]=myRBFmqf.interp(ppt)
    }
    
    printf("     Discrepancy: %f\n", maxel(vecsub(actual,estim)))
    localflag = maxel(vecsub(actual,estim)) > sbeps
    globalflag = globalflag || localflag
    if (localflag) {
      println("*** RBF_interp,multiquadric: Inaccurate multquadric interpolation with no normalization.")
    }

    println("Testing RBF_interp with thinplate function")
    scale=2.0
    thinplate = new RBF_thinplate(scale)
    myRBFtpf = new RBF_interp(pts,y,thinplate,false)
    for (i=0;i<M;i++) {
      ppt[0]=pt[i][0]
      ppt[1]=pt[i][1]
      estim[i]=myRBFtpf.interp(ppt)
    }
    printf("     Discrepancy: %f\n", maxel(vecsub(actual,estim)));
    localflag = maxel(vecsub(actual,estim)) > sbeps;
    globalflag = globalflag || localflag;
    if (localflag) {
      println("*** RBF_interp,thinplate: Inaccurate thinplate interpolation with no normalization.");
    }

    println("Testing RBF_interp with gauss function")
    scale=5.0
    gauss = new RBF_gauss(scale)
    myRBFgf = new RBF_interp (pts,y,gauss,false)
    for (i=0;i<M;i++) {
      ppt[0]=pt[i][0];
      ppt[1]=pt[i][1];
      estim[i]=myRBFgf.interp(ppt);
    }
    printf("     Discrepancy: %f\n", maxel(vecsub(actual,estim)))
    localflag = maxel(vecsub(actual,estim)) > sbeps
    globalflag = globalflag || localflag
    if (localflag) {
      println("*** RBF_interp,gauss: Inaccurate gauss interpolation with no normalization.")
      
    }

    println("Testing RBF_interp with inversemultiquadric function")
    scale=3.0
    inversemultiquadric = new RBF_inversemultiquadric(scale)
    myRBFimqf =new RBF_interp(pts,y,inversemultiquadric, false)
    for (i=0;i<M;i++) {
      ppt[0]=pt[i][0]
      ppt[1]=pt[i][1]
      estim[i]=myRBFimqf.interp(ppt)
    }
    printf("     Discrepancy: %f\n", maxel(vecsub(actual,estim)))
    localflag = maxel(vecsub(actual,estim)) > sbeps
    globalflag = globalflag || localflag
    if (localflag) {
      println("*** RBF_interp,inversemultiquadric: Inaccurate inversemultiquadric interpolation with no normalization.")
    }

    // Test same interpolators with normalization turned on
    scale=3.0
    println("Testing RBF_interp with multiquadric function")
    myRBFmqt = new RBF_interp(pts,y,multiquadric,true)
    for (i=0;i<M;i++) {
      ppt[0]=pt[i][0]
      ppt[1]=pt[i][1]
      estim[i]=myRBFmqt.interp(ppt)
    }
    printf("     Discrepancy: %f\n", maxel(vecsub(actual,estim)))
    localflag = maxel(vecsub(actual,estim)) > sbeps
    globalflag = globalflag || localflag
    if (localflag) {
      println("*** RBF_interp,multiquadric: Inaccurate multiquadric interpolation with normalization.");
     }

    println("Testing RBF_interp with thinplate function")
    scale=2.0
    myRBFtpt =new RBF_interp(pts,y,thinplate,true)
    for (i=0;i<M;i++) {
      ppt[0]=pt[i][0];
      ppt[1]=pt[i][1];
      estim[i]=myRBFtpt.interp(ppt);
    }
    printf("     Discrepancy: %f\n", maxel(vecsub(actual,estim)))
    localflag = maxel(vecsub(actual,estim)) > sbeps
    globalflag = globalflag || localflag
    if (localflag) {
      println("*** RBF_interp,thinplate: Inaccurate thinplate interpolation with normalization.");
    }

    println("Testing RBF_interp with gauss function")
    scale=5.0
    myRBFgt = new RBF_interp(pts,y,gauss,true)
    for (i=0;i<M;i++) {
      ppt[0]=pt[i][0]
      ppt[1]=pt[i][1]
      estim[i]=myRBFgt.interp(ppt)
    }
    printf("     Discrepancy: %f\n", maxel(vecsub(actual,estim)))
    localflag = maxel(vecsub(actual,estim)) > sbeps
    globalflag = globalflag || localflag
    if (localflag) {
      println("*** RBF_interp,gauss: Inaccurate gauss interpolation with normalization.");
    }

    println("Testing RBF_interp with inverse multiquadric function")
    scale=2.0
    myRBFimqt = new RBF_interp(pts,y,inversemultiquadric,true)
    for (i=0;i<M;i++) {
      ppt[0]=pt[i][0]
      ppt[1]=pt[i][1]
      estim[i]=myRBFimqt.interp(ppt)
    }
    printf("     Discrepancy: %f\n", maxel(vecsub(actual,estim)))
    localflag = maxel(vecsub(actual,estim)) > sbeps
    globalflag = globalflag || localflag
    if (localflag) {
      println("*** RBF_interp,inversemultiquadric: Inaccurate inversemultiquadric interpolation with normalization.")
    }

    if (globalflag)  println("Failed\n")
    else  println("Passed\n")

    
    
```

## Cholesky Decomposition ##

```




import static com.nr.NRUtil.*
import static com.nr.test.NRTestUtil.*

import com.nr.la.Cholesky

 diag=10.0
 a = new double[50][50]
    
 r=new double[50]
  
 y=new double[50]
 globalflag=false
    
 ranmat(a, diag)
 ranvec(r)   

println("Testing cholesky")

// make a positive definite matrix
aposdef = a*t(a)   // multiply matrix with its transpose

// perform a Cholesky factorization of the matrix, p. 101 NR3
ach = new Cholesky(aposdef)

// solve using Cholesky factorization, p. 101 NR3
ach.solve(r,y)

sbeps = 5.0e-15
localflag = maxel(vecsub(matmul(aposdef,y),r)) > sbeps
    globalflag = globalflag || localflag
    if (localflag) {
      fail("*** cholesky: Error in solve() method");
      
    }

residual = aposdef*y-r   // should be zero

    

```


## Eigenvalue computation for nonsymmetric matrices ##

```

// tests the EigenDecomposition routines from NR for unsymmetric matrices

import static com.nr.test.NRTestUtil.matmul
import static com.nr.test.NRTestUtil.maxel
import static com.nr.test.NRTestUtil.ranmat
import static com.nr.test.NRTestUtil.vecsub

import com.nr.Complex
import com.nr.eig.Unsymmeig
import com.nr.ran.Ran

    N=10
    sbeps=5.0e-14
    vec=new double[N] 
    res=new double[N]
    zres=new Complex[N]
    zvec=new Complex[N]
    a=new double[N][N]
    localflag=false
    globalflag=false

    

    // Test Unsymmeig, symmetric, interface1
    println("Testing Unsymmeig, symmetric, interface1")

// generate a random matrix and then make it symmetric
    myran=new Ran(17)
    for (i=0;i<N;i++) {
      a[i][i]=myran.doub()
      for (j=0;j<i;j++) {
        a[i][j]=myran.doub()
        a[j][i]=a[i][j]  // symmetric matrix
      }
    }

    // compute all eigenvalues and (optionally) eigenvectors of a real
    // nonsymmetric matrix a[0..n-1][0..n-1] by reduution to Hessenberg form
    // followed by QR iteration. Compute also eigenvectors (2nd param is true)
    usym = new Unsymmeig(a, true, false)
    // Test that all eigenvalues are real for symmetric matrix
    for (i=0;i<N;i++) 
      localflag = localflag || (usym.wri[i].im() != 0);
    globalflag = globalflag || localflag;
    if (localflag) {
      fail("*** Unsymmeig, symmetric, interface1: Symmetric matrix gave an eigenvalue that was not real");
      
    }

    // Test eigenvector/eigenvalue pairs
    for (i=0;i<N;i++) {   // for each eigenvector
      for (j=0;j<N;j++) vec[j]=usym.zz[j][i]   // take ith eigenvector
      res = a * vec  // multiply a*vec
      vecTimesLambda  = a*vec  // multiply eigenvalue_i*vec
      localflag = localflag || (maxel(vecTimesLambda - res))
    }
    
    globalflag = globalflag || localflag
    if (localflag) {
      fail("*** Unsymmeig, symmetric, interface1: Matrix times eigenvector was not the same as lambda*eigenvector")
    }
    
    // Test the sorting of the eigenvalues
    for (i=1; i<N; i++) 
      localflag = localflag || (usym.wri[i].re() > usym.wri[i-1].re())
    globalflag = globalflag || localflag
    if (localflag) {
      fail("*** Unsymmeig, symmetric, interface1: Eigenvalues not sorted in high-to-low order")
      
    }

    //------------------------------------------------------------------------
    // Test Unsymmeig, non-symmetric, interface1
    println("Testing Unsymmeig, non-symmetric, interface1")
    ranmat(a)
    usym2 = new Unsymmeig(a, true, false)

    // Test eigenvector/eigenvalue pairs
    for (i=0;i<N;i++) {   // for each eigenvector
      if (usym2.wri[i].im() == 0.0) {   // real eigenvalue
        for (j=0;j<N;j++) vec[j]=usym2.zz[j][i]
        res=matmul(a,vec)
        for (j=0;j<N;j++) vec[j] *= usym2.wri[i].re()
        localflag = localflag || (maxel(vecsub(res,vec)) > sbeps)
      } else {  // imaginary eigenvalue
        if (usym2.wri[i].im() > 0.0)
          for (j=0;j<N;j++) zvec[j]=new Complex(usym2.zz[j][i],usym2.zz[j][i+1])
        else
          for (j=0;j<N;j++) zvec[j]=new Complex(usym2.zz[j][i-1],-usym2.zz[j][i])
        for (j=0;j<N;j++) {
          zres[j]=new Complex(0.0,0.0)
          for (k=0;k<N;k++) zres[j] = zres[j].add(zvec[k].mul(a[j][k]))
        }
        for (j=0;j<N;j++) zvec[j] = zvec[j].mul(usym2.wri[i]);
        max=0.0
        for (j=0;j<N;j++) max = (max > zres[j].sub(zvec[j]).abs() ? max : zres[j].sub(zvec[j]).abs())
//        System.out.println("imag eigenvalue  " << max);
        localflag = localflag || (max > sbeps)
      }
      max=0.0
      for (j=0;j<N;j++) max = (max > zres[j].sub(zvec[j]).abs() ? max : zres[j].sub(zvec[j]).abs())
      localflag = localflag || (max > sbeps)
    }
    globalflag = globalflag || localflag
    if (localflag) {
      fail("*** Unsymmeig, non-symmetric, interface1: Matrix times eigenvector was not the same as lambda*eigenvector")
      
    }
    
    // Test the sorting of the eigenvalues
    for (i=1;i<N;i++)
      localflag = localflag || (usym2.wri[i].re() > usym2.wri[i-1].re())
    globalflag = globalflag || localflag
    if (localflag) {
    	fail("*** Unsymmeig, non-symmetric, interface1: Eigenvalues not sorted in high-to-low order of real part")
      
    }

    for (i=1;i<N;i++)
      localflag = localflag || (usym2.wri[i].im() > 0.0) && (!usym2.wri[i].equals(usym2.wri[i+1].conj()))
    globalflag = globalflag || localflag
    if (localflag) {
      fail("*** Unsymmeig, non-symmetric, interface1: A complex eigenvalue with positive imag part is not followed by its conjugate")
      
    }

    //------------------------------------------------------------------------
    // Test Unsymmeig, non-symmetric, interface2
	println("Testing Unsymmeig, non-symmetric, interface2")
    for (i=0;i<N;i++)
      for (j=0;j<N;j++) 
        a[i][j]= (i > j+1 ? 0.0d : myran.doub())
    
     usym3 = new Unsymmeig(a,true,true)


    // Test eigenvector/eigenvalue pairs
    for (i=0;i<N;i++) {   // for each eigenvector
      if (usym3.wri[i].im() == 0.0) {
        for (j=0;j<N;j++) vec[j]=usym3.zz[j][i]
        res=matmul(a,vec)
        for (j=0;j<N;j++) vec[j] *= usym3.wri[i].re()
//        System.out.println("real eigenvalue  " << maxel(vecsub(res,vec)));
        localflag = localflag || (maxel(vecsub(res,vec)) > sbeps)
      } else {
        if (usym3.wri[i].im() > 0.0)
          for (j=0;j<N;j++) zvec[j]=new Complex(usym3.zz[j][i],usym3.zz[j][i+1])
        else
          for (j=0;j<N;j++) zvec[j]=new Complex(usym3.zz[j][i-1],-usym3.zz[j][i])
        for (j=0;j<N;j++) {
          zres[j]=new Complex(0.0,0.0)
         
          for (k=0;k<N;k++) zres[j] = zres[j].add(zvec[k].mul(a[j][k]))
        }
        for (j=0;j<N;j++) zvec[j] = zvec[j].mul(usym3.wri[i])
        max=0.0
        for (j=0;j<N;j++) max = (max > zres[j].sub(zvec[j]).abs() ? max : zres[j].sub(zvec[j]).abs())

        localflag = localflag || (max > sbeps)
      }
      max=0.0
      for (j=0;j<N;j++) max = (max > zres[j].sub(zvec[j]).abs() ? max : zres[j].sub(zvec[j]).abs())
      localflag = localflag || (max > sbeps)
    }
    globalflag = globalflag || localflag
    if (localflag) {
      fail("*** Unsymmeig, non-symmetric, interface2: Matrix times eigenvector was not the same as lambda*eigenvector")
      
    }
    
    // Test the sorting of the eigenvalues
    for (i=1;i<N;i++) 
      localflag = localflag || (usym3.wri[i].re() > usym3.wri[i-1].re())
    globalflag = globalflag || localflag;
    if (localflag) {
      fail("*** Unsymmeig, non-symmetric, interface2: Eigenvalues not sorted in high-to-low order of real part");
    }

    for (i=1;i<N;i++)
      localflag = localflag || (usym3.wri[i].im() > 0.0) && (!usym3.wri[i].equals(usym3.wri[i+1].conj()));
    globalflag = globalflag || localflag;
    if (localflag) {
      fail("*** Unsymmeig, non-symmetric, interface1: A complex eigenvalue with positive imag part is not followed by its conjugate");
      
    }

    if (globalflag) println("Failed\n");
    else println("Passed\n");
  


```