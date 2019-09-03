
package org.jblas.util;

public class Functions {
	public static double sinc(double x) {
		if (x == 0)
			return 1.0;
		else
			return Math.sin(Math.PI * x) / (Math.PI * x);
	}

    public static int min(int a, int b) { return a < b ? a : b; }
    public static int max(int a, int b) { return a > b ? a : b; }

  private static final double LOG2 = 0.6931471805599453;

  public static double log2(double x) {
    return Math.log(x) / LOG2;
  }
}
