

package org.jblas.benchmark;

/**
 *
 * @author mikio
 */
interface Benchmark {

    public String getName();

    public BenchmarkResult run(int size, double seconds);
}
