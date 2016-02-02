package javarepl;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import javarepl.expressions.Expression;
import javarepl.expressions.Import;

import java.io.File;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.option;
import static com.googlecode.totallylazy.Predicates.*;
import static com.googlecode.totallylazy.Sequences.empty;
import static com.googlecode.totallylazy.Sequences.join;
import static com.googlecode.totallylazy.Sequences.sequence;
import static javarepl.Result.functions.key;
import static javarepl.Utils.javaVersionAtLeast;
import static javarepl.Utils.randomOutputDirectory;

public class EvaluationContext {
    private final File outputDirectory;
    private final Sequence<Expression> expressions;
    private final Sequence<Result> results;
    private final Option<String> lastSource;

    private EvaluationContext(File outputDirectory, Sequence<Expression> expressions, Sequence<Result> results, Option<String> lastSource) {
        this.outputDirectory = outputDirectory;
        this.expressions = expressions;
        this.results = results;
        this.lastSource = lastSource;
    }

    public static EvaluationContext evaluationContext() {
        return new EvaluationContext(randomOutputDirectory(), defaultExpressions(), empty(Result.class), none(String.class));
    }

    
         /* 
     new Import(
                 new Import(   "import groovySci.math.plot.PlotController; \n"+
                 new Import(
                 new Import(    "import static groovySci.math.array.MatrixConvs.*; \n"+  // conversions between GroovySci Matrix and matrices of other libraries
                 new Import(                   "import static gExec.Interpreter.MatlabConnection.*; \n"+
                     new Import("import static gExec.Interpreter.SciLabConnection.*; \n"+
                
                // JFreeChart imports
                  "import JFreePlot.* \n"+
                  "import static JFreePlot.jFigure.*; \n"+
                  "import static JFreePlot.jPlot.*\n"+
                
                   "import java.awt.*; \n"+
                   "import javax.swing.*; \n"+   // Java standard UI and graphics support
                   "import static groovySci.math.io.MatIO.*;\n"+   // support for .mat Matlab files
                   "import java.awt.event.*; \n"+ 
                   "import groovy.swing.SwingBuilder; \n"+
                   "import java.text.DecimalFormat; \n"+
                   "import  static  groovySci.math.array.DoubleArray.*;\n"+
                  " import JSci.maths.*; \n"+
                  "import JSci.maths.wavelet.*; \n"+
                  "import JSci.maths.wavelet.daubechies2.*; \n"+
                  "import groovySci.math.array.*; \n"+
                   "import groovySci.FFT.ApacheFFT \n"+
                  "import static groovySci.FFT.ApacheFFT.*\n"+
                  "import static groovySci.FFT.FFTCommon.*\n"+
                
                // some very useful packages from Numerical Recipes
                "import NR.*\n"+
                "import static NR.gaussj.* \n"+
                "import com.nr.sp.*\n"+
                
                "import static java.lang.Math.*;  \n";    // standard Java math routines, allows calling directly e.g sin(9.8) instead of Math.sin(9.8)
*/                   
  
    private static Sequence<Expression> defaultJavaImports() {
        return sequence(
                new Import("import java.lang.*", "java.lang.*"),
                new Import("import java.util.*", "java.util.*"),
                new Import("import java.util.stream.*", "java.util.stream.*"),
                
                new Import("import static groovySci.math.array.BasicDSP.*", "groovySci.math.array.BasicDSP.*"),    
                new Import("import groovySci.math.array.Vec", "groovySci.math.array.Vec"),  // Vector class
                new Import("import static groovySci.math.array.Vec.*", "groovySci.math.array.Vec"),
                new Import("import groovySci.math.array.Matrix", "groovySci.math.array.Matrix"),  // Matrix class
                new Import("import static groovySci.math.array.Matrix.*", "groovySci.math.array.Matrix.*"),  
                new Import("import groovySci.math.array.PMatrix", "groovySci.math.array.PMatrix"),
                new Import("import static groovySci.math.array.PMatrix.*", "groovySci.math.array.PMatrix.*"),
                new Import("import groovySci.math.array.CCMatrix", "groovySci.math.array.CCMatrix"),
                new Import("import static groovySci.math.array.CCMatrix.*", "groovySci.math.array.CCMatrix.*"),
                new Import("import groovySci.math.array.Sparse", "groovySci.math.array.Sparse"),
                new Import("import static groovySci.math.array.Sparse.*", "groovySci.math.array.Sparse.*"),
                new Import("import groovySci.math.array.JILapack", "groovySci.math.array.JILapack"),
                new Import("import static groovySci.math.array.JILapack.*", "groovySci.math.array.JILapack.*"),
                new Import("import Jama.*", "Jama.*"),
                new Import("import static groovySciCommands.BasicCommands.*", "groovySciCommands.BasicCommands.*"),                  
                new Import("import static groovySci.math.plot.plot.*", "groovySci.math.plot.plot.*"),    // plotting routines 
                new Import("import static groovySci.math.plot.plotFunctional.*", "groovySci.math.plot.plotFunctional.*"),
                new Import("import static groovySci.math.plot.plotAdaptiveFunctional.*", "groovySci.math.plot.plotAdaptiveFunctional.*")
        
        
        ).safeCast(Expression.class);
    }

        
    private static Sequence<Expression> defaultJava8Imports() {
        return javaVersionAtLeast("1.8.0")
                ? sequence(new Import("import java.util.function.*", "java.util.function.*")).safeCast(Expression.class)
                : empty(Expression.class);
    }

    public static Sequence<Expression> defaultExpressions() {
        return join(defaultJavaImports(), defaultJava8Imports());
    }

    public File outputDirectory() {
        return outputDirectory;
    }

    public Option<String> lastSource() {
        return lastSource;
    }

    public Sequence<Result> results() {
        return results
                .reverse()
                .unique(key())
                .reverse();
    }

    public Sequence<Expression> expressions() {
        return expressions;
    }

    public <T extends Expression> Sequence<T> expressionsOfType(Class<T> type) {
        return expressions
                .filter(instanceOf(type))
                .safeCast(type);
    }

    public Option<Result> result(final String key) {
        return results().filter(where(key(), equalTo(key))).headOption();
    }

    public String nextResultKey() {
        return "res" + results().size();
    }

    public EvaluationContext lastSource(String lastSource) {
        return new EvaluationContext(outputDirectory, expressions, results, option(lastSource));
    }

    public EvaluationContext addResult(Result result) {
        return new EvaluationContext(outputDirectory, expressions, results.append(result), lastSource);
    }

    public EvaluationContext addResults(Sequence<Result> result) {
        return new EvaluationContext(outputDirectory, expressions, results.join(result), lastSource);
    }

    public EvaluationContext addExpression(Expression expression) {
        return new EvaluationContext(outputDirectory, expressions.append(expression), results, lastSource);
    }

    public EvaluationContext removeExpressionWithKey(String key) {
        return new EvaluationContext(outputDirectory, expressions.filter(where(Expression.functions.key(), not(key))), results, lastSource);
    }
}
