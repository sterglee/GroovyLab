
package org.jblas;

/**
 * A complex value with double precision.
 *
 * @author Mikio L. Braun
 */
public class ComplexDouble {

  private double r, i;
  public static final ComplexDouble UNIT = new ComplexDouble(1.0, 0.0);
  public static final ComplexDouble I = new ComplexDouble(0.0, 1.0);
  public static final ComplexDouble NEG_UNIT = new ComplexDouble(-1.0, 0.0);
  public static final ComplexDouble NEG_I = new ComplexDouble(0.0, -1.0);
  public static final ComplexDouble ZERO = new ComplexDouble(0.0);

  public ComplexDouble(double real, double imag) {
    r = real;
    i = imag;
  }

  public ComplexDouble(double real) {
    this(real, 0.0);
  }

  public String toString() {
    if (i >= 0) {
      return r + " + " + i + "i";
    } else {
      return r + " - " + (-i) + "i";
    }
  }

  public ComplexDouble set(double real, double imag) {
    r = real;
    i = imag;
    return this;
  }

  public double real() {
    return r;
  }

  public double imag() {
    return i;
  }

  public ComplexDouble dup() {
    return new ComplexDouble(r, i);
  }

  public ComplexDouble copy(ComplexDouble other) {
    r = other.r;
    i = other.i;
    return this;
  }

  /**
   * Add two complex numbers in-place
   *
   * @param c      other complex number
   * @param result complex number where result is stored
   * @return same as result
   */
  public ComplexDouble addi(ComplexDouble c, ComplexDouble result) {
    if (this == result) {
      r += c.r;
      i += c.i;
    } else {
      result.r = r + c.r;
      result.i = i + c.i;
    }
    return result;
  }

  /**
   * Add two complex numbers in-place storing the result in this.
   *
   * @param c other complex number
   * @return resulting complex number
   */
  public ComplexDouble addi(ComplexDouble c) {
    return addi(c, this);
  }

  /**
   * Add two complex numbers.
   *
   * @param c other complex number
   * @return new complex number with result
   */
  public ComplexDouble add(ComplexDouble c) {
    return dup().addi(c);
  }

  /**
   * Add a real number to a complex number in-place.
   *
   * @param a      real number to add
   * @param result complex number to hold result
   * @return same as result
   */
  public ComplexDouble addi(double a, ComplexDouble result) {
    if (this == result) {
      r += a;
    } else {
      result.r = r + a;
      result.i = i;
    }
    return result;
  }

  /**
   * Add a real number to complex number in-place, storing the result in this.
   *
   * @param c real number to add
   * @return resulting complex number
   */
  public ComplexDouble addi(double c) {
    return addi(c, this);
  }

  /**
   * Add a real number to a complex number.
   *
   * @param c real number to add
   * @return new complex number with result
   */
  public ComplexDouble add(double c) {
    return dup().addi(c);
  }

  /**
   * Subtract two complex numbers, in-place
   *
   * @param c      complex number to subtract
   * @param result resulting complex number
   * @return same as result
   */
  public ComplexDouble subi(ComplexDouble c, ComplexDouble result) {
    if (this == result) {
      r -= c.r;
      i -= c.i;
    } else {
      result.r = r - c.r;
      result.i = i - c.i;
    }
    return this;
  }

  public ComplexDouble subi(ComplexDouble c) {
    return subi(c, this);
  }

  /**
   * Subtract two complex numbers
   *
   * @param c complex number to subtract
   * @return new complex number with result
   */
  public ComplexDouble sub(ComplexDouble c) {
    return dup().subi(c);
  }

  public ComplexDouble subi(double a, ComplexDouble result) {
    if (this == result) {
      r -= a;
    } else {
      result.r = r - a;
      result.i = i;
    }
    return result;
  }

  public ComplexDouble subi(double a) {
    return subi(a, this);
  }

  public ComplexDouble sub(double r) {
    return dup().subi(r);
  }

  /**
   * Multiply two complex numbers, in-place
   *
   * @param c      other complex number
   * @param result complex number where product is stored
   * @return same as result
   */
  public ComplexDouble muli(ComplexDouble c, ComplexDouble result) {
    double newR = r * c.r - i * c.i;
    double newI = r * c.i + i * c.r;
    result.r = newR;
    result.i = newI;
    return result;
  }

  public ComplexDouble muli(ComplexDouble c) {
    return muli(c, this);
  }

  /**
   * Multiply two complex numbers
   *
   * @param c other complex number
   * @return new complex number with product of this and c
   */
  public ComplexDouble mul(ComplexDouble c) {
    return dup().muli(c);
  }

  public ComplexDouble mul(double v) {
    return dup().muli(v);
  }

  public ComplexDouble muli(double v, ComplexDouble result) {
    if (this == result) {
      r *= v;
      i *= v;
    } else {
      result.r = r * v;
      result.i = i * v;
    }
    return this;
  }

  public ComplexDouble muli(double v) {
    return muli(v, this);
  }

  /**
   * Divide two complex numbers
   *
   * @param c complex number to divide this by
   * @return new complex number with quotient of this and c
   */
  public ComplexDouble div(ComplexDouble c) {
    return dup().divi(c);
  }

  /**
   * Divide two complex numbers, in-place
   *
   * @param c      complex number to divide this by
   * @param result complex number to hold result
   * @return same as result
   */
  public ComplexDouble divi(ComplexDouble c, ComplexDouble result) {
    double d = c.r * c.r + c.i * c.i;
    double newR = (r * c.r + i * c.i) / d;
    double newI = (i * c.r - r * c.i) / d;
    result.r = newR;
    result.i = newI;
    return result;
  }

  public ComplexDouble divi(ComplexDouble c) {
    return divi(c, this);
  }

  public ComplexDouble divi(double v, ComplexDouble result) {
    if (this == result) {
      r /= v;
      i /= v;
    } else {
      result.r = r / v;
      result.i = i / v;
    }
    return this;
  }

  public ComplexDouble divi(double v) {
    return divi(v, this);
  }

  public ComplexDouble div(double v) {
    return dup().divi(v);
  }

  /**
   * Return the absolute value
   *
   * @return the result (length of the vector in 2d plane)
   */
  public double abs() {
    return (double) Math.sqrt(r * r + i * i);
  }

  /**
   * Returns the argument of a complex number.
   *
   * @return the result (angle in radians of the vector in 2d plane)
   */
  public double arg() {
    return (double) Math.atan2(i, r);
  }

  public ComplexDouble invi() {
    double d = r * r + i * i;
    r = r / d;
    i = -i / d;
    return this;
  }

  public ComplexDouble inv() {
    return dup().invi();
  }

  public ComplexDouble neg() {
    return dup().negi();
  }

  public ComplexDouble negi() {
    r = -r;
    i = -i;
    return this;
  }

  public ComplexDouble conji() {
    i = -i;
    return this;
  }

  public ComplexDouble conj() {
    return dup().conji();
  }

  public ComplexDouble sqrt() {
    double a = abs();
    double s2 = (double) Math.sqrt(2);
    double p = (double) Math.sqrt(a + r) / s2;
    double sgn = Math.signum(i);
    if (sgn == 0.0) {
      sgn = 1.0;
    }
    double q = (double) Math.sqrt(a - r) / s2 * Math.signum(sgn);
    return new ComplexDouble(p, q);
  }

  /**
   * Comparing two ComplexDouble values.
   *
   * @param o object to compare this against
   * @return true if both numbers have the same value
   */
  public boolean equals(Object o) {
    if (!(o instanceof ComplexDouble)) {
      return false;
    }
    ComplexDouble c = (ComplexDouble) o;

    return r == c.r && i == c.i;
  }

  public int hashCode() {
    return Double.valueOf(r).hashCode() ^ Double.valueOf(i).hashCode();
  }

  public boolean eq(ComplexDouble c) {
    return Math.abs(r - c.r) + Math.abs(i - c.i) < (double) 1e-6;
  }

  public boolean ne(ComplexDouble c) {
    return !eq(c);
  }

  public boolean isZero() {
    return r == 0.0 && i == 0.0;
  }

  public boolean isReal() {
    return i == 0.0;
  }

  public boolean isImag() {
    return r == 0.0;
  }
}
