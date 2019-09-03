
package gExec.gLab;

import com.google.common.collect.ImmutableMap;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import gExec.ClassLoaders.JarClassLoader;
import gExec.Interpreter.GlobalValues;
import gExec.gui.GBC;
import gExec.gui.groovySciScriptsPathsTree;
import gLabGlobals.JavaGlobals;
import groovy.transform.TimedInterrupt;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.codehaus.groovy.control.CompilerConfiguration;

// a class to handle GroovySci toolboxes
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
public class groovySciToolboxes {

        public   static JList  groovySciToolboxesAvailableList;    // the available toolboxes for GroovySci as full .jar pathnames
        public   static JList  groovySciToolboxesLoadedList;   // the list of already loaded toolboxes as full .jar pathnames
        public   static DefaultListModel  groovySciToolboxesAvailableListModel;   // the ListModel that keeps the data displayed at the groovySciToolboxesAvailable list
        public   static DefaultListModel  groovySciToolboxesLoadedListModel; // the ListModel that keeps the data displayed at the groovySciToolboxesAvailable list
        public   static JPanel groovySciToolboxesPanel = new JPanel(new GridBagLayout());
        // keeps information on all loaded toolboxes. Each entry keeps the toolbox name as the full pathname of the .jar file of the toolbox and
        // a Vector that keeps the loaded classes of the toolbox
        public   static Vector <groovySciToolbox>   gsciToolboxes = new Vector();

        public   static HashMap  <String, JFrame> framesOfToolboxClasses  = new HashMap();   // keeps track of the toolboxes frames in order to be able to remove them
        public   static int selectedAvailableToolboxIndex;   // the selected toolbox index by the user from the list of the available toolboxes
        public   static int selectedLoadedToolboxIndex;   // the selected toolbox index by the user from the list of the loaded toolboxes
        public   static JPanel groovyShellSettingsPanel;   // components that cocern the state of GroovyShell
        public   static groovySciScriptsPathsTree  groovyShellConfig;  
        
        public   static  JPanel allPanel;
        
     // initializes a new Groovy Shell with a classpath consisted of the default libraries plus the array of additionalToolboxes      
 public  static LinkedList<String> warmUpGroovy() {
     return warmUpGroovy(null);
 }
        
