package groovySci.math.plot;

import groovy.lang.Closure;
import java.util.Vector;



// this class implements functional plotting by adaptively adjusting the sampling rate 
// where the function changes quickly.
//  The fastest the function change, the faster sampling rate becomes,
//  in order to catch signal variations

public class plotAdaptiveFunctional {

 static public double   dxForDeriv = 0.000001;  // a very small increment to compute the function's derivative
 static public double   thresholdSmallRateOfChange = 1.0;    // for smaller than this we use the default dx
 static public double   thresholdLargeRateOfChange = 1000.0;  // for very fast changing functions avoid taking too much samples
    
 static public void  setDxForDeriv(double newDx)  { dxForDeriv = newDx; }

 static public void  setThresholdSmallRateOfChange(double newThresholdSmallRateOfChange)    { thresholdSmallRateOfChange = newThresholdSmallRateOfChange; }
  
 static public void   setThresholdLargeRateOfChange(double newThresholdLargeRateOfChange)  { thresholdLargeRateOfChange = newThresholdLargeRateOfChange; }
        
// adaptive function plotting. Plot a function by changing automatically the sampling rate in regions where the function
// changes quickly
public static  double [][]    faplot( Closure f,  double  low, double high, int nP)   { 
    
    int   N = nP;
    // default sampling increment interval
    double  dx = (high-low)/N;
    double  defaultDx = dx;  // the default sampling interval, dx
    Vector   ax = new Vector();    // the "time" axis with the non-uniform sampling
    Vector   ay = new Vector();    // the corresponding values of the function
    
    // at this loop we will try to estimate the rate of change of the function
    // If the rate of change is large we decrease the sampling interval
    //  in order to have better function description.
    // If it is small we reset to the defaultDx
    double  currx = low;  // current value of x, i.e. the variable used to sample the function
    while (currx < high) {  // currx steps across the whole sampling axis
        double  frate = ((Double)f.call(currx+dxForDeriv)-(Double)f.call(currx)) / dxForDeriv;  // an estimate of the rate of change near currx
        // avoid to use too large and too small rate of change
        if (frate > thresholdLargeRateOfChange) 
            frate = thresholdLargeRateOfChange;
        else if (frate < thresholdSmallRateOfChange)
            frate = thresholdSmallRateOfChange;
        
        double  currStep = defaultDx / frate;   // adjust the stepping size with the rate of change
        ax.add(currx);  // append "time" sample
        ay.add((Double)f.call(currx)); // append a function value
        
        currx += currStep;   // update the point on the x-axis, fast variation areas lead to dense sampling
   }
  
     // convert lists to arrays
    int nsamples = ax.size();
    double [][] samplesValuesArray = new double[2][nsamples];
    
    for (int k=0; k<nsamples; k++)  {
        samplesValuesArray[0][k] = (Double)ax.get(k);
        samplesValuesArray[1][k] = (Double)ay.get(k);
    }
    
    groovySci.math.plot.plot.plot(samplesValuesArray[0], samplesValuesArray[1]);   // plot the computed arrays      
   
    return samplesValuesArray;
    
    

  }

}

// Example: the function sin(x*x) changes generally more rapidly as x increases,
//  however, as can be seen from its derivative x*x*cos(x*x),
//  the rate of change oscilates also with increasing frequency as x increases 
 /* 
f = {x ->  sin(x*x) }

closeAll()
Npoints = 200
figure(1)
subplot(2, 1, 1)
fplot(f, 0, 10,  Npoints )
xlabel("Fixed sampling functional ploting")
subplot(2, 1, 2)
res  = faplot(f, 0, 10, nP = Npoints)
xlabel("Adaptive sampling functional ploting")

  
*/



