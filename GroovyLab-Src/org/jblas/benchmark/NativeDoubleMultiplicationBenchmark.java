
package org.jblas.benchmark;

import org.jblas.DoubleMatrix;
import static org.jblas.DoubleMatrix.*;

/**
 *
 */
class NativeDoubleMultiplicationBenchmark implements Benchmark {

    public String getName() {
        return "native matrix multiplication, double precision";
    }

    public BenchmarkResult run(int size, double seconds) {
        int counter = 0;
        long ops = 0;

        DoubleMatrix A = randn(size, size);
        DoubleMatrix B = randn(size, size);
        DoubleMatrix C = randn(size, size);

        Timer t = new Timer();
        t.start();
        while (!t.ranFor(seconds)) {
            A.mmuli(B, C);
            counter++;
            ops += 2L * size * size * size;
        }
        t.stop();

        return new BenchmarkResult(ops, t.elapsedSeconds(), counter);
    }
}
