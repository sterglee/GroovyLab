# Introduction #

`We demonstrate here the implementation of a script that implements the Runge-Kutta 4th order method. The code is translated from Numerical Recipes and therefore speed comparisons with Java code are applicable. We obtained the ` **`same`** ` speed as Java code with Groovy's static compilation!!`


# Solution of the Lorenz system with Runge-Kutta 4th order code #

```



// you can buffer that imports using "Imports"->"Buffer Selected Imports" for convenient execution
import static com.nr.test.NRTestUtil.maxel
import static com.nr.test.NRTestUtil.vecsub
import com.nr.ode.*
import com.nr.sf.Bessjy

// you can buffer that classes using "Code Buffering" -> "Buffer selected class(es) code" for convenient execution statement-by-statement
  
@groovy.transform.CompileStatic
class rhs_Lorenz  implements DerivativeInf {
    public void derivs(final double x,double[] y,double[] dydx) {
      dydx[0] = 10*(y[1]-y[0])
      dydx[1] = -y[0]*y[2]+143*y[0] - y[1]
      dydx[2] = y[0]*y[1]-2.66667*y[2]
    }
    public void jacobian(final double x, double[] y, double[] dfdx, double[][] dfdy){}
  }

         
  
 // Given values for the variables y[0..n-1] and their derivatives dydx[0..n-1] known at x,
  // use the fourth-order Runge-Kutta method to advance the solution over an interval h and 
  // return the incremented variables as yout[0..n-1].The user supplies the routine derivs(x, y, dydx),
  // which returns derivatives at x
@groovy.transform.CompileStatic
  public static void grk4(final double[] y, final double[] dydx, final double x, final double h, final double[] yout, final DerivativeInf derivs) {
    int n=y.length;
    double[] dym = new double[n],dyt = new double[n],yt = new double[n];
    double hh=h*0.5;
    double h6=h/6.0;
    double xh=x+hh;
    for (int i=0;i<n;i++) yt[i]=y[i]+hh*dydx[i];
    derivs.derivs(xh,yt,dyt);
    for (int i=0;i<n;i++) yt[i]=y[i]+hh*dyt[i];
    derivs.derivs(xh,yt,dym);
    for (int i=0;i<n;i++) {
      yt[i]=y[i]+h*dym[i];
      dym[i] += dyt[i];
    }
    derivs.derivs(x+h,yt,dyt);
    for (int i=0;i<n;i++)
      yout[i]=y[i]+h6*(dydx[i]+dyt[i]+2.0*dym[i]);
  }




 lo = new rhs_Lorenz()   // construct a Lorenz object
 K = 3 // order of our system (Lorenz here)
 dydx = new double[K]  // keeps the return results
  
 tic()
  // now we can use Runge-Kutta 4th order formulas to integrate
  N=200000  // number of points to compute
  Ntransients = 520  // drop these initial "out-of-orbit" evaluations
  h = 0.0001    // Runge-Kutta step size
  yout  = new double [K]  // keeps the returned results from our integration routine

  orbit = new double[3][N]   // accumulates the orbit

  y0 = [0.1, 0.2, 0.11] as double[]         // initial values of the variables
  t = 0.0  // where the derivatives are computed (e.g. time axis)
  lo.derivs(t, y0, dydx)  // derivatives at y0 are returned at dydx
  for (k in 0..Ntransients) { // let the system stabilize
  	grk4(y0, dydx,  t, h, yout, lo)
    y0[0] = yout[0]; y0[1] = yout[1]; y0[2] =  yout[2];  // next step on is from where we left
    t += h  // advance  time
    lo.derivs(t, y0, dydx) 
  }
  
  for (k in 0..N-1) {
    grk4(y0, dydx,  t, h, yout, lo)
    orbit[0][k] = yout[0]; orbit[1][k] = yout[1]; orbit[2][k] = yout[2];   //   save the orbit's point
    y0[0] = yout[0]; y0[1] = yout[1]; y0[2] =  yout[2];  // next step on is from where we left
    t += h  // advance  time
    lo.derivs(t, y0, dydx) 
  }

tm = toc()
  
  linePlotsOn()
  plot(orbit, "steps = "+N+", time = "+tm)



// non-static: 0.631 sec, N=200000
// compile-static: 0.34 sec
// indy: 1.037 sec
// Java - Numerical Recipes: 0.35 sec

```