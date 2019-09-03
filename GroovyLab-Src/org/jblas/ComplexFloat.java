

package org.jblas;

/**
 * A complex value with float precision.
 *
 * @author Mikio L. Braun
 */
public class ComplexFloat {

  private float r, i;
  public static final ComplexFloat UNIT = new ComplexFloat(1.0f, 0.0f);
  public static final ComplexFloat I = new ComplexFloat(0.0f, 1.0f);
  public static final ComplexFloat NEG_UNIT = new ComplexFloat(-1.0f, 0.0f);
  public static final ComplexFloat NEG_I = new ComplexFloat(0.0f, -1.0f);
  public static final ComplexFloat ZERO = new ComplexFloat(0.0f);

  public ComplexFloat(float real, float imag) {
    r = real;
    i = imag;
  }

  public ComplexFloat(float real) {
    this(real, 0.0f);
  }

  public String toString() {
    if (i >= 0) {
      return r + " + " + i + "i";
    } else {
      return r + " - " + (-i) + "i";
    }
  }

  public ComplexFloat set(float real, float imag) {
    r = real;
    i = imag;
    return this;
  }

  public float real() {
    return r;
  }

  public float imag() {
    return i;
  }

  public ComplexFloat dup() {
    return new ComplexFloat(r, i);
  }

  public ComplexFloat copy(ComplexFloat other) {
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
  public ComplexFloat addi(ComplexFloat c, ComplexFloat result) {
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
  public ComplexFloat addi(ComplexFloat c) {
    return addi(c, this);
  }

  /**
   * Add two complex numbers.
   *
   * @param c other complex number
   * @return new complex number with result
   */
  public ComplexFloat add(ComplexFloat c) {
    return dup().addi(c);
  }

  /**
   * Add a real number to a complex number in-place.
   *
   * @param a      real number to add
   * @param result complex number to hold result
   * @return same as result
   */
  public ComplexFloat addi(float a, ComplexFloat result) {
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
  public ComplexFloat addi(float c) {
    return addi(c, this);
  }

  /**
   * Add a real number to a complex number.
   *
   * @param c real number to add
   * @return new complex number with result
   */
  public ComplexFloat add(float c) {
    return dup().addi(c);
  }

  /**
   * Subtract two complex numbers, in-place
   *
   * @param c      complex number to subtract
   * @param result resulting complex number
   * @return same as result
   */
  public ComplexFloat subi(ComplexFloat c, ComplexFloat result) {
    if (this == result) {
      r -= c.r;
      i -= c.i;
    } else {
      result.r = r - c.r;
      result.i = i - c.i;
    }
    return this;
  }

  public ComplexFloat subi(ComplexFloat c) {
    return subi(c, this);
  }

  /**
   * Subtract two complex numbers
   *
   * @param c complex number to subtract
   * @return new complex number with result
   */
  public ComplexFloat sub(ComplexFloat c) {
    return dup().subi(c);
  }

  public ComplexFloat subi(float a, ComplexFloat result) {
    if (this == result) {
      r -= a;
    } else {
      result.r = r - a;
      result.i = i;
    }
    return result;
  }

  public ComplexFloat subi(float a) {
    return subi(a, this);
  }

  public ComplexFloat sub(float r) {
    return dup().subi(r);
  }

  /**
   * Multiply two complex numbers, in-place
   *
   * @param c      other complex number
   * @param result complex number where product is stored
   * @return same as result
   */
  public ComplexFloat muli(ComplexFloat c, ComplexFloat result) {
    float newR = r * c.r - i * c.i;
    float newI = r * c.i + i * c.r;
    result.r = newR;
    result.i = newI;
    return result;
  }

  public ComplexFloat muli(ComplexFloat c) {
    return muli(c, this);
  }

  /**
   * Multiply two complex numbers
   *
   * @param c other complex number
   * @return new complex number with product of this and c
   */
  public ComplexFloat mul(ComplexFloat c) {
    return dup().muli(c);
  }

  public ComplexFloat mul(float v) {
    return dup().muli(v);
  }

  public ComplexFloat muli(float v, ComplexFloat result) {
    if (this == result) {
      r *= v;
      i *= v;
    } else {
      result.r = r * v;
      result.i = i * v;
    }
    return this;
  }

  public ComplexFloat muli(float v) {
    return muli(v, this);
  }

  /**
   * Divide two complex numbers
   *
   * @param c complex number to divide this by
   * @return new complex number with quotient of this and c
   */
  public ComplexFloat div(ComplexFloat c) {
    return dup().divi(c);
  }

  /**
   * Divide two complex numbers, in-place
   *
   * @param c      complex number to divide this by
   * @param result complex number to hold result
   * @return same as result
   */
  public ComplexFloat divi(ComplexFloat c, ComplexFloat result) {
    float d = c.r * c.r + c.i * c.i;
    float newR = (r * c.r + i * c.i) / d;
    float newI = (i * c.r - r * c.i) / d;
    result.r = newR;
    result.i = newI;
    return result;
  }

  public ComplexFloat divi(ComplexFloat c) {
    return divi(c, this);
  }

  public ComplexFloat divi(float v, ComplexFloat result) {
    if (this == result) {
      r /= v;
      i /= v;
    } else {
      result.r = r / v;
      result.i = i / v;
    }
    return this;
  }

  public ComplexFloat divi(float v) {
    return divi(v, this);
  }

  public ComplexFloat div(float v) {
    return dup().divi(v);
  }

  /**
   * Return the absolute value
   *
   * @return the result (length of the vector in 2d plane)
   */
  public float abs() {
    return (float) Math.sqrt(r * r + i * i);
  }

  /**
   * Returns the argument of a complex number.
   *
   * @return the result (angle in radians of the vector in 2d plane)
   */
  public float arg() {
    return (float) Math.atan2(i, r);
  }

  public ComplexFloat invi() {
    float d = r * r + i * i;
    r = r / d;
    i = -i / d;
    return this;
  }

  public ComplexFloat inv() {
    return dup().invi();
  }

  public ComplexFloat neg() {
    return dup().negi();
  }

  public ComplexFloat negi() {
    r = -r;
    i = -i;
    return this;
  }

  public ComplexFloat conji() {
    i = -i;
    return this;
  }

  public ComplexFloat conj() {
    return dup().conji();
  }

  public ComplexFloat sqrt() {
    float a = abs();
    float s2 = (float) Math.sqrt(2);
    float p = (float) Math.sqrt(a + r) / s2;
    float sgn = Math.signum(i);
    if (sgn == 0.0f) {
      sgn = 1.0f;
    }
    float q = (float) Math.sqrt(a - r) / s2 * Math.signum(sgn);
    return new ComplexFloat(p, q);
  }

  /**
   * Comparing two ComplexFloat values.
   *
   * @param o object to compare this against
   * @return true if both numbers have the same value
   */
  public boolean equals(Object o) {
    if (!(o instanceof ComplexFloat)) {
      return false;
    }
    ComplexFloat c = (ComplexFloat) o;

    return r == c.r && i == c.i;
  }

  public int hashCode() {
    return Float.valueOf(r).hashCode() ^ Float.valueOf(i).hashCode();
  }

  public boolean eq(ComplexFloat c) {
    return Math.abs(r - c.r) + Math.abs(i - c.i) < (float) 1e-6;
  }

  public boolean ne(ComplexFloat c) {
    return !eq(c);
  }

  public boolean isZero() {
    return r == 0.0f && i == 0.0f;
  }

  public boolean isReal() {
    return i == 0.0f;
  }

  public boolean isImag() {
    return r == 0.0f;
  }
}