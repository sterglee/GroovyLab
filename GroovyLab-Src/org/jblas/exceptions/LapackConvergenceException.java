
package org.jblas.exceptions;

/**
 *
 * @author mikio
 */
public class LapackConvergenceException extends LapackException {
  	public LapackConvergenceException(String function, String msg) {
		super(function, msg);
	}

}
