# Introduction #

`The Scala interpreter keeps both import and compiled code context. This is much convenient in ScalaLab.`

`However, the GroovyShell does not keep previous imports and compiled code (e.g. classes).`

`For that reason GroovyLab implements an external  buffering mechanism that allows the user to accumulate import statements and class definitions that are passed to GroovyShell with each execution.`

`This allows a convenient statement by statement execution, with the same sense as ScalaLab.`

`The following script that tests the Powell optimization routine from Numerical Recipes demonstrates the utilization of the buffering feature.`

```



//  buffer that import statements for convenient execution
// using menu choice: "Imports->Buffer selected imports"
import static com.nr.NRUtil.SQR
import static java.lang.Math.abs
import static java.lang.Math.acos
import static java.lang.Math.cos
import static java.lang.Math.sin
import static java.lang.Math.sqrt
import com.nr.RealValueFun
import com.nr.min.Powell
import com.nr.sf.Bessjy

//  buffer that class for convenient execution
//  using menu choice:  "Code Buffering -> Buffer selected class(es) code"
class Test_Powell implements RealValueFun {
  public double funk(double[] x) {
    return Bessj0_Powell(x)
  }
  
  double Bessj0_Powell(double[] x) {
    Bessjy b = new Bessjy()
    return(b.j0(sqrt(SQR(x[0])+SQR(x[1])+SQR(x[2]))))
  }

}

// the script begins here. You should have buffered import statements and the class Test_Powell,
// to execute it conveniently (e.g. statement-by-statement) 
    N = 20
    pi = acos(-1.0)
    sbeps1 = 5.0e-8 
    sbeps2 = 1.0e-15
    p= new double[3]
    ximat = new double[3][3]

    localflag = false
    globalflag = false


    // Test Powell
    println("Testing Powell, interface1")
    testPowell = new Test_Powell()  

    pow1 = new Powell(testPowell)
    p[0] = p[1] = p[2] = 0.0
    p = pow1.minimize(p)
    f0 = pow1.fret
    d0 = sqrt(SQR(p[0])+SQR(p[1])+SQR(p[2]))

    for (i=0;i<N;i++) {
      theta=pi*i/N
      phi=pi*i/N
      p[0]=sin(phi)*cos(theta)
      p[1]=sin(phi)*sin(theta)
      p[2]=cos(phi)
      p=pow1.minimize(p)
      f=pow1.fret
      d=sqrt(SQR(p[0])+SQR(p[1])+SQR(p[2]))
      localflag = localflag || abs(d-d0) > sbeps1
      globalflag = globalflag || localflag
      if (localflag) {
        fail("*** Powell, interface1: First minimum of radial function reported at different radius for different starting points")
        
      }
    }

      localflag = localflag || abs(f-f0) > sbeps2
      globalflag = globalflag || localflag
      if (localflag) {
        fail("*** Powell, interface1: Reported function value at first minimum is different for different starting points");
      }
    

    println("Testing Powell, interface2")
     pow2 = new Powell(testPowell)
    for (i=0;i<N;i++) {
      p[0]=0.1
      p[1]=0.1
      p[2]=-0.1
      theta=pi*i/N
      phi=pi*i/N
      ximat[0][0]=sin(theta)*cos(phi)
      ximat[1][0]=sin(theta)*sin(phi)
      ximat[2][0]=cos(theta)
      ximat[0][1]=cos(theta)*cos(phi)
      ximat[1][1]=cos(theta)*sin(phi)
      ximat[2][1]=-sin(theta)
      ximat[0][2]=-sin(theta)
      ximat[1][2]=cos(theta)
      ximat[2][2]=0.0
      p=pow2.minimize(p,ximat)
      f=pow2.fret
      d=sqrt(SQR(p[0])+SQR(p[1])+SQR(p[2]))

      localflag = localflag || abs(d-d0) > sbeps1
      globalflag = globalflag || localflag
      if (localflag) {
        fail("*** Powell, interface2: First minimum of radial function reported at different radius for different xi[]");
        
      }

      localflag = localflag || abs(f-f0) > sbeps2
      globalflag = globalflag || localflag
      if (localflag) {
        fail("*** Powell, interface2: Reported function value at first minimum is different for different xi[i]");
        
      }
    }

    if (globalflag) println("Failed\n")
    else println("Passed\n")
  

 

```