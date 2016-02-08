
package DSP.filter.iir;

import java.io.PrintStream;
import java.util.ArrayList;

import DSP.filter.Polynomial;
import DSP.filter.Rational;


/*
  Base class, with partial implementation, for analog prototype filters. 
  
 */
public class AnalogPrototype {
	
  /** Data structure for second-order sections comprising the filter implementation */
  protected ArrayList< Rational > sections;
  
  
  /** Overall transfer function of the filter, represented as a rational object */
  protected Rational              T;

    
	
	/**
	 * Default constructor for a new analog prototype.
	 * 
	 * Instantiates an analog prototype with no second order sections.  This constructor 
	 * is called by the super() methods in subclasses.
	 */
  public AnalogPrototype() {
    sections = new ArrayList< Rational >();	
    T        = null;
	}
	
	
	
	/**
	 * Method to add a second order section to the analog prototype representation.
	 *
	 * @param R     Rational object containing a second order section representation.
	 */
	public void addSection( Rational R ) {
	  sections.add( R );
	}
	
	
	
	/**
	 * Returns the number of second order sections in the analog prototype representation.
	 *
	 * @return    int containing the number of second order sections.
	 */
	public int nSections() {
	  return sections.size();
	}
	
	
	
	/**
	 * Accessor for second order sections in the prototype representation.
	 *
	 * @param index     int specifying the desired second order section.
	 * @return          Rational object containing the representation of the desired section.
	 */
	public Rational getSection( int index ) {
		return  new Rational( sections.get(index) );
	}
	
	
	
	// spectral transformations
	
	//   lowpass to lowpass transformation
	
	/**
	 * Converts a lowpass prototype with cutoff at 1 rad/sec to lowpass with a new cutoff frequency.
	 *
	 * @param omega0     double specifying the cutoff of the transformed lowpass prototype filter.
	 * @return           AnalogPrototype object containing the transformed filter representation.
	 */
	public AnalogPrototype lptolp( double omega0 ) {
	    
	  double[] tn = {0.0, 1.0};
	  double[] td = { omega0 };
	  
	  Rational T = new Rational( tn, td );
	  
	  AnalogPrototype retval = new AnalogPrototype();
	  
      for ( int i = 0;  i < sections.size();  i++ )
        retval.addSection( sections.get(i).map( T ) );
      
      return retval;
	}

	
	
   //   lowpass to highpass transformation
    
    /**
    * Converts a lowpass analog prototype with cutoff at 1 rad/sec to a highpass filter with a new cutoff.
    *
    * @param omega0     double specifying the desired new cutoff frequency - now a low cutoff.
    * @return           AnalogPrototype object containing the transformed filter representation.
    */
   public AnalogPrototype lptohp( double omega0 ) {
      
      double[] tn = { omega0 };
      double[] td = { 0.0, 1.0 };
      
      Rational T = new Rational( tn, td );
      
      AnalogPrototype retval = new AnalogPrototype();
      
      for ( int i = 0;  i < sections.size();  i++ )
        retval.addSection( sections.get(i).map( T ) );
      
      return retval;
    }
    
    
    
    //   lowpass to bandpass transformation
    