     // initializes a new Groovy Shell with a classpath consisted of the default libraries plus the array of additionalToolboxes      
 public  static LinkedList<String> warmUpGroovy(String []additionalToolboxes) {
            
     // set the compiler configuration to have the list of the specified .jar files in its classpath
            CompilerConfiguration cf = new CompilerConfiguration();
            if (GlobalValues.useAlwaysDefaultImports)  // use the default imports on each script execution in GroovyLab
            {
               GlobalValues.prepareImports();  // prepare the import statements using a Groovy import customizer
               cf.addCompilationCustomizers(GlobalValues.globallmportCustomizer);  // add that customizer to the compiler configuration
            }
             if (GlobalValues.timedInterruptScriptingOn==true) {
     final Map<String, Object> timedInterruptAnnotationParams = new HashMap<>();
    timedInterruptAnnotationParams.put("value", GlobalValues.timedInterruptValue);
    timedInterruptAnnotationParams.put("unit", GeneralUtils.propX(GeneralUtils.classX(TimeUnit.class), TimeUnit.MILLISECONDS.toString()));
  
            //  Map<String, Object> timeoutArgs = ImmutableMap.<String, Object>of("value", 2);
              
              ASTTransformationCustomizer  timedInterruptCustomizer = new ASTTransformationCustomizer(timedInterruptAnnotationParams, TimedInterrupt.class);
              
             cf.addCompilationCustomizers(timedInterruptCustomizer);
                 }
            GlobalValues.prepareImports();
            
            cf.addCompilationCustomizers(GlobalValues.globallmportCustomizer);
           
  
            if (GlobalValues.CompileDecimalsToDoubles)  {    // convert BigDecimalsToDoubles for efficiency
            
              cf.addCompilationCustomizers(new CompilationCustomizer(CompilePhase.CONVERSION) {
                
                    @Override
                    public void call(final SourceUnit source, final  GeneratorContext context, final ClassNode classNode)   {
                        new expandRunTime.ConstantTransformer(source).visitClass(classNode);
                    }
                });
            }
            
            cf.setTargetBytecode(GlobalValues.jdkTarget);
            if (GlobalValues.CompileIndy == true)  {
               cf.getOptimizationOptions().put("indy", true);
               cf.getOptimizationOptions().put("int", false);
            }
            else             {
               cf.getOptimizationOptions().put("indy", false);
               cf.getOptimizationOptions().put("int", true);
            }
            
            cf.setRecompileGroovySource(true);
            
            
            detectGroovyClassPaths.detectClassPaths();   // just to be sure that paths to the "system" jars are inited well
            
            // construct the classpath for the Groovy Shell
             GlobalValues.GroovyShellPathsList = new LinkedList<String>();
             GlobalValues.GroovyShellPathsList.add(".");  // current directory
 
            String javaClassPath =  System.getProperty("java.class.path");
            System.out.println("javaclasspath = "+javaClassPath);
            GlobalValues.GroovyShellPathsList.add(javaClassPath);
            
             GlobalValues.GroovyShellPathsList.add(JavaGlobals.javacppfile);
             GlobalValues.GroovyShellPathsList.add(JavaGlobals.openblasfile);
             GlobalValues.GroovyShellPathsList.add(GlobalValues.workingDir);
             GlobalValues.GroovyShellPathsList.add(JavaGlobals.jarFilePath);
             GlobalValues.GroovyShellPathsList.add(JavaGlobals.groovyJarFile);
    
             GlobalValues.GroovyShellPathsList.add(JavaGlobals.jsciFile);
             GlobalValues.GroovyShellPathsList.add(JavaGlobals.mtjColtSGTFile);
             GlobalValues.GroovyShellPathsList.add(JavaGlobals.ejmlFile);
             GlobalValues.GroovyShellPathsList.add(JavaGlobals.jblasFile);
             GlobalValues.GroovyShellPathsList.add(JavaGlobals.numalFile);
             GlobalValues.GroovyShellPathsList.add(JavaGlobals.ApacheCommonsFile);
      
             GlobalValues.GroovyShellPathsList.add(JavaGlobals.LAPACKFile);
             GlobalValues.GroovyShellPathsList.add(JavaGlobals.ARPACKFile);
             GlobalValues.GroovyShellPathsList.add(JavaGlobals.JASFile);
            
            System.out.println("appending to GroovyShell path:  "+GlobalValues.workingDir); 
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.jarFilePath);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.groovyJarFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.jsciFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.mtjColtSGTFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.ejmlFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.openblasfile);
            System.out.println("appending to GroovyShell path: "+JavaGlobals.jblasFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.numalFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.ApacheCommonsFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.LAPACKFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.ARPACKFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.JASFile);
             
        
           //   any .jar file in the defaultToolboxes folder is automatically appended to classpath 
          String  defaultToolboxesFolder = GlobalValues.gLabLibPath.replace("lib", "defaultToolboxes");
          System.out.println("appending toolboxes of DefaultToolboxes folder:  "+defaultToolboxesFolder);
          
          File [] toolboxesFolderFiles = (new java.io.File(defaultToolboxesFolder)).listFiles();  // get the list of files at the default toolboxes folder
          if (toolboxesFolderFiles!=null) {  // DefaultToolboxes folder not empty
           int numFiles = toolboxesFolderFiles.length; 
           for (int f=0; f < numFiles;  f++) {
               String currentFileName = toolboxesFolderFiles[f].getAbsolutePath();
           
                  if (currentFileName.endsWith(".jar")) {
               
               if (GlobalValues.GroovyShellPathsList.contains(currentFileName) == false) {  // current toolbox not already in classpath
                   GlobalValues.GroovyShellPathsList.add(currentFileName);
                   System.out.println("appending from defaultToolboxes folder toolbox: "+currentFileName);
               }
              }  // endsWith("jar")
            }   // for all files of then DefaultToolboxes folder
          }   // DefaultToolboxes folder not empty
          
         // append now the GroovySciClassPath components
            for (int k=0; k<GlobalValues.GroovySciClassPathComponents.size(); k++) {
                String currentPath = (String)GlobalValues.GroovySciClassPathComponents.elementAt(k);
                if (GlobalValues.GroovyShellPathsList.contains(currentPath)== false)  // current GroovySciClassPath component not already in classpath
                  GlobalValues.GroovyShellPathsList.add(currentPath);
            }
                
          if (additionalToolboxes!=null)
            for (int k=0; k<additionalToolboxes.length; k++)  {
                String additionalToolbox = additionalToolboxes[k];
                if (GlobalValues.GroovySciClassPathComponents.contains(additionalToolbox)== false)  {
                        GlobalValues.GroovySciClassPathComponents.add(additionalToolbox);
                        if (GlobalValues.GroovyShellPathsList.contains(additionalToolbox) == false)
                                GlobalValues.GroovyShellPathsList.add(additionalToolbox);
                 }
            }
            
            cf.setClasspathList(GlobalValues.GroovyShellPathsList);
            if (GlobalValues.groovyBinding == null)
                GlobalValues.groovyBinding = new Binding();
            final ClassLoader parentClassLoader = GlobalValues.gLabMainFrame.getClass().getClassLoader();
           
            
            // construct a properly inited Groovy Shell
            GlobalValues.GroovyShell  = new GroovyShell(parentClassLoader, GlobalValues.groovyBinding, cf);
            
            // expand the capabilities of the GroovyLab's classes using run-time metaprogramming, 
            // e.g. the type double [][] performs like a Matrix class, operation 2+A, where A is a double[][] array, or Matrix becomes legal etc.
            expandRunTime.expandGroovy expG = new expandRunTime.expandGroovy();
            expG.run();
            
           GlobalValues.jarToolboxesClassPathUpdatedForGroovyShell = true;   
       
           
           return GlobalValues.GroovyShellPathsList;
       }
     
   // handles the main tab panel for GroovySci's toolboxes
   public static  JPanel  handleGroovySciTab()  {
       if (groovySciToolboxesAvailableListModel == null)  {
          groovySciToolboxesAvailableListModel = new DefaultListModel();   // the list of .jar files that will be available for GroovySci toolboxes
                    // put the available toolboxes in a list
          groovySciToolboxesAvailableList = new JList(groovySciToolboxesAvailableListModel);
          groovySciToolboxesAvailableList.addMouseListener(new MouseAdapterForGSIAvailableToolboxes());
          groovySciToolboxesAvailableList.addListSelectionListener(new ListSelectionListener() {
    //    this listener tracks the toolbox selected by the user from the list
            @Override
            public void valueChanged(ListSelectionEvent event)
            {
               selectedAvailableToolboxIndex  = groovySciToolboxesAvailableList.getSelectedIndex();
            }
        });
       }

       if (groovySciToolboxesLoadedListModel == null)   {
           groovySciToolboxesLoadedListModel = new DefaultListModel();  // the list of .jar files that are already loaded as GroovySci toolboxes
           groovySciToolboxesLoadedList = new JList(groovySciToolboxesLoadedListModel);
           groovySciToolboxesLoadedList.addMouseListener(new MouseAdapterForGSILoadedToolboxes());
          groovySciToolboxesLoadedList.addListSelectionListener(new ListSelectionListener() {
    //    this listener tracks the toolbox selected by the user from the list
            @Override
            public void valueChanged(ListSelectionEvent event)
            {  
               selectedLoadedToolboxIndex = groovySciToolboxesLoadedList.getSelectedIndex();
            }
        });
       }
       
        //groovySciToolboxesLoadedList
        JScrollPane availableToolboxesScrollPane = new JScrollPane(groovySciToolboxesAvailableList);
        JScrollPane loadedToolboxesScrollPane = new JScrollPane(groovySciToolboxesLoadedList);
        
          // the help panel displays the steps that the user has to follow in order to add toolboxes
        JTextArea helpArea = new JTextArea();
        helpArea.setFont(GlobalValues.uifont);
        helpArea.append("Help on basic toolbox operations: \n"+
                "\n  Add new toolboxes: \n\n"+
                "Step 1.  Specify your .jar toolboxes by clicking on the 'Specify toolboxes' button\n"+
                "Step 2:  Import the specified toolboxes in the 'Available Toolboxes' list with the 'Import toolboxes' button \n"+
                " Toolboxes can be removed from the 'Available Toolboxes' list with a right-mouse click\n"+
                "\n  Remove toolboxes: \n"+
                "Step 1.  Select the toolbox from the loaded toolboxes list \n"+
                "Step 2.  Right-mouse click and select the remove option of the popup menu \n");
        
        JPanel helpPanel = new JPanel();
        helpPanel.add(new JScrollPane(helpArea));

        // the buttons for specifying and importing toolboxes
        JPanel buttonsPanel = new JPanel();   // keeps the control buttons together
        JButton loadBtn = new JButton("Import toolboxes");
        loadBtn.setToolTipText("Import the specified toolboxes and scanning their classes");
        loadBtn.addActionListener(new updateSystemWithJarToolboxes());
        
        JButton appendClassPathBtn = new JButton("Append to classpath");
        appendClassPathBtn.setToolTipText("Append the toolboxes to the GroovyShell's classpath, without scanning classes, is a safer option");
        appendClassPathBtn.addActionListener(new updateSystemWithJarToolboxesOnlyClassPath());
        
        
         JButton addBtn = new JButton("Specify toolboxes");
        addBtn.setToolTipText("Specify additional toolboxes for Groovy");
         
         JCheckBox loadAlsoMethodsCB = new JCheckBox("Retrieve also methods: ", GlobalValues.retrieveAlsoMethods);
         loadAlsoMethodsCB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
          GlobalValues.retrieveAlsoMethods = ((JCheckBox)e.getSource()).isSelected();
            }
        });
        buttonsPanel.add(addBtn);
        buttonsPanel.add(appendClassPathBtn);
        buttonsPanel.add(loadBtn);
        buttonsPanel.add(loadAlsoMethodsCB);
        
        addBtn.addActionListener(new  ActionListener() {  // adds a toolbox

            public void actionPerformed(ActionEvent e) {
               JFileChooser  chooser = new JFileChooser(GlobalValues.workingDir);
               chooser.setDialogTitle("Specify JAR file containing Java classes for GroovySci");
               int retVal = chooser.showOpenDialog(GlobalValues.gLabMainFrame);
               if (retVal == JFileChooser.APPROVE_OPTION) {   // approve toolbox
                    File selectedFile = chooser.getSelectedFile();
                    String jarFileName = selectedFile.getAbsolutePath();
    if (jarFileName.endsWith(".jar") )  
        {  // if toolbox is not already specified insert it to the list of available toolboxes
           boolean toolboxFound = false;
           int loadedToolboxesCnt = groovySciToolboxesLoadedListModel.size();
           for (int toolboxIdx=0; toolboxIdx<loadedToolboxesCnt; toolboxIdx++)  {  // search for the selected toolbox
               String currentToolboxName = (String) groovySciToolboxesLoadedListModel.get(toolboxIdx);   // the name of the current toolbox
               if (currentToolboxName.equalsIgnoreCase(jarFileName) == true)   {  // toolbox found
                   toolboxFound = true;
                   break;
                 }
           }  // search for the selected toolbox
           
           if (toolboxFound == false)  {  // selected toolbox not already exists, thus insert it
        groovySciToolboxesAvailableListModel.addElement(jarFileName);      // update the list model that keeps the jar files that are used as toolboxes
        
        GlobalValues.groovyJarClassesLoaded = false;  // reload classes
        GlobalValues.groovyClassLoader = null;   //  a new toolbox has been added, thus create a new GroovyClassLoader that will consider that toolbox in its classpath
            
        
           }
                 }  // if toolbox is not already specified 
              }  // approve toolbox
            }  // ActionPerformed
        });
                     
            groovySciToolboxesPanel.add(helpPanel, new GBC(0, 0, 6, 1 ));
            groovySciToolboxesPanel.add(buttonsPanel, new GBC(0, 1, 6, 1));
            groovySciToolboxesPanel.add(new JLabel("Available Toolboxes for GroovySci"), new GBC(0, 2, 1, 1));
            groovySciToolboxesPanel.add(availableToolboxesScrollPane, new GBC(1, 2, 2, 1 ));
            groovySciToolboxesPanel.add(new JLabel("Loaded Toolboxes for GroovySci"), new GBC(3, 2, 1, 1));
            groovySciToolboxesPanel.add(loadedToolboxesScrollPane, new GBC(4, 2, 2, 1));
            
            allPanel = new JPanel();
            groovyShellSettingsPanel = new JPanel();
            groovyShellConfig = new groovySciScriptsPathsTree();
            groovyShellConfig.buildVariablesTree();
            groovyShellSettingsPanel.add(groovyShellConfig);
            allPanel.setLayout(new GridLayout(2,1));
            allPanel.add(groovySciToolboxesPanel);
            allPanel.add(groovyShellConfig);
  
  return allPanel;
  
                    
     }
   
}  // class groovySciToolboxes





// this class updates the system in order to consider the additional .jar toolboxes placed at the "Available" toolboxes list
    
    class updateSystemWithJarToolboxesOnlyClassPath extends AbstractAction {
             updateSystemWithJarToolboxesOnlyClassPath() { super("Update system with jar toolboxes by creating a new GroovyShell with the proper classpath "); }
            
             
       
           public void actionPerformed(ActionEvent e) {
   //  set some flags that control the re-initialization of GroovyClassLoader, GroovyShell, GroovyScriptEngine
               GlobalValues.jarToolboxesClassPathUpdatedForGroovyClassLoader = false;  // require from the GroovyClassLoader  to account the .jar toolboxes
               GlobalValues.jarToolboxesClassPathUpdatedForGroovyShell  = false;  // require from the GroovyShell to account the .jar toolboxes
               GlobalValues.jarToolboxesClassPathUpdatedForGroovyScriptEngine = false;  // require from the GroovyScriptEngine to account the .jar toolboxes

           
           // for all the elements of the available toolboxes for GroovySci load the corresponding toolbox
           int numToolboxes = groovySciToolboxes.groovySciToolboxesAvailableListModel.size();
     
           // move the .jar toolboxes strings from groovySciToolboxesAvailableList to groovySciToolboxesLoadedList
           Enumeration  availableToolboxes =  groovySciToolboxes.groovySciToolboxesAvailableListModel.elements();
                 // copy the toolboxes to the loaded list
           while (availableToolboxes.hasMoreElements())   {
               String currentToolbox = (String)availableToolboxes.nextElement();
               if (groovySciToolboxes.groovySciToolboxesLoadedListModel.contains(currentToolbox) == false) 
                 groovySciToolboxes.groovySciToolboxesLoadedListModel.addElement(currentToolbox);
           }
           groovySciToolboxes.groovySciToolboxesAvailableListModel.removeAllElements();   // remove the toolboxes list from the available list


           GlobalValues.groovyJarClassesLoaded = true;    // sets this flag in order to avoid reloading the toolbox list unless there is a change

           // Configure the ClassPath for GroovyShell
            // set the compiler configuration to have the list of the specified .jar files in its classpath
      
           String [] additionalToolboxes = new String[numToolboxes];
           for (int k=0; k< groovySciToolboxes.groovySciToolboxesLoadedListModel.size(); k++)  {
                String currentToolboxName = (String)groovySciToolboxes.groovySciToolboxesLoadedListModel.getElementAt(k);
                additionalToolboxes[k] = currentToolboxName;
            }
      
            CompilerConfiguration cf = new CompilerConfiguration();
            GlobalValues.prepareImports();
            cf.addCompilationCustomizers(GlobalValues.globallmportCustomizer);
  
            if (GlobalValues.CompileDecimalsToDoubles)  {    // convert BigDecimalsToDoubles for efficiency
            
               cf.addCompilationCustomizers(new CompilationCustomizer(CompilePhase.CONVERSION) {
                
                    @Override
                    public void call(final SourceUnit source, final  GeneratorContext context, final ClassNode classNode)   {
                        new expandRunTime.ConstantTransformer(source).visitClass(classNode);
                    }
                });
            }
            
            cf.setTargetBytecode(GlobalValues.jdkTarget);
            if (GlobalValues.CompileIndy == true)  {
               cf.getOptimizationOptions().put("indy", true);
               cf.getOptimizationOptions().put("int", false);
            }
                        else             {
               cf.getOptimizationOptions().put("indy", false);
               cf.getOptimizationOptions().put("int", true);
            }

            cf.setRecompileGroovySource(true);
            
            LinkedList <String> pathsList =  groovySciToolboxes.warmUpGroovy(additionalToolboxes);
        
            cf.setClasspathList(pathsList);
            
            groovySciToolboxes.groovyShellConfig = new groovySciScriptsPathsTree();
            groovySciToolboxes.groovyShellConfig.buildVariablesTree();
            groovySciToolboxes.groovyShellSettingsPanel.add(groovySciToolboxes.groovyShellConfig);
            groovySciToolboxes.allPanel.removeAll();
            groovySciToolboxes.allPanel.setLayout(new GridLayout(2,1));
            groovySciToolboxes.allPanel.add(groovySciToolboxes.groovySciToolboxesPanel);
            groovySciToolboxes.allPanel.add(groovySciToolboxes.groovyShellConfig);
            groovySciToolboxes.allPanel.repaint();
  
   //  delegate the class loading its parent class loader, in case the parent class loader fails to load the class,
   //  then the class paths specified for the GroovyShell will be attempted
            final ClassLoader parentClassLoader = GroovyShell.class.getClassLoader();
            GlobalValues.GroovyShell  = new GroovyShell( parentClassLoader, GlobalValues.groovyBinding, cf);
        
          
            // perform Groovy's MetaClass related extensions
            expandRunTime.expandGroovy expG = new expandRunTime.expandGroovy();
            expG.run();
           
          
             
          
           }
  
         }
        
               
// this class updates the system in order to consider the additional .jar toolboxes placed at the "Available" toolboxes list
    
    class updateSystemWithJarToolboxes extends AbstractAction {
             updateSystemWithJarToolboxes() { super("Update system with jar toolboxes  "); }
            
             
       
           public void actionPerformed(ActionEvent e) {
   //  set some flags that control the re-initialization of GroovyClassLoader, GroovyShell, GroovyScriptEngine
               GlobalValues.jarToolboxesClassPathUpdatedForGroovyClassLoader = false;  // require from the GroovyClassLoader  to account the .jar toolboxes
               GlobalValues.jarToolboxesClassPathUpdatedForGroovyShell  = false;  // require from the GroovyShell to account the .jar toolboxes
               GlobalValues.jarToolboxesClassPathUpdatedForGroovyScriptEngine = false;  // require from the GroovyScriptEngine to account the .jar toolboxes

              if (GlobalValues.groovyJarClassesLoaded ==  false) {   // the classes of the toolboxes for Groovy were not loaded
             GlobalValues.groovyToolboxesLoader = new JarClassLoader();
          try {
           
           // for all the elements of the available toolboxes for GroovySci load the corresponding toolbox
           int numToolboxes = groovySciToolboxes.groovySciToolboxesAvailableListModel.size();
           for (int k=0; k<numToolboxes; k++)    {  // for all toolboxes for GroovySci
               int numClassesOfToolbox;
               String  forGroovyToolboxJarName = (String) groovySciToolboxes.groovySciToolboxesAvailableListModel.getElementAt(k);  // full path name of the toolbox JAR file
               numClassesOfToolbox  = GlobalValues.groovyToolboxesLoader.scanAllBuiltInClasses(forGroovyToolboxJarName);
               System.out.println("numClassesOfToolbox = "+numClassesOfToolbox+" available from toolbox "+forGroovyToolboxJarName);
           }  // for all toolboxes  
     
           // move the .jar toolboxes strings from groovySciToolboxesAvailableList to groovySciToolboxesLoadedList
           Enumeration  availableToolboxes =  groovySciToolboxes.groovySciToolboxesAvailableListModel.elements();
                 // copy the toolboxes to the loaded list
           while (availableToolboxes.hasMoreElements())   {
               String currentToolbox = (String)availableToolboxes.nextElement();
               if (groovySciToolboxes.groovySciToolboxesLoadedListModel.contains(currentToolbox) == false) 
                   groovySciToolboxes.groovySciToolboxesLoadedListModel.addElement(currentToolbox);
           }
           groovySciToolboxes.groovySciToolboxesAvailableListModel.removeAllElements();   // remove the toolboxes list from the available list


           GlobalValues.groovyJarClassesLoaded = true;    // sets this flag in order to avoid reloading the toolbox list unless there is a change

           // Configure the ClassPath for GroovyShell
            // set the compiler configuration to have the list of the specified .jar files in its classpath
      
           String [] additionalToolboxes = new String[numToolboxes];
           for (int k=0; k< groovySciToolboxes.groovySciToolboxesLoadedListModel.size(); k++)  {
                String currentToolboxName = (String)groovySciToolboxes.groovySciToolboxesLoadedListModel.getElementAt(k);
                additionalToolboxes[k] = currentToolboxName;
            }
      
            CompilerConfiguration cf = new CompilerConfiguration();
            GlobalValues.prepareImports();
            cf.addCompilationCustomizers(GlobalValues.globallmportCustomizer);
  
            if (GlobalValues.CompileDecimalsToDoubles)  {    // convert BigDecimalsToDoubles for efficiency
            
               cf.addCompilationCustomizers(new CompilationCustomizer(CompilePhase.CONVERSION) {
                
                    @Override
                    public void call(final SourceUnit source, final  GeneratorContext context, final ClassNode classNode)   {
                        new expandRunTime.ConstantTransformer(source).visitClass(classNode);
                    }
                });
            }
            
            cf.setTargetBytecode(GlobalValues.jdkTarget);
            if (GlobalValues.CompileIndy == true)  {
               cf.getOptimizationOptions().put("indy", true);
               cf.getOptimizationOptions().put(":int", false);
                }
                        else             {
               cf.getOptimizationOptions().put("indy", false);
               cf.getOptimizationOptions().put("int", true);
            }

            cf.setRecompileGroovySource(true);
            
            LinkedList <String> pathsList =  groovySciToolboxes.warmUpGroovy(additionalToolboxes);
        
            cf.setClasspathList(pathsList);
            
            groovySciToolboxes.groovyShellConfig = new groovySciScriptsPathsTree();
            groovySciToolboxes.groovyShellConfig.buildVariablesTree();
            groovySciToolboxes.groovyShellSettingsPanel.add(groovySciToolboxes.groovyShellConfig);
            groovySciToolboxes.allPanel.removeAll();
            groovySciToolboxes.allPanel.setLayout(new GridLayout(2,1));
            groovySciToolboxes.allPanel.add(groovySciToolboxes.groovySciToolboxesPanel);
            groovySciToolboxes.allPanel.add(groovySciToolboxes.groovyShellConfig);
            groovySciToolboxes.allPanel.repaint();
  
   //  delegate the class loading its parent class loader, in case the parent class loader fails to load the class,
   //  then the class paths specified for the GroovyShell will be attempted
            final ClassLoader parentClassLoader = GroovyShell.class.getClassLoader();
            GlobalValues.GroovyShell  = new GroovyShell( parentClassLoader, GlobalValues.groovyBinding, cf);
        //}   // construct a properly inited GroovyShell
        /*else  { // append the toolboxes to the current GroovyShell
            for (int k=0; k< groovySciToolboxes.groovySciToolboxesLoadedListModel.size(); k++)  {
                String currentToolboxName = (String)groovySciToolboxes.groovySciToolboxesLoadedListModel.getElementAt(k);
                GlobalValues.GroovyShell.getClassLoader().addClasspath(currentToolboxName);
                System.out.println("adding to GroovyShell path "+currentToolboxName);
            }
        }*/

          
          // display the classes of the available toolboxes using reflection
          for (int k=0; k<groovySciToolboxes.groovySciToolboxesLoadedListModel.size(); k++) {
             groovySciToolbox gsciToolbox = gExec.gLab.groovySciToolboxes.gsciToolboxes.elementAt(k);
             String currentToolboxName = gsciToolbox.toolboxName;
             if (groovySciToolboxes.framesOfToolboxClasses.containsKey(currentToolboxName) == false)  
                { // scan classes if toolbox is not already loaded
                        gExec.gui.WatchClasses  watchClassesOfToolbox = new gExec.gui.WatchClasses();
            
            watchClassesOfToolbox.displayClasses( gsciToolbox.toolboxClasses, gsciToolbox.toolboxName, gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
           }
          
             
          
          }  // construct a properly inited GroovyShell
          
          catch (java.io.IOException ex)  {
              System.out.println("java.io.IOException in loading Groovy jar files");
              ex.printStackTrace();
           }
          
  
         }
  
        }
               
    } 
       

