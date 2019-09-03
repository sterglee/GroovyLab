/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package groovySciCommands;

import groovy.lang.Closure;

/**
 *
 * @author sterg
 */
public class BenchClosure {

      public static String  bench(int repeat, Closure worker)  {
        long   start = System.currentTimeMillis();
        for (int k=0; k<repeat;k++)
           worker.call();
        long  end = System.currentTimeMillis();
        double timeSecs = (end-start)/1000.0;
        return String.valueOf(timeSecs);
    }
}
