

package org.jblas.benchmark;

import org.jblas.FloatMatrix;
import static org.jblas.FloatMatrix.*;

/**
 *
 */
class NativeFloatMultiplicationBenchmark implements Benchmark {

    public String getName() {
        return "native matrix multiplication, single precision";
    }

    public BenchmarkResult run(int size, double seconds) {
        int counter = 0;
        long ops = 0;

        FloatMatrix A = randn(size, size);
        FloatMatrix B = randn(size, size);
        FloatMatrix C = randn(size, size);

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
