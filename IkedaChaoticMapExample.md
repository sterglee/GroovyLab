# Ikeda Chaotic Map #

`The example that follows illustrated computation of the Ikeda Chaotic map and can be used to benchmark also the indy based results, the @CompileStatic and the call site caching based method dispatch. `


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