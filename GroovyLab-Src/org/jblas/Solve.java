

package org.jblas;

/**
 * Solving linear equations.
 */
public class Solve {
	/** Solves the linear equation A*X = B. */
	public static DoubleMatrix solve(DoubleMatrix A, DoubleMatrix B) {
		A.assertSquare();
		DoubleMatrix X = B.dup();
		int[] ipiv = new int[B.rows];
		SimpleBlas.gesv(A.dup(), ipiv, X);
		return X;
	}

	/** Solves the linear equation A*X = B for symmetric A. */
	public static DoubleMatrix solveSymmetric(DoubleMatrix A, DoubleMatrix B) {
		A.assertSquare();
		DoubleMatrix X = B.dup();
		int[] ipiv = new int[B.rows];
		SimpleBlas.sysv('U', A.dup(), ipiv, X);
		return X;
	}

	
	/** Solves the linear equation A*X = B for symmetric and positive definite A. */
	public static DoubleMatrix solvePositive(DoubleMatrix A, DoubleMatrix B) {
		A.assertSquare();
		DoubleMatrix X = B.dup();
		SimpleBlas.posv('U', A.dup(), X);
		return X;
	}

  /** Computes the Least Squares solution for over or underdetermined
   * linear equations A*X = B
   *
   * In the overdetermined case, when m > n, that is, there are more equations than
   * variables, it computes the least squares solution of X -> ||A*X - B ||_2.
   *
   * In the underdetermined case, when m < n (less equations than variables), there are infinitely
   * many solutions and it computes the minimum norm solution.
   *
   * @param A an (m,n) matrix
   * @param B a (m,k) matrix
   * @return either the minimum norm or least squares solution.
   */
  public static DoubleMatrix solveLeastSquares(DoubleMatrix A, DoubleMatrix B) {
    if (B.rows < A.columns) {
      DoubleMatrix X = DoubleMatrix.concatVertically(B, new DoubleMatrix(A.columns - B.rows, B.columns));
      SimpleBlas.gelsd(A.dup(), X);
      return X;
    } else {
      DoubleMatrix X = B.dup();
      SimpleBlas.gelsd(A.dup(), X);
      return X.getRange(0, A.columns, 0, B.columns);
    }
  }

  /**
   * Computes the pseudo-inverse.
   *
   * Note, this function uses the solveLeastSquares and might produce different numerical
   * solutions for the underdetermined case than matlab.
   *
   * @param A rectangular matrix
   * @return matrix P such that A*P*A = A and P*A*P = P.
   */
  public static DoubleMatrix pinv(DoubleMatrix A) {
    return solveLeastSquares(A, DoubleMatrix.eye(A.rows));
  }

//BEGIN
  // The code below has been automatically generated.
  // DO NOT EDIT!
	/** Solves the linear equation A*X = B. */
	public static FloatMatrix solve(FloatMatrix A, FloatMatrix B) {
		A.assertSquare();
		FloatMatrix X = B.dup();
		int[] ipiv = new int[B.rows];
		SimpleBlas.gesv(A.dup(), ipiv, X);
		return X;
	}

	/** Solves the linear equation A*X = B for symmetric A. */
	public static FloatMatrix solveSymmetric(FloatMatrix A, FloatMatrix B) {
		A.assertSquare();
		FloatMatrix X = B.dup();
		int[] ipiv = new int[B.rows];
		SimpleBlas.sysv('U', A.dup(), ipiv, X);
		return X;
	}

	
	/** Solves the linear equation A*X = B for symmetric and positive definite A. */
	public static FloatMatrix solvePositive(FloatMatrix A, FloatMatrix B) {
		A.assertSquare();
		FloatMatrix X = B.dup();
		SimpleBlas.posv('U', A.dup(), X);
		return X;
	}

  /** Computes the Least Squares solution for over or underdetermined
   * linear equations A*X = B
   *
   * In the overdetermined case, when m > n, that is, there are more equations than
   * variables, it computes the least squares solution of X -> ||A*X - B ||_2.
   *
   * In the underdetermined case, when m < n (less equations than variables), there are infinitely
   * many solutions and it computes the minimum norm solution.
   *
   * @param A an (m,n) matrix
   * @param B a (m,k) matrix
   * @return either the minimum norm or least squares solution.
   */
  public static FloatMatrix solveLeastSquares(FloatMatrix A, FloatMatrix B) {
    if (B.rows < A.columns) {
      FloatMatrix X = FloatMatrix.concatVertically(B, new FloatMatrix(A.columns - B.rows, B.columns));
      SimpleBlas.gelsd(A.dup(), X);
      return X;
    } else {
      FloatMatrix X = B.dup();
      SimpleBlas.gelsd(A.dup(), X);
      return X.getRange(0, A.columns, 0, B.columns);
    }
  }

  /**
   * Computes the pseudo-inverse.
   *
   * Note, this function uses the solveLeastSquares and might produce different numerical
   * solutions for the underdetermined case than matlab.
   *
   * @param A rectangular matrix
   * @return matrix P such that A*P*A = A and P*A*P = P.
   */
  public static FloatMatrix pinv(FloatMatrix A) {
    return solveLeastSquares(A, FloatMatrix.eye(A.rows));
  }

//END
}
