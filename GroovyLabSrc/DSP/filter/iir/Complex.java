
package DSP.filter.iir;

import java.text.DecimalFormat;


/*
  Class to represent complex numbers and certain basic functions of complex numbers.
 */
public class Complex {
    
    /** Real part of the complex number. */
    private double real;
    
    /** Imaginary part of the complex number. */
    private double imag;
    
    // constructors

    /**
     * Instantiates a new complex number object.
     *
     * @param real           double specifying the real part.
     * @param imag           double specifying the imaginary part.
     */
    public Complex( double real, double imag ) {
        this.real = real;
        this.imag = imag;
    }
    
    
    /**
     * Instantiates a new complex number object from a real number (imaginary part is zero).
     *
     * @param real       double specifying the real part.
     */
    public Complex( double real ) {
        this.real = real;
        this.imag = 0.0;
    }
    
    
    // static methods
    
    
    /**
     * Instantiates a new complex number object from polar representation parameters.
     *
     * @param r          double specifying the radius (magnitude) of the complex number.
     * @param phi        double specifying the phase angle of the complex number.
     * @return           Resulting Complex number object.
     */
    public static Complex ComplexFromPolar( double r, double phi ) {
        return new Complex( r*Math.cos(phi), r*Math.sin(phi) );
    }
    
    
    /**
     * Calculates the sum of a real number and a complex number.
     *
     * @param a         double specifying the real number.
     * @param c         Complex number object.
     * @return          New Complex object containing the sum.
     */
    public static Complex add( double a, Complex c ) {
        return new Complex( a + c.real, c.imag );
    }
    
    
    /**
     * Calculates the sum of a complex number and a real number.
     *
     * @param c         Complex number object.       
     * @param a         double specifying the real number.
     * @return          New Complex object containing the sum.
     */
    public static Complex add( Complex c, double a ) {
    	return add( a, c );
    }
    
    
    /**
     * Calculates the difference of a complex number and a real number.
     *
     * @param c         Complex number object.
     * @param a         double specifying the real number.
     * @return          New Complex object containing the difference.
     */
    public static Complex subtract( Complex c, double a ) {
    	return new Complex( c.real - a, c.imag );
    }
    
    
    /**
     * Calculates the difference of a real number and a complex number.
     *
     * @param a         double specifying the real number.
     * @param c         Complex number object.
     * @return          New Complex object containing the difference.
     */
    public static Complex subtract( double a, Complex c ) {
    	return  new Complex( a - c.real, c.imag );
    }
    
    
    /**
     * Unary minus - negates a complex number.
     *
     * @param c         Complex number to be negated.
     * @return          New Complex object containing the negative of the operand.
     */
    public static Complex unaryMinus( Complex c ) {
    	return new Complex( -c.real, -c.imag );
    }
    
    
    /**
     * Multiplies a real and a complex number.
     *
     * @param a           double specifying the real factor.
     * @param c           Complex object specifying the complex factor.
     * @return            New Complex object containing the product.
     */
    public static Complex multiply( double a, Complex c ) {
        return new Complex( a*c.real, a*c.imag );
    }
    
    
    /**
     * Multiplies a real and a complex number.
     *
     * @param c           Complex object specifying the complex factor.
     * @param a           double specifying the real factor.
     * @return            New Complex object containing the product.
     */
    public static Complex multiply( Complex c, double a ) {
    	return multiply( a, c );
    }
    
    
    /**
     * Adds two complex numbers.
     *
     * @param c1     First Complex summand.
     * @param c2     Second Complex summand.
     * @return       New Complex object containing the sum.
     */
    public static Complex add( Complex c1, Complex c2 ) {
    	return new Complex( c1.real + c2.real, c1.imag + c2.imag );
    }
    
    
    /**
     * Subtracts one complex number from another.
     *
     * @param c1      First Complex number.
     * @param c2      Second Complex number to be subtracted from the first.
     * @return        New Complex object containing the difference.
     */
    public static Complex subtract( Complex c1, Complex c2 ) {
    	return new Complex( c1.real - c2.real, c1.imag - c2.imag );
    }
    
    
    /**
     * Multiplies two complex numbers.
     *
     * @param c1      First Complex factor.
     * @param c2      Second Complex factor.
     * @return        New Complex object containing the product.
     */
    public static Complex multiply( Complex c1, Complex c2 ) {
    	return new Complex( c1.real*c2.real - c1.imag*c2.imag, c1.real*c2.imag + c1.imag*c2.real );
    }
    
    
    /**
     * Divides a complex number by a real number.
     *
     * @param c     The Complex number.
     * @param a     double containing the real divisor.
     * @return      New Complex object containing the result of division.
     */
    public static Complex divide( Complex c, double a ) {
    	return new Complex ( c.real/a, c.imag/a );
    }
    
    
    /**
     * Divide a real number by a complex number.
     *
     * @param a     double containing the real number.
     * @param c     Complex divisor.
     * @return      New Complex object containing the result of division.
     */
    public static Complex divide( double a, Complex c ) {
    	double scale = c.real*c.real + c.imag*c.imag;
    	return new Complex( c.real/scale, -c.imag/scale );
    }
    
    
    /**
     * Divides one complex number by another.
     *
     * @param c1       The first Complex number.
     * @param c2       The Complex divisor.
     * @return         New Complex object containing the result of division.
     */
    public static Complex divide( Complex c1, Complex c2 ) {   // c1/c2 = conjg(c2)*c1/( conjg(c2)*c2 )
    	double scale = c2.real*c2.real + c2.imag*c2.imag;
    	return new Complex( (c1.real*c2.real + c1.imag*c2.imag)/scale, (c1.imag*c2.real - c1.real*c2.imag )/scale );
    }
    
    
    /**
     * Computes the square root of a complex number.
     *
     * @param c     Complex argument of the square root function.
     * @return      New Complex object containing the square root of the argument.
     */
    public static Complex sqrt( Complex c ) {
        return ComplexFromPolar( Math.sqrt( abs(c) ), angle(c)/2.0 );
    }
    
    
    /**
     * Computes the absolute value of a complex number.
     *
     * @param c     Complex argument of the absolute value operator.
     * @return      double containing the absolute value of the argument.
     */
    public static double abs( Complex c ) {
    	return Math.sqrt( c.real*c.real + c.imag*c.imag );
    }
    
    
    /**
     * Computes the phase angle of a complex number.
     *
     * @param c      Complex argument of the phase function.
     * @return       double containing the phase of the argument.
     */
    public static double angle( Complex c ) {
    	return Math.atan2( c.imag, c.real );
    }
    
    
    /**
     * Computes the complex exponential function of a complex number.
     *
     * @param c      Complex argument to the exponential function.
     * @return       New Complex object containing the complex exponential of the argument.
     */
    public static Complex exp( Complex c ) {
        double r = Math.exp( c.real );
        return new Complex( r*Math.cos( c.imag ), r*Math.sin( c.imag ) );
    }
    
    
    /**
     * Conjugates a complex number.
     *
     * @param c       Complex argument.
     * @return        New Complex object containing the conjugate of the argument.
     */
    public static Complex conjugate( Complex c ) {
    	return new Complex( c.real, -c.imag );
    }
    
    
    // other methods
    
    
    /**
     * Returns the real part of a complex number.
     *
     * @return        double containing the real part of a Complex number object.
     */
    public double real() { return real; }
    
    
    /**
     * Returns the imaginary part of a complex number.
     *
     * @return        double containing the imaginary part of a Complex number object.
     */
    public double imag() { return imag; }

    
    /**
     * Computes the absolute value of this Complex number.
     *
     * @return     double containing the absolute value of this Complex number.
     */
    public double abs() {
        return abs( this );
    }
    
    
    /**
     * Computes the phase angle of this Complex number.
     *
     * @return     double containing the phase angle of this Complex number.
     */
    public double angle() {
        return angle( this );
    }
    
    
    /**
     * Multiplies this Complex number by another Complex number.
     * 
     * Does not alter the value of this Complex object.
     *
     * @param c     The other Complex factor.
     * @return      New Complex object containing the product.
     */
    public Complex times( Complex c ) {
        return multiply( this, c );
    }
    
    
    /**
     * Multiplies this Complex number by a real number.
     * 
     * Does not alter the value of this Complex object.
     *
     * @param a     The real multiplicand.
     * @return      New Complex object containing the product.
     */
    public Complex times( double a ) {
        return multiply( this, a );
    }
    
    
    /**
     * Returns the conjugate of this Complex number.
     * 
     * Does not alter the value of this Complex object.
     *
     * @return     New Complex object containing the conjugate of this.
     */
    public Complex conjugate() {
        return conjugate( this );
    }
    
    
    /**
     * Computes the sum of this Complex number and another. 
     *
     * Does not alter the value of this Complex object.
     *
     * @param c     Complex object containing the other summand.
     * @return      New Complex object containing the sum.
     */
    public Complex plus( Complex c ) {
        return add( this, c );
    }
    
    
    /**
     * Computes the sum of this Complex number and a real number.
     * 
     * Does not alter the value of this Complex object.
     *
     * @param a     double containing the real summand.
     * @return      New Complex object containing the sum.
     */
    public Complex plus( double a ) {
    	return add( this, a );
    }
    
    
    /**
     * Subtracts a complex number from this complex number.
     * 
     * Does not alter the value of this Complex object.
     *
     * @param c     Complex number to be subtracted from this Complex number.  
     * @return      New Complex object containing the difference.
     */
    public Complex minus( Complex c ) {
        return subtract( this, c );
    }
    
    
    /**
     * Subtracts a real number from this Complex number.
     * 
     * Does not alter the value of this Complex object.
     *
     * @param a    double containing the real number to be subtracted from this Complex number.
     * @return     New Complex object containing the complex difference.
     */
    public Complex minus( double a ) {
    	return subtract( this, a );
    }
    
    
    /**
     * Divides this Complex number by a real number.
     *
     * Does not alter the value of this Complex object.
     * 
     * @param a    double containing the real divisor.
     * @return     New Complex object containing the result of division.
     */
    public Complex over( double a ) {
        return divide( this, a );
    }

    
    /**
     * Divides this Complex number by another Complex number.
     * 
     * Does not alter the value of this Complex object.
     *
     * @param c     The Complex divisor.
     * @return      New Complex object containint the result of division.
     */
    public Complex over( Complex c ) {
        return divide( this, c );
    }
    
    
    /**
     * Adds a real number to this Complex number.  
     * 
     * Alters the value of this Complex object.
     *
     * @param a   double containing the other summand.
     */
    public void plusEquals( double a ) {
    	real += a;
    }
    
    
    /**
     * Adds a Complex number to this Complex number.
     * 
     * Alters the value of this Complex object.
     *
     * @param c   Complex object containing the other summand.
     */
    public void plusEquals( Complex c ) {
    	real += c.real;
    	imag += c.imag;
    }
    
    
    /**
     * Subtracts a real number from this Complex number.
     * 
     * Alters the value of this Complex object.
     *
     * @param a    double containing the real number to be subtracted from this Complex number.
     */
    public void minusEquals( double a ) {
    	real -= a;
    }
    
    
    /**
     * Subtracts another Complex number from this Complex number.
     * 
     * Alters the value of this Complex object.
     *
     * @param c    The other Complex number to be subtracted from this Complex number.
     */
    public void minusEquals( Complex c ) {
    	real -= c.real;
    	imag -= c.imag;
    }
    
    
    /**
     * Multiplies this Complex number by a real number.
     * 
     * Alters the value of this Complex object.
     *
     * @param a     double containing the multiplicand.
     */
    public void timesEquals( double a ) {
    	real *= a;
    	imag *= a;
    }
    
    
    /**
     * Multiplies this Complex number by another Complex number.
     * 
     * Alters the value of this Complex object.
     *
     * @param c     Complex object containing the other multiplicand.
     */
    public void timesEquals( Complex c ) {
        double tmp = real*c.real - imag*c.imag;
        imag       = real*c.imag + imag*c.real;
        real       = tmp;
    }
    
    
    /**
     * Divides this Complex number by a real number.
     * 
     * Alters the value of this Complex object.
     *
     * @param a     double containing the real divisor.
     */
    public void divideEquals( double a ) {
    	real /= a;
    	imag /= a;
    }
    
    
    /**
     * Divides this Complex number by another Complex number.
     * 
     * Alters the value of this Complex object.
     *
     * @param c     Complex object containing the divisor.
     */
    public void divideEquals( Complex c ) {
    	double scale = c.real*c.real + c.imag*c.imag;
    	double tmp   = c.real*real + c.imag*imag;
    	imag         = c.real*imag - c.imag*real;
    	real         = tmp;
    	this.divideEquals( scale );
    }
    
    
    /** 
     * Generates a String representation for this Complex number object.
     */
    public String toString() {
    	DecimalFormat formatter = new DecimalFormat( "0.00000E00" );
        return  formatter.format( real ) + "  +  i * " + formatter.format( imag );
    }

    
}
