
package DSP.filter.iir;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import DSP.filter.Polynomial;
import DSP.filter.Rational;


/*
  Class to design analog Chebyshev type II prototype filters.
  
  This analog prototype is a lowpass filter with a cutoff at 1 radian per second.  Chebyshev
  type II filters have zeros in the stopband and a sharper transition from passband to 
  stopband than a Butterworth filter.  Like Butterworth filters they are flat and monotonic 
  in the passband.
 */
public class AnalogChebyshevII extends AnalogPrototype {
	  
	  /**
  	 * Instantiates a new analog Chebyshev type II filter.
  	 *
  	 * @param order     The order of the filter (number of poles.
  	 * @param epsilon   double parameter controlling the stopband attenuation of the filter.
  	 */
  	public AnalogChebyshevII ( int order, double epsilon ) {
	    
	    super();
	    
	    double alpha = ( 1.0 + Math.sqrt( 1.0 + epsilon*epsilon ) ) / epsilon ;
	    double p     = Math.pow( alpha, 1.0/order );
	    double a     = 0.5*( p - 1/p );
	    double b     = 0.5*( p + 1/p );
	    
	    System.out.println( "alpha: " + alpha );
	    System.out.println( "p:     " + p );
	    System.out.println( "a:     " + a );
	    System.out.println( "b:     " + b );
	    
	    int nRealPoles        = order - 2*(order/2);
	    int nComplexPolePairs = order/2;
	    int nPoles            = nRealPoles + 2*nComplexPolePairs;
	    
	    if ( nRealPoles == 1 ) {
	      double[] td = { 1.0/a, 1.0};
	      addSection( new Rational( new Polynomial(1.0), new Polynomial(td) ) );
	    }
	    
	    double dAngle = Math.PI/nPoles;

	    for ( int i = 0;  i < nComplexPolePairs;  i++ ) {
	        double angle = -Math.PI/2  +  dAngle/2 *( 1 + nRealPoles )  +  i*dAngle;
	        Complex pole = Complex.divide( 1.0, new Complex( a*Math.sin(angle), b*Math.cos(angle ) ) );
	        double[] td    = { pole.real()*pole.real() + pole.imag()*pole.imag(), -2*pole.real(), 1.0 };
	        double zeroimag = 1.0/Math.cos((2*i+1)*Math.PI/(2*order) );
	        double[] tn     = { zeroimag*zeroimag, 0.0, 1.0 };
	        addSection( new Rational( new Polynomial(tn), new Polynomial(td) ) );
	    }
	    
	    // scale to 1 at s = 0
	    
	    double DCvalue = evaluate( 0.0 ).abs();
	    sections.get(0).timesEquals( 1.0/DCvalue );
	    
	  }
	  
	  
	  
  	public static void main( String[] args ) {
		    
		    AnalogChebyshevII A = new AnalogChebyshevII( 8, 0.1 );

		    double[] tmp = new double[201];
		    for ( int i = 0;  i < 201;  i++ ) {
		      Complex C = A.evaluate( i*0.02 );
		      tmp[i] = (double) Complex.abs( C );
		    }
		    
		    PrintStream ps;
		    try {
		      ps = new PrintStream( new FileOutputStream( "C:\\DATA\\Test\\AnalogResponse.m" ) );
		      ps.print( "R = [ " );
		      for ( int i = 0;  i < 200;  i++ ) {
		        ps.println( tmp[i] + "; ..." );
		      }
		      ps.println( tmp[200] + "];" );
		      ps.close();
		    } catch (FileNotFoundException e) {
		      e.printStackTrace();
		    }

		  }
	  
}

	  