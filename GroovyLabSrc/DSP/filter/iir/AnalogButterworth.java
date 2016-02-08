

package DSP.filter.iir;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import DSP.filter.Polynomial;
import DSP.filter.Rational;


/*
 Class to design analog Butterworth prototype filters.
 This analog prototype is a lowpass filter with a cutoff at 1 radian per second.
 */
public class AnalogButterworth extends AnalogPrototype {

	/**
	 * Instantiates a new analog Butterworth filter design with the indicated number of poles.
	 *
	 * @param order   int specifying the number of poles of the filter.
	 */
	public AnalogButterworth( int order ) {
		
		super();
		
		int nRealPoles        = order - 2*(order/2);
		int nComplexPolePairs = order/2;
		int nPoles            = nRealPoles + 2*nComplexPolePairs;
		
        if ( nRealPoles == 1 ) {
          double[] td = {1.0, 1.0};
          addSection( new Rational( new Polynomial(1.0), new Polynomial(td) ) );
        }
        
    	double dAngle = Math.PI/nPoles;

    	for ( int i = 0;  i < nComplexPolePairs;  i++ ) {
    	    double   angle = -Math.PI/2  +  dAngle/2 *( 1 + nRealPoles )  +  i*dAngle;
    	    double[] td    = {1.0, -2*Math.sin(angle), 1.0 };
    	    addSection( new Rational( new Polynomial(1.0), new Polynomial(td) ) );
    	}
    	
	}
	
	
	
	public static void main( String[] args ) {
	  
	  AnalogButterworth B = new AnalogButterworth( 6 );

	  AnalogPrototype   A = B.lptobp( 2*Math.PI*2.0, 2*Math.PI*3.0 );
		
	  double[] tmp = new double[201];
	  double[] gd  = new double[201];
	  for ( int i = 0;  i < 201;  i++ ) {
	    double omega = i*2.0*Math.PI/20.0;
	    tmp[i] = (double) Complex.abs( A.evaluate( omega ) );
	    gd[i] = (double) A.groupDelay( omega );
	  }
	  
	  PrintStream ps;
	  try {
		ps = new PrintStream( new FileOutputStream( "C:\\DATA\\Test\\AnalogButterworthResponse.m" ) );
		ps.print( "R = [ " );
		for ( int i = 0;  i < 200;  i++ ) {
			ps.println( tmp[i] + "; ..." );
		}
		ps.println( tmp[200] + "];" );
		ps.close();
	  } catch (FileNotFoundException e) {
		e.printStackTrace();
	  }
	  
	  try {
	     ps = new PrintStream( new FileOutputStream( "C:\\DATA\\Test\\AnalogButterworthGroupDelay.m" ) );
	     ps.print( "gd = [ " );
	     for ( int i = 0;  i < 200;  i++ ) {
	       ps.println( gd[i] + "; ..." );
	     }
	     ps.println( gd[200] + "];" );
	     ps.close();
	     } catch (FileNotFoundException e) {
	     e.printStackTrace();
	     }

	}

}
