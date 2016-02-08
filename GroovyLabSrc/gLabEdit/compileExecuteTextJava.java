
package gLabEdit;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import gExec.Interpreter.GlobalValues;
import gExec.gLab.StreamGobbler;
import gLabGlobals.JavaGlobals;
import java.io.FileReader;

public class compileExecuteTextJava extends AbstractAction {
        int rv=0;  // the return value of the compilation phase

        static boolean processUserSelection = false;
        String tempFileName = "";

        File tempFile= null;
        public compileExecuteTextJava() {
            super("Compile and Execute Java text in editor");
            setEnabled(true);
        }

        
        
        
        
    public void executeTextExternalJava() {
       int rv=0;  // the return value of the compilation phase

       String className="";
            // execute now the compiled code
       String programText = GlobalValues.globalEditorPane.getSelectedText(); 
       String programOutput;
       if (programText != null)  { // selected text
           // put selected code in comments
           GlobalValues.globalEditorPane.replaceSelection("/* \n "+programText+" \n */"); 
           className = JOptionPane.showInputDialog("Select a name for the Java class", "userClassesStaticJava");
           programOutput = " class "+className+"  \n { \n "+
                   "\n static "+programText+ "\n}";
           tempFileName = className+".java";   // public classes and Java files should have the same name
          // System.out.println("program text = "+programOutput);
           
           processUserSelection = true;
       }

       else  { // use the whole program text
           programText = GlobalValues.globalEditorPane.getText();  // get the program's text in order to search for the class's name
       
           processUserSelection = false;
           
           programOutput = programText;
           
       int clPos = programText.indexOf("class");
       if (clPos != -1)  { // "class" exists
             int afterClassPos = clPos+6;
             // after the word "object an identifier and a "{" must exist
             programText = programText.substring(afterClassPos);
             StringTokenizer stTok = new StringTokenizer(programText, " ");
             className = stTok.nextToken();
             tempFileName = className+".java";   // public classes and Java files should have the same name
       }
       else
           return;
       }  // use the whole program text
       
         String jarFileName = GlobalValues.jarFilePath;
                  // get the user's home path
         String homePath = GlobalValues.fullJarFilePath;
         String wholePathName = tempFileName; // homePath+File.separatorChar+tempFileName;
         try {
                // take the program's text and save it to a temporary file
                
                tempFile = new File(wholePathName);
                
                if (tempFile.exists()) {  // read and append the previous file contents
                 JOptionPane.showMessageDialog(null, "File "+wholePathName+" already exist. Please specify another ", " File already exists",  JOptionPane.INFORMATION_MESSAGE);   
                 return;
                   }
               
                // write the extended Java source file
                FileWriter fw = new FileWriter(tempFile);
//                System.out.println("SOS: Writing to "+wholePathName);
  //             System.out.println("Program text = \n"+programText);
                fw.write(programText);
                fw.close();   
             
                tempFile = new File(homePath+File.separatorChar+tempFileName);
                fw = new FileWriter(tempFile);
               
                fw.write(programOutput, 0, programOutput.length());
             }
            catch (IOException ioe) {
                System.out.println("Exception trying to write to "+tempFileName);
                System.out.println("Probably you tried to execute GroovyLab with direct double-click on the .jar file");
                System.out.println("Correct this problem by making a shortcut to the desktop");
                System.out.println("or with the command:   java -jar GroovyLab.jar ");
                JOptionPane.showMessageDialog(null, "Please Start GroovyLab  from a terminal with: java -jar GroovyLab.jar,  for the external compilation to work properly", "Start from terminal", JOptionPane.WARNING_MESSAGE);
                return;
            }
       // compile the temporary file


    String selectedValue = tempFileName;
    String [] command  = new String[6];
       command[0] =  "javac";
       command[1] = "-cp";
       command[2] =  GlobalValues.jarFilePath+File.pathSeparator
               +  File.pathSeparator+JavaGlobals.jsciFile
               +JavaGlobals.numalFile+File.pathSeparator+JavaGlobals.mtjColtSGTFile+  File.pathSeparator
               +JavaGlobals.LAPACKFile+File.pathSeparator
               +JavaGlobals.ARPACKFile+File.pathSeparator
               +JavaGlobals.JASFile+homePath;
       command[3] = "-d";   // where to place output class files
       command[4] = homePath;   //  the path to save the compiled class files
            try {
                command[5] =  tempFile.getCanonicalPath();  // full filename to compile
            } catch (IOException ex) {
                System.out.println("Exception in tempFile.getCanonicalPath()");
                return;
            }

       String compileCommandString = command[0]+"  "+command[1]+"  "+command[2]+" "+command[3]+" "+command[4]+" "+command[5];

       System.out.println("compileCommand Java= "+compileCommandString);

            try {
                Runtime rt = Runtime.getRuntime();
                Process javaProcess = rt.exec(command);
                // an error message?
                StreamGobbler errorGobbler = new StreamGobbler(javaProcess.getErrorStream(), "ERROR");

                // any output?
                StreamGobbler outputGobbler = new StreamGobbler(javaProcess.getInputStream(), "OUTPUT");

                // kick them off
                errorGobbler.start();
                outputGobbler.start();

                // any error???
                javaProcess.waitFor();
                rv = javaProcess.exitValue();
                String commandString = command[0]+"  "+command[1]+"  "+command[2];
                if (rv==0)
                 System.out.println("Process:  "+commandString+"  exited successfully ");
                else
                 System.out.println("Process:  "+commandString+"  exited with error, error value = "+rv);

                 GlobalValues.bufferingImports += "\n import static "+className+".* \n ";

                } catch (IOException exio) {
                    System.out.println("IOException trying to executing "+command);
                    exio.printStackTrace();

                }
               catch (InterruptedException ie) {
                    System.out.println("Interrupted Exception  trying to executing "+command);
                    ie.printStackTrace();
                }

       // tempFile.delete(); // delete the temporary source file

       if (processUserSelection == false)  {
       if (rv==0)  {  // compilation success: proceed to run
             command  = new String[4];   // the run command
             command[0] =  "java";
             command[1] = "-cp";
             command[2] =  "."+ File.pathSeparator+ GlobalValues.jarFilePath+File.pathSeparator
                     +JavaGlobals.jsciFile
               +JavaGlobals.numalFile+File.pathSeparator+JavaGlobals.mtjColtSGTFile+  File.pathSeparator
                     +JavaGlobals.LAPACKFile+File.pathSeparator
                     +JavaGlobals.ARPACKFile+File.pathSeparator
                     +JavaGlobals.JASFile+File.pathSeparator+homePath;
             command[3] =  className;   

                  try {
               Runtime rt = Runtime.getRuntime();
               Process javaProcess = rt.exec(command);
                // an error message?
               StreamGobbler errorGobbler = new StreamGobbler(javaProcess.getErrorStream(), "ERROR");

                // any output?
                StreamGobbler outputGobbler = new StreamGobbler(javaProcess.getInputStream(), "OUTPUT");

                // kick them off
               errorGobbler.start();
               outputGobbler.start();

                // any error???
                javaProcess.waitFor();
                rv = javaProcess.exitValue();
                String commandString = command[0]+"  "+command[1]+"  "+command[2]+" "+command[3];
                if (rv==0)
                   System.out.println("Process:  "+commandString+"  exited successfully ");
                else
                   System.out.println("Process:  "+commandString+"  exited with error, error value = "+rv);

                } catch (IOException ex) {
                     System.out.println("IOException trying to executing "+command);
                     ex.printStackTrace();

                }
                catch (InterruptedException ie) {
                   System.out.println("Interrupted Exception  trying to executing "+command);
                   ie.printStackTrace();
                  }
	    }
          }
        }

        
        
        
        
        public void compileExecuteTextJava(JEditorPane editorPane) {
               String programText =editorPane.getText();  // get the program's text in order to search for the class's name
               processCode(programText);
        }
        public void compileExecuteTextJava() {
       String className="";
            // execute now the compiled code
       String programText = GlobalValues.globalEditorPane.getSelectedText(); 
       String programOutput;
       if (programText != null)  { // selected text
           // put selected code in comments
           GlobalValues.globalEditorPane.replaceSelection("/* \n "+programText+" \n */"); 
           className = JOptionPane.showInputDialog("Select a name for the Java class", "userClassesStaticJava");
           programOutput = " class "+className+"  \n { \n "+
                   "\n static "+programText+ "\n}";
           tempFileName = className+".java";   // public classes and Java files should have the same name
//           System.out.println("program text = "+programOutput);
           
           processUserSelection = true;
           programText = programOutput;
       }

       else  { // use the whole program text
       
          programText = GlobalValues.globalEditorPane.getText();  // get the program's text in order to search for the class's name
          processUserSelection = false;
       }
               processCode(programText);
        }
        
        
        
        public void compileExecuteTextJavaEmbedded(JEditorPane editorPane) {
               String programText =editorPane.getText();  // get the program's text in order to search for the class's name
               processCodeEmbedded(programText);
        }
        public void compileExecuteTextJavaEmbedded() {
               String programText = GlobalValues.globalEditorPane.getText();  // get the program's text in order to search for the class's name
               processCodeEmbedded(programText);
        }

        
        public void processCodeEmbedded(String programText) {
            String canonicalClassName="";
            String pathOfCanonicalClassName="";
            
            String className="";
            // execute now the compiled code
       int clPos = programText.indexOf("class");
       if (clPos != -1)  { // "class" exists
             int afterClassPos = clPos+6;
             // after the word "class an identifier and a "{" must exist
             String afterProgramText = programText.substring(afterClassPos, afterClassPos+50);
             StringTokenizer stTok = new StringTokenizer(afterProgramText, " {\n");
             className = stTok.nextToken();
             tempFileName = className+".java";   // public classes and Java files should have the same name
             }
       else
           return;

          String homePath=".";
          String wholePathName = tempFileName; // homePath+File.separatorChar+tempFileName;
          try {
                // take the program's text and save it to a temporary file
                tempFile = new File(wholePathName);
                FileWriter fw = new FileWriter(tempFile);
                GlobalValues.globalEditorPane.write(fw);
             }
            catch (IOException ioe) {
                System.out.println("Exception trying to write to "+wholePathName);
                System.out.println("Probably you tried to execute GroovyLab with direct double-click on the .jar file");
                System.out.println("Correct this problem by making a shortcut to the desktop");
                System.out.println("or with the command:   java -jar GroovyLab.jar ");
                JOptionPane.showMessageDialog(null, "Please Start GroovyLab from a terminal with: java -jar GroovyLab.jar,  for the external compilation to work properly", "Start from terminal", JOptionPane.WARNING_MESSAGE);
                return;
            }
       String selectedValue = tempFileName;
       System.out.println("selectedValue = "+tempFileName);
       String [] command  = new String[11];
       String toolboxes = "";
       for (int k=0; k<GlobalValues.GroovySciClassPathComponents.size();k++)
         toolboxes = toolboxes+File.pathSeparator+GlobalValues.GroovySciClassPathComponents.elementAt(k);
       System.out.println("current GroovySciPath = "+toolboxes);   

       command[0] =  "java";
       command[1] = "-classpath";
       command[2] =  "."+File.pathSeparator
               +JavaGlobals.javacFile+     File.pathSeparator
               +JavaGlobals.ejmlFile+File.pathSeparator
               +JavaGlobals.jsciFile+File.pathSeparator
               +JavaGlobals.mtjColtSGTFile+File.pathSeparator
               +JavaGlobals.ApacheCommonsFile+File.pathSeparator
               +JavaGlobals.numalFile+File.pathSeparator
               +JavaGlobals.LAPACKFile+File.pathSeparator
               +JavaGlobals.ARPACKFile+File.pathSeparator;
       command[3] =  "com.sun.tools.javac.Main";    // the name of the Java  compiler class
       command[4] = "-classpath";
       command[5] =  command[2];
       command[6] = "-sourcepath";
       command[7] =  command[2];
        try {
            command[10] = tempFile.getCanonicalPath();
            canonicalClassName = command[10];
            pathOfCanonicalClassName = canonicalClassName.substring(0, canonicalClassName.lastIndexOf(File.separatorChar));
             
        } catch (IOException ex) {
            Logger.getLogger(RSyntaxEditorMouseMotionAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        command[8] = "-d";
        command[9] = pathOfCanonicalClassName;
       
       String compileCommandString = command[0]+"  "+command[1]+"  "+command[2]+" "+command[3]+" "+command[4]+" "+command[5]+" "+command[6]+" "+command[7]+" "+command[8]+" "+command[9]+" "+command[10];


       System.out.println("compileCommand JavaEmbedded = "+compileCommandString);
       
            try {
                Runtime rt = Runtime.getRuntime();
                Process javaProcess = rt.exec(command);
                // an error message?
                StreamGobbler errorGobbler = new StreamGobbler(javaProcess.getErrorStream(), "ERROR");

                // any output?
                StreamGobbler outputGobbler = new StreamGobbler(javaProcess.getInputStream(), "OUTPUT");

                // kick them off
                errorGobbler.start();
                outputGobbler.start();

                // any error???
                javaProcess.waitFor();
                int rv = javaProcess.exitValue();
                String commandString = command[0]+"  "+command[1]+"  "+command[2];
                if (rv==0)
                 System.out.println("Process:  "+commandString+"  exited successfully ");
                else
                 System.out.println("Process:  "+commandString+"  exited with error, error value = "+rv);

                } catch (IOException exio) {
                    System.out.println("IOException trying to executing "+command);
                    exio.printStackTrace();

                }
               catch (InterruptedException ie) {
                    System.out.println("Interrupted Exception  trying to executing "+command);
                    ie.printStackTrace();
                }

       tempFile.delete(); // delete the temporary source file

       if (rv==0) {  // compilation success: proceed to run
             command  = new String[4];   // the run command
             command[0] =  "java";
             command[1] = "-cp";
             command[2] =  "."+ File.pathSeparator+ pathOfCanonicalClassName+File.pathSeparator
               +JavaGlobals.ejmlFile+File.pathSeparator
               +JavaGlobals.jsciFile+File.pathSeparator
               +JavaGlobals.mtjColtSGTFile+File.pathSeparator
               +JavaGlobals.ApacheCommonsFile+File.pathSeparator
               +JavaGlobals.numalFile+File.pathSeparator
               +JavaGlobals.LAPACKFile+File.pathSeparator
               +JavaGlobals.ARPACKFile+File.pathSeparator;
             command[3] =  className;    

                  try {
               Runtime rt = Runtime.getRuntime();
               Process javaProcess = rt.exec(command);
                // an error message?
               StreamGobbler errorGobbler = new StreamGobbler(javaProcess.getErrorStream(), "ERROR");

                // any output?
                StreamGobbler outputGobbler = new StreamGobbler(javaProcess.getInputStream(), "OUTPUT");

                // kick them off
               errorGobbler.start();
               outputGobbler.start();

                // any error???
                javaProcess.waitFor();
                rv = javaProcess.exitValue();
                String commandString = command[0]+"  "+command[1]+"  "+command[2]+" "+command[3];
                if (rv==0)
                   System.out.println("Process:  "+commandString+"  exited successfully ");
                else
                   System.out.println("Process:  "+commandString+"  exited with error, error value = "+rv);

                } catch (IOException ex) {
                     System.out.println("IOException trying to executing "+command);
                     ex.printStackTrace();

                }
                catch (InterruptedException ie) {
                   System.out.println("Interrupted Exception  trying to executing "+command);
                   ie.printStackTrace();
                  }
       } // rv==0
       }
        
        
       public void processCode(String programText) {
            String canonicalClassName="";
            String pathOfCanonicalClassName="";
            
            String className="";
            // execute now the compiled code
       int clPos = programText.indexOf("class");
       if (clPos != -1)  { // "class" exists
             int afterClassPos = clPos+6;
             // after the word "class an identifier and a "{" must exist
             String afterProgramText = programText.substring(afterClassPos, afterClassPos+50);
             StringTokenizer stTok = new StringTokenizer(afterProgramText, " {\n");
             className = stTok.nextToken();
             tempFileName = className+".java";   // public classes and Java files should have the same name
             }
       else
           return;

          String jarFileName = GlobalValues.jarFilePath;
                  
          String homePath=".";
          String wholePathName = tempFileName; // homePath+File.separatorChar+tempFileName;
          try {
                // take the program's text and save it to a temporary file
                
              
                tempFile = new File(wholePathName);
                
                if (tempFile.exists()) {  // read and append the previous file contents
                 JOptionPane.showMessageDialog(null, "File "+wholePathName+" already exist. Please specify another ", " File already exists",  JOptionPane.INFORMATION_MESSAGE);   
                 return;
                   }
               
                // write the extended Java source file
                FileWriter fw = new FileWriter(tempFile);
//                System.out.println("SOS: Writing to "+wholePathName);
//               System.out.println("Program text = \n"+programText);
                fw.write(programText);
                fw.close();
                
             }
            catch (IOException ioe) {
                System.out.println("Exception trying to write to "+wholePathName);
                System.out.println("Probably you tried to execute jlab with direct double-click on the .jar file");
                System.out.println("Correct this problem by making a shortcut to the desktop");
                System.out.println("or with the command:   java -jar jlab.jar ");
                JOptionPane.showMessageDialog(null, "Please Start jlab from a terminal with: java -jar jlab.jar,  for the external compilation to work properly", "Start from terminal", JOptionPane.WARNING_MESSAGE);
                return;
            }
       String selectedValue = tempFileName;
       System.out.println("selectedValue = "+tempFileName);
       String [] command  = new String[11];
       String toolboxes = "";
       for (int k=0; k<GlobalValues.GroovySciClassPathComponents.size();k++)
         toolboxes = toolboxes+File.pathSeparator+GlobalValues.GroovySciClassPathComponents.elementAt(k);
       System.out.println("current GroovySciPath = "+toolboxes);   // Sterg-Sterg-Sterg

       command[0] =  "java";
       command[1] = "-classpath";
       command[2] =  "."+File.pathSeparator+
               GlobalValues.jarFilePath+File.pathSeparator
               +GlobalValues.gLabLibPath+File.pathSeparator
               +JavaGlobals.javacFile+      File.pathSeparator
               +JavaGlobals.jsciFile
               +JavaGlobals.numalFile+File.pathSeparator+JavaGlobals.mtjColtSGTFile+  File.pathSeparator
               +JavaGlobals.LAPACKFile+File.pathSeparator
               +JavaGlobals.ARPACKFile+File.pathSeparator
               +JavaGlobals.JASFile
               +File.pathSeparator+toolboxes;
       command[3] =  "com.sun.tools.javac.Main";    // the name of the Java  compiler class
       command[4] = "-classpath";
       command[5] =  command[2];
       command[6] = "-sourcepath";
       command[7] =  command[2];
        try {
            command[10] = tempFile.getCanonicalPath();
            canonicalClassName = command[10];
            pathOfCanonicalClassName = canonicalClassName.substring(0, canonicalClassName.lastIndexOf(File.separatorChar));
             
        } catch (IOException ex) {
            System.out.println("Exception Compiling");
            ex.printStackTrace();
        }
        command[8] = "-d";
        command[9] = pathOfCanonicalClassName;
       
       String compileCommandString = command[0]+"  "+command[1]+"  "+command[2]+" "+command[3]+" "+command[4]+" "+command[5]+" "+command[6]+" "+command[7]+" "+command[8]+" "+command[9]+" "+command[10];


       System.out.println("compileCommand Java= "+compileCommandString);
       
            try {
                Runtime rt = Runtime.getRuntime();
                Process javaProcess = rt.exec(command);
                // an error message?
                StreamGobbler errorGobbler = new StreamGobbler(javaProcess.getErrorStream(), "ERROR");

                // any output?
                StreamGobbler outputGobbler = new StreamGobbler(javaProcess.getInputStream(), "OUTPUT");

                // kick them off
                errorGobbler.start();
                outputGobbler.start();

                // any error???
                javaProcess.waitFor();
                int rv = javaProcess.exitValue();
                String commandString = command[0]+"  "+command[1]+"  "+command[2];
                if (rv==0)
                 System.out.println("Process:  "+commandString+"  exited successfully ");
                else
                 System.out.println("Process:  "+commandString+"  exited with error, error value = "+rv);

                GlobalValues.bufferingImports += "\n import static "+className+".* \n ";

                } catch (IOException exio) {
                    System.out.println("IOException trying to executing "+command);
                    exio.printStackTrace();

                }
               catch (InterruptedException ie) {
                    System.out.println("Interrupted Exception  trying to executing "+command);
                    ie.printStackTrace();
                }

       //tempFile.delete(); // delete the temporary source file

       if (processUserSelection == false) {
       if (rv==0) {  // compilation success: proceed to run
             command  = new String[4];   // the run command
             command[0] =  "java";
             command[1] = "-cp";
             command[2] =  "."+ File.pathSeparator+ pathOfCanonicalClassName+File.pathSeparator+
                     GlobalValues.jarFilePath+File.pathSeparator+
                     JavaGlobals.jsciFile+
               JavaGlobals.numalFile+File.pathSeparator+JavaGlobals.mtjColtSGTFile+  File.pathSeparator+
                     JavaGlobals.LAPACKFile+File.pathSeparator +
                     JavaGlobals.ARPACKFile+File.pathSeparator+
                     JavaGlobals.JASFile+
                     File.pathSeparator+homePath+File.separator+toolboxes;
             command[3] =  className;    

                  try {
               Runtime rt = Runtime.getRuntime();
               Process javaProcess = rt.exec(command);
                // an error message?
               StreamGobbler errorGobbler = new StreamGobbler(javaProcess.getErrorStream(), "ERROR");

                // any output?
                StreamGobbler outputGobbler = new StreamGobbler(javaProcess.getInputStream(), "OUTPUT");

                // kick them off
               errorGobbler.start();
               outputGobbler.start();

                // any error???
                javaProcess.waitFor();
                rv = javaProcess.exitValue();
                String commandString = command[0]+"  "+command[1]+"  "+command[2]+" "+command[3];
                if (rv==0)
                   System.out.println("Process:  "+commandString+"  exited successfully ");
                else
                   System.out.println("Process:  "+commandString+"  exited with error, error value = "+rv);

                } catch (IOException ex) {
                     System.out.println("IOException trying to executing "+command);
                     ex.printStackTrace();

                }
                catch (InterruptedException ie) {
                   System.out.println("Interrupted Exception  trying to executing "+command);
                   ie.printStackTrace();
                  }
            } // rv==0
          
         }    // processUserSelection == false
       
       }


    public void actionPerformed(ActionEvent e) {
       compileExecuteTextJava();
      }
    }
 
    

