

package org.jblas.benchmark;

/**
 *
 * @author mikio
 */
class Timer {
    long startTime;
    long stopTime;

    Timer() {
        startTime = -1;
        stopTime = -1;
    }
    
    void start() {
        startTime = System.nanoTime();
    }
    
    long stop() {
        stopTime = System.nanoTime();
        return stopTime - startTime;
    }

    boolean ranFor(double seconds) {
        return (System.nanoTime() - startTime) / 1e9 >= seconds;
    }

    double elapsedSeconds() {
        return (stopTime - startTime) / 1e9;
    }
}
