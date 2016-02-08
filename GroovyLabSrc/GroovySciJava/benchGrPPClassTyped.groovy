
 class testc {
  
 double c() {
tic();
double sm = 0.0;
int N=100;
int M=100;
int K=100;
for (k in 1..N) {
   for (m in 1..M) {
     for (r in 1..K) {
   sm +=  k*m+8.9*k*m*r;
 sm /= 0.12*m*k*r*sm;
   }
  }
}
sm 
 }
}

testObj = new testc()
tic();
res = testObj.c()
timeCompute = toc(); 
println("result = "+res+" time to compute = "+timeCompute)
 