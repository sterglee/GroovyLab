package gLabEdit;


import gExec.Interpreter.GlobalValues;
import gExec.gLab.EditorPaneHTMLHelp;
import gLabGlobals.JavaGlobals;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.script.ScriptException;
import javax.swing.*;
import javax.swing.text.BadLocationException;

import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import jdk.jshell.SnippetEvent;
import jdk.jshell.SourceCodeAnalysis;
import jdk.jshell.VarSnippet;
import org.codehaus.groovy.control.CompilationFailedException;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;







  
public class gLabEditor  extends JFrame implements WindowListener {
        JMenuBar mainJMenuBar;

        JMenu  fileMenu;
        JMenuItem  saveEditorTextJMenuItem;
        JMenuItem  saveAsEditorTextJMenuItem;
        JMenuItem   loadEditorTextJMenuItem;
        JMenuItem exitJMenuItem;
        JMenu recentPaneFilesMenu = new JMenu("Recent Files");  // created dynamically to keep the recent files list
        JMenuItem clearRecentFilesJMenuItem;    
        JMenuItem  recentFileMenuItem;
        JMenu helpMenu;
        
        JMenu codeBufferingMenu = new JMenu("Code Buffering");
        JMenuItem codeBufferingMenuItem = new JMenuItem("Buffer selected class(es) code (CTRL-F8)");
        JMenuItem editCodeBufferingMenuItem;
        JMenuItem clearCodeBufferingMenuItem;

        
        JMenu switchLibrariesMenu = new JMenu("Switch Libraries");
        JMenuItem switchJBLASMenuItem = new JMenuItem("Switch To JBLAS");

        JMenu importsMenu = new JMenu("Imports");
        JMenuItem bufferImportsJMenuItem = new JMenuItem("Buffer selected imports (F8)");
        JMenuItem bufferedFrameJMenuItem = new JMenuItem(new bufferedContentsAction());
        
        JMenuItem clearBufferedImportsJMenuItem = new JMenuItem("Clear buffered Imports");
        private JTextField  searchField;
        private JCheckBox regexCB;
        private JCheckBox matchCaseCB;
        static public  JLabel  progressComputationLabel = new JLabel("Computing ... ");
        
        private boolean forward=true;
        private JButton gotoLineButton;
        private JTextField gotoLineField;
        
    static boolean documentEditsPendable;
    org.fife.ui.rsyntaxtextarea.RSyntaxTextArea  jep;
    public static  JFrame currentFrame  = null;
    public boolean editorTextSaved = false;
    public String editedFileName;   // the full pathname of the file being currently edited
    public static String titleStr = "GroovyLab programmer's editor - Indy version ";

    public  Vector<String>  recentPaneFiles = new Vector<String>();  // keeps the full names of the recent files
    public  String  fileWithFileListOfPaneRecentFiles = "recentsPaneFile.txt"; // the list of the recent editor's pane files

    public RTextScrollPane  scrPane;


    public EditorKeyMouseHandler   keyMouseHandler = new EditorKeyMouseHandler();
    
    public void gLabEdit(String selectedValue) {
                       
      
                   FileReader fr = null;
            try {
                fr = new FileReader(selectedValue);
                jep.read(fr, null);
                
            } catch (FileNotFoundException ex) {
                System.out.println("file "+selectedValue+" not found");
            }
            catch (IOException ex) {
                    System.out.println("cannot close file "+selectedValue);
                }
/*            finally {
                try {
                    fr.close();
                } 
                catch (IOException ex) {
                    System.out.println("cannot close file "+selectedValue);
                }
            }*/
            
            editedFileName = selectedValue;   // current file is the new loaded one
            editorTextSaved = false;  // a freshly loaded file doesn't require saving
            gLabEditor.currentFrame.setTitle(titleStr+":  File: "+editedFileName);
           
    }
    
    
  
    public void saveRecentPaneFiles() {  // the file that keeps the recent files list is kept in GlobalValues.gLabRecentFilesList
                                                                  // at the same directory as the GroovyLab.jar executable, i.e. GlobalValues.jarFilePath
         //create streams
         try {
    // open the file for writing the recent files         
            FileOutputStream output = new FileOutputStream(fileWithFileListOfPaneRecentFiles);  

            //create writer stream
           OutputStreamWriter  recentsWriter= new OutputStreamWriter(output);
            int  fileCnt=0;  // restrict the maximum number of recent files

           for (int k=0; k<recentPaneFiles.size(); k++) {
                String currentRecentFile = (String)recentPaneFiles.elementAt(k)+"\n";
                recentsWriter.write(currentRecentFile, 0, currentRecentFile.length());
                if (fileCnt++ == GlobalValues.maxNumberOfRecentFiles)  break;
            }
            recentsWriter.close();
            output.close();
    }
        catch(java.io.IOException except)
        {
            System.out.println("IO exception in saveRecentFiles");
            System.out.println(except.getMessage());
            except.printStackTrace();
        }
    }

    // update the recent files menu with the items taken from recentFiles
    public void updateRecentPaneFilesMenu()
    {
           recentPaneFilesMenu.removeAll();  // clear previous menu items
           recentPaneFilesMenu.setFont(GlobalValues.uifont);
           clearRecentFilesJMenuItem = new JMenuItem("Clear the list of recent files");
           clearRecentFilesJMenuItem.setFont(GlobalValues.uifont);
           clearRecentFilesJMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                recentPaneFiles.clear();
                recentPaneFilesMenu.removeAll();
            }
        });

           recentPaneFilesMenu.add(clearRecentFilesJMenuItem);
           
           int numberRecentFiles = recentPaneFiles.size();
        for (int k=numberRecentFiles-1; k>=0; k--)  {     // reverse order for displaying the most recently loaded first
            final String  recentFileName = (String)recentPaneFiles.elementAt(k);   // take the recent filename
            recentFileMenuItem = new JMenuItem(recentFileName);
            recentFileMenuItem.setFont(GlobalValues.uifont);
            recentFileMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
             gLabEdit(recentFileName);   // reload the recent file in editor
             
            // update the workingDir
            String pathOfLoadFileName = recentFileName.substring(0, recentFileName.lastIndexOf(File.separatorChar));
            GlobalValues.workingDir = pathOfLoadFileName;
                }
            });
            recentPaneFilesMenu.add(recentFileMenuItem);    // add the menu item corresponding to the recent file
        }  // for all the recently accessed files 

            recentPaneFilesMenu.setToolTipText("Tracks \"Saved As\" Files");
            mainJMenuBar.add(recentPaneFilesMenu);   // finally add the recent files menu to the main menu bar
        
       }
    
  // load the recent files list from the disk updating also the menu
    public  void loadRecentPaneFiles() {
         // create streams
       
        boolean exists = (new File(fileWithFileListOfPaneRecentFiles)).exists();
if (exists) {
    
        try {
  // open the file containing the stored list of recent files
             FileInputStream input = new FileInputStream(fileWithFileListOfPaneRecentFiles);
             
             //create reader stream
           BufferedReader  recentsReader= new BufferedReader(new InputStreamReader(input));

          recentPaneFiles.clear();    // clear the Vector of recent files
          String currentLine;     // refill it from disk
          while ((currentLine = recentsReader.readLine()) != null)
              if (recentPaneFiles.indexOf(currentLine) == -1)    // file not already in list
                recentPaneFiles.add(currentLine);

            recentsReader.close();
            input.close();
            updateRecentPaneFilesMenu();   // update the recent files menu

         }
        catch(java.io.IOException except)
        {
            System.out.println("IO exception in readRecentsFiles. File: "+fileWithFileListOfPaneRecentFiles+"  not found");
            recentPaneFilesMenu.removeAll();  // clear previous menu items
           clearRecentFilesJMenuItem = new JMenuItem("Clear the list of recent files");
           clearRecentFilesJMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                recentPaneFiles.clear();
                recentPaneFilesMenu.removeAll();
            }
        });

           recentPaneFilesMenu.add(clearRecentFilesJMenuItem);
            mainJMenuBar.add(recentPaneFilesMenu);   // finally add the recent files menu to the main menu bar
        
        }
     }
    }
    
         
    // perform common editing operations for the file selectedValue.
    // if the isMainFrame flag is true then the file will be edited in the main editor window, 
    // that has attached at its bottom the Console's output, therefore some different chores 
    // should be performed
    public RSyntaxTextArea  commonEditingActions(String selectedValue, boolean isMainFrame) {
        currentFrame = new JFrame("Editing "+selectedValue);    // keep the current frame handle
        editedFileName = selectedValue;    // keep the edited filename
        jep = new  RSyntaxTextArea();   // construct a JEditorPane component 
        
        jep.setToolTipText("Type here GroovyLab code. Use the corresponding Help menu for help on the available keystrokes");
        
        jep.setFont(new Font(GlobalValues.paneFontName, Font.PLAIN, GlobalValues.paneFontSize));
      
        jep.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        jep.setCodeFoldingEnabled(true);
        
        // create a toolbar with searching options
        JToolBar toolBar = new JToolBar();
        
        gotoLineButton = new JButton("Go To Line: ");
        gotoLineButton.setToolTipText("Positions the cursor to the line entered at the corresponding field ");
        toolBar.add(gotoLineButton);
        gotoLineButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
        int lineNo = Integer.parseInt(gotoLineField.getText());
        int apos;
                try {
        apos = GlobalValues.globalEditorPane.getLineStartOffset(lineNo-1);
        GlobalValues.globalEditorPane.setCaretPosition(apos);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
        
            }
        });
        
        gotoLineField = new JTextField();
        toolBar.add(gotoLineField);
        
        
        searchField = new JTextField(30);
        toolBar.add(searchField);
        final JButton nextButton = new JButton("Find Next");
        nextButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
          forward  = true;
          performSearch(forward);
            }
        });
        toolBar.add(nextButton);
        searchField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
         nextButton.doClick();
            }
        });
        toolBar.add(searchField);
        
        JButton prevButton = new JButton("Find Previous");
        prevButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
          forward = false;
          performSearch(forward);                 
            }
        });
        toolBar.add(prevButton);

        regexCB = new JCheckBox("Regex");
        toolBar.add(regexCB);

        matchCaseCB = new JCheckBox("Match Case");
        toolBar.add(matchCaseCB);
        
        
        // add the key and mouse handlers
        keyMouseHandler = new EditorKeyMouseHandler();
        jep.addKeyListener (keyMouseHandler);
        jep.addMouseListener(keyMouseHandler);
        
        RSyntaxEditorMouseMotionAdapter ekmadapter = new RSyntaxEditorMouseMotionAdapter();
        jep.addMouseMotionListener(ekmadapter);
        
        mainJMenuBar = new JMenuBar();
                        
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.setToolTipText("File editing operations");
        fileMenu.setFont(GlobalValues.uifont);
                
        importsMenu.setToolTipText("Buffers import statements for the GroovyShell: Allows to execute a script in parts.");

        
        
                    JMenuItem  ejmlCompletionJMenuItem = new JMenuItem("Retrieve code completion for the EJML library");
                ejmlCompletionJMenuItem.setFont(GlobalValues.uifont);
                ejmlCompletionJMenuItem.addActionListener(new ActionListener() {

             @Override
             public void actionPerformed(ActionEvent e) {
                 if (GlobalValues.provider==null) {
                     JOptionPane.showMessageDialog(null, "Please open the rsyntaxArea based editor first", "Open RSyntaxArea editor then retry completion load ", JOptionPane.INFORMATION_MESSAGE);
                 }        else  {
           try {
               int nejmlMethods = GlobalValues.providerObject.scanBuiltInLibraryClassesForEditor((DefaultCompletionProvider) GlobalValues.provider, "org/ejml", JavaGlobals.ejmlFile);
               System.out.println("readed number of methods:  "+nejmlMethods);
              }  
           catch (IOException ex) {
            System.out.println("exception in scanBuiltInLibraryClassesForEditor");
            System.out.println(ex.getMessage());
        }
                }
              }
             }
         );
         
                 JMenuItem  mtjCompletionJMenuItem = new JMenuItem("Retrieve code completion for the MTJ library");
                 mtjCompletionJMenuItem.setFont(GlobalValues.uifont);
                mtjCompletionJMenuItem.addActionListener(new ActionListener() {

             @Override
             public void actionPerformed(ActionEvent e) {
                 if (GlobalValues.provider==null) {
                     JOptionPane.showMessageDialog(null, "Please open the rsyntaxArea based editor first", "Open RSyntaxArea editor then retry completion load ", JOptionPane.INFORMATION_MESSAGE);
                 }        else  {
           try {
               int nmtjMethods = GlobalValues.providerObject.scanBuiltInLibraryClassesForEditor((DefaultCompletionProvider) GlobalValues.provider, "no/uib/cipr", JavaGlobals.mtjColtSGTFile);
               System.out.println("readed number of methods:  " + nmtjMethods);
              }  
           catch (IOException ex) {
            System.out.println("exception in scanBuiltInLibraryClassesForEditor");
            System.out.println(ex.getMessage());
        }
                }
              }
             }
         );
                 
                
                 JMenuItem  apacheCompletionJMenuItem = new JMenuItem("Retrieve code completion for the Apache Commons");
                 apacheCompletionJMenuItem.setFont(GlobalValues.uifont);
                apacheCompletionJMenuItem.addActionListener(new ActionListener() {

             @Override
             public void actionPerformed(ActionEvent e) {
                 if (GlobalValues.provider==null) {
                     JOptionPane.showMessageDialog(null, "Please open the rsyntaxArea based editor first", "Open RSyntaxArea editor then retry completion load ", JOptionPane.INFORMATION_MESSAGE);
                 }        else  {
           try {
               int nApacheMethods  = GlobalValues.providerObject.scanBuiltInLibraryClassesForEditor((DefaultCompletionProvider) GlobalValues.provider, "org/apache/commons/math", JavaGlobals.JASFile);
               System.out.println("readed number of methods:  "+nApacheMethods);
              }  
           catch (IOException ex) {
            System.out.println("exception in scanBuiltInLibraryClassesForEditor");
            System.out.println(ex.getMessage());
        }
                }
              }
             }
         );
                
                
                JMenuItem  numericalRecipesCompletionJMenuItem = new JMenuItem("Retrieve code completion for the Numerical Recipes  library");
                numericalRecipesCompletionJMenuItem.setFont(GlobalValues.uifont);
                numericalRecipesCompletionJMenuItem.addActionListener(new ActionListener() {

             @Override
             public void actionPerformed(ActionEvent e) {
                 if (GlobalValues.provider==null) {
                     JOptionPane.showMessageDialog(null, "Please open the rsyntaxArea based editor first", "Open RSyntaxArea editor then retry completion load ", JOptionPane.INFORMATION_MESSAGE);
                 }        else  {
           try {
               int nrMethods = GlobalValues.providerObject.scanBuiltInLibraryClassesForEditor((DefaultCompletionProvider) GlobalValues.provider, "com/nr", JavaGlobals.numalFile);
               System.out.println("readed number of methods:  "+nrMethods);
              }  
           catch (IOException ex) {
            System.out.println("exception in scanBuiltInLibraryClassesForEditor");
            System.out.println(ex.getMessage());
        }
                }
              }
             }
         );
                
                
                JMenuItem  numalCompletionJMenuItem = new JMenuItem("Retrieve code completion for the NUMAL  library");
                numalCompletionJMenuItem.setFont(GlobalValues.uifont);
                numalCompletionJMenuItem.addActionListener(new ActionListener() {

             @Override
             public void actionPerformed(ActionEvent e) {
                 if (GlobalValues.provider==null) {
                     JOptionPane.showMessageDialog(null, "Please open the rsyntaxArea based editor first", "Open RSyntaxArea editor then retry completion load ", JOptionPane.INFORMATION_MESSAGE);
                 }        else  {
           try {
               int numalMethods = GlobalValues.providerObject.scanBuiltInLibraryClassesForEditor((DefaultCompletionProvider) GlobalValues.provider, "numal", JavaGlobals.numalFile);
               System.out.println("readed number of methods:  "+numalMethods);
              }  
           catch (IOException ex) {
            System.out.println("exception in scanBuiltInLibraryClassesForEditor");
            System.out.println(ex.getMessage());
        }
                }
              }
             }
         );
                
                
                JMenuItem  jlapackCompletionJMenuItem = new JMenuItem("Retrieve code completion for the JLAPACK  library");
                jlapackCompletionJMenuItem.setFont(GlobalValues.uifont);
                jlapackCompletionJMenuItem.addActionListener(new ActionListener() {

             @Override
             public void actionPerformed(ActionEvent e) {
                 if (GlobalValues.provider==null) {
                     JOptionPane.showMessageDialog(null, "Please open the rsyntaxArea based editor first", "Open RSyntaxArea editor then retry completion load ", JOptionPane.INFORMATION_MESSAGE);
                 }        else  {
           try {
               int lapackMethods = GlobalValues.providerObject.scanBuiltInLibraryClassesForEditor((DefaultCompletionProvider) GlobalValues.provider, "org/netlib", JavaGlobals.LAPACKFile);
               System.out.println("readed number of methods:  "+lapackMethods);
           }               
           catch (IOException ex) {
            System.out.println("exception in scanBuiltInLibraryClassesForEditor");
            System.out.println(ex.getMessage());
        }
                }
              }
             }
         );
                
 JMenu compileMenu = new JMenu("Java");
 compileMenu.setToolTipText("Compiles complete Java  files (i.e. not scripts) and executes them");
 compileMenu.setFont(GlobalValues.uifont);
  
 JMenuItem classCompletionWithJShell = new JMenuItem("Class Completion with JShell of selected class");
           classCompletionWithJShell.setFont(gExec.Interpreter.GlobalValues.puifont);
           classCompletionWithJShell.addActionListener( new ActionListener() {
    @Override
            public void actionPerformed(ActionEvent e) {
         String simpleTypeName = GlobalValues.globalEditorPane.getSelectedText();
        System.out.println(simpleTypeName);
       
                SourceCodeAnalysis.QualifiedNames  qualifiedNames = GlobalValues.jshell.sourceCodeAnalysis().listQualifiedNames(simpleTypeName, simpleTypeName.length());
      
               List<String>  allQualifiedNames = qualifiedNames.getNames();
      
               // priority to grooovySci related classes
                String groovySciClass = null;  
                for (Iterator<String> it = allQualifiedNames.iterator(); it.hasNext();) {
                  String  elem = it.next();
                  if (elem.contains("groovySci")) {
                      groovySciClass = elem;
                      break;
                  }
                }
                String mainName = groovySciClass;
                if (mainName==null)
                    mainName = allQualifiedNames.get(0);
                groovySciCommands.Inspect.inspectCompletionList(mainName);

            }       
           });
               
                  JMenuItem executeWithJShellJMenuItem = new JMenuItem("Execute Code with Jshell (use also F7 key)");
           executeWithJShellJMenuItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           executeWithJShellJMenuItem.addActionListener( new ActionListener() {
    @Override
            public void actionPerformed(ActionEvent e) {
         String currentText = GlobalValues.globalEditorPane.getSelectedText();
         
           if (currentText != null) {
       SourceCodeAnalysis sca = gExec.Interpreter.GlobalValues.jshell.sourceCodeAnalysis();
       List<String> snippets = new ArrayList<>();
       do {
           SourceCodeAnalysis.CompletionInfo info = sca.analyzeCompletion(currentText);
           snippets.add(info.source());
           currentText = info.remaining();
       } while (currentText.length() > 0);
       
        List<SnippetEvent> grResultSnippets = snippets.stream().map(gExec.Interpreter.GlobalValues.jshell::eval).flatMap(List::stream).collect(Collectors.toList());       
         
                  //    if (grResultSnippets != null) {
       //     String rmSuccess = grResultSnippets.toString().replace("Success", "");    
               
    //    GlobalValues.consoleOutputWindow.output.append(rmSuccess);
   //     GlobalValues.consoleOutputWindow.output.setCaretPosition(GlobalValues.consoleOutputWindow.output.getText().length());
              //   }
           
    System.out.flush();
    
           }
            }
           });
        
                  
                  JMenuItem variablesOfJShellJMenuItem = new JMenuItem("Display JShell's variables");
           variablesOfJShellJMenuItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           variablesOfJShellJMenuItem.addActionListener( new ActionListener() {
    @Override
            public void actionPerformed(ActionEvent e) {
         System.out.println("Variables: ");
         GlobalValues.jshell.variables().forEach(v->{{
             String vn = v.name();
             String valueOfVar = gExec.Interpreter.GlobalValues.jshell.eval(vn).get(0).value();
             VarSnippet varX = gExec.Interpreter.GlobalValues.jshell.variables().
                     filter(x->vn.equals(x.name())).findFirst().get() ;
             String typeOfVar = varX.typeName();
             
             System.out.println("Variable: "+vn+" type: "+typeOfVar+" value = "+valueOfVar);
             if (vn.contains("$")==false) {
              GlobalValues.jshellBindingValues.put(vn, valueOfVar);
              GlobalValues.jshellBindingTypes.put(vn, typeOfVar);
                     }
         }
                     });
              }
           });
           
               JMenuItem bindingOfJShellJMenuItem = new JMenuItem("Display JShell's binding");
           bindingOfJShellJMenuItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           bindingOfJShellJMenuItem.addActionListener( new ActionListener() {
    @Override
            public void actionPerformed(ActionEvent e) {
         System.out.println("Variable Values: ");
         GlobalValues.jshellBindingValues.entrySet().forEach(v-> {
             System.out.println(v.getKey());
             System.out.println(v.getValue());
         } 
         );
         System.out.println("Variable Types: ");
         
         GlobalValues.jshellBindingTypes.entrySet().forEach(v-> {
             System.out.println(v.getKey());
             System.out.println(v.getValue());
         } 
         );
         }
           });

        
        JMenuItem methodsOfJShellJMenuItem = new JMenuItem("Display JShell's methods");
           methodsOfJShellJMenuItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           methodsOfJShellJMenuItem.addActionListener( new ActionListener() {
    @Override
            public void actionPerformed(ActionEvent e) {
         System.out.println("Methods: ");
         GlobalValues.jshell.methods().forEach(m ->System.out.println(m.name()+" "+m.signature()));
            }
     });
           
        
 JMenuItem compileExecuteJavaEmbeddedJMenuItem = new JMenuItem("Compile and Execute for Embedded Java");
 compileExecuteJavaEmbeddedJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
          new compileExecuteTextJava().compileExecuteTextJavaEmbedded();
            }
        });
 
 JMenuItem compileExecuteJavaJMenuItem = new JMenuItem("Compile and execute for Java");
 compileExecuteJavaJMenuItem.setToolTipText("Compiles and executes the Java object in the editor");
 compileExecuteJavaJMenuItem.setAccelerator(KeyStroke.getKeyStroke("F9"));
 compileExecuteJavaJMenuItem.setFont(GlobalValues.uifont);
 compileExecuteJavaJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
          new compileExecuteTextJava().compileExecuteTextJava();
          
            }
        });
 
 
 compileExecuteJavaEmbeddedJMenuItem.setFont(GlobalValues.uifont);
 compileExecuteJavaEmbeddedJMenuItem.setToolTipText("Compiles and executes the Java object in the editor, with Java Embedded Runtime compatibility ");
 compileExecuteJavaEmbeddedJMenuItem.setAccelerator(KeyStroke.getKeyStroke("shift F9"));
 compileExecuteJavaEmbeddedJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
          new compileExecuteTextJava().compileExecuteTextJavaEmbedded();
          
            }
        });
        
        
        compileMenu.add(executeWithJShellJMenuItem);
        compileMenu.add(variablesOfJShellJMenuItem);
        compileMenu.add(bindingOfJShellJMenuItem);
        compileMenu.add(methodsOfJShellJMenuItem);
        compileMenu.add(classCompletionWithJShell);
        compileMenu.add(compileExecuteJavaJMenuItem);
        compileMenu.add(compileExecuteJavaEmbeddedJMenuItem);
        
                
JMenu applicationMenu = new JMenu("Application");
JMenuItem standAloneMenuItem = new JMenuItem("Create stand alone application from the GroovySci script code");
standAloneMenuItem.setToolTipText("Wraps the script to a Groovy class, compiles the class and creates a script file that can be used to run the code as a standalone application");
standAloneMenuItem.addActionListener(new StandAloneApplicationActionGroovy() );
applicationMenu.add(standAloneMenuItem);


final  JMenuItem GlobalGroovyCompletionJMenuItem = new JMenuItem("Toggle Global/Groovy completion mode, current mode is:  "+GlobalValues.rsyntaxInGroovyCompletionModeProp);
             GlobalGroovyCompletionJMenuItem.setToolTipText("Global completion concerns basic GroovyLab routines and libraries, Groovy completion results are obtained from Java reflection");
             GlobalGroovyCompletionJMenuItem.setFont(GlobalValues.uifont);
             GlobalGroovyCompletionJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
              if (gExec.Interpreter.GlobalValues.rsyntaxInGroovyCompletionMode  == true) {
                  gExec.Interpreter.GlobalValues.rsyntaxInGroovyCompletionMode  = false;
                  gExec.Interpreter.GlobalValues.rsyntaxInGroovyCompletionModeProp = "Global";
                  gLabEdit.GCompletionProvider.installAutoCompletion();   
                  GlobalGroovyCompletionJMenuItem.setText("Toggle Global/Groovy completion mode, current mode is:  "+GlobalValues.rsyntaxInGroovyCompletionModeProp);
                         }
              else
              {
      gExec.Interpreter.GlobalValues.rsyntaxInGroovyCompletionMode  = true;
      gExec.Interpreter.GlobalValues.rsyntaxInGroovyCompletionModeProp = "Groovy";
      GlobalGroovyCompletionJMenuItem.setText("Toggle Global/Groovy completion mode, current mode is: "+GlobalValues.rsyntaxInGroovyCompletionModeProp);
            
              }               
            }
             });
          
 
final  JMenuItem IdentifierVsPackageCompletionJMenuItem = new JMenuItem("Toggle  Identifier vs Package  completion mode, Identifier completion is now : " + (GlobalValues.performPackageCompletion == false) );
             IdentifierVsPackageCompletionJMenuItem.setToolTipText("Identifier completion concerns program variables, package completion allows to explore packages");
                     
             IdentifierVsPackageCompletionJMenuItem.setFont(GlobalValues.uifont);
             IdentifierVsPackageCompletionJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
              if (gExec.Interpreter.GlobalValues.performPackageCompletion  == true) {
                  gExec.Interpreter.GlobalValues.performPackageCompletion  = false;
                  IdentifierVsPackageCompletionJMenuItem.setText("Toggle  Identifier vs Package  completion mode, Identifier completion is now : " + (GlobalValues.performPackageCompletion == false)) ;
                         }
              else
              {
      gExec.Interpreter.GlobalValues.performPackageCompletion  = true;
      IdentifierVsPackageCompletionJMenuItem.setText("Toggle  Identifier vs Package  completion mode, Identifier completion is now : " + (GlobalValues.performPackageCompletion == false)) ;
              }               
            }
             });
          
 JMenu  completionMenu = new JMenu("Completion");
 completionMenu.setToolTipText("Retrieves information for code completion by the editor (i.e. CTRL-SPACE) from libraries using Java Reflection");
 completionMenu.setFont(GlobalValues.uifont);
 completionMenu.add(GlobalGroovyCompletionJMenuItem);
 completionMenu.add(IdentifierVsPackageCompletionJMenuItem);
 completionMenu.add(ejmlCompletionJMenuItem);
 completionMenu.add(numericalRecipesCompletionJMenuItem);
 completionMenu.add(numalCompletionJMenuItem);
 completionMenu.add(mtjCompletionJMenuItem);
 completionMenu.add(apacheCompletionJMenuItem);
 completionMenu.add(jlapackCompletionJMenuItem);
 
        saveEditorTextJMenuItem = new JMenuItem("Save Editor Text ");
        saveEditorTextJMenuItem.addActionListener(new saveEditorTextAction());
        saveEditorTextJMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        saveEditorTextJMenuItem.setFont(GlobalValues.uifont);
                
        saveAsEditorTextJMenuItem = new JMenuItem("Save As Editor Text to File");
        saveAsEditorTextJMenuItem.addActionListener(new saveAsEditorTextAction());
        saveAsEditorTextJMenuItem.setFont(GlobalValues.uifont);
                
        loadEditorTextJMenuItem = new JMenuItem("Load  File to Editor");
        loadEditorTextJMenuItem.addActionListener(new loadEditorTextAction());
        loadEditorTextJMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl L"));
        loadEditorTextJMenuItem.setFont(GlobalValues.uifont);
        
        
        exitJMenuItem = new JMenuItem("Exit");
        exitJMenuItem.setFont(GlobalValues.uifont); 
        
        exitJMenuItem.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent e) {
            GlobalValues.gLabMainFrame.closeGUI();
            }
        });

        
        fileMenu.add(saveEditorTextJMenuItem);
        fileMenu.add(saveAsEditorTextJMenuItem);
        fileMenu.add(loadEditorTextJMenuItem);
        fileMenu.add(exitJMenuItem);

    JMenu importantTipsMenu = new JMenu("Important Tips");
    JMenuItem bufferImportsTipJMenuItem = new JMenuItem("Buffer the required import statements (similarly you can buffer code)");
    bufferImportsTipJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
             EditorPaneHTMLHelp  inPlaceHelpPane = new EditorPaneHTMLHelp("BufferingImports.html");
       if (GlobalValues.useSystemBrowserForHelp==false) {
          inPlaceHelpPane.setSize(GlobalValues.figFrameSizeX, GlobalValues.figFrameSizeY);
          inPlaceHelpPane.setLocation(GlobalValues.sizeX/4, GlobalValues.sizeY/4);
          inPlaceHelpPane.setVisible(true);
          
              }
            }
        });
    
    JMenuItem abbreviationsMenuItem = new JMenuItem("Display the current abbreviations");
    abbreviationsMenuItem.setToolTipText("Displays the currently active set of abbreviations (you should use at least one time F11 for the abbreviation list to initialize)");
    abbreviationsMenuItem.setFont(GlobalValues.uifont);
    abbreviationsMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               Abbreviations.displayAbbreviations();
            }
        }); 
    importantTipsMenu.add(abbreviationsMenuItem);
    
    clearBufferedImportsJMenuItem.setToolTipText("Clears the buffered import statements");
    clearBufferedImportsJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
          GlobalValues.bufferingImports="";
            }
        });
   
    
    importantTipsMenu.add(bufferImportsTipJMenuItem);
      
    JMenuItem interfaceWithLibrariesJMenuItem = new JMenuItem("Matrix Assignment - Tip - Matrix Conversions");
    interfaceWithLibrariesJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
             EditorPaneHTMLHelp  inPlaceHelpPane = new EditorPaneHTMLHelp("MatrixConversions.html");
       if (GlobalValues.useSystemBrowserForHelp==false) {
          inPlaceHelpPane.setSize(GlobalValues.figFrameSizeX, GlobalValues.figFrameSizeY);
          inPlaceHelpPane.setLocation(GlobalValues.sizeX/4, GlobalValues.sizeY/4);
          inPlaceHelpPane.setVisible(true);
          
              }
            }
        });
    importantTipsMenu.add(interfaceWithLibrariesJMenuItem);
    
    helpMenu = new JMenu("Programmer'sHelp");
    helpMenu.setToolTipText("Help on the basic editor commands for controlling script execution");
    helpMenu.setFont(GlobalValues.uifont);
    
    JMenuItem editorBasicCommandsMenuItem = new JMenuItem("Editor Basic Commands for Programming");
    editorBasicCommandsMenuItem.setFont(GlobalValues.uifont);
    helpMenu.add(editorBasicCommandsMenuItem);
    editorBasicCommandsMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
             EditorPaneHTMLHelp  inPlaceHelpPane = new EditorPaneHTMLHelp("KeysHelp.html");
       if (GlobalValues.useSystemBrowserForHelp==false) {
          inPlaceHelpPane.setSize(GlobalValues.figFrameSizeX, GlobalValues.figFrameSizeY);
          inPlaceHelpPane.setLocation(GlobalValues.sizeX/4, GlobalValues.sizeY/4);
          inPlaceHelpPane.setVisible(true);
                 }
               }
            
            }
    );
    
    JMenuItem insertCompileJavaAnnotation = new JMenuItem("Insert Compile Java Annotation");
    insertCompileJavaAnnotation.setFont(GlobalValues.uifont);
    insertCompileJavaAnnotation.setToolTipText("Inserts an annotatation for compiling a method using the Java compiler");
    insertCompileJavaAnnotation.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
          
             String editedText = GlobalValues.globalEditorPane.getText();
             int  caretPos = GlobalValues.globalEditorPane.getCaretPosition();
             String compileJavaText = "\n@expandRunTime.CompileJava\n";
             GlobalValues.globalEditorPane.setText(editedText.substring(0, caretPos)+ compileJavaText+editedText.substring(caretPos, editedText.length()));
             GlobalValues.globalEditorPane.setCaretPosition(caretPos+compileJavaText.length());
            }
        });
    helpMenu.add(insertCompileJavaAnnotation);
    
    
    JMenuItem insertCompileStaticAnnotation = new JMenuItem("Insert Compile Static Annotation");
    insertCompileStaticAnnotation.setFont(GlobalValues.uifont);
    insertCompileStaticAnnotation.setToolTipText("Inserts an annotatation for compiling a method or a class using the static compilation mode  of Groovy compiler");
    insertCompileStaticAnnotation.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
          
             String editedText = GlobalValues.globalEditorPane.getText();
             int  caretPos = GlobalValues.globalEditorPane.getCaretPosition();
             String compileStaticText = "\n@groovy.transform.CompileStatic\n";
             GlobalValues.globalEditorPane.setText(editedText.substring(0, caretPos)+ compileStaticText+editedText.substring(caretPos, editedText.length()));
             GlobalValues.globalEditorPane.setCaretPosition(caretPos+compileStaticText.length());
            }
        });
    helpMenu.add(insertCompileStaticAnnotation);
    
    
    
    JMenuItem insertMapComputeFunction  = new JMenuItem("Inserts Java Class implementing compute function for mapf application");
    insertMapComputeFunction.setFont(GlobalValues.uifont);
    insertMapComputeFunction.setToolTipText("You can implement your function code within the f() method");
    insertMapComputeFunction.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
          
             String editedText = GlobalValues.globalEditorPane.getText();
             int  caretPos = GlobalValues.globalEditorPane.getCaretPosition();
             String functionStaticText = "\n@groovy.transform.CompileStatic\n"+
                     "class fx implements groovySci.math.array.computeFunction { \n"+
                     "  double f(double x) {   \n // define your code for the function here, e.g.  return x*x*x; \n"+
                     "    } \n  } \n"+
                     "\n fxo = new fx();  // the object that performs the mapping computation  \n"+
                     "\n /* Example of using your function \n"+
                     "A=rand(100, 100)  // a matrix object \nA.mapf(fx0) // apply the function to each element of the matrix\n */ \n\n";
             
             GlobalValues.globalEditorPane.setText(editedText.substring(0, caretPos)+ functionStaticText+editedText.substring(caretPos, editedText.length()));
             GlobalValues.globalEditorPane.setCaretPosition(caretPos+functionStaticText.length());
            }
        });
    helpMenu.add(insertMapComputeFunction);
    
    
    bufferImportsJMenuItem.setToolTipText("Buffers the selected import statements in order to be used automatically");
    bufferImportsJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
           String selectedImports = GlobalValues.globalEditorPane.getSelectedText();
           GlobalValues.bufferingImports = GlobalValues.bufferingImports+"\n"+selectedImports;
           
            }
        });
    
    switchLibrariesMenu.setFont(GlobalValues.uifont);
    switchJBLASMenuItem.setFont(GlobalValues.uifont);
    switchJBLASMenuItem.setToolTipText("Switch some operations to the JBLAS fast native routines");;
    switchJBLASMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
          gLabEdit.SwitchLibraries.switchJBLAS();
            }
        });
   
   codeBufferingMenu.setFont(GlobalValues.uifont);
   codeBufferingMenuItem.setFont(GlobalValues.uifont);
   codeBufferingMenuItem.setToolTipText("Buffers the selected code statements in order to be executed automatically with each script");
    codeBufferingMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
           String selectedCode = GlobalValues.globalEditorPane.getSelectedText();
           GlobalValues.bufferingCode  = GlobalValues.bufferingCode+"\n"+selectedCode;
          
            }
        });
    
    
    clearCodeBufferingMenuItem = new JMenuItem("Clear the buffered code statements");
    clearCodeBufferingMenuItem.setFont(GlobalValues.uifont);
    clearCodeBufferingMenuItem.setToolTipText("Clears the buffered code statements");
    clearCodeBufferingMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GlobalValues.bufferingCode="";
            }
        });
    
    editCodeBufferingMenuItem= new JMenuItem("Edit current buffered code");
    editCodeBufferingMenuItem.setFont(GlobalValues.uifont);
    editCodeBufferingMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
    JFrame dframe = new JFrame("Edit the contents of the buffered code ");
    final RSyntaxTextArea jt = new RSyntaxTextArea();
        
     jt.setFont(new Font(GlobalValues.paneFontName, Font.PLAIN, GlobalValues.paneFontSize));
      
      jt.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
      jt.setCodeFoldingEnabled(true);
      jt.setText(GlobalValues.bufferingCode);
    
        
      RTextScrollPane   jp = new RTextScrollPane(jt);
      
      
    JButton updateButton = new JButton("Update code buffer");
    updateButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          GlobalValues.bufferingCode = jt.getText();
        }
    });
    
    dframe.setLayout(new BorderLayout());
    dframe.add(jp, BorderLayout.NORTH);
    dframe.add(updateButton, BorderLayout.SOUTH);
    dframe.pack();
    dframe.setLocation(200, 200);
    dframe.setVisible(true);
            }
        });
    
    JMenuItem editBufferedImportsJMenuItem = new JMenuItem("Edit current buffered imports");
   editBufferedImportsJMenuItem.setFont(GlobalValues.uifont);
   editBufferedImportsJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
    JFrame dframe = new JFrame("Edit the contents of the buffered imports");
    final RSyntaxTextArea jt = new RSyntaxTextArea();
        
     jt.setFont(new Font(GlobalValues.paneFontName, Font.PLAIN, GlobalValues.paneFontSize));
      
      jt.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
      jt.setCodeFoldingEnabled(true);
      jt.setText(GlobalValues.bufferingImports);
        
      RTextScrollPane   jp = new RTextScrollPane(jt);
    JButton updateButton = new JButton("Update the buffered imports");
    updateButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          GlobalValues.bufferingImports  = jt.getText();
        }
    });
    
    dframe.setLayout(new BorderLayout());
    dframe.add(jp, BorderLayout.NORTH);
    dframe.add(updateButton, BorderLayout.SOUTH);
    dframe.pack();
    dframe.setLocation(200, 200);
    dframe.setVisible(true);
            }
        });
    
    
   JMenu threadsMenu = new JMenu("Threads");
   threadsMenu.setFont(GlobalValues.guifont);
   
   JMenuItem cancelJMenuItem = new JMenuItem("Cancel Pending Tasks");
                cancelJMenuItem.setFont(GlobalValues.uifont);
                cancelJMenuItem.setToolTipText("Attempt  to cancel any pending computational threads");

                cancelJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                gExec.Interpreter.PendingThreads.cancelPendingThreads();
            }          
        });
      
         
                 threadsMenu.add(cancelJMenuItem);
                 
                 
   codeBufferingMenu.add(codeBufferingMenuItem);
   codeBufferingMenu.add(editCodeBufferingMenuItem);
   codeBufferingMenu.add(clearCodeBufferingMenuItem);
   
   // switchLibrariesMenu.add(switchJBLASMenuItem);
   
    importsMenu.setFont(GlobalValues.uifont);
    bufferImportsJMenuItem.setFont(GlobalValues.uifont);
    
    
    importsMenu.add(bufferImportsJMenuItem);
    clearBufferedImportsJMenuItem.setFont(GlobalValues.uifont);
    importsMenu.add(clearBufferedImportsJMenuItem);
    
    importsMenu.add(editBufferedImportsJMenuItem);

    importsMenu.add(bufferedFrameJMenuItem);
    
    JMenuItem basicPlotsDirectlyImportJMenuItem = new JMenuItem("Basic Plots Imports");
    basicPlotsDirectlyImportJMenuItem.setFont(GlobalValues.uifont);
    basicPlotsDirectlyImportJMenuItem.setToolTipText("Injects the statements for the JMathPlot based routines");
    basicPlotsDirectlyImportJMenuItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                 gLabEdit.importsHelper.injectBasicPlotsImports();
                    }
                });
         
    
    JMenuItem groovySciImportJMenuItem = new JMenuItem("GroovySci Imports");
    groovySciImportJMenuItem.setFont(GlobalValues.uifont);
    groovySciImportJMenuItem.setToolTipText("Injects directly the statements for the GroovySci based routines");
    groovySciImportJMenuItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                 gLabEdit.importsHelper.injectGroovySciImports();
                    }
                });

         
    JMenuItem javaSwingImportJMenuItem = new JMenuItem("Java Swing Imports");
    javaSwingImportJMenuItem.setFont(GlobalValues.uifont);
    javaSwingImportJMenuItem.setToolTipText("Injects the statements for the Java Swing based routines");
    javaSwingImportJMenuItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                 gLabEdit.importsHelper.injectJavaSwing();
                    }
                });
    
    
    JMenuItem numalImportJMenuItem = new JMenuItem("NUMAL library");
    numalImportJMenuItem.setFont(GlobalValues.uifont);
    numalImportJMenuItem.setToolTipText("Injects the statements for the NUMAL library routines");
    numalImportJMenuItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                 gLabEdit.importsHelper.injectNumAl();
                    }
                });
    
    importsMenu.add(groovySciImportJMenuItem );
    importsMenu.add(basicPlotsDirectlyImportJMenuItem );
    importsMenu.add(javaSwingImportJMenuItem );
    importsMenu.add(numalImportJMenuItem);
    
    mainJMenuBar.add(fileMenu);
    mainJMenuBar.add(completionMenu);
    mainJMenuBar.add(compileMenu);
    mainJMenuBar.add(threadsMenu);
    mainJMenuBar.add(applicationMenu);
    mainJMenuBar.add(importsMenu);
    mainJMenuBar.add(codeBufferingMenu);
    //mainJMenuBar.add(switchLibrariesMenu);
    mainJMenuBar.add(importantTipsMenu);
    mainJMenuBar.add(helpMenu);
    
    loadRecentPaneFiles();
    
    currentFrame.setJMenuBar(mainJMenuBar);

    currentFrame.setTitle(titleStr+":  File: "+selectedValue);
        
// use user settings for edit frames to adjust location and size
        currentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      
        
        
  // load the file      
                   FileReader fr = null;
            try {
                fr = new FileReader(selectedValue);
                if (fr != null)
                  jep.read(fr, null);
                
            } catch (FileNotFoundException ex) {
                System.out.println("file "+selectedValue+" not found");
            }
            catch (IOException ex) {
                    System.out.println("cannot close file "+selectedValue);
                }
            finally {
                try {
        if (fr!=null)
            fr.close();
   
        
                } 
                
                catch (IOException ex) {
                    System.out.println("cannot close file "+selectedValue);
                }
            
            }
        
        Rectangle  b = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        if (GlobalValues.rememberSizesFlag == false) {
        currentFrame.setSize( (b.width / 2)-20, b.height * 5 / 6 );
        currentFrame.setLocation(50, 100);
        }
        else {
            currentFrame.setSize(GlobalValues.rsizeX, GlobalValues.rsizeY);
            currentFrame.setLocation(GlobalValues.rlocX, GlobalValues.rlocY);
        }
        currentFrame.setVisible(true);
     
        JPopupMenu popup = jep.getPopupMenu();
        popup.addSeparator();
        popup.add(new JMenuItem(new plotSignalAction()));
        popup.add(new JMenuItem(new executeSelectedAction()));
        
     
        
        scrPane = new RTextScrollPane(jep);
        //scrPane.setFoldIndicatorEnabled(true);
        
        toolBar.add(progressComputationLabel);
        progressComputationLabel.setVisible(false);
        
        currentFrame.add(toolBar, BorderLayout.NORTH);
 
        currentFrame.add(scrPane);
        currentFrame.setTitle(titleStr+":  File: "+selectedValue);
        
        //   if that Editor Frame is the main Editor frame, additional settings is required e.g.
        //   adding  the console output frame at the bottom 
   if (isMainFrame) {
        
        JSplitPane sp = new JSplitPane(SwingConstants.HORIZONTAL);
        sp.setTopComponent(scrPane);
        sp.setBottomComponent(GlobalValues.outputPane);
        
              
        currentFrame.add(sp);
        currentFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        currentFrame.setVisible(true);
        sp.setDividerLocation( 0.7 );
                
            
   }
        else {
 fileMenu.add(exitJMenuItem); 
  }
            
        return jep;
      
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        GlobalValues.gLabMainFrame.closeGUI();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        GlobalValues.gLabMainFrame.closeGUI();
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

        private class plotSignalAction extends TextAction {
        
        public plotSignalAction() {
            super("Plot Signal");
        }
        

        
        
        @Override
        public void actionPerformed(ActionEvent e) {
        JTextComponent tc = getTextComponent(e);
        String signalName = null;
        
        // Get the name of the signal to plot. If there is a selection, use that as the signal name,
        // otherwise, scan for a signalname around the caret
        try {
            int selStart = tc.getSelectionStart();
            int selEnd = tc.getSelectionEnd();
            if (selStart != selEnd) {
                signalName = tc.getText(selStart, selEnd - selStart);
            }
            else {
                signalName = getSignalNameAtCaret(tc);
            }
        }
        catch (BadLocationException ble) {
            ble.printStackTrace();;
            UIManager.getLookAndFeel().provideErrorFeedback(tc);
            return;
            }
        
                // GlobalValues.GroovyShell.evaluate(GlobalValues.basicGlobalImports+"\n"+"plot("+signalName+")");
            GlobalValues.GroovyShell.evaluate("plot("+signalName+")");
        }
      }
    
        
        
        
        private class executeSelectedAction extends TextAction {
        
        public executeSelectedAction() {
            super("Execute Selected Code or current line");
        }
        

        
        
        @Override
        public void actionPerformed(ActionEvent e) {
        JTextComponent tc = getTextComponent(e);
       
        String selectedCode = GlobalValues.myGEdit.keyMouseHandler.getSelectedTextOrCurrentLine();
                
     
        GlobalValues.GroovyShell.evaluate(selectedCode);
        }
      }
    
    // gets the signal name that the caret is sitting on
    public String getSignalNameAtCaret(JTextComponent tc) throws BadLocationException {
        int caret = tc.getCaretPosition();
        int start = caret;
        Document doc = tc.getDocument();
        while (start > 0) {
            char ch = doc.getText(start-1, 1).charAt(0);
            if (isSignalNameChar(ch)) {
                start--;
            }
            else {
                break;
            }
          }
        int end = caret;
        while (end < doc.getLength()) {
            char ch = doc.getText(end, 1).charAt(0);
            if (isSignalNameChar(ch)) {
                end++;
            }
            else {
                break;
            }
        }
        return doc.getText(start, end-start);
    }
    
    public boolean isSignalNameChar(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_';
    }
    

    private void performSearch(boolean forward) {
        SearchContext context = new SearchContext();
        String text = searchField.getText();
        if (text.length() ==0)
            return;
        context.setSearchFor(text);
        context.setMatchCase(matchCaseCB.isSelected());
        context.setRegularExpression(regexCB.isSelected());
        context.setSearchForward(forward);
        context.setWholeWord(false);
        
            SearchResult found = SearchEngine.find(jep, context);
        if (!found.wasFound())
            JOptionPane.showMessageDialog(this, "Text not found");
    }
    
   
        
        
        
        
   
    // edit the file with name selectedValue
    public gLabEditor(String selectedValue) {
        RSyntaxTextArea  jep = commonEditingActions(selectedValue, false);
        GlobalValues.globalEditorPane = jep;
        GCompletionProvider.installAutoCompletion();
        
 }
    

         // edit the file with name selectedValue
    public gLabEditor(String selectedValue, boolean initConsoleWindow) {
        RSyntaxTextArea  jep = commonEditingActions(selectedValue, true);
        GlobalValues.globalEditorPane = jep;
        GCompletionProvider.installAutoCompletion();
        
    }
   
      
   

    // save the current file kept in editor
class saveEditorTextAction extends AbstractAction  {
    public saveEditorTextAction() { super("Save Editor Text"); }
    public void actionPerformed(ActionEvent e)  {
        String saveFileName = editedFileName;   // file name to save is the one currently edited
        if (saveFileName == null)  { // not file specified thus open a FileChooser in order the user to determine it
        javax.swing.JFileChooser chooser = new JFileChooser(new File(GlobalValues.workingDir));
        
        int retVal = chooser.showSaveDialog(GlobalValues.gLabMainFrame);
        
        if (retVal == JFileChooser.APPROVE_OPTION) { 
                 File selectedFile = chooser.getSelectedFile();
                 saveFileName = selectedFile.getAbsolutePath();
                 editedFileName = saveFileName;    // update the edited file
                 GlobalValues.myGEdit.setTitle(titleStr+":  File: "+editedFileName);
   
         }
        }
        
        File saveFile = new File(saveFileName);
                    try {
                        FileWriter fw = new FileWriter(saveFile);
                        jep.write(fw);
                        editorTextSaved = true;  //  not need to save anything yet
                        
                    } catch (FileNotFoundException ex) {
                        System.out.println("Cannot open file "+saveFile+" for saving editor text "+ex.getMessage());
                    }
                    catch (Exception ex) {
                        System.out.println("Exception writing editor's text "+ex.getMessage());
                    }
                           
    }
  }

  // save the contents of the edit buffer to a file, asking the user to specify it 
class saveAsEditorTextAction extends AbstractAction  {
    public saveAsEditorTextAction() { super("Save As Editor Text"); }
    public void actionPerformed(ActionEvent e)  {
        javax.swing.JFileChooser chooser = new JFileChooser(new File(GlobalValues.workingDir));
        
        int retVal = chooser.showSaveDialog(GlobalValues.gLabMainFrame);
        if (retVal == JFileChooser.APPROVE_OPTION) { 
                 File selectedFile = chooser.getSelectedFile();
                 String saveFileName = selectedFile.getAbsolutePath();
                 File saveFile = new File(saveFileName);
                    try {
                        FileWriter fw = new FileWriter(saveFile);
                        jep.write(fw);
                        editorTextSaved = true;  //  not need to save anything yet
                     
                        gLabEditor.currentFrame.setTitle(titleStr+":  File: "+editedFileName);

                        //  add the loaded file to the recent files menu
            if (recentPaneFiles.contains(saveFileName) ==  false)  {
                recentPaneFiles.add(saveFileName);
                updateRecentPaneFilesMenu();
              }

            // update the workingDir
            String pathOfLoadFileName = saveFileName.substring(0, saveFileName.lastIndexOf(File.separatorChar));
            GlobalValues.workingDir = pathOfLoadFileName;
            
                    } catch (FileNotFoundException ex) {
                        System.out.println("Cannot open file "+saveFile+" for saving editor text "+ex.getMessage());
                    }
                    catch (Exception ex) {
                        System.out.println("Exception writing editor's text "+ex.getMessage());
                    }
                           
    }
  }
    }



// load a new file for editing
class loadEditorTextAction extends AbstractAction  {
    public loadEditorTextAction() { super("Load Editor Text"); }
    public void actionPerformed(ActionEvent e)  {
           int userOption = JOptionPane.CANCEL_OPTION;
            if (editorTextSaved == false ) 
      userOption = JOptionPane.showConfirmDialog(null, "File: "+editedFileName +" not saved. Proceed? ", 
                        "Warning: Exit without Save?", JOptionPane.CANCEL_OPTION);
            else userOption = JOptionPane.YES_OPTION;
            if (userOption == JOptionPane.YES_OPTION)  {
         
        javax.swing.JFileChooser chooser = new JFileChooser(new File(GlobalValues.workingDir));
        
        int retVal = chooser.showOpenDialog(GlobalValues.gLabMainFrame);
        if (retVal == JFileChooser.APPROVE_OPTION) { 
                 File selectedFile = chooser.getSelectedFile();
                 String loadFileName = selectedFile.getAbsolutePath();
                       
                   FileReader fr = null;
            try {
                fr = new FileReader(loadFileName);
                jep.read(fr, null);
  
           //  add the loaded file to the recent files menu
            if (recentPaneFiles.contains(loadFileName) ==  false)  {
                recentPaneFiles.add(loadFileName);
                updateRecentPaneFilesMenu();
              }
        }
            catch (FileNotFoundException ex) {
                System.out.println("file "+loadFileName+" not found");
            }
            catch (IOException ex) {
                    System.out.println("cannot close file "+loadFileName);
                }
            finally {
                try {
                    fr.close();
                } 
                catch (IOException ex) {
                    System.out.println("cannot close file "+loadFileName);
                }
            }
            
            editedFileName = loadFileName;   // current file is the new loaded one
            // update the workingDir
            String pathOfLoadFileName = editedFileName.substring(0, editedFileName.lastIndexOf(File.separatorChar));
            GlobalValues.workingDir = pathOfLoadFileName;
            
            editorTextSaved = true;  // a freshly loaded file doesn't require saving
            gLabEditor.currentFrame.setTitle(titleStr+":  File: "+editedFileName);
                           
     }
   }
 }
    }
}



class bufferedContentsAction extends AbstractAction {
    public bufferedContentsAction() {
        super("Buffered Imports and Code Frame");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
                GlobalValues.bufferedImportsTextArea = new RSyntaxTextArea(); 
                GlobalValues.bufferedImportsTextArea.setText(GlobalValues.bufferingImports);
                GlobalValues.bufferedImportsScrollPane = new RTextScrollPane(GlobalValues.bufferedImportsTextArea);
                GlobalValues.bufferedCodeTextArea = new RSyntaxTextArea();
                GlobalValues.bufferedCodeTextArea.setText(GlobalValues.bufferingCode);
                GlobalValues.bufferedImportsScrollPane = new RTextScrollPane(GlobalValues.bufferedCodeTextArea);
        
                JFrame  bufferedFrame = new JFrame("Buffered Imports and Code Frame");
                
                bufferedFrame.setLayout(new GridLayout(2,1));
                bufferedFrame.add(GlobalValues.bufferedImportsScrollPane);
                bufferedFrame.add(GlobalValues.bufferedImportsScrollPane);
                
                bufferedFrame.pack();
                bufferedFrame.setVisible(true);
    }
}

class StandAloneApplicationActionGroovy extends AbstractAction {
        
    // !!!new
        public StandAloneApplicationActionGroovy() {
            super("Transform Script to Stand Alone GroovySci Application");
           }

        public void transformScriptToStandAlone() {
            String scriptText = GlobalValues.globalEditorPane.getText();
            
            GlobalValues.scriptClassName = JOptionPane.showInputDialog(null,   "Creating StandAlone application", GlobalValues.scriptClassName);
            String standAloneText =  GlobalValues.basicGlobalImports+"\n\n"+
                    "\n\n\n\nclass "+GlobalValues.scriptClassName+  " { \n\n"+
                    "public static void    main(String [] args)   \n  { "+ "\n\n"+
             "// expand the runtime with GroovySci extensions \n"+
                    "def   expG = new expandRunTime.expandGroovy(); \n"+
                    "expG.run(); \n\n"+
                    scriptText +
             "\n} \n } \n";        
            
       String toRunCommand =  "java "+" -cp "+ GlobalValues.jarFilePath+File.pathSeparator+
                         gLabGlobals.JavaGlobals.groovyJarFile +   File.pathSeparator+
                         gLabGlobals.JavaGlobals.ejmlFile+File.pathSeparator+
                         gLabGlobals.JavaGlobals.jsciFile+File.pathSeparator+
                         gLabGlobals.JavaGlobals.mtjColtSGTFile+File.pathSeparator+
                         gLabGlobals.JavaGlobals.ApacheCommonsFile+File.pathSeparator+
                         gLabGlobals.JavaGlobals.numalFile+File.pathSeparator+
                         gLabGlobals.JavaGlobals.LAPACKFile+File.pathSeparator+
                         gLabGlobals.JavaGlobals.ARPACKFile+File.pathSeparator+
               "./lib/jtransforms.jar"+File.pathSeparator+
               "."+"   "+   GlobalValues.scriptClassName;
       
       
       // create the run script file
       String runScriptExtension = ".sh";
       if (GlobalValues.hostIsUnix == false)
       runScriptExtension = ".bat";  // Windows host
       
       String runScriptFileName = GlobalValues.scriptClassName+runScriptExtension;
       File scriptToRunFile = new File(runScriptFileName); 
       
       standAloneText = standAloneText+" /*  \n \n You can run the produced compiled code for the script as a Java standalone application \n "
               + "by running the automatically produced script:   "+  runScriptFileName + " \n */";
       
       // set the editor's text to the code of the created standalone GroovySci application
       GlobalValues.globalEditorPane.setText(standAloneText);
        

    try {
       FileWriter outScriptFile= new FileWriter(scriptToRunFile);
       outScriptFile.write(toRunCommand);
       outScriptFile.close();
    }
    catch (Exception e) {
        System.out.println("exception trying to write standalone script "+GlobalValues.scriptClassName+runScriptExtension);
    }
    
    // create the Groovy code file
      String groovyApplFileName = GlobalValues.scriptClassName+".groovy";
      File groovyApplFile = new File(groovyApplFileName);
    try {
       FileWriter groovyApplFileWriter= new FileWriter(groovyApplFile);
       
       String groovySciText = GlobalValues.globalEditorPane.getText();
       groovyApplFileWriter.write(groovySciText);
       groovyApplFileWriter.close();
    }
    catch (Exception e) {
        System.out.println("exception trying to write the Grovy code for the script "+groovyApplFileName);
    }
        
    // compile the Groovy code
     System.out.println("\n Compiling Groovy file "+groovyApplFileName+"\n");          
     org.codehaus.groovy.ant.GroovycExt.groovyCompile( "" ,  groovyApplFileName);
                
          
           
            
        }

        @Override
        public void actionPerformed(ActionEvent e) {
          transformScriptToStandAlone();
        }

        

  

    }





