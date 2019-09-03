package expandRunTime

import org.codehaus.groovy.transform.*
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.control.*
import org.codehaus.groovy.control.messages.*
import org.codehaus.groovy.control.io.ReaderSource
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.syntax.SyntaxException

import javax.tools.*
import java.lang.reflect.*
import java.util.regex.*
import groovy.io.FileType


// compile with:
// groovyc -cp D:\NBProjects\GroovyLab\GroovyLab.jar CompileJavaTransformation.groovy

@GroovyASTTransformation(phase = CompilePhase.CLASS_GENERATION)
public class CompileJavaTransformation extends AbstractASTTransformation implements ASTTransformation {

  public class CompilationListener implements DiagnosticListener<JavaFileObject> {
      private MethodNode _methodNode;

      public CompilationListener(MethodNode methodNode) {
          _methodNode = methodNode;
      } 

      public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
          if(diagnostic != null) {
              String msg = diagnostic.getMessage(null);
              msg = msg.replaceFirst(/\s*location: .*$/,"")
              addError(msg, _methodNode); 
          }
      }
  }

  private SourceUnit _sourceUnit;

  protected void init(ASTNode[] nodes, SourceUnit sourceUnit) {
     super.init(nodes, sourceUnit)
     _sourceUnit = sourceUnit
  }

   public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        init(astNodes, sourceUnit)
        MethodNode annotatedMethod = astNodes[1]
        ClassNode declaringClass = annotatedMethod.declaringClass

        //
        // Obtain the source for the method from the AST and create a new class for it.
        //
        Map<String,String> newClassDefinition = createNewClassDefinition(annotatedMethod, sourceUnit)
        String newClassName = newClassDefinition.className
        JavaFileObject newClassFileObject = getJavaFileObject(newClassName, newClassDefinition.classDefinition)
        //println "${newClassDefinition.classDefinition}"

        //
        // Compile the new class and save it to disk.
        //
        CompileUnit compileUnit = declaringClass.module.unit
        GroovyClassLoader loader = compileUnit.classLoader
        CompilerConfiguration config = compileUnit.config
        String classOutputDir = config.targetDirectory
        if(!classOutputDir) {
            classOutputDir = new File(".").getAbsolutePath()
        }
        DiagnosticListener listener = new CompilationListener(annotatedMethod)
        compile(newClassFileObject, classOutputDir, null) // listener)

        //
        // Remove any old compiled classes that are hanging around.
        //
        removeOldCompiledClasses(classOutputDir, annotatedMethod)

    //    Object[] args = ['test']
    //    runIt(classOutputDir, loader, newClassName, annotatedMethod.name, args)

        //
        // Replace the original method with a call to the static method in the new class.
        //
        replaceMethodBody(newClassName, annotatedMethod)

    }

   private static JavaFileObject getJavaFileObject(String className, String content) {
       return new InMemoryJavaFileObject(className, content)
   } 

    private static String convertMethodToSource(MethodNode node, SourceUnit sourceUnit) {
	ReaderSource sourceFileReader = sourceUnit.getSource()
	int first = node.getLineNumber()
	int last = node.getLastLineNumber()
	StringBuilder result = new StringBuilder()
		
	for (int line in first..last) {
	   String content = sourceFileReader.getLine(line, null)
           if(content.contains("@")) {
               continue;
           } 
            if(first == last) {
                content = content[node.getColumnNumber()-1 .. node.getLastColumnNumber()-2]
            }
            else {
    	        if (line == first) {
                    content = content[node.getColumnNumber()-1 .. -1]
                }
            }
    
    	    if (line == last) {
                content = content[0 .. node.getLastColumnNumber()-2]
             }
	     result.append(content).append('\n')
        }
        return result.toString()
    }


    private Map<String, String> createNewClassDefinition(MethodNode methodNode, SourceUnit sourceUnit) {
       ClassNode declaringClass = methodNode.declaringClass
       ModuleNode moduleNode = declaringClass.module

       List<ImportNode> imports = moduleNode.imports + moduleNode.starImports + moduleNode.staticImports.values() + moduleNode.staticStarImports.values()
       StringBuilder importStatements = new StringBuilder()
       imports.each({importStatements.append("${getImportStatement(it)};\n")});

       String packageName = methodNode.declaringClass.packageName
       String className = methodNode.declaringClass.nameWithoutPackage 
       String newClassName = "${className}_${System.currentTimeMillis()}"
       String methodText = convertMethodToSource(methodNode, sourceUnit)

       int modifiers = methodNode.modifiers
       String accessModifier = ""
       if (Modifier.isPublic(modifiers)) {
          accessModifier = "public"
       } else if (Modifier.isPrivate(modifiers)) {
           accessModifier = "private"
       } else if (Modifier.isProtected(modifiers)) {
           accessModifier = "protected"
       }
    
       //
       // Remove static modifier and access modifiers. The method MUST be public and static.
       //
       methodText = methodText.replaceFirst(accessModifier, "")
       methodText = methodText.replaceFirst("static", "")

       String packageStatement = (packageName)?"package ${packageName};":""
       

       String newClassDefinition = """
${packageStatement};
${importStatements}
public class ${newClassName} {
public static $methodText
}
"""
       String fullyQualifiedNewClassName = (packageName)?(packageName + "."):""  + newClassName;
       return [className:fullyQualifiedNewClassName, classDefinition:newClassDefinition]
    }

   private static String getImportStatement(ImportNode node) {
        String typeName = node.getClassName();
        if (node.isStar() && !node.isStatic()) {
            return "import " + node.getPackageName() + "*";
        }
        if (node.isStar()) {
            return "import static " + typeName + ".*";
        }
        if (node.isStatic()) {
            return "import static " + typeName + "." + node.getFieldName();
        }
            return "import " + typeName;
   }


   private static void compile(JavaFileObject file, String classOutputDir, DiagnosticListener listener) {
       JavaCompiler compiler = ToolProvider.getSystemJavaCompiler()
       StandardJavaFileManager fileManager = compiler.getStandardFileManager(listener, Locale.ENGLISH, null)
       Iterable options = ["-d", classOutputDir]
//       JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, listener, options, null, [file])
       Writer writer = new StringWriter()
       JavaCompiler.CompilationTask task = compiler.getTask(writer, fileManager, null, options, null, [file])
       Boolean result = task.call()
       if (result == true) {
           // println("${file.name}  successfully compiled.")
       }
       else {
//           println(listener.report())
           println(writer.toString())
       }
   }


   //
   // This method is for testing purposed to ensure the new class can be located and the method can be executed.
   //
   private static void runIt(String classOutputDir, ClassLoader parent, String newClassName, String methodName, Object[] args) {
       File file = new File(classOutputDir)

       try {
           URL url = file.toURL()
           URL[] urls = [url]
           ClassLoader loader = new URLClassLoader(urls, parent)
           Class thisClass = loader.loadClass(newClassName)

           Class[] params = args.collect({it.class})
           Object instance = thisClass.newInstance()
           Method thisMethod = thisClass.getDeclaredMethod(methodName, params)

           thisMethod.invoke(instance,args) 
       }
       catch (MalformedURLException e) {}
       catch (ClassNotFoundException e) {}
       catch (Exception ex) {
           ex.printStackTrace()
       }
    }

    //
    // Replace the body of the original method with a call to the static method in the new class.
    //
    private static void replaceMethodBody(String newClassName, MethodNode method) {
        String newMethodCall = "${newClassName}.${method.name}"
        String arguments = "(${method.parameters.collect({it}).join(',')})" 
        newMethodCall += arguments
        def methodCallAST = new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, false, newMethodCall) 
        method.setCode(methodCallAST[0]) 
     }

     //
     // Remove old compiled files.
     //
  //   private static int MAX_AGE = 8*60*60*100 // 8 hours
     private static void removeOldCompiledClasses(String classOutputDir, MethodNode methodNode) {
        String className = methodNode.declaringClass.nameWithoutPackage
        Pattern classNamePattern = ~/${className}_(\d+)\.class/
        File dir = new File(classOutputDir)
        long currentTime = System.currentTimeMillis() 
        dir.eachFileRecurse(FileType.FILES, {
            Matcher matches = (it.name =~ classNamePattern)
            if(matches) {
//              Long ts = new Long(matches[0][1])
    //          if(currentTime - ts > MAX_AGE) {
                  it.delete()
       //      }
            }
         })
     }

    public void addError(String msg, ASTNode expr) {
        ErrorCollector collector = _sourceUnit.getErrorCollector();
        collector.addFatalError(new SyntaxErrorMessage(
                new SyntaxException(msg + '\n', expr.getLineNumber(), expr.getColumnNumber(),
                        expr.getLastLineNumber(), expr.getLastColumnNumber()),
                _sourceUnit));

    }
}
