// helps to perform conveniently imports for basic application types
package gLabEdit;

import gExec.Interpreter.GlobalValues;
  // Java standard UI and graphics support

public class importsHelper {
    

 public static void  injectJavaSwing()   {
      String  javaSwingStr = "import _root_.java.awt.* ; \n"+ 
    "import _root_.java.awt.event.*\n"+
    "import _root_.javax.swing.* \n"+
    "import _root_.javax.swing.event.* \n";
    
    GlobalValues.globalEditorPane.setText(javaSwingStr + GlobalValues.globalEditorPane.getText());
    }
 
 public static void injectGroovySciImports() {
    String allImportsStr =  "import static groovySci.math.array.BasicDSP.*; \n"+   
                "import groovySci.math.array.Vec; \n"+  // Vector class
                    "import static groovySci.math.array.Vec.*; \n"+  
                    "import groovySci.math.array.Matrix; \n"+  // Matrix class
                    "import static groovySci.math.array.Matrix.*; \n"+  
                    "import groovySci.math.array.CCMatrix; \n"+  // Matrix class
                    "import static groovySci.math.array.CCMatrix.*; \n"+  
                    "import groovySci.math.array.Sparse; \n"+  // Sparse Matrix class
                    "import static groovySci.math.array.Sparse.*; \n"+  
                    "import groovySci.math.array.JILapack; \n"+  // JLapack Matrix class
                    "import static groovySci.math.array.JILapack.*; \n"+  
                    "import Jama.*\n"+
                    "import numal.*;\n"+    // numerical analysis library routines
                   "import static groovySci.math.plot.plot.*;\n"+     // plotting routines 
                   "import static groovySci.math.plot.plotFunctional.*; \n"+
                    "import static groovySci.math.plot.plotAdaptiveFunctional.*; \n"+
    
                   "import java.awt.*; \n"+
                   "import javax.swing.*; \n"+   // Java standard UI and graphics support
                   "import static groovySci.math.io.MatIO.*;\n"+   // support for .mat Matlab files
                   "import java.awt.event.*; \n"+ 
                   "import groovy.swing.SwingBuilder; \n"+
                   "import java.text.DecimalFormat; \n"+
                   "import static groovySciCommands.BasicCommands.*;\n"+  // support for GroovySci's console commands
                   "import  static  groovySci.math.array.DoubleArray.*;\n"+
                  " import JSci.maths.*; \n"+
                  "import JSci.maths.wavelet.*; \n"+
                  "import JSci.maths.wavelet.daubechies2.*; \n"+
                  "import groovySci.math.array.*; \n"+
                   "import groovySci.FFT.ApacheFFT \n"+
                    "import static groovySci.FFT.ApacheFFT.*\n"+
                "import NR.*\n"+
                "import static NR.gaussj.* \n"+
                "import com.nr.sp.*\n"+
                "import static java.lang.Math.*;  \n";    // standard Java math routines, allows calling directly e.g sin(9.8) instead of Math.sin(9.8)
                
    GlobalValues.globalEditorPane.setText(allImportsStr + GlobalValues.globalEditorPane.getText());
 }

 public static void injectBasicPlotsImports() {
    String basicPlotsImportsStr =  "import static groovySci.math.plot.plot.*;\n"+     // plotting routines 
                   "import static groovySci.math.plot.plotFunctional.*; \n"+
                    "import static groovySci.math.plot.plotAdaptiveFunctional.*; \n"+
    
                   "import java.awt.*; \n"+
                   "import javax.swing.*; \n";   // Java standard UI and graphics support
                   
    GlobalValues.globalEditorPane.setText(basicPlotsImportsStr  + GlobalValues.globalEditorPane.getText());
 }

  // the NUMAL library related staff
  public static void injectNumAl() {
    String  numAlImports  =    "import  java.util.Vector ; \n"+
                    "import  numal.Algebraic_eval.* \n"+
                    "import  numal.Analytic_eval.* \n"+
                    "import  numal.Analytic_problems.* \n"+
                    "import  numal.Approximation.* \n"+
                    "import  numal.Basic.* \n"+
                    "import  numal.FFT.* \n"+
                    "import  numal.Linear_algebra.* \n"+
                    "import  numal.Special_functions.* \n"+
                    "import java.text.DecimalFormat";
    
    GlobalValues.globalEditorPane.setText(numAlImports + GlobalValues.globalEditorPane.getText());
  }
  
/*
  def injectNumAl()  = {
      val numAlStr = "\n importNumAl \n\n"
      scalaExec.Interpreter.GlobalValues.editorPane.setText(numAlStr+scalaExec.Interpreter.GlobalValues.editorPane.getText)
    }
    
  
   def injectNumAlDirectly()  = {
      val numAlStr = """
       import _root_.java.util.Vector ; 
    import _root_.numal._ ; 
    import _root_.numal.Algebraic_eval._;
    import _root_.numal.Analytic_eval._
    import _root_.numal.Analytic_problems._
    import _root_.numal.Approximation._
    import _root_.numal.Basic._;
    import _root_.numal.FFT._;
    import  _root_.numal.Linear_algebra._; 
    import _root_.numal.Special_functions._;
    import java.text.DecimalFormat 
    """
    scalaExec.Interpreter.GlobalValues.editorPane.setText(numAlStr + GlobalValues.editorPane.getText)
    }


   def importScalaSciDefaultMat() = {
     GlobalValues.globalInterpreter.interpret("""
        import _root_.scalaSci.Vec;
        import _root_.scalaSci.Matrix ; 
        import _root_.scalaSci.Vec._ ; 
        import _root_.scalaSci.RichNumber; 
        import _root_.scalaSci.Matrix._ ; 
        import _root_.scalaSci.RichDouble1DArray ; 
        import _root_.scalaSci.RichDouble2DArray ;
        import _root_.scalaSci.RichDouble1DArray._ ; 
        import _root_.scalaSci.RichDouble2DArray._ ;
        import _root_.scalaSci.Mat ; 
        import _root_.scalaSci.Mat._ ; 
        import _root_.scalaSci.StaticMaths._ ; 
                  """)
   }

   def injectScalaSciDefaultMat()  = {
      val str = "\n importScalaSciDefaultMat\n\n"
      scalaExec.Interpreter.GlobalValues.editorPane.setText(str+scalaExec.Interpreter.GlobalValues.editorPane.getText)
    }

  def injectScalaSciDefaultMatDirectly() = {
    val defaultMatStr =   """
import _root_.scalaSci.Vec;
        import _root_.scalaSci.Matrix ; 
        import _root_.scalaSci.Vec._ ; 
        import _root_.scalaSci.RichNumber; 
        import _root_.scalaSci.Matrix._ ; 
        import _root_.scalaSci.RichDouble1DArray ; 
        import _root_.scalaSci.RichDouble2DArray ;
        import _root_.scalaSci.RichDouble1DArray._ ; 
        import _root_.scalaSci.RichDouble2DArray._ ;
        import _root_.scalaSci.Mat ; 
        import _root_.scalaSci.Mat._ ; 
        import _root_.scalaSci.StaticMaths._ ; 
        """
    scalaExec.Interpreter.GlobalValues.editorPane.setText(defaultMatStr + GlobalValues.editorPane.getText)
  }
  
   def importScalaSciEJMLMat() = {
     GlobalValues.globalInterpreter.interpret("""
        import _root_.scalaSci.Vec;
        import _root_.scalaSci.Matrix ; 
        import _root_.scalaSci.Vec._ ; 
        import _root_.scalaSci.RichNumber; 
        import _root_.scalaSci.Matrix._ ; 
        import _root_.scalaSci.RichDouble1DArray ; 
        import _root_.scalaSci.RichDouble2DArray ;
        import _root_.scalaSci.RichDouble1DArray._ ; 
        import _root_.scalaSci.RichDouble2DArray._ ;
        import _root_.scalaSci.EJML.Mat ; 
        import _root_.scalaSci.EJML.BMat ; 
        import _root_.scalaSci.EJML.Mat._ ; 
        import _root_.scalaSci.EJML.BMat._ ; 
        import _root_.scalaSci.EJML.StaticMathsEJML._ ; 
                      """)
   }

   def injectScalaSciEJMLMat()  = {
      var str = "\n importScalaSciEJMLMat\n\n"
      scalaExec.Interpreter.GlobalValues.editorPane.setText(str+scalaExec.Interpreter.GlobalValues.editorPane.getText)
    }
    
  def injectScalaSciEJMLMatDirectly() = {
    val EJMLStr =   """
        import _root_.scalaSci.Vec;
        import _root_.scalaSci.Matrix ; 
        import _root_.scalaSci.Vec._ ; 
        import _root_.scalaSci.RichNumber; 
        import _root_.scalaSci.Matrix._ ; 
        import _root_.scalaSci.RichDouble1DArray ; 
        import _root_.scalaSci.RichDouble2DArray ;
        import _root_.scalaSci.RichDouble1DArray._ ; 
        import _root_.scalaSci.RichDouble2DArray._ ;
        import _root_.scalaSci.EJML.Mat ; 
        import _root_.scalaSci.EJML.BMat ; 
        import _root_.scalaSci.EJML.Mat._ ; 
        import _root_.scalaSci.EJML.BMat._ ; 
        import _root_.scalaSci.EJML.StaticMathsEJML._ ; 
                """
    scalaExec.Interpreter.GlobalValues.editorPane.setText(EJMLStr + GlobalValues.editorPane.getText)
  }
  
   def importScalaSciMTJMat() = {
     GlobalValues.globalInterpreter.interpret("""
        import _root_.scalaSci.Vec;
        import _root_.scalaSci.Matrix ; 
        import _root_.scalaSci.Vec._ ; 
        import _root_.scalaSci.RichNumber; 
        import _root_.scalaSci.Matrix._ ; 
        import _root_.scalaSci.RichDouble1DArray ; 
        import _root_.scalaSci.RichDouble2DArray ;
        import _root_.scalaSci.RichDouble1DArray._ ; 
        import _root_.scalaSci.RichDouble2DArray._ ;
        import _root_.scalaSci.MTJ.Mat ; 
        import _root_.scalaSci.MTJ.Mat._ ; 
        import _root_.scalaSci.MTJ.StaticMathsMTJ._ ;
                                                        
                      """)
   }

   def injectScalaSciMTJMat()  = {
      val str = "\n importScalaSciMTJMat\n\n"
      scalaExec.Interpreter.GlobalValues.editorPane.setText(str+scalaExec.Interpreter.GlobalValues.editorPane.getText)
    }
    
    def injectScalaSciMTJMatDirectly = {
      val mtjStr = """
        import _root_.scalaSci.Vec;
        import _root_.scalaSci.Matrix ; 
        import _root_.scalaSci.Vec._ ; 
        import _root_.scalaSci.RichNumber; 
        import _root_.scalaSci.Matrix._ ; 
        import _root_.scalaSci.RichDouble1DArray ; 
        import _root_.scalaSci.RichDouble2DArray ;
        import _root_.scalaSci.RichDouble1DArray._ ; 
        import _root_.scalaSci.RichDouble2DArray._ ;
        import _root_.scalaSci.MTJ.Mat ; 
        import _root_.scalaSci.MTJ.Mat._ ; 
        import _root_.scalaSci.MTJ.StaticMathsMTJ._ ;
"""
      scalaExec.Interpreter.GlobalValues.editorPane.setText(mtjStr+scalaExec.Interpreter.GlobalValues.editorPane.getText)
    
    }

  def importScalaSciCommonMathsMat() = {
     GlobalValues.globalInterpreter.interpret("""
        import _root_.scalaSci.Vec;
        import _root_.scalaSci.Matrix ; 
        import _root_.scalaSci.Vec._ ; 
        import _root_.scalaSci.RichNumber; 
        import _root_.scalaSci.Matrix._ ; 
        import _root_.scalaSci.RichDouble1DArray ; 
        import _root_.scalaSci.RichDouble2DArray ;
        import _root_.scalaSci.RichDouble1DArray._ ; 
        import _root_.scalaSci.RichDouble2DArray._ ;
        import _root_.scalaSci.CommonMaths.Mat ; 
        import _root_.scalaSci.CommonMaths.Mat._ ; 
        import _root_.scalaSci.CommonMaths.StaticMathsCommonMaths._ ; 
          """)
   }

   def injectScalaSciCommonMathsMat()  = {
      val str = "\n importScalaSciCommonMathsMat \n\n"
      scalaExec.Interpreter.GlobalValues.editorPane.setText(str+scalaExec.Interpreter.GlobalValues.editorPane.getText)
    }
  
  def injectScalaSciCommonMathsMatDirectly() = {
    val cmStr = """
        import _root_.scalaSci.Vec;
        import _root_.scalaSci.Matrix ; 
        import _root_.scalaSci.Vec._ ; 
        import _root_.scalaSci.RichNumber; 
        import _root_.scalaSci.Matrix._ ; 
        import _root_.scalaSci.RichDouble1DArray ; 
        import _root_.scalaSci.RichDouble2DArray ;
        import _root_.scalaSci.RichDouble1DArray._ ; 
        import _root_.scalaSci.RichDouble2DArray._ ;
        import _root_.scalaSci.CommonMaths.Mat ; 
        import _root_.scalaSci.CommonMaths.Mat._ ; 
        import _root_.scalaSci.CommonMaths.StaticMathsCommonMaths._ ; 
        """  
scalaExec.Interpreter.GlobalValues.editorPane.setText(cmStr+scalaExec.Interpreter.GlobalValues.editorPane.getText)    

  }
  
  def importIO() = {
    GlobalValues.globalInterpreter.interpret("""
            import java.text.DecimalFormat;
            import System.out._;
            import scalaSci.math.io.XMLMethods._
            import  _root_.scalaSciCommands.BasicCommands; 
            import  _root_.scalaSciCommands.BasicCommands._; 
            import _root_.scalaSci.math.io.MatIO._ ; 
            import _root_.scalaSci.math.io.ioUtils._ ; 
            """)
  }
          
  
  def injectIO()  = {
      val ioStr = "\n importIO\n\n"
      scalaExec.Interpreter.GlobalValues.editorPane.setText(ioStr+scalaExec.Interpreter.GlobalValues.editorPane.getText)
    }
    
 def injectIODirectly() = {
   val ioStr = """
            import java.text.DecimalFormat;
            import System.out._;
            import scalaSci.math.io.XMLMethods._
            import  _root_.scalaSciCommands.BasicCommands; 
            import  _root_.scalaSciCommands.BasicCommands._; 
            import _root_.scalaSci.math.io.MatIO._ ; 
            import _root_.scalaSci.math.io.ioUtils._ ; 
   """
    scalaExec.Interpreter.GlobalValues.editorPane.setText(ioStr+scalaExec.Interpreter.GlobalValues.editorPane.getText)
 } 

def importLAPACK() = {
  GlobalValues.globalInterpreter.interpret("""
     import _root_.org.netlib.lapack.LAPACK;
     import _root_.scalaSci.ILapack._;
     import _root_.scalaSci.ILapack.Eig; 
      """
    );
}

  
  def injectLAPACK()  = {
      val LAPACKStr = "\n importLAPACK\n\n"
      scalaExec.Interpreter.GlobalValues.editorPane.setText(LAPACKStr+scalaExec.Interpreter.GlobalValues.editorPane.getText)
    }

  def injectLAPACKDirectly = {
     val LAPACKStr = """
      import _root_.org.netlib.lapack.LAPACK
     import _root_.scalaSci.ILapack._
     import _root_.scalaSci.ILapack.Eig
      """
      scalaExec.Interpreter.GlobalValues.editorPane.setText(LAPACKStr+scalaExec.Interpreter.GlobalValues.editorPane.getText)
    
  }
  
  def importComputerAlgebra() = {
  GlobalValues.globalInterpreter.interpret("""
           import _root_.scala._;
           import  _root_.scala.collection._; 
           import numerics.Numerics._; 
           import org.matheclipse.core.eval.EvalUtilities;
           import org.matheclipse.core.expression.F;
           import org.matheclipse.core.form.output.OutputFormFactory;
           import org.matheclipse.core.form.output.StringBufferWriter;
           import org.matheclipse.core.interfaces.IExpr;
            import _root_.scalaSci.Complex; 
            import _root_.scalaSci.Complex._; 
            import _root_.PatRec.PatternRecognition._; 
            """)
   
  }  
  
  
  def injectComputerAlgebra()  = {
      val computerAlgebraStr = "\n importComputerAlgebra\n\n"
      scalaExec.Interpreter.GlobalValues.editorPane.setText(computerAlgebraStr+scalaExec.Interpreter.GlobalValues.editorPane.getText)
    }

  def injectComputerAlgebraDirectly() = {
    val computerAlgebraStr = """
             import _root_.scala._;
           import  _root_.scala.collection._; 
           import numerics.Numerics._; 
           import org.matheclipse.core.eval.EvalUtilities;
           import org.matheclipse.core.expression.F;
           import org.matheclipse.core.form.output.OutputFormFactory;
           import org.matheclipse.core.form.output.StringBufferWriter;
           import org.matheclipse.core.interfaces.IExpr;
            import _root_.scalaSci.Complex; 
            import _root_.scalaSci.Complex._; 
            import _root_.PatRec.PatternRecognition._; 
  """
      scalaExec.Interpreter.GlobalValues.editorPane.setText(computerAlgebraStr+scalaExec.Interpreter.GlobalValues.editorPane.getText)
    
  }
    
  def importApacheCommons() = {
      GlobalValues.globalInterpreter.interpret(GlobalValues.ApacheCommonsImports)
    }
    
  
  def injectApacheCommons()  = {
      val apacheCommonsStr = "\n importApacheCommons\n\n"
      scalaExec.Interpreter.GlobalValues.editorPane.setText(apacheCommonsStr+scalaExec.Interpreter.GlobalValues.editorPane.getText)
    }

  def injectApacheCommonsDirectly = {
    val apacheCommonsStr = GlobalValues.ApacheCommonsImports
      scalaExec.Interpreter.GlobalValues.editorPane.setText(apacheCommonsStr+scalaExec.Interpreter.GlobalValues.editorPane.getText)
    
  }  
}            
*/            
    




}
