
@groovy.transform.CompileStatic
 class testcTyped {
  

 double ct() {
tic();
double sm = 0.0;
int N=100;
int M=100;
int K=100;
int k=1;
int m = 1;
int r = 1;
while (k<=N) {
 m=1; 
 while (m <=M) {
      r=1;
      while (r<=K) {
   sm +=  k*m+8.9*k*m*r;
 sm /= 0.12*m*k*r*sm;
 r++
   }

  m++
  }
  k++
}
sm 
 }
}

testObjTyped = new testcTyped()
tic();
res = testObjTyped.ct()
timeComputeTyped = toc(); 
println("result = "+res+" time to compute typed= "+timeComputeTyped)
 