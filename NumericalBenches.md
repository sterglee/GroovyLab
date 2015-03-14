# Introduction #

`Some simple numerical benches that can be used to test improvements with static compilation, and with invoke dynamic mode.`


## Simple Maths ##

```

tic(); 
sm = 0;
N=100;
M=100;
K=50;
for (k in 1..N) {
 for (m in 1..M) {
  for (r in 1..K) {
   sm += sin(k*m)/sin(r);
   sm = sin(sm-cos(k*m));
   }
 }
}
tm = toc();

```



## Ikeda Chaotic Map ##

```



@groovy.transform.CompileStatic
class Ikeda {
        int nIters
        double [] xall
        double [] yall
  Ikeda(int niters) {
   nIters = niters
   xall =  new double[niters]
   yall  = new double[niters]
  }

def computeMap(int niters) {
  double c1 = 0.4
  double c2 = 0.9
  double c3 = 9.0
  double rho = 0.85

 double x = 0.5; double y = 0.5;

for (k in 1..niters-1)  {
  def xp = x; def yp=y
  def taut = c1-c3/(1.0+xp*xp+yp*yp)
  x = rho + c2*xp*cos(taut)-yp*sin(taut)
  y = c2*(xp*sin(taut)+yp*cos(taut))
  xall[k] = x
  yall[k] = y
}
        }

 }
 
niters = 5000000

g = new Ikeda(niters)

tic() 
g.computeMap(niters)
 
tm = toc()
println(" tm = "+tm)

 scatterPlotsOn()
 plot(g.xall, g.yall)
 

```


## A simple method performing computations ##

```
@groovy.transform.CompileStatic
double comp(int N) {
    double sm = 0.0
    
    for (int n=0; n<=N; n++) {
        int  k=0;
        while (k < 10000) {
    sm += (k*n*0.787)
    if (sm > 0)  sm = 1/sm;
    else sm = 1/(-sm-1)
      k++
        }
    }
    return sm
}

tic()
sm = comp(1000)
tmTyped = toc()
println("sm = "+sm+ " time = "+tmTyped)

```