# Introduction #

`Matrix multiplication is a basic operation that should be performed as fast as possible. We illustrate a method for fast matrix multiplication using the Pthreads library .`

`The code for Pthread based matrix multiplication is illustrated below. `

`The ` **`pt`** `method performs fast matrix multiplication. `

`You can use fast matrix multiplication as: `

```

N = 2002; M = 1600; K = 2345;

x = rand(N, M); y = rand(M, K)

tic()
xyJavaMulti = x * y  // multiply using Java multithreading
tmJavaMulti = toc()


tic()
xySerial = x.multiplySerial(y)   // multiply using serial Java multiplication
tmSerialJava=toc()

// verify that the results are the same
max(max(xySerial - xyJavaMulti))
min(min(xySerial-xyJavaMulti))


tic()
xycSerial = x.cc(y)  // multiply using serial C
tmcSerial = toc()
// verify that the results are the same
max(max(xycSerial - xyJavaMulti))
min(min(xycSerial-xyJavaMulti))


tic()
xycParallel = x.pt(y)   // multiply using Pthread based  C multithreading
tmcParallel = toc()
// verify that the results are the same
max(max(xycParallel - xyJavaMulti))
min(min(xycParallel-xyJavaMulti))

```


# Pthread based fast matrix multiplication #

```


static int N1, N2, N3;  // A  is N1XN2, B is N2XN3
double *AA, *BB, *CC;  // pointer to matrices A, B, C
int NumThreads = 20;  // number of threads to use
int SliceRows;   // how many rows each slice has


// multiply a slice of the matrix consisted from rows sliceStart up to sliceEnd
void multiplySlice( int sliceStart, int sliceEnd, int threadId)  {
	double *pA;
	double *pB;
	double *pC;
	double smrowcol;
  
  if (sliceStart >= N1) return;
  if (sliceEnd >= N1) sliceEnd = N1;
  
  pC = CC + threadId*SliceRows*N3;  // position that this thread starts outputting results to C matrix
  // for all the rows of the matrix A that the thread has the responsibility to compute
  for (int i=sliceStart; i<sliceEnd; i++) {
 for (int j=0; j<N3; j++) {

	  smrowcol = 0.0;
	  pA = AA + i*N2;  // current row start
	  pB = BB+j*N2;
	  
	  for (int k=0; k<N2; k++) {
		smrowcol += *pA * *pB;
		pA++;  
		//pB += n3;  // normally this advances to the next column element of B, but it's transposed!
		pB++;  // matrix B enters the routine transposed to exploit cache locality
		}
		*pC++ = smrowcol;
	}
  }
 }

 // the thread function
void * threadMultiply( void * slice) {
  int s = (int) slice; // which slice of the matrix belongs to that thread

	multiplySlice( SliceRows*s,  SliceRows*s+SliceRows, s);  // each thread multiplies its part of rows
	
  return (int *)1;
}


  

  
extern "C"
JNIEXPORT void JNICALL Java_CCOps_CCOps_pt
 (JNIEnv *env, jobject obj, jdoubleArray a, jint n1, jint n2, 
	jdoubleArray b, jint n3, jdoubleArray c) {
	AA = (double *)env->GetDoubleArrayElements( a, NULL);
	BB = (double *)env->GetDoubleArrayElements( b, NULL);
	CC = (double *)env->GetDoubleArrayElements( c, NULL);
		
	double *pA;
	double *pB;
	double *pC;
	double smrowcol;
  
  // keep these parameters globally since they are needed by all threads
   N1 = n1;
   N2 = n2;
   N3 = n3;
  
   SliceRows = (int)(N1/NumThreads);  // number of rows to process each thread
   
   if (SliceRows==0)  // a very small matrix: multiply serially
      multiplySlice(0, N1, 0);
	else {
   // allocate memory for the threads. One more thread is required to process the last part of the matrix
   pthread_t * thread;
   thread = (pthread_t*) malloc((NumThreads+1)*sizeof(pthread_t));
   
   // wait for the threads to finish
   for (int i = 0; i <= NumThreads; i++) {
    if (pthread_create(&thread[i], NULL, threadMultiply, (void *)i) != 0)
	{
	perror("Can't create thread");
	free(thread);
	return;
	 }
	}
	
   for (int i=0; i<= NumThreads; i++)
     pthread_join(thread[i], NULL);
	 
	 free(thread);
	}
	
	env->ReleaseDoubleArrayElements(a, AA, 0);
	env->ReleaseDoubleArrayElements( b, BB, 0);
	env->ReleaseDoubleArrayElements(c, CC, 0);
	
	
}


```