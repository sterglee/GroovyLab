# Multithreaded matrix multiplication implementation #

`Matrix multiplication is a very common operation with significant computational requirements for large matrices. Therefore, we try to improve the execution time of this operation in GroovyLab, using Java multithreading. `

`Specifically, we use multithreading with the ` _`Matrix.`_

`The multithreaded matrix multiplication is still experimental. There is a speedup, but perhaps a significant improvement can be achieved with better design. The speedup is very dependent on matrix sizes, therefore cache locality issues are very important. `

`The current multithreaded matrix multiplication code is listed below: `

```


    public Matrix multiply( Matrix v2)  {
        
            // return new Matrix(LinearAlgebra.times( getRef(),  v2.getRef()), false );
        final int    rN = d.length;   final int  rM = d[0].length;
        int    sN = v2.fnrows;  int   sM = v2.fncols;
  
    boolean  useMultithreading = false;
    if (rN*rM*sM >  gExec.Interpreter.GlobalValues.mulMultithreadingLimit) 
        useMultithreading = true;
   if (useMultithreading )  {
    
       
    // transpose first matrix that. This operation is very important in order to exploit cache locality
final double [][]  thatTrans = new double[sM][sN];
int  r=0; int c = 0;
while (r<sN) {
  c=0;
  while (c<sM) {
    thatTrans[c][r] = v2.d[r][c];
    c++;
  }
  r++;
}

  final double [][]   vr = new double[rN][sM];   // for computing the return Matrix
  int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  nthreads = Math.min(nthreads, rN);
  
  Future<?>[] futures = new Future[nthreads];
            
  int   rowsPerThread = (int)(sM / nthreads);  // how many rows the thread processes

  int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
    final int  firstRow = threadId * rowsPerThread;
    final int  lastRow =   threadId == nthreads-1? sM: firstRow+rowsPerThread;
    
 futures[threadId] = ConcurrencyUtils.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             int  b = 0;
             while (b < rN )  {
                 double  s = 0.0;
                 int  c = 0;
                 while (c < rM) {
                    s += d[b][c] * thatTrans[a][c];
                    c++;
                   }
                vr[b][a]   = s;
                b++;
             }
             a++;
      }
   }
 });
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
  return new Matrix(vr);
	}
   else  // serial multiplication
   {
       
       return new Matrix(LinearAlgebra.times(getRef(), v2.d), false);
    }
    }
    
```

`At my laptop I obtain:`

```
 tmp = 0.797175489 sec
 tms = 3.377879568 sec
```

`which for 4 cores is a very good speedup. `


`The following script tests some choices for performing multiplication in GroovyLab (some require CUDA installation) and demonstrates that Java multithreaded multiplication performs significantly better than optimized serial C.`


```


// the size of matrices
N = 2000; M = 1400; K = 2200

x = ones(N,M); y = ones(M, K)
// test CUDA multiplication, single precision with CUBLAS
tic()
xxfCUBLAS = x.fmul( y)
tmfCUBLAS = toc()

// test CUDA multiplication, double precision with CUBLAS
tic()
xxdCUBLAS = x.dmul( y)
tmdCUBLAS = toc()

// test fast CUDA multiplication, single precision 
tic()
xxCUDAf = x.fmmul( y)
tmmf = toc()

// test fast CUDA multiplication, double precision 
tic()
xxCUDAd = x.dmmul( y)
tmmd = toc()


// test native C multiplication implementation
tic()
xxC =  cc( x.d, y.d)
tmC = toc()

// test Java multithreaded multiplication
tic()
xxJ = x * y
tmJ = toc()

```