
package org.jblas.benchmark;

import java.io.PrintStream;
import org.jblas.util.Logger;

/**
 * A simple command-line style benchmarking program.
 * 
 * <p>Benchmarks matrix-matrix multiplication, and compares to a 
 * pure Java implementation</p>
 *
 * @author Mikio L. Braun
 */
public class Main {

    static Benchmark[] multiplicationBenchmarks = {
        new JavaDoubleMultiplicationBenchmark(),
        new JavaFloatMultiplicationBenchmark(),
        new NativeDoubleMultiplicationBenchmark(),
        new NativeFloatMultiplicationBenchmark(),};

    public static void printHelp() {
        System.out.printf("Usage: benchmark [opts]%n"
                + "%n"
                + "with options:%n"
                + "%n"
                + "  --arch-flavor=value     overriding arch flavor (e.g. --arch-flavor=sse2)%n"
                + "  --skip-java             don't run java benchmarks%n"
                + "  --help                  show this help%n"
                + "  --debug                 set config levels to debug%n"
                + "%njblas version " + org.jblas.Info.VERSION + "%n");
    }

    public static void main(String[] args) {
        int[] multiplicationSizes = {10, 100, 1000};
        PrintStream out = System.out;

        boolean skipJava = false;
        boolean unrecognizedOptions = false;

        Logger log = Logger.getLogger();

        log.info("jblas version is " + org.jblas.Info.VERSION);

        for (String arg : args) {
            if (arg.startsWith("--")) {
                int i = arg.indexOf('=');
                String value = null;
                if (i != -1) {
                    value = arg.substring(i + 1);
                    arg = arg.substring(0, i);
                }

                if (arg.equals("--arch-flavor")) {
                    Logger.getLogger().info("Setting arch flavor to " + value);
                    org.jblas.util.ArchFlavor.overrideArchFlavor(value);
                } else if (arg.equals("--skip-java")) {
                    skipJava = true;
                } else if (arg.equals("--help")) {
                    printHelp();
                    return;
                } else if (arg.equals("--debug")) {
                    Logger.getLogger().setLevel(Logger.DEBUG);
                } else {
                    Logger.getLogger().warning("Unrecognized option \"" + arg + "\"");
                    unrecognizedOptions = true;
                }
            }
        }
        if (unrecognizedOptions) {
            return;
        }

        out.println("Simple benchmark for jblas");
        out.println();

        out.println("Running sanity benchmarks.");
        out.println();
        org.jblas.util.SanityChecks.main(args);
        out.println();

        out.println("Each benchmark will take about 5 seconds...");

        for (Benchmark b : multiplicationBenchmarks) {
            if (skipJava) {
                if (b.getName().contains("Java")) {
                    continue;
                }
            }

            out.println();
            out.println("Running benchmark \"" + b.getName() + "\".");
            for (int n : multiplicationSizes) {
                out.printf("n = %-5d: ", n);
                out.flush();

                BenchmarkResult result = b.run(n, 5.0);

                result.printResult();
            }
        }
    }
}
