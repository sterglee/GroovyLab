# Introduction #

`GroovyLab provides support for CUDA 6.5. CUDA offers the potential for significant speedup of many difficult computationally operations. `

`You should set however the variable CUDA_PATH AND CUDA_PATH_V6_5 to point to your CUDA installation, for example on my computer: `

```

export CUDA_PATH=/home/sterg/cuda65
export CUDA_PATH_V6_5=$CUDA_PATH
```

**`High-level operators, work correctly with NVIDIA devices of compute capability 1.3 and above. This is because, we use double precision arithmetic, and older devices supported adequately only single precision arithmetic. In any case, older GPUs either yield an insignificant speedup, or even work slower!`**

**`Thus to benefit from CUDA in GroovyLab, a newer NVIDIA GPU is required, and the speedup can be significant, e.g. ten or hundrends of times faster than the CPU!`**

**`To use CUDA operations, it is very easy, only the installation of CUDA from NVIDIA is required that can be obtained from `**

https://developer.nvidia.com/cuda-downloads

`Currently, there exists a ` **`KernelOps.cu`** `file. It can be obtained from the GroovyLab sources in the directory ` **`CUDAOps`** `That file implements all the CUDA linking functionality, i.e. the ` **`CUDA kernels`** `, C utility routines and JNI related routines. `

`The Java file ` **`KernelOps.java`** ` declares the relating JNI interface for CUDA routines. `



`An example GroovySci program that utilizes CUDA is: `
```


 km = new CUDAOps.KernelOps()


// test matrix addition
 N=100
 a = new float[N*N]
 b = new float[N*N]
 c = new float[N*N]

for (k in 0.. N-1) a[k]=k*1.0f
for (k in 0.. N-1) b[k]=k*1.0f

km.sgemm(a, N, N,  b,  N, c)

km.cma(a, b, c)





// test multiplication 
 Aw = 100;  Ah = 30;  Bw = 15;  Bh = Aw
 amg = new float[Aw*Ah]
 bmg = new float[Ah*Bh]
 cmg = new float[Ah*Bw]

for (k in 0.. Aw*Ah-1) amg[k]=1.0f
for (k in  0.. Ah*Bh-1) bmg[k]=1.0f

tic()
km.cmm(amg, bmg, cmg, Ah, Aw, Bw)
tm = toc()

```

`Currently, there exist a few high level operators, some are based on direct kernel implementations and some on CUDA BLAS. CUDA BLAS versions are faster, and also single precision arithmetic based routines perform faster. This fact can change as new GPUs support better double precision arithmetic hardware.These operators are illustrated at the script example below.`

`A test script for CUDA based matrix multiplication follows: `


```

// test the CUDA multiplication 

ARows = 500;  ACols = 502;   BRows = ACols;
BCols = 501;   CRows = ARows;  CCols = BCols;


 A = ones(ARows, ACols)
 B = ones(BRows, BCols)

tic()
C = A*B
tmGroovy = toc()

tic()
C = A.fmmul(B.d)
tmCUDA = toc()

```