
package groovySci.math.plot;

import edu.jas.application.Condition.Color;
import gExec.Interpreter.GlobalValues;
import groovy.lang.Closure;


public class plotFunctional {
    
    
  static Closure  mkFunction1d(String expr)  {

// instantiate  a Function1 trait with the function's expression 
    //String mkClFromExpr = "groovySci.math.plot.plotFunctional.globalFunction1d =  { x ->  "+expr+"}";
    
   String  importMath = "import static java.lang.Math.*\n";
   
   String mkClFromExpr = importMath+"\n  { x ->  "+expr+"}";
   
Closure plotCl = (Closure)  GlobalValues.GroovyShell.evaluate(mkClFromExpr);

return plotCl;
  }
  
  
  // evaluate and plot the function y=f(x), given by the expression within [low, high].
  // Note that the variable should be named 'x' '
   /* 
     
     cubeSine = " x*x*x*sin(x)"   // a function
     low = -5;  high= 5; color = Color.RED; linePlotsFlag = false; nP = 2000
     figure(1); subplot(2,1,1);
     splot(cubeSine, low, high, cubeSine, color, linePlotsFlag, nP) 
     squareCos = "x*x*cos(12.3*x)"
      low2 = -15;  high2= 15
     subplot(2,1,2)
     splot(squareCos, low2, high2, squareCos); // use defaults
    
   
   */

  public static void  splot(String expr, double low, double  high, String title, java.awt.Color color,  boolean linePlotsFlag,  int nP )  {
    Closure onedf =  mkFunction1d(expr);  // make the 1D function object according to the expr
    fplot(onedf, low, high, title, color, linePlotsFlag, nP );
  }

  public static void  splot(String expr, double low, double  high, java.awt.Color color,  boolean linePlotsFlag,  int nP )  {
    Closure onedf =  mkFunction1d(expr);  // make the 1D function object according to the expr
    fplot(onedf, low, high, color, linePlotsFlag, nP );
  }

  public static void  splot(String expr, double low, double high, String title )  {
    java.awt.Color color = java.awt.Color.RED;
    boolean linePlotsFlag = true;
    int nP = GlobalValues.maxPointsToPlot;
    splot(expr, low, high, title, color, linePlotsFlag, nP);
  }
  
  public static void  splot(String expr, double low, double high )  {
    java.awt.Color color = java.awt.Color.RED;
    boolean linePlotsFlag = true;
    int nP = GlobalValues.maxPointsToPlot;
    splot(expr, low, high, color, linePlotsFlag, nP);
  }
  
  public static void  splot(String expr, double low, double high, java.awt.Color color )  {
    boolean linePlotsFlag = true;
    int nP = GlobalValues.maxPointsToPlot;
    splot(expr, low, high, color, linePlotsFlag, nP);
  }
  
  public static void  splot(String expr, double low, double high, java.awt.Color color, boolean linePlotsFlag )  {
    int nP = GlobalValues.maxPointsToPlot;
    splot(expr, low, high, color, linePlotsFlag, nP);
  }
  
// plots the function described by the closure f1d, e.g. 
/*
     cube = { x -> x*x*x}   // a cube function
     low = -5;  high= 5; color = Color.RED; linePlotsFlag = false; nP = 2000
     fplot(cube, low, high, color, linePlotsFlag, nP)
     */
public static void  fplot(Closure f1d,  double  low,  double high,  String title, java.awt.Color color, boolean  linePlotsFlag,  int  nP ) {
    double  dx = (high-low)/nP;
    double  []valsx = new double[nP];
    double  []valsy = new double [nP];
    
    double   currx = low;
    for (int k=0; k<nP; k++)  {
      valsx[k] = currx;
      valsy[k] = (Double)f1d.call(currx);   // call the closure
      currx =  currx+dx;
    }
    
    if (linePlotsFlag) 
       plot.linePlotsOn();
     else
       plot.scatterPlotsOn();
    groovySci.math.plot.plot.plot(valsx, valsy, title, color);
    
  }

public static void  fplot(Closure f1d,  double  low,  double high,  java.awt.Color color, boolean  linePlotsFlag,  int  nP ) {
  fplot(f1d, low, high, "Functional Plot", color, linePlotsFlag, nP);
}

public static void  fplot(Closure f1d,  double  low,  double high, String title ) {
    java.awt.Color color = java.awt.Color.RED;
    boolean linePlotsFlag = false;
    int nP = GlobalValues.maxPointsToPlot;
    fplot(f1d, low, high, title, color, linePlotsFlag, nP);
}

public static void  fplot(Closure f1d,  double  low,  double high ) {
    java.awt.Color color = java.awt.Color.RED;
    boolean linePlotsFlag = false;
    int nP = GlobalValues.maxPointsToPlot;
    fplot(f1d, low, high, color, linePlotsFlag, nP);
}

public static void  fplot(Closure f1d,  double  low,  double high,  java.awt.Color color ) {
    boolean linePlotsFlag = false;
    int nP = GlobalValues.maxPointsToPlot;
    fplot(f1d, low, high, color, linePlotsFlag, nP);
}

public static void  fplot(Closure f1d,  double  low,  double high,  java.awt.Color color,  boolean linePlotsFlag) {
    int nP = GlobalValues.maxPointsToPlot;
    fplot(f1d, low, high, color, linePlotsFlag, nP);
}

    


}
