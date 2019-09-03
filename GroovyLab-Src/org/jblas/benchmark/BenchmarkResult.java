
package org.jblas.benchmark;

/**
 *
 */
class BenchmarkResult {
    long numOps;
    double duration;
    int iterations;

    BenchmarkResult(long numOps, double duration, int iterations) {
        this.numOps = numOps;
        this.duration = duration;
        this.iterations = iterations;
    }

    void printResult() {
        System.out.printf("%6.3f GFLOPS (%d iterations in %.1f seconds)%n",
                numOps / duration / 1e9,
                iterations,
                duration);
    }
}