    /**
     * Converts a lowpass analog prototype with cutoff at 1 rad/sec to a bandpass filter with specified cutoffs.
     *
     * @param omega1     double containing the low cutoff frequency in radians/sec.
     * @param omega2     double containing the high cutoff frequency in radians/sec.
     * @return           AnalogPrototype object containing the transformed filter representation.
     */
    public AnalogPrototype lptobp( double omega1, double omega2 ) {
    	
    	double BW   = omega2 - omega1;
    	double prod = omega1*omega2;
    	
  	    double[] tn = { prod, 0.0, 1.0 };
	    double[] td = { 0.0, BW };
	  
	    Rational T = new Rational( tn, td );
	    
	    AnalogPrototype retval = new AnalogPrototype();
	    
	    double A = 1.0;
	    
	    for ( int i = 0;  i < sections.size();  i++ ) {
	    	
	    	Rational section = sections.get(i);
	    	Rational Tsection = section.map( T );
	    	A *= Tsection.canonicalForm();

	    	int[] order = section.order();
	    	
	    	if ( order[0] < 2  &&  order[1] < 2 ) retval.addSection( Tsection );
	    	
	    	else if ( order[1] == 2 ) {
	    		
	    		Polynomial[] DT = lptobpFactors( section.denominator(), BW, prod );
	    		double[] t1 = { 0.0, 1.0 };
	    		
	    		if ( order[0] == 0 ) {
	    			retval.addSection( new Rational( new Polynomial(t1), DT[0] ) );
	    			retval.addSection( new Rational( new Polynomial(t1), DT[1] ) );
	    		}
	    		else if ( order[0] == 1 ) {
	    			retval.addSection( new Rational( new Polynomial(t1), DT[0] ) );
	    			double[] t2 = new double[3];
	    			double[] tc = Tsection.numerator().coefficients();
	    	    for ( int j = 0;  j < 3;  j++ ) t2[j] = tc[j+1];
	    	    retval.addSection( new Rational( new Polynomial(t2), DT[1] ) );
	    		}
	    		else if ( order[0] == 2 ) {
	    			Polynomial[] NT = lptobpFactors( section.numerator(), BW, prod );
	    			retval.addSection( new Rational( NT[0], DT[0] ) );
	    			retval.addSection( new Rational( NT[1], DT[1] ) );
	    		}
	    		
	    	}
	    	
	    }
	    
	    retval.sections.get(0).timesEquals(A);
    	
        return retval;	
    }
    
    
    
    
    /**
     * Method to compute polynomial factors for bandpass transformed quadratic polynomials in a second-order section.
     *
     * @param P       Polynomial object to be transformed.
     * @param BW      Bandwidth parameter of the transform.
     * @param prod    Product parameter of the transform.
     * @return        Array of Polynomial factors (there will be two for each quadratic in a second order section).
     */
    private static Polynomial[] lptobpFactors( Polynomial P, double BW, double prod ) {
    	
    	Polynomial[] retval = new Polynomial[2];
    	
    	double[] p = P.coefficients();
    	double c = p[0] / p[2];
    	double b = p[1] / p[2];
    	double discriminant = b*b-4*c;
    	
    	if ( discriminant >= 0.0 ) {
    		double   root = ( -b + Math.sqrt(discriminant) ) / 2.0;
        double   f1   = root*BW/2.0;
        double   f2   = f1*f1 - prod;
        Complex  C    = new Complex( f1 ).plus( Complex.sqrt( new Complex(f2) ) );
        double[] t0   = { C.conjugate().times(C).real(), -2.0*C.real(), 1.0 };
        retval[0]     = new Polynomial( t0 );
            
        root = ( -b -Math.sqrt(discriminant) ) / 2.0;
        f1   = root*BW/2.0;
        f2   = f1*f1 - prod;
        C    = new Complex( f1 ).plus( Complex.sqrt( new Complex(f2) ) );
        double[] t1 = { C.conjugate().times(C).real(), -2.0*C.real(), 1.0 };
        retval[1]     = new Polynomial( t1 );
    	}
    	else {
    		Complex root = new Complex( -b/2.0, Math.sqrt( -discriminant ) / 2.0 );
    		
        Complex f1  = root.times( BW/2.0 );
        Complex f2  = (f1.times(f1)).minus( prod );
        Complex C   = f1.plus( Complex.sqrt( f2 ) );
        double[] t0 = { C.conjugate().times(C).real(), -2.0*C.real(), 1.0 };
        retval[0]   = new Polynomial( t0 );
            
        C = f1.minus( Complex.sqrt( f2 ) );
        double[] t1 = { C.conjugate().times(C).real(), -2.0*C.real(), 1.0 };
        retval[1]   = new Polynomial( t1 );
      }
    	
    	return retval;
    }
    
    
    
   	
	/**
	 * Computes the transfer function representation of the filter as a product of second-order section transfer fuctions.
	 *
	 * @return     Rational object containing the resulting transfer function representation.
	 */
	protected void computeTransferFunction() {
		
		T = new Rational( 1.0 );
		
		for ( int i = 0;  i < sections.size();  i++ ) 
		  T.timesEquals( sections.get(i) );
		
	}
	
	
	
	/**
	 * Accessor for the transfer function representation for the filter.
	 *
	 * @return      Rational object containing the transfer function representation for the filter.
	 */
	public Rational getTransferFunction() {
	  if ( T == null ) computeTransferFunction();
	  return new Rational( T );
	}
	
  
  
  /**
   * Evaluates the filter transfer function at analog frequency omega.
   *
   * @param omega          double containing the analog frequency for evaluation of the transfer function.
   * @return               Complex object containing the value of the transfer function at this frequency.
   */
  protected Complex evaluate( double omega ) {
    
    if ( T == null ) computeTransferFunction();
    
    return T.evaluate( new Complex( 0.0, omega ) );
  }	
  
  
  
  /**
   * Evaluates the filter's group delay at analog frequency omega.
   *
   * @param omega          double containing the analog frequency for evaluation of the group delay.
   * @return               double containing the group delay at this frequency.
   */
  protected double groupDelay( double omega ) {
    
    if ( T== null ) computeTransferFunction();
    
    return T.groupDelay( omega );
  }
  
	
	
	/**
	 * Prints the coefficients of the second-order section factors of this analog prototype filter.
	 *
	 * @param ps     PrintStream to which the representation is printed.
	 */
	public void print( PrintStream ps ) {
		
		ps.println( "AnalogPrototype: \n" );
		
      for ( int i = 0;  i < sections.size();  i++ ) {
        ps.println("  section " + i + ":" );
        sections.get( i ).print( ps );
      }
		
	}
	
	
}
