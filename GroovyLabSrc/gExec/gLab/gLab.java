
package gExec.gLab;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.URL;

import gExec.Interpreter.*;

import gExec.gui.*;

import gLab.help.helpToolboxes;

import gLabEdit.*;


import gExec.Interpreter.GlobalValues;
import gLabGlobals.JavaGlobals;


import java.net.URLDecoder;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;


 class replClass implements Runnable {

        @Override
        public void run() {
                     String [] rargs = new String[1];
            rargs[0] = "";
            try {
                javarepl.Main.main(rargs);
            } catch (Exception ex) {
                Logger.getLogger(gLab.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
 }
    
/**
 * Simple GUI for GroovyLab
 */
public class gLab extends JFrame implements WindowListener, ActionListener
{
        static private String netbeansjLabArg;  // name of gLab executable .jar when starting from Netbeans
    // applications that can start from within
        static public gLab uiMainFrame; 
        static public replClass replThread;                      
      
        public  JTabbedPane  tabbedToolbars;
        private  BasicControlOperationsToolbar  basicOpsToolbar;
        private  DoubleDoubleArrayToolbar doubleDoubleArrayToolbar;
        private  VecOperationsToolbar  vecOpsToolbar;
        private  MatrixOperationsToolbar  matrixOpsToolbar;
        private  PlotOperationsToolbar plotOpsToolbar;
        private OptimizationToolbar optimizationToolbar;
        private ODEToolbar odeToolbar;
        private CalculusToolbar  calculusToolbar;
        private WaveletToolbar waveletToolbar;
        
        public  JMenuBar  mainJMenuBar;
        

        private JMenu   editMenu;
        private JMenuItem jLabIDEJMenuItem;
        private JMenuItem editJMenuItem;
        
        public JMenu    FileMenu;
        
        private JMenuItem saveHistoryItem;
        private JMenuItem loadHistoryItem;
        private JMenuItem displayHistoryItem;
        private JMenuItem clearHistoryItem;
        
        private JMenu   confMenuGroovy;   // basic configurations for the Groovy Interpreter
        private JMenu   confMenuOthers;    // other configurations
        public  JMenuItem controlIndyJMenuItem; 
        public  JMenuItem controlDefaultlmportsJMenuItem; 
        
        public JMenuItem groovyLabServerPortJMenuItem;
        
        private JMenuItem resetGroovyShellLoadedClassesJMenuItem;
        private JMenuItem resetGroovyShellJMenuItem;        ;
        private JMenuItem adjustFontsUIMenuItem;  // for main menus
        private JMenuItem adjustFontspUIMenuItem;  // for popup menus
        private JMenuItem adjustFontsgUIMenuItem; // for other GUI components 
        private JMenuItem adjustHtmlFontsJMenuItem; // for html help
        private JMenuItem adjustsFontsbUIMenuItem;   // for buttons
        private JMenuItem outConsoleAdjustFontMenuItem;
        private JMenuItem htmlWithSystemBrowser;   
        
        private JMenuItem controlMainToolbarJMenuItem;
        private JMenuItem paneAdjustFontJMenuItem;
        private JMenuItem adjustFontMenuItem;
        private JMenuItem adjustLookAndFeelMenuItem;
        private JMenuItem browseFileSysForPathsJMenuItem;
        private JMenuItem browseClassesJMenuItem;
        private JMenuItem browseGroovySciJMenuItem;
        private JMenuItem promptJMenuItem;
        //private JMenuItem configAlphaParamJMenuItem;

        
        private JMenu     helpMenu;
        private JMenuItem aboutHelpJMenuItem;
        
        
        public JMenuItem  getPromptJMenuItem()  { return promptJMenuItem; }
        
        private JMenu      toolboxesMenu;
        private JMenu  toolBarsMenu;
        
        private JMenu    examplesMenu;
        private JMenuItem GroovySciExamplesHelpJMenuItem;
        private JMenuItem GroovySciPlotsExamplesJMenuItem;
        private JMenuItem GroovySciExamplesHelpJTreeJMenuItem;
        private JMenuItem GroovySciPlotsExamplesHelpJTreeJMenuItem;
        
        private JMenu  wizardsMenu;
        private JMenuItem wizardsGroovySciJMenuItem;
        private JMenuItem wizardsGroovySciGroovyJMenuItem;
        private JMenuItem wizardsGroovySciGroovyTypedJMenuItem;
        private JMenuItem wizardsODEScriptJMenuItem;
        
        private JMenuItem toolboxesHelpJMenuItem;
        
        private JMenuItem basicFunctionsHelpJMenuItem;
        private JMenuItem exitJMenuItem; 
        
        private JMenu symbolicAlgebraJMenu;
        private JMenuItem startsymJavaJMenuItem;
        
        static public  Dimension ScreenDim;
        
        static JScrollPane  varsScrolPane;
        static JScrollPane  historyScrolPane;
        static JScrollPane  pathsScrollPane;
        public static JSplitPane      outerPane;
        static public JTabbedPane     uiTabbedPane;
        
        /**The area used for user input and where the answers are displayed*/
        public gExec.gui.gLabConsole   jLabConsole;
        private JScrollPane  consPane;
        private JPanel  mainStatusPanel;
        private JPanel consPanel; // the console's panel
        public static  JPanel  historyPanel = new JPanel();
        public  static JPanel    variablesWorkSpacePanel = new JPanel();
        public static  JPanel   historyVarsPanel = new JPanel();
        public static gLabExplorer    explorerPanel;
        
        /**The interpreter*/
        static public Interpreter interpreter;

        /**String used for defining user functions*/
        private String function;

        public JFrame myId;

        public  static int xSizeMainFrame, ySizeMainFrame;
        public  int xSizeVarsFrame, ySizeVarsFrame;
        public static int xLocMainFrame, yLocMainFrame;

        
        
         private  URL watchURL;
         private URL  displayCachedClassesURL, ODEWizardURL, ClassWizardURL;
         private URL  pathsConfigURL;
         private URL saveURL;
         private URL groovyImageURL, groovyLogoImageURL;
         private Image watchImage, sunJavaImage, ODEWizardImage, ClassWizardImage;
         private Image saveImage, userFunctionsImage;

         public  Image  javaHelpImage,  IDEImage, groovyImage, groovyLogoImage;
         public Image configImage;
         
         static Class [] formals = { String [].class };
         static Object [] actuals = { new String [] {""}};

        static public JTable  varsTable;
        static public JPopupMenu variablesPanelPopupMenu;
        static public JPopupMenu historyPanelPopupMenu;
        
        public GlobalValues  instanceGlobals;

        private int   horizDividerLoc; 
        private int  vertDividerLocConsole;

        // for history list handling
        private Object[] values;  // currently selected values
        private JList historyList;
        private String watchStr;

        	
        /**Reacts to the user menu and update (if necessary) the interface.*/
        public void actionPerformed(ActionEvent e)
        {
                Object o = e.getSource();

                if (o == exitJMenuItem ) {
                        closeGUI();
                }
        }

        
/**Function called when the gui is being close*/
        public void closeGUI()
        {
                
            String fullNameOfPropsFile = "Glab.props";
    
            try {
    File outPropFile = new File(fullNameOfPropsFile);
               
   FileOutputStream outFile= new FileOutputStream(outPropFile);
   GlobalValues.passPropertiesFromWorkspaceToSettings(GlobalValues.settings);  // update properties to the current values kept in workspace
   GlobalValues.settings.store(outFile, "Saved GroovyLab global conf parameters");
   outFile.close();
    }
     catch (Exception fnfe) {
        JOptionPane.showMessageDialog(null, "Cannot write configuration file. Perhaps you do not have access rights for write, try making a shortcut to gLab using a proper \"Start in\" directory ","Cannot write configuration file", JOptionPane.INFORMATION_MESSAGE);
        System.out.println("error opening file for writing configuration");
        fnfe.printStackTrace();
        
        }
            int userOption = JOptionPane.CANCEL_OPTION;
            if (GlobalValues.myGEdit.editorTextSaved == false )  
      userOption = JOptionPane.showConfirmDialog(null, "File: "+GlobalValues.myGEdit.editedFileName+" not saved. Proceed? ", 
                        "Warning: Exit without Save?", JOptionPane.CANCEL_OPTION);
            else userOption = JOptionPane.YES_OPTION;
            if (userOption == JOptionPane.YES_OPTION)  {
                GlobalValues.myGEdit.saveRecentPaneFiles();   // save the list of the recently accessed files
              
           
    
   try {
   GlobalValues.writeUserPaths();
   
    gExec.gLab.commandHistory.saveCommandHistory(gExec.Interpreter.GlobalValues.GroovyLabCommandHistoryFile, GlobalValues.userConsole.previousCommands);
    gExec.gLab.favouritePaths.saveFavouritePaths(gExec.Interpreter.GlobalValues.GroovyLabFavoritePathsFile, gExec.Interpreter.GlobalValues.gLabMainFrame.explorerPanel.favouritePathsCB);
    System.exit(0);
    }
    catch (Exception fnfe) {
        JOptionPane.showMessageDialog(null, "Cannot write user paths");
        System.out.println("error opening file for writing configuration");
        fnfe.printStackTrace();
        System.exit(0);
        }
      }
  }


  /*
    * This method will take a file name and try to "decode" any URL encoded characters.  For example
    * if the file name contains any spaces this method call will take the resulting %20 encoded values
    * and convert them to spaces.
    *
    */
    public static String decodeFileName(String fileName) {
        String decodedFile = fileName;

        try {
            decodedFile = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println("Encounted an invalid encoding scheme when trying to use URLDecoder.decode() inside of the GroovyClassLoader.decodeFileName() method.  Returning the unencoded URL.");
            System.err.println("Please note that if you encounter this error and you have spaces in your directory you will run into issues.  Refer to GROOVY-1787 for description of this bug.");
        }

        return decodedFile;
    }

    
       
        
public void redirectConsole()  {
    
}
        /**Create the main graphical interface (menu, buttons, delays...).*/
        public gLab( String gLabClassPath)
        {
            
        
                
                 
                setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                setTitle(GlobalValues.TITLE + " indy = "+GlobalValues.CompileIndy +",  BigDecimalsToDoubles = "+GlobalValues.CompileDecimalsToDoubles);
                String crossPlatformLookAndFeel="";
                ScreenDim = Toolkit.getDefaultToolkit().getScreenSize();
                //position the frame in the centre of the screen
                xSizeMainFrame = (int)((double)ScreenDim.width/1.2);  
                ySizeMainFrame = (int)((double)ScreenDim.height/1.4);
                xLocMainFrame = (ScreenDim.width-xSizeMainFrame) / 25;
                yLocMainFrame = (ScreenDim.height-ySizeMainFrame)/25 ; 
               
                GlobalValues.sizeX = xSizeMainFrame;
                GlobalValues.sizeY = ySizeMainFrame;
                horizDividerLoc =  (int)(0.3*xSizeMainFrame);
                vertDividerLocConsole = (int)(0.2*ySizeMainFrame);
        
                GlobalValues.figFrameSizeX = (int)((double)xSizeMainFrame/2.0);
                GlobalValues.figFrameSizeY = (int)((double)ySizeMainFrame/1.5);
          
                GlobalValues.consoleOutputWindow = new SysUtils.ConsoleWindow();
                
                  
         
              // the watchURL will serve to retrieve the name of the .jar file of the GroovyLab system (i.e. "GroovyLab.jar") 
       // the GroovyLab's .jar file is used to load all the basic external classes and thus is very important.
       // When GroovyLab is running within the Netbeans environment it is passed as the first program argument.          
            watchURL = getClass().getResource("resources/system-search.png");
            watchStr = watchURL.toString();
    
            watchStr = decodeFileName(watchStr);
            
            GlobalValues.rt = Runtime.getRuntime();
            GlobalValues.memAvailable = GlobalValues.rt.freeMemory();
       
            detectGroovyClassPaths.detectClassPaths();
            
            detectPaths(watchStr);
           
            
            if (GlobalValues.hostIsUnix==false)  {  // handle Windows file system naming
               int idxOfColon = watchStr.lastIndexOf(':'); 
               watchStr = watchStr.substring(idxOfColon-1, watchStr.length());
            }
            int sepIndex = watchStr.indexOf('!');
            if (sepIndex!=-1) {
                
                String fullJarPath = watchStr.substring(0, sepIndex);
                String jLabJarName = fullJarPath.substring(fullJarPath.lastIndexOf(File.separatorChar)+1, fullJarPath.length() );
                GlobalValues.jarFilePath =jLabJarName;
                GlobalValues.fullJarFilePath = fullJarPath;
            }
        
       //     System.out.println("GlobalValues.fullJarFilePath = "+GlobalValues.fullJarFilePath+"  GlobalValues.jarFilePath = "+GlobalValues.jarFilePath);
            
        
        URL configImageURL = getClass().getResource("resources/groovyLogo.png");
        groovyImageURL = getClass().getResource("resources/groovyLogo.png");
        groovyLogoImageURL = getClass().getResource("resources/groovyLogo.png");
       
   configImage = Toolkit.getDefaultToolkit().getImage(configImageURL);
   groovyImage = Toolkit.getDefaultToolkit().getImage(groovyImageURL);
   groovyLogoImage = Toolkit.getDefaultToolkit().getImage(groovyLogoImageURL);
   GlobalValues.groovyIcon = new ImageIcon(groovyLogoImage);
   groovyLogoImage = Toolkit.getDefaultToolkit().getImage(groovyLogoImageURL);
       
            watchImage = Toolkit.getDefaultToolkit().getImage(watchURL);
            ODEWizardURL = getClass().getResource("resources/wizard.png");
            ODEWizardImage = Toolkit.getDefaultToolkit().getImage(ODEWizardURL);
            displayCachedClassesURL = getClass().getResource("resources/sun-java.png");
            sunJavaImage = Toolkit.getDefaultToolkit().getImage(displayCachedClassesURL);
            saveURL = getClass().getResource("resources/save.gif");
            saveImage = Toolkit.getDefaultToolkit().getImage(saveURL);
            pathsConfigURL = getClass().getResource("resources/paths.png");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                  
        watchURL = getClass().getResource("resources/system-search.png");
        watchImage = Toolkit.getDefaultToolkit().getImage(watchURL);
        myId = this;

         
        GlobalValues.initGlobals();
            
        GlobalValues.passPropertiesFromSettingsToWorkspace(GlobalValues.settings);
        
           // Force SwingSet to come up in the Cross Platform L&F
	try {
            crossPlatformLookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            if (GlobalValues.nativeLookAndFeel==false) {
                UIManager.setLookAndFeel(crossPlatformLookAndFeel);
            }  else {  
            String sysLookAndFeel = UIManager.getSystemLookAndFeelClassName();
             UIManager.setLookAndFeel(sysLookAndFeel);
            }
	} catch (Exception exc) {
	    System.err.println("Error loading L&F: " + exc);
	}
        
        InitJMenuBar(this);
        
        GlobalValues.gLabMainFrame = this;   // keep instance of GroovyLab main frame
          
        
        if (GlobalValues.gLabMainFrame != null)  {
            Rectangle  b = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
            if (GlobalValues.rememberSizesFlag == false)  {
         GlobalValues.gLabMainFrame.setLocation(b.width/2, 100);
         GlobalValues.gLabMainFrame.setSize((b.width / 2)-50, b.height * 5 / 6 );
            }
            else
            {
                GlobalValues.gLabMainFrame.setLocation(GlobalValues.locX, GlobalValues.locY);
                GlobalValues.gLabMainFrame.setSize(GlobalValues.sizeX, GlobalValues.sizeY);
            }
           int uiFontSizeVal = Integer.parseInt(GlobalValues.uiFontSize);
           int consoleFontSizeVal = Integer.parseInt(GlobalValues.outConsoleFontSize);
            GlobalValues.gLabMainFrame.setFont(new Font(GlobalValues.uiFontName, Font.PLAIN, uiFontSizeVal));
       }
        
        
        
        GlobalValues.readUserPaths();
        
        
        InitConsole();

        
        interpreter = new Interpreter();
        groovySciToolboxes.warmUpGroovy();
        
        
        
    
        gExec.gLab.commandHistory.loadCommandHistory(gExec.Interpreter.GlobalValues.GroovyLabCommandHistoryFile, GlobalValues.userConsole.previousCommands);
        
                SwingUtilities.invokeLater(new Runnable() {
     public void run() {  // run in  */
                myId.addWindowListener((WindowListener)myId);
                
                
                myId.setResizable(true);
        
        uiTabbedPane = new JTabbedPane();
        uiTabbedPane.addTab("", GlobalValues.groovyIcon, null);
        uiTabbedPane.setToolTipTextAt(GlobalValues.mainTab, "The main tab consists of the Console Prompt, the Explorer and the Workspace viewer. Symbolic formulas in LaTeX style are displayed also."); 
        uiTabbedPane.addTab("GroovySci Toolboxes", null, null);
        uiTabbedPane.setToolTipTextAt(GlobalValues.groovySciTab, "The GroovySci toolboxes tab allows to import additional libraries packed in .jar files"); 
        
        
        add(uiTabbedPane, "Center");
        
        
           // refocus always on the main input console window
        uiTabbedPane.addFocusListener(new FocusListener() {
           public void focusGained(FocusEvent e) {
                        FocusEvent fe = new FocusEvent(jLabConsole, FocusEvent.FOCUS_GAINED);
                        jLabConsole.dispatchEvent(fe);    
         }
           public void focusLost(FocusEvent e) {
                        FocusEvent fe = new FocusEvent(jLabConsole, FocusEvent.FOCUS_GAINED);
                        jLabConsole.dispatchEvent(fe);    
                    }
                });
        
        uiTabbedPane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
              int n = uiTabbedPane.getSelectedIndex();
              loadTab(n);
                    }
                });
      
                loadTab(GlobalValues.mainTab);
                
                
    GlobalValues.consoleOutputWindow.output.setFont(new Font(GlobalValues.outConsoleFontName, Font.PLAIN, Integer.parseInt(GlobalValues.outConsoleFontSize)));
                
GlobalValues.myGEdit = new gLabEditor("Untitled", true);
    
         GlobalAutoCompletion.initAutoCompletion();
                
                }         
            }); 

                
                
                
                    
            }                
        
         private  class MouseAdapterForHistory extends  MouseAdapter {

             public void mouseClicked(MouseEvent evt) {
                 if (evt.getClickCount() == 2) {
                 
               StringBuilder text = new StringBuilder();
               for (int i = 0; i < values.length; i++)
               {
                  String word = (String) values[i];
                  text.append(word);
                  text.append(" ");
               }

         interpretLine(text.toString());
        

                 }
             }

           public void mousePressed(MouseEvent e) {   
               
                 historyPanelPopupMenu  = new JPopupMenu(); 
                 JMenuItem clearHistoryLocalItem = new JMenuItem("Clear History");
                 clearHistoryLocalItem.addActionListener(new clearHistoryAction());
                JMenuItem loadHistoryLocalItem = new JMenuItem("Load History");
                 loadHistoryLocalItem.addActionListener(new loadHistoryAction());
                JMenuItem saveHistoryLocalItem = new JMenuItem("Save History");
                 saveHistoryLocalItem.addActionListener(new saveHistoryAction());
    
                 historyPanelPopupMenu.add(clearHistoryLocalItem);
                 historyPanelPopupMenu.add(loadHistoryLocalItem);
                 historyPanelPopupMenu.add(saveHistoryLocalItem);
                 
               if (e.isPopupTrigger()){  
               historyPanelPopupMenu.show((Component) e.getSource(), e.getX(), e.getY());
             }
           }
    
            public void mouseReleased(MouseEvent e) { 
           if (e.isPopupTrigger()){
                historyPanelPopupMenu.show((Component) e.getSource(), e.getX(), e.getY());
             }       
             
          }
       }
       
        public void updateHistoryWindow() {
             historyList = new JList(GlobalValues.userConsole.previousCommands);
             historyPanel.removeAll();
             historyPanel.add(historyList);
             historyList.setToolTipText("History of recent commands. You can reexecute a command by selecting it. Double-clicking the command executes it directly..");
             historyList.addMouseListener(new MouseAdapterForHistory());
         
             
             historyList.addListSelectionListener(new        ListSelectionListener()
         {
            public void valueChanged(ListSelectionEvent event)
            {  
               values = historyList.getSelectedValues();

               StringBuilder text = new StringBuilder();
               for (int i = 0; i < values.length; i++)
               {  
                  String word = (String) values[i];
                  text.append(word);
                  text.append(" ");
               }
               
               // replace current command with next command
    	String textArea = GlobalValues.userConsole.getText();
    	int    pos1     = textArea.lastIndexOf("# ") + 2;
    	GlobalValues.userConsole.setText(textArea.substring(0,pos1)+text);
    	
    	// set cursor at the end of the text area
        GlobalValues.userConsole.setCaretPosition(GlobalValues.userConsole.getText().length());
            }
         });

          
        } 
        
        private void loadTab(int n)
        {
            String  title = uiTabbedPane.getTitleAt(n);
            
            Dimension dimTab = uiTabbedPane.getSize();
            GlobalValues.xSizeTab = dimTab.width;
            GlobalValues.ySizeTab = dimTab.height;
                  
        
        switch (n)  {
                case GlobalValues.mainTab:
        
        historyVarsPanel = new JPanel(new GridLayout(2,1));
        historyPanel  = new JPanel(new BorderLayout());
        
        variablesWorkSpacePanel = new JPanel(new BorderLayout());
        updateHistoryWindow();
        
        varsScrolPane = new JScrollPane(variablesWorkSpacePanel);
        historyScrolPane = new JScrollPane(historyPanel);
        historyVarsPanel.add(historyScrolPane);
        historyVarsPanel.add(varsScrolPane);
        
        gExec.gui.WatchWorkspace.displayGroovySciBinding(gExec.gLab.gLab.variablesWorkSpacePanel);
         
        explorerPanel = new gLabExplorer(); 
        explorerPanel.setLayout(new BorderLayout());
        explorerPanel.buildClassScriptPathsTree();
        
     
          
        JPanel  textPanelExplorerDirs = new JPanel(new BorderLayout());  // up panel containing label text
        java.text.DecimalFormat fmt = new java.text.DecimalFormat("0.00");
        String  mem =  fmt.format( (double)GlobalValues.rt.freeMemory()/1000000);
        String dispStr = "Available memory : "+mem+"MB.  ";
         GlobalValues.availMemLabel  = new JLabel(dispStr);
         GlobalValues.availMemLabel .setFont(new Font("Arial", Font.BOLD, 12));
         textPanelExplorerDirs.add(GlobalValues.availMemLabel, BorderLayout.EAST);
        
        
        JPanel  textPanelExplorerBrowser = new JPanel(new BorderLayout());  // up panel containing label text
        JLabel  labelExplorerBrowser = new JLabel("GroovyLab explorer: edit, compile, run, create, delete files");
         labelExplorerBrowser.setFont(new Font("Arial", Font.BOLD, 12));
        textPanelExplorerBrowser.add(labelExplorerBrowser, BorderLayout.EAST);
         
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.add(textPanelExplorerDirs, BorderLayout.WEST);
        descriptionPanel.add(textPanelExplorerBrowser, BorderLayout.EAST);
         
        JPanel  explorerLabelPanel = new JPanel(new BorderLayout());
        explorerLabelPanel.add(descriptionPanel, BorderLayout.NORTH);
        explorerLabelPanel.add(explorerPanel, BorderLayout.SOUTH);
        
        pathsScrollPane = new JScrollPane(explorerLabelPanel);
        
        horizDividerLoc =  (int)(0.2*xSizeMainFrame);  // controls Explorer vs (History and Variables panels)
        JSplitPane varsHistPathsPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, historyVarsPanel, pathsScrollPane);
        varsHistPathsPane.setDividerLocation(horizDividerLoc);
        
        vertDividerLocConsole = (int)(0.3*ySizeMainFrame);  // controls the command input console size
        pathsScrollPane.setSize(horizDividerLoc, vertDividerLocConsole);
        
        outerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, varsHistPathsPane, consPane);
        outerPane.setDividerLocation(vertDividerLocConsole);
        
        varsScrolPane.setToolTipText("Displayed GroovyLab's workspace variables");
        uiTabbedPane.setComponentAt(GlobalValues.mainTab, outerPane);  
     
        
        
        // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(jLabConsole, FocusEvent.FOCUS_GAINED);
        jLabConsole.dispatchEvent(fe);    
        
        break;
            
                case GlobalValues.groovySciTab:
                    JScrollPane  GroovySciToolboxesPane =   new JScrollPane(groovySciToolboxes.handleGroovySciTab());
                    JFrame groovySciToolboxesFrame = new JFrame("GroovySci toolboxes");
                    groovySciToolboxesFrame.setSize(GlobalValues.sizeX, GlobalValues.sizeY);
                    groovySciToolboxesFrame.add(GroovySciToolboxesPane);
                    groovySciToolboxesFrame.setVisible(true);
                    
                  
                    break;  
                    
                      
                    
                
            default: break;
            } 
                
            }                    
                    
        public static void updateTree() {
              SwingUtilities.invokeLater(new Runnable() {
     public void run() {  // run in  */
  if (explorerPanel != null)   {
            explorerPanel.removeAll();
             explorerPanel.buildClassScriptPathsTree();
             explorerPanel.revalidate();
                    }
                }
              });
        }
              
          

        
       
        /**The main console initializer.*/
        private void InitConsole()
        {
               jLabConsole = new gExec.gui.gLabConsole();
               gExec.Interpreter.GlobalValues.userConsole = jLabConsole;
                
               int xSize = GlobalValues.gLabMainFrame.getSize().width;
               
               Font consoleFont = GlobalValues.consoleOutputWindow.output.getFont();
               int pts = consoleFont.getSize();
               GlobalValues.consoleCharsPerLine =(int) (0.8*(xSize/pts));
               
               SwingUtilities.invokeLater(new Runnable() {
     public void run() {  // run in  */
                consPanel = new  JPanel();
                consPanel.setLayout(new BorderLayout());
                consPanel.add(jLabConsole, BorderLayout.CENTER);
                mainStatusPanel = new JPanel();
                basicOpsToolbar = new BasicControlOperationsToolbar();
                doubleDoubleArrayToolbar = new DoubleDoubleArrayToolbar();
                matrixOpsToolbar = new MatrixOperationsToolbar();
                vecOpsToolbar = new VecOperationsToolbar();
                plotOpsToolbar = new PlotOperationsToolbar();
                tabbedToolbars  = new JTabbedPane();
                
                tabbedToolbars.setToolTipText("The toolbar of basic functions. Press CTRL-T to switch visibility status (e.g. hide/show)");
                
              LAToolbar laOpsToolbar = new LAToolbar();
              laOpsToolbar.setToolTipText("Linear Algebra");
              
              WaveletToolbar   waveletToolbar = new WaveletToolbar();
              waveletToolbar.setToolTipText("Wavelet Analysis Toolbar. At initial state of development yet!!");
              OptimizationToolbar  optimizationToolbar = new OptimizationToolbar();
              optimizationToolbar.setToolTipText("Wizards for performing numerical optimization tasks.  At initial state of development yet!!");
              ODEToolbar odeToolbar = new ODEToolbar();
              odeToolbar.setToolTipText("Wizards for performing ODE numerical solving tasks.  At initial state of development yet!!");
              PDEToolbar pdeToolbar = new PDEToolbar();
              pdeToolbar.setToolTipText("Partial Differential Equations solving tasks");
              SpecialFunctionsToolbar specialFunctionsToolbar = new SpecialFunctionsToolbar();
              specialFunctionsToolbar.setToolTipText("Special Functions");
              
              CalculusToolbar  calculusToolbar = new  CalculusToolbar();
              calculusToolbar.setToolTipText(" Wizards for performing Calculus.  At initial state of development yet!!");

              tabbedToolbars.setToolTipText("The  toolbar. Use CTRL-T to hide/show it (.. sorry, many toolbar operations are not yet complete ..)");

              int toolBarCnt=0;
                tabbedToolbars.addTab("[[D]]", doubleDoubleArrayToolbar);
                tabbedToolbars.setToolTipTextAt(toolBarCnt++, "2-D Array, i.e. Array[Array[Double]], supports NUMAL based one-indexed routines");
                tabbedToolbars.addTab("Vec", vecOpsToolbar);
                tabbedToolbars.setToolTipTextAt(toolBarCnt++, "Matrix Operations ");
                tabbedToolbars.addTab("Matrix", matrixOpsToolbar);
                tabbedToolbars.setToolTipTextAt(toolBarCnt++, "Matrix Operations ");
                tabbedToolbars.addTab("Plot", plotOpsToolbar);
                tabbedToolbars.setToolTipTextAt(toolBarCnt++, "Operations for controlling the environment");
                tabbedToolbars.addTab("Control", basicOpsToolbar);
                tabbedToolbars.setToolTipTextAt(toolBarCnt++, "GUI for Plotting Functions ");
                tabbedToolbars.addTab("LinearAlgebra", laOpsToolbar);
                tabbedToolbars.setToolTipTextAt(toolBarCnt++, "Linear Algrebra Functions");
              
                tabbedToolbars.addTab("Optimization", optimizationToolbar);
                tabbedToolbars.setToolTipTextAt(toolBarCnt++, "Numerical Optimization of functions");
                
                tabbedToolbars.addTab("ODE", odeToolbar);
                tabbedToolbars.setToolTipTextAt(toolBarCnt++, "Ordinary Differential Equations (ODEs) solvers");
                tabbedToolbars.addTab("PDE", pdeToolbar);
                tabbedToolbars.setToolTipTextAt(toolBarCnt++, "Partial Differential Equations (PDEs) solvers");
                tabbedToolbars.addTab("SpecialFunctions", specialFunctionsToolbar);
                tabbedToolbars.setToolTipTextAt(toolBarCnt++, "Special Functions toolbar");
                tabbedToolbars.addTab("Calculus", calculusToolbar);
                tabbedToolbars.setToolTipTextAt(toolBarCnt++, "Calculus utility routines, e.g. numerical integration, differentiation");
                tabbedToolbars.addTab("Wavelet", waveletToolbar);
                tabbedToolbars.setToolTipTextAt(toolBarCnt++, "Wavelet toolbox interface");
              
                GlobalValues.toolbarFrame= new JFrame("GroovyLab toolbars and buffered content");
                GlobalValues.toolbarFrame.add(tabbedToolbars);
                
                GlobalValues.toolbarFrame.pack();
                GlobalValues.toolbarFrame.setVisible(true);
            
        
                consPanel.add(mainStatusPanel, BorderLayout.SOUTH);
                //add(mainStatusPanel, BorderLayout.SOUTH);
                consPane = new JScrollPane(consPanel);
                add(consPane, BorderLayout.SOUTH);
                  }
               });
        }

     

         /**The menu initializer.*/
        private void InitJMenuBar(final ActionListener listener)
        {
                mainJMenuBar = new JMenuBar();
                mainJMenuBar.setFont(GlobalValues.uifont);
                        
               
                FileMenu = new JMenu("File");
                FileMenu.setFont(GlobalValues.uifont);
                FileMenu.setMnemonic('F');
                FileMenu.setToolTipText("File editing, Command history, Variable workspace operations");
           
                editMenu = new JMenu("Edit");
                editMenu.setFont(GlobalValues.uifont);
                editJMenuItem = new JMenuItem(new editAction());
                editJMenuItem.setMnemonic('E');
                editJMenuItem.setToolTipText("Starts a new RSyntaxArea based GroovyLab editor");
                editJMenuItem.setFont(GlobalValues.uifont);
                editJMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
               
                
             
                
                 
                 saveHistoryItem= new JMenuItem("Save Command History");
                 saveHistoryItem.setFont(GlobalValues.uifont);
                 saveHistoryItem.setToolTipText("Saves the buffer of the issued commands to an external file");
                 saveHistoryItem.addActionListener(new saveHistoryAction());
    
                 loadHistoryItem= new JMenuItem("Load Command History");
                 loadHistoryItem.setFont(GlobalValues.uifont);
                 loadHistoryItem.setToolTipText("Loads the buffer of the issued commands from an external file");
                 loadHistoryItem.addActionListener(new loadHistoryAction());
                         
                 displayHistoryItem= new JMenuItem("Display Command History");
                 displayHistoryItem.setFont(GlobalValues.uifont);
                 displayHistoryItem.setToolTipText("Displays the buffer of the issued commands");
                 displayHistoryItem.addActionListener(new displayHistoryAction());
              
                    
                 clearHistoryItem= new JMenuItem("Clear Command History");
                 clearHistoryItem.setFont(GlobalValues.uifont);
                 clearHistoryItem.setToolTipText("Clears the buffer of the issued commands");
                 clearHistoryItem.addActionListener(new clearHistoryAction());
                     
               
                exitJMenuItem = new JMenuItem("Exit");
                exitJMenuItem.setFont(GlobalValues.uifont);
                exitJMenuItem.addActionListener((ActionEvent e) -> {
                   closeGUI();
                });
              
                exitJMenuItem = new JMenuItem("Exit");
                
                editMenu.add(editJMenuItem);
                editMenu.setFont(GlobalValues.uifont);
          
                FileMenu.add(saveHistoryItem);
                FileMenu.add(loadHistoryItem);
                FileMenu.add(displayHistoryItem);
                FileMenu.add(clearHistoryItem);
                FileMenu.add(exitJMenuItem);
              
                JMenu  librariesMenu = new JMenu("Libraries");
                librariesMenu.setToolTipText("Investigates routines of various libraries using Java reflection in order to provide help");
                librariesMenu.setFont(GlobalValues.uifont);
                JMenuItem nrMenuItem = new JMenuItem("Numerical Recipes ");
                nrMenuItem.setFont(GlobalValues.uifont);
                librariesMenu.add(nrMenuItem);
                nrMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
         gExec.gui.WatchClasses  watchClassesOfNRNumAl = new gExec.gui.WatchClasses();

         Vector NRNumALClasses    =  gExec.ClassLoaders.JarClassLoader.scanNRNumAlLibs(JavaGlobals.numalFile);

         int k=1;
         watchClassesOfNRNumAl.displayClassesAndMethods( NRNumALClasses, "Numerical Recipes", gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                

                
                
                JMenuItem scalaSciRoutinesMenuItem = new JMenuItem("GroovySci  routines");
                scalaSciRoutinesMenuItem.setToolTipText("Display information using reflection for all the ScalaSci classes and methods");
                scalaSciRoutinesMenuItem.setFont(GlobalValues.uifont);
                librariesMenu.add(scalaSciRoutinesMenuItem);
                
                scalaSciRoutinesMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfGroovySci = new gExec.gui.WatchClasses();

         Vector scalaSciClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.jarFilePath, "groovySci");

         int k=1;
         watchClassesOfGroovySci.displayClassesAndMethods( scalaSciClasses, "GroovySci Classes", gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });

                JMenuItem groovySciGraphicsRoutinesMenuItem = new JMenuItem("groovySci  Plotting routines");
                groovySciGraphicsRoutinesMenuItem.setToolTipText("Display information using reflection for the groovySci plotting classes and methods");
                groovySciGraphicsRoutinesMenuItem.setFont(GlobalValues.uifont);
                librariesMenu.add(groovySciGraphicsRoutinesMenuItem);
                
                groovySciGraphicsRoutinesMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfGroovySci = new gExec.gui.WatchClasses();

         Vector groovySciPlottingClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.jarFilePath, "groovySci/math/plot");

         int k=1;
         watchClassesOfGroovySci.displayClassesAndMethods( groovySciPlottingClasses, "GroovySci Plotting Classses", gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                
                JMenuItem ejmlRoutinesMenuItem = new JMenuItem("EJML  routines");
                ejmlRoutinesMenuItem.setToolTipText("Display information using reflection for the EJML library classes and methods");
                ejmlRoutinesMenuItem.setFont(GlobalValues.uifont);
                librariesMenu.add(ejmlRoutinesMenuItem);
                
                ejmlRoutinesMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfEJML = new gExec.gui.WatchClasses();

         Vector EJMLClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.ejmlFile, "org");

         int k=1;
         watchClassesOfEJML.displayClassesAndMethods( EJMLClasses, "EJML Library", gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                JMenuItem apacheCommonsRoutinesJMenuItem = new JMenuItem("Apache Commons Math Routines");
                apacheCommonsRoutinesJMenuItem.setToolTipText("Display information using reflection for the Apache Commons math library classes and methods");
                apacheCommonsRoutinesJMenuItem.setFont(GlobalValues.uifont);
                librariesMenu.add(apacheCommonsRoutinesJMenuItem);
                
                apacheCommonsRoutinesJMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfApacheCommonsMath = new gExec.gui.WatchClasses();

         Vector ApacheCommonsClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.ApacheCommonsFile, "org/apache/commons/math3");

         int k=1;
         watchClassesOfApacheCommonsMath.displayClassesAndMethods( ApacheCommonsClasses, "Apache Common Maths Library", gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                
                
                JMenuItem JASRoutinesJMenuItem = new JMenuItem("Java Algebra System (JAS) Routines");
                JASRoutinesJMenuItem.setToolTipText("Display information using reflection for the Java Algebra System (JAS library classes and methods");
                JASRoutinesJMenuItem.setFont(GlobalValues.uifont);
                librariesMenu.add(JASRoutinesJMenuItem);
                
                JASRoutinesJMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfJAS  = new gExec.gui.WatchClasses();

         Vector JASClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.JASFile, "edu/jas");

         int k=1;
         watchClassesOfJAS.displayClassesAndMethods( JASClasses, "Java Algebra System (JAS) Library", gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                JMenuItem MathEclipseRoutinesJMenuItem = new JMenuItem("Math Eclipse  Routines");
                MathEclipseRoutinesJMenuItem.setToolTipText("Display information using reflection for the Math Eclipse system for symbolic maths");
                MathEclipseRoutinesJMenuItem.setFont(GlobalValues.uifont);
                librariesMenu.add(MathEclipseRoutinesJMenuItem);
                
                MathEclipseRoutinesJMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfMathEclipse  = new gExec.gui.WatchClasses();

         Vector MathEclipseClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.JASFile, "org/matheclipse/core");

         int k=1;
         watchClassesOfMathEclipse.displayClassesAndMethods( MathEclipseClasses, "Math Eclipse symbolic math evaluator", gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                JMenuItem numalMenuItem = new JMenuItem("NUMAL routines");
                numalMenuItem.setToolTipText("Display information using reflection for the NUMAL classes and methods");
                numalMenuItem.setFont(GlobalValues.uifont);
                librariesMenu.add(numalMenuItem);
                numalMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfNRNumAl = new gExec.gui.WatchClasses();

         Vector numalClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.numalFile, "numal");

         int k=1;
         watchClassesOfNRNumAl.displayClassesAndMethods( numalClasses, "NUMAL", gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                
                
                
                JMenuItem jlapackMenuItem = new JMenuItem("JLAPACK routines");
                jlapackMenuItem.setToolTipText("Display information using reflection for the JLAPACK  classes and methods");
                jlapackMenuItem.setFont(GlobalValues.uifont);
                librariesMenu.add(jlapackMenuItem);
                jlapackMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfJLAPACK = new gExec.gui.WatchClasses();

         Vector numalClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.LAPACKFile, "org");

         int k=1;
         watchClassesOfJLAPACK.displayClassesAndMethods( numalClasses, "JLAPACK", gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                
                JMenuItem jsciMenuItem = new JMenuItem("JSci  routines (contains Wavelet library, plotting, statistical routines)");
                jsciMenuItem.setToolTipText("Display information using reflection for the JSci classes and methods");
                jsciMenuItem.setFont(GlobalValues.uifont);
                librariesMenu.add(jsciMenuItem);
                jsciMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfJSci = new gExec.gui.WatchClasses();

         Vector JSciClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.jsciFile, "JSci");

         int k=1;
         watchClassesOfJSci.displayClassesAndMethods( JSciClasses, "JSci Library Routines", gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                
                
                JMenuItem joregonDSPMenuItem = new JMenuItem("Oregon Digital Signal Processing library routines");
                joregonDSPMenuItem.setToolTipText("Display information using reflection for the classes and methods of  the Oregon DSP  library ");
                joregonDSPMenuItem.setFont(GlobalValues.uifont);
                librariesMenu.add(joregonDSPMenuItem);
                joregonDSPMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfOregonDSP = new gExec.gui.WatchClasses();

         Vector oregonDSPClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.numalFile, "DSP");

         
         int k=1;
         watchClassesOfOregonDSP.displayClassesAndMethods( oregonDSPClasses, "Oregon DSP Library", gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });

                
                
               JMenuItem mtjMenuItem = new JMenuItem("Matrix Toolkit for Java  routines");
               mtjMenuItem.setToolTipText("Display information using reflection for the MTJ classes and methods");
               mtjMenuItem.setFont(GlobalValues.uifont);
               librariesMenu.add(jsciMenuItem);
               mtjMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfJSci = new gExec.gui.WatchClasses();

         Vector  mtjClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.mtjColtSGTFile, "no");

         int k=1;
         watchClassesOfJSci.displayClassesAndMethods( mtjClasses, "MTJ Library Routines", gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
        
                
                JMenuItem coltMenuItem = new JMenuItem("CERN Colt  routines");
                coltMenuItem.setToolTipText("Display information using reflection for the classes and methods of  the Colt Linear Algebra Library of CERN ");
                coltMenuItem.setFont(GlobalValues.uifont);
                librariesMenu.add(coltMenuItem);
                coltMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfNRNumAl = new gExec.gui.WatchClasses();

         Vector NRNumALClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.mtjColtSGTFile, "cern");

         int k=1;
         watchClassesOfNRNumAl.displayClassesAndMethods( NRNumALClasses, "Colt  Linear Algebra Library of CERN", gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                
                
                JMenuItem csparseMenuItem = new JMenuItem("CSparse library routines");
                csparseMenuItem.setToolTipText("Display information using reflection for the classes and methods of  the CSparse library  for sparse matrices");
                csparseMenuItem.setFont(GlobalValues.uifont);
                librariesMenu.add(csparseMenuItem);
                csparseMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfCSparse  = new gExec.gui.WatchClasses();

         String csparseFile = JavaGlobals.jarFilePath;
         
         Vector csparseClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(csparseFile, "edu");

         int k=1;
         watchClassesOfCSparse.displayClassesAndMethods( csparseClasses, "CSparse Library for sparse matrices", gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                
                JMenuItem jtransformsMenuItem = new JMenuItem("JTransforms  library routines");
                jtransformsMenuItem.setToolTipText("Display information using reflection for the classes and methods of  the Jtransforms  library ");
                jtransformsMenuItem.setFont(GlobalValues.uifont);
                librariesMenu.add(jtransformsMenuItem);
                jtransformsMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfJTransforms = new gExec.gui.WatchClasses();

         String jtransformsFile = JavaGlobals.mtjColtSGTFile;
         
         Vector jtransformsClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(jtransformsFile, "edu");

         int k=1;
         watchClassesOfJTransforms.displayClassesAndMethods( jtransformsClasses, "JTransforms Library", gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });

                
                
                // now prepare the "Search Keyword" menu items
                
                JMenuItem GroovySciRoutinesMenuItemWithKeyword = new JMenuItem("Search keyword in GroovySci Routines");
                GroovySciRoutinesMenuItemWithKeyword.setToolTipText("Display information for the GroovySci classes and methods having a keyword");
                GroovySciRoutinesMenuItemWithKeyword.setFont(GlobalValues.uifont);
                librariesMenu.add(GroovySciRoutinesMenuItemWithKeyword);
                
                GroovySciRoutinesMenuItemWithKeyword.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfGroovySci = new gExec.gui.WatchClasses();

         Vector groovySciClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.jarFilePath, "groovySci");

         int k=1;
         String filterString = groovySciCommands.BasicCommands.getString("Search for keyword"); 
         watchClassesOfGroovySci.displayClassesAndMethodsAsString(groovySciClasses, "groovySci ", filterString,  gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                

                JMenuItem GroovySciPlottingRoutinesMenuItemWithKeyword = new JMenuItem("Search keyword in GroovySci Plotting Routines");
                GroovySciPlottingRoutinesMenuItemWithKeyword.setToolTipText("Display information for the GroovySci Plotting classes and methods having a keyword");
                GroovySciPlottingRoutinesMenuItemWithKeyword.setFont(GlobalValues.uifont);
                librariesMenu.add(GroovySciPlottingRoutinesMenuItemWithKeyword);
                
                GroovySciPlottingRoutinesMenuItemWithKeyword.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfScalaSci = new gExec.gui.WatchClasses();

         Vector groovySciPlottingClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.jarFilePath, "groovySci/math/plot");

         int k=1;
         String filterString = groovySciCommands.BasicCommands.getString("Search for keyword"); 
         watchClassesOfScalaSci.displayClassesAndMethodsAsString(groovySciPlottingClasses,  "GroovySci Plot", filterString,  gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                JMenuItem EJMLRoutinesMenuItemWithKeyword = new JMenuItem("Search keyword in EJML");
                EJMLRoutinesMenuItemWithKeyword.setToolTipText("Display information for the EJML classes and methods having a keyword");
                EJMLRoutinesMenuItemWithKeyword.setFont(GlobalValues.uifont);
                librariesMenu.add(EJMLRoutinesMenuItemWithKeyword);
                
                EJMLRoutinesMenuItemWithKeyword.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfGroovySci = new gExec.gui.WatchClasses();

         Vector EJMLClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.ejmlFile, "org");

         int k=1;
         String filterString = groovySciCommands.BasicCommands.getString("Search for keyword"); 
         watchClassesOfGroovySci.displayClassesAndMethodsAsString(EJMLClasses, "EJML Classses and Methods relevant to keyword", filterString,  gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                

                
                JMenuItem ApacheCommonsRoutinesMenuItemWithKeyword = new JMenuItem("Search keyword in Apache Commons");
                ApacheCommonsRoutinesMenuItemWithKeyword.setToolTipText("Display information for the Apache Commons classes and methods having a keyword");
                ApacheCommonsRoutinesMenuItemWithKeyword.setFont(GlobalValues.uifont);
                librariesMenu.add(ApacheCommonsRoutinesMenuItemWithKeyword);
                
               ApacheCommonsRoutinesMenuItemWithKeyword.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfGroovySci = new gExec.gui.WatchClasses();

         Vector ApacheCommonsClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.ApacheCommonsFile, "org/apache/commons/math3");

         int k=1;
         String filterString = groovySciCommands.BasicCommands.getString("Search for keyword"); 
         watchClassesOfGroovySci.displayClassesAndMethodsAsString(ApacheCommonsClasses, "Apache Commons Math", filterString,  gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                
                JMenuItem JASRoutinesMenuItemWithKeyword = new JMenuItem("Search keyword in Java Algebra System (JAS)");
                JASRoutinesMenuItemWithKeyword.setToolTipText("Display information for the Java Algebra System (JAS) classes and methods having a keyword");
                JASRoutinesMenuItemWithKeyword.setFont(GlobalValues.uifont);
                librariesMenu.add(JASRoutinesMenuItemWithKeyword);
                
                JASRoutinesMenuItemWithKeyword.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfGroovySci = new gExec.gui.WatchClasses();

         Vector JASClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.JASFile, "edu/jas");

         int k=1;
         String filterString = groovySciCommands.BasicCommands.getString("Search for keyword"); 
         watchClassesOfGroovySci.displayClassesAndMethodsAsString(JASClasses, "Java Algebra System (JAS) ", filterString,  gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                
                
                JMenuItem NUMALRoutinesMenuItemWithKeyword = new JMenuItem("Search keyword in NUMAL");
                NUMALRoutinesMenuItemWithKeyword.setToolTipText("Display information for the NUMAL classes and methods having a keyword");
                NUMALRoutinesMenuItemWithKeyword.setFont(GlobalValues.uifont);
                librariesMenu.add(NUMALRoutinesMenuItemWithKeyword);
                
                NUMALRoutinesMenuItemWithKeyword.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfGroovySci = new gExec.gui.WatchClasses();

         Vector NUMALClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.numalFile, "numal");

         int k=1;
         String filterString = groovySciCommands.BasicCommands.getString("Search for keyword"); 
         watchClassesOfGroovySci.displayClassesAndMethodsAsString(NUMALClasses, "NUMAL ", filterString,  gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                

                JMenuItem JLAPACKRoutinesMenuItemWithKeyword = new JMenuItem("Search keyword in JLAPACK");
                JLAPACKRoutinesMenuItemWithKeyword.setToolTipText("Display information for the JLAPACK classes and methods having a keyword");
                JLAPACKRoutinesMenuItemWithKeyword.setFont(GlobalValues.uifont);
                librariesMenu.add(JLAPACKRoutinesMenuItemWithKeyword);
                
                JLAPACKRoutinesMenuItemWithKeyword.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfGroovySci = new gExec.gui.WatchClasses();

         Vector JLAPACKClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.LAPACKFile, "org");

         int k=1;
         String filterString = groovySciCommands.BasicCommands.getString("Search for keyword"); 
         watchClassesOfGroovySci.displayClassesAndMethodsAsString(JLAPACKClasses, "JLAPACK ", filterString,  gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
             
                
                JMenuItem jsciRoutinesMenuItemWithKeyword = new JMenuItem("Search keyword in JSci");
                jsciRoutinesMenuItemWithKeyword.setToolTipText("Display information for the JSci classes and methods having a keyword");
                jsciRoutinesMenuItemWithKeyword.setFont(GlobalValues.uifont);
                librariesMenu.add(jsciRoutinesMenuItemWithKeyword);
                
                jsciRoutinesMenuItemWithKeyword.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfGroovySci = new gExec.gui.WatchClasses();

         Vector jsciClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.jsciFile, "JSci");

         int k=1;
         String filterString = groovySciCommands.BasicCommands.getString("Search for keyword"); 
         watchClassesOfGroovySci.displayClassesAndMethodsAsString(jsciClasses, "JSci Classses and Methods relevant to keyword", filterString,  gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                JMenuItem nrRoutinesMenuItemWithKeyword = new JMenuItem("Search keyword in Numerical Recipes");
                nrRoutinesMenuItemWithKeyword.setToolTipText("Display information for the Numerical Recipes classes and methods having a keyword");
                nrRoutinesMenuItemWithKeyword.setFont(GlobalValues.uifont);
                librariesMenu.add(nrRoutinesMenuItemWithKeyword);
                
                nrRoutinesMenuItemWithKeyword.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfScalaSci = new gExec.gui.WatchClasses();

         Vector nrClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.numalFile, "com");

         int k=1;
         String filterString = groovySciCommands.BasicCommands.getString("Search for keyword"); 
         watchClassesOfScalaSci.displayClassesAndMethodsAsString(nrClasses, "Numerical Recipes ", filterString,  gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                JMenuItem mtjRoutinesMenuItemWithKeyword = new JMenuItem("Search keyword in MTJ");
                mtjRoutinesMenuItemWithKeyword.setToolTipText("Display information for the MTJ classes and methods having a keyword");
                mtjRoutinesMenuItemWithKeyword.setFont(GlobalValues.uifont);
                librariesMenu.add(EJMLRoutinesMenuItemWithKeyword);
                
                mtjRoutinesMenuItemWithKeyword.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfScalaSci = new gExec.gui.WatchClasses();

         Vector mtjClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.mtjColtSGTFile, "no");

         int k=1;
         String filterString = groovySciCommands.BasicCommands.getString("Search for keyword"); 
         watchClassesOfScalaSci.displayClassesAndMethodsAsString(mtjClasses, "EJML ", filterString,  gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                
                
                
                JMenuItem csparseRoutinesMenuItemWithKeyword = new JMenuItem("Search keyword in CSparse");
                csparseRoutinesMenuItemWithKeyword.setToolTipText("Display information for the CSparse classes and methods having a keyword");
                csparseRoutinesMenuItemWithKeyword.setFont(GlobalValues.uifont);
                librariesMenu.add(csparseRoutinesMenuItemWithKeyword);
                
                csparseRoutinesMenuItemWithKeyword.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfGroovySci = new gExec.gui.WatchClasses();

         String csparseFile = JavaGlobals.jarFilePath;
         Vector csparseClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(csparseFile, "edu");

         int k=1;
         String filterString = groovySciCommands.BasicCommands.getString("Search for keyword"); 
         watchClassesOfGroovySci.displayClassesAndMethodsAsString(csparseClasses, "CSparse ", filterString,  gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });

                
                                
                
                JMenuItem joregonDSPMenuItemWithKeyword = new JMenuItem("Seacrh Keyword in Oregon Digital Signal Processing library routines");
                joregonDSPMenuItemWithKeyword.setToolTipText("Display information using reflection for the classes and methods of  the Oregon DSP  library having a keyword");
                joregonDSPMenuItemWithKeyword.setFont(GlobalValues.uifont);
                librariesMenu.add(joregonDSPMenuItemWithKeyword);
                joregonDSPMenuItemWithKeyword.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfOregonDSP = new gExec.gui.WatchClasses();

         Vector oregonDSPClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.jarFilePath, "DSP");
         
         int k=1;
         String filterString = groovySciCommands.BasicCommands.getString("Search for keyword"); 
         watchClassesOfOregonDSP.displayClassesAndMethodsAsString( oregonDSPClasses, "com.oregondsp", filterString, gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });


                JMenuItem sgtRoutinesMenuItemWithKeyword = new JMenuItem("Search keyword in Scientific Graphics Toolbox (SGT)");
                sgtRoutinesMenuItemWithKeyword.setToolTipText("Display information for the Scientific Graphics Toolkit (SGT) classes and methods having a keyword");
                sgtRoutinesMenuItemWithKeyword.setFont(GlobalValues.uifont);
                librariesMenu.add(sgtRoutinesMenuItemWithKeyword);
                
                sgtRoutinesMenuItemWithKeyword.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfGroovySci = new gExec.gui.WatchClasses();

         Vector sgtClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.mtjColtSGTFile, "gov");

         int k=1;
         String filterString = groovySciCommands.BasicCommands.getString("Search for keyword"); 
         watchClassesOfGroovySci.displayClassesAndMethodsAsString(sgtClasses, "Scientifc Graphics Toolkit (SGT) ", filterString,  gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
                JMenuItem coltRoutinesMenuItemWithKeyword = new JMenuItem("Search keyword in CERN Colt");
                coltRoutinesMenuItemWithKeyword.setToolTipText("Display information for the CERN Colt classes and methods having a keyword");
                coltRoutinesMenuItemWithKeyword.setFont(GlobalValues.uifont);
                librariesMenu.add(coltRoutinesMenuItemWithKeyword);
                
                coltRoutinesMenuItemWithKeyword.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
        gExec.gui.WatchClasses  watchClassesOfGroovySci = new gExec.gui.WatchClasses();

         Vector coltClasses    =  gExec.ClassLoaders.JarClassLoader.scanLib(JavaGlobals.mtjColtSGTFile, "cern");

         int k=1;
         String filterString = groovySciCommands.BasicCommands.getString("Search for keyword"); 
         watchClassesOfGroovySci.displayClassesAndMethodsAsString(coltClasses, "CERN Colt ", filterString,  gExec.gui.WatchClasses.watchXLoc+k*50, gExec.gui.WatchClasses.watchYLoc+k*50);
             }
                    });
                
             
              
        JMenu appearanceMenu = new JMenu("Appearance");
        appearanceMenu.setFont(GlobalValues.uifont);
        appearanceMenu.setToolTipText("Appearance related settings (e.g. fonts, etc. )");
        
                confMenuGroovy = new JMenu("ConfigurationGroovy", true);
                confMenuGroovy.setFont(GlobalValues.uifont);
                confMenuGroovy.setToolTipText("configurations for the Groovy run time environment");
                
                confMenuOthers  = new JMenu("EnvironmentConfiguration", true);
                confMenuOthers.setFont(GlobalValues.uifont);
                confMenuOthers.setToolTipText("configurations concerning the GroovyLab environment");
                       
                 controlIndyJMenuItem = new JMenuItem("Toggle INDY - Current state is  "+GlobalValues.CompileIndy);
                 controlIndyJMenuItem.setFont(GlobalValues.uifont);
                 controlIndyJMenuItem.setToolTipText("Controls compile using Invoke dynamic (indy)");
                controlIndyJMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                     GlobalValues.GroovyShell = null;
                     GlobalValues.CompileIndy = !GlobalValues.CompileIndy;
                     GlobalValues.gLabMainFrame.setTitle(GlobalValues.TITLE+ " indy = "+GlobalValues.CompileIndy);
                     controlIndyJMenuItem.setText("Toggle INDY - Current state is  "+GlobalValues.CompileIndy);
                    }
                });
                
                
                 final JMenuItem controlDecimalsToDoublesJMenuItem = new JMenuItem("Transforms BigDecimals to Doubles - Current state is  "+GlobalValues.CompileDecimalsToDoubles);
                 controlDecimalsToDoublesJMenuItem.setFont(GlobalValues.uifont);
                 controlDecimalsToDoublesJMenuItem.setToolTipText("Controls transforming BigDecimals to doubles for efficiency");
                controlDecimalsToDoublesJMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                     GlobalValues.GroovyShell = null;
                     GlobalValues.CompileDecimalsToDoubles  = !GlobalValues.CompileDecimalsToDoubles;
                     GlobalValues.gLabMainFrame.setTitle(GlobalValues.TITLE+ " transform BigDecimals to doubles = "+GlobalValues.CompileDecimalsToDoubles);
                     controlDecimalsToDoublesJMenuItem.setText("Toggle transform BigDecimals to doubles- Current state is  "+GlobalValues.CompileDecimalsToDoubles);
                    }
                });
                
                JMenuItem  controlPrecisionJMenuItem = new JMenuItem("Control  the format of displayed numbers and truncation of large matrices");
                controlPrecisionJMenuItem.setToolTipText("Controls verbose on/off, the precision displayed for floating point numbers, truncation of large matrices/vectors");
                controlPrecisionJMenuItem.setFont(GlobalValues.uifont);
                controlPrecisionJMenuItem.addActionListener(new controlPrecisionAction());
                
                groovyLabServerPortJMenuItem = new JMenuItem("IP address on which GroovyLab server runs");
                groovyLabServerPortJMenuItem.setToolTipText("set the IP o which GroovyLab server runs");
                groovyLabServerPortJMenuItem.setFont(GlobalValues.uifont);
                groovyLabServerPortJMenuItem.addActionListener(new groovyLabServerAction());
                
                controlDefaultlmportsJMenuItem = new JMenuItem("Toggle useAlwaysDefaultImports - Current state is  "+GlobalValues.useAlwaysDefaultImports);
                 controlDefaultlmportsJMenuItem.setFont(GlobalValues.uifont);
                 controlDefaultlmportsJMenuItem.setToolTipText("Controls importing the default imports even when the staments have their own imports");
                controlDefaultlmportsJMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                     GlobalValues.useAlwaysDefaultImports = !GlobalValues.useAlwaysDefaultImports;
                     GlobalValues.GroovyShell = null;  // force the creation of a new GroovyShell 
                     controlDefaultlmportsJMenuItem.setText("Toggle useAlwaysDefaultImports - Current state is  "+GlobalValues.useAlwaysDefaultImports);
                    }
                });
                
                htmlWithSystemBrowser = new JMenuItem("Use System Browser for HTML help, Current State is "+GlobalValues.useSystemBrowserForHelp);
                htmlWithSystemBrowser.setFont(GlobalValues.uifont);
                htmlWithSystemBrowser.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
              GlobalValues.useSystemBrowserForHelp =  !GlobalValues.useSystemBrowserForHelp;
              htmlWithSystemBrowser.setText("Use System Browser for HTML help, Current State is "+GlobalValues.useSystemBrowserForHelp); 
                    }
                });
            
                resetGroovyShellLoadedClassesJMenuItem = new JMenuItem("Reset Loaded Clases of Groovy Shell");
                resetGroovyShellLoadedClassesJMenuItem.setFont(GlobalValues.uifont);
                resetGroovyShellLoadedClassesJMenuItem.setToolTipText("Clears only loaded classes, variable bindings remain");
                resetGroovyShellLoadedClassesJMenuItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                    GlobalValues.GroovyShell.resetLoadedClasses();
                    }
                });
                
                paneAdjustFontJMenuItem = new JMenuItem("Adjust Fonts of the GroovyLab edtor"); // configIcon);
                paneAdjustFontJMenuItem.addActionListener(new paneFontAdjusterAction());
                paneAdjustFontJMenuItem.setFont(GlobalValues.uifont);
                
                   
               JMenuItem increaseRSyntaxFontMenuItem = new JMenuItem("Increase the font size of the rsyntaxarea editor");
               increaseRSyntaxFontMenuItem.setToolTipText("Increases the font size of the rsyntaxarea editor");
               increaseRSyntaxFontMenuItem.setFont(GlobalValues.guifont);
               increaseRSyntaxFontMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {  // increase the font size of the rsyntaxarea editor
                      Font currentFont = GlobalValues.globalEditorPane.getFont();
                      Font newFont = new Font(currentFont.getFontName(), currentFont.getStyle(), currentFont.getSize()+1);
                      GlobalValues.globalEditorPane.setFont(newFont);
                    }
                });
               
               
               JMenuItem decreaseRSyntaxFontMenuItem = new JMenuItem("Decrease the font size of the rsyntaxarea editor");
               decreaseRSyntaxFontMenuItem.setToolTipText("Decreases the font size of the rsyntaxarea editor");
               decreaseRSyntaxFontMenuItem.setFont(GlobalValues.guifont);
               decreaseRSyntaxFontMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {  // increase the font size of the rsyntaxarea editor
                      Font currentFont = GlobalValues.globalEditorPane.getFont();
                      Font newFont = new Font(currentFont.getFontName(), currentFont.getStyle(), currentFont.getSize()-1);
                      GlobalValues.globalEditorPane.setFont(newFont);
                    }
                });
               
                adjustFontMenuItem = new JMenuItem("Adjust Fonts of GroovyLab Console"); // configIcon);
                adjustFontMenuItem.addActionListener(new FontAdjusterAction());
                adjustFontMenuItem.setFont(GlobalValues.uifont);
                
                adjustFontsUIMenuItem  = new JMenuItem("Adjust Fonts of GroovyLab main menus and help menus (changes apply after restart)"); // configIcon);
                adjustFontsUIMenuItem.addActionListener(new UIFontAdjusterAction());
                adjustFontsUIMenuItem.setFont(GlobalValues.uifont);
                
                adjustFontspUIMenuItem  = new JMenuItem("Adjust Fonts of GroovyLab popup menus (changes apply after restart)"); // configIcon);
                adjustFontspUIMenuItem.addActionListener(new pUIFontAdjusterAction());
                adjustFontspUIMenuItem.setFont(GlobalValues.uifont);
              
                adjustFontsgUIMenuItem  = new JMenuItem("Adjust Fonts of GroovyLab GUI components, e.g. JTrees, JLists (changes apply after restart)"); // configIcon);
                adjustFontsgUIMenuItem.addActionListener(new gUIFontAdjusterAction());
                adjustFontsgUIMenuItem.setFont(GlobalValues.uifont);
              
                adjustHtmlFontsJMenuItem  = new JMenuItem("Adjust JEditorPane based HTML Help Pages Fonts "); // configIcon);
                adjustHtmlFontsJMenuItem.addActionListener(new htmlFontAdjusterAction());
                adjustHtmlFontsJMenuItem.setFont(GlobalValues.uifont);
              
                adjustsFontsbUIMenuItem  = new JMenuItem("Adjust Fonts for buttons/labels (changes apply after restart)"); // configIcon);
                adjustsFontsbUIMenuItem.addActionListener(new bUIFontAdjusterAction());
                adjustsFontsbUIMenuItem.setFont(GlobalValues.uifont);
              
                
                outConsoleAdjustFontMenuItem  = new JMenuItem("Adjust Fonts of output Console"); // configIcon);
                outConsoleAdjustFontMenuItem.addActionListener(new outConsFontAdjusterAction());
                outConsoleAdjustFontMenuItem.setFont(GlobalValues.uifont);
                
                controlMainToolbarJMenuItem = new JMenuItem(new controlMainToolBarAction());
                controlMainToolbarJMenuItem.setFont(GlobalValues.uifont);
                controlMainToolbarJMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl T"));
              
                resetGroovyShellJMenuItem = new JMenuItem("Reset Groovy Shell context");
                resetGroovyShellJMenuItem.setFont(GlobalValues.uifont);
                resetGroovyShellLoadedClassesJMenuItem.setToolTipText("Clears both variable bindings and loaded classes");
                resetGroovyShellJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
          GlobalValues.GroovyShell.resetLoadedClasses();
          GlobalValues.GroovyShell.getContext().getVariables().clear();
            }
        });
                adjustLookAndFeelMenuItem = new JMenuItem("Configure Look and Feel  (changes to most UI items apply after restart)");
                adjustLookAndFeelMenuItem.setFont(GlobalValues.uifont);
                adjustLookAndFeelMenuItem.addActionListener(new LookAndFeelAdjusterAction());
                
                browseFileSysForPathsJMenuItem = new JMenuItem("Browse File system For Paths");
                browseFileSysForPathsJMenuItem.setFont(GlobalValues.uifont);
                browseFileSysForPathsJMenuItem.addActionListener(new browseFileSysForPaths());
                
                browseGroovySciJMenuItem = new JMenuItem(new browseGroovySciFilesAction());
                browseGroovySciJMenuItem.setFont(GlobalValues.uifont);
                
                browseClassesJMenuItem = new JMenuItem(new browseJavaClassesAction());
                browseClassesJMenuItem.setFont(GlobalValues.uifont);
                browseClassesJMenuItem.setIcon(new ImageIcon(sunJavaImage));
                
                promptJMenuItem = new JMenuItem(new promptConfigAction());
                promptJMenuItem.setFont(GlobalValues.uifont);
              //  configAlphaParamJMenuItem = new JMenuItem(new configAlphaAction());
               // configAlphaParamJMenuItem.setFont(GlobalValues.uifont);
                
               
                confMenuGroovy.add(controlIndyJMenuItem);
                confMenuGroovy.add(controlDecimalsToDoublesJMenuItem);
                confMenuGroovy.add(controlDefaultlmportsJMenuItem);
                confMenuGroovy.add(resetGroovyShellJMenuItem);
                confMenuGroovy.add(resetGroovyShellLoadedClassesJMenuItem);
                
               confMenuOthers.add(groovyLabServerPortJMenuItem);
                confMenuOthers.add(controlPrecisionJMenuItem);
                confMenuOthers.add(browseFileSysForPathsJMenuItem);
                confMenuOthers.add(browseGroovySciJMenuItem);
                confMenuOthers.add(htmlWithSystemBrowser);
                confMenuOthers.add(promptJMenuItem);
                //confMenu.add(configAlphaParamJMenuItem);
                
            
                appearanceMenu.add(paneAdjustFontJMenuItem);
                appearanceMenu.add(increaseRSyntaxFontMenuItem);
                appearanceMenu.add(decreaseRSyntaxFontMenuItem);
                appearanceMenu.add(adjustFontMenuItem);
                appearanceMenu.add(outConsoleAdjustFontMenuItem);
                appearanceMenu.add(adjustFontsUIMenuItem);
                appearanceMenu.add(adjustFontspUIMenuItem);
                appearanceMenu.add(adjustFontsgUIMenuItem);
                appearanceMenu.add(adjustsFontsbUIMenuItem);
                
                appearanceMenu.add(adjustFontMenuItem);
                appearanceMenu.add(controlMainToolbarJMenuItem);
                appearanceMenu.add(adjustLookAndFeelMenuItem);
                
                
                symbolicAlgebraJMenu = new JMenu("ComputerAlgebra");
                symbolicAlgebraJMenu.setFont(GlobalValues.uifont);
                symbolicAlgebraJMenu.setToolTipText("A bridge to Computer Algebra functionality in GroovyLab");
                
                startsymJavaJMenuItem = new JMenuItem("Main symja interface");
                startsymJavaJMenuItem.setFont(GlobalValues.uifont);
                startsymJavaJMenuItem.setToolTipText("Start the main symja GUI window");
                startsymJavaJMenuItem.addActionListener(new ActionListener() {
                        // start the main symja GUI window
                    public void actionPerformed(ActionEvent e) {
                      String []  nullArgs = new String[1];
                      nullArgs[0]= "";
                      org.matheclipse.symja.Main.main(nullArgs);
                     }
                });
                symbolicAlgebraJMenu.add(startsymJavaJMenuItem);
                
                
                wizardsMenu = new JMenu("Wizards");
                wizardsMenu.setFont(GlobalValues.uifont);
                wizardsMenu.setToolTipText("The Wizards menu allows to build almost automatically the code for categories of applications, e.g. ODE solvers");
                
                wizardsGroovySciJMenuItem = new JMenuItem(new ODEWizardGroovySciAction());
                wizardsGroovySciJMenuItem.setFont(GlobalValues.uifont);
                wizardsGroovySciJMenuItem.setToolTipText("Builds a GroovySci ODE Solver with Java based implementation of the equations");
                wizardsGroovySciJMenuItem.setIcon(new ImageIcon(ODEWizardImage));
                wizardsMenu.add(wizardsGroovySciJMenuItem);
 
                wizardsGroovySciGroovyJMenuItem = new JMenuItem(new ODEWizardGroovySciGroovyAction());
                wizardsGroovySciGroovyJMenuItem.setFont(GlobalValues.uifont);
                wizardsGroovySciGroovyJMenuItem.setToolTipText("Builds a GroovySci ODE Solver with Groovy based implementation of the equations ");
                wizardsGroovySciGroovyJMenuItem.setIcon(new ImageIcon(ODEWizardImage));
                wizardsMenu.add(wizardsGroovySciGroovyJMenuItem);
 
 
                wizardsGroovySciGroovyTypedJMenuItem = new JMenuItem(new ODEWizardGroovySciGroovyActionTypedCode());
                wizardsGroovySciGroovyTypedJMenuItem.setFont(GlobalValues.uifont);
                wizardsGroovySciGroovyTypedJMenuItem.setToolTipText("Builds a GroovySci ODE Solver with Groovy based implementation of the equations, using CompileStatic annotation ");
                wizardsGroovySciGroovyTypedJMenuItem.setIcon(new ImageIcon(ODEWizardImage));
                wizardsMenu.add(wizardsGroovySciGroovyTypedJMenuItem);
                
                
                
        exitJMenuItem.addActionListener(listener);
        exitJMenuItem.setFont(GlobalValues.uifont);
                
        examplesMenu = new JMenu("Demos  ", true);
        examplesMenu.setFont(GlobalValues.uifont);
        examplesMenu.setToolTipText("Examples and Demos for GroovySci");
        
        GroovySciExamplesHelpJMenuItem = new JMenuItem("GroovySci Examples ");
        GroovySciExamplesHelpJMenuItem.setFont(GlobalValues.uifont);
        GroovySciExamplesHelpJMenuItem.addActionListener(new GroovySciExamplesAction());
        GroovySciExamplesHelpJMenuItem.setToolTipText("Provides examples of GroovySci scripts. To execute Copy and Paste in editor");
        
        GroovySciPlotsExamplesHelpJTreeJMenuItem  = new JMenuItem("GroovySci Plotting Examples with JTree format ");
        GroovySciPlotsExamplesHelpJTreeJMenuItem.setFont(GlobalValues.uifont);
        GroovySciPlotsExamplesHelpJTreeJMenuItem.addActionListener(new groovySciPlotExamplesJTreeAction());
        GroovySciPlotsExamplesHelpJTreeJMenuItem.setToolTipText("Provides examples of GroovySci plotting scripts with a convenient JTree displaying. To execute Copy and Paste in editor");
        
        GroovySciExamplesHelpJTreeJMenuItem  = new JMenuItem("GroovySci Examples with JTree format ");
        GroovySciExamplesHelpJTreeJMenuItem.setFont(GlobalValues.uifont);
        GroovySciExamplesHelpJTreeJMenuItem.addActionListener(new groovySciExamplesJTreeAction());
        GroovySciExamplesHelpJTreeJMenuItem.setToolTipText("Provides examples of GroovySci scripts with a convenient JTree displaying. To execute Copy and Paste in editor");
        
//GroovySciExamplesHelpJMenuItem.setIcon(new ImageIcon(groovyImage));
       
        GroovySciPlotsExamplesJMenuItem  = new JMenuItem("GroovySci Plot Examples ");
        GroovySciPlotsExamplesJMenuItem.setFont(GlobalValues.uifont);
        GroovySciPlotsExamplesJMenuItem.addActionListener(new GroovySciPlotsExamplesAction());
        GroovySciPlotsExamplesJMenuItem.setToolTipText("Provides examples of GroovySci Plot scripts. To execute Copy and Paste in editor");
        
        JMenuItem JavaSGTExamplesPlotHelpJMenuItem = new JMenuItem("Scientific Graphics: SGT Plotting Examples, in Java");
        JavaSGTExamplesPlotHelpJMenuItem.setToolTipText("Java examples for the Scientific Graphics Toolkit plotting system. Use F9 from jsyntaxpane to run them");
        JavaSGTExamplesPlotHelpJMenuItem.addActionListener(new JavaSGTExamplesPlottingAction());
        JavaSGTExamplesPlotHelpJMenuItem.setFont(GlobalValues.uifont);
        
        JMenuItem GroovySciWaveletExamplesHelpJMenuItem = new JMenuItem("GroovySci Wavelet Examples ");
        GroovySciWaveletExamplesHelpJMenuItem.setFont(GlobalValues.uifont);
        GroovySciWaveletExamplesHelpJMenuItem.addActionListener(new GroovySciWaveletExamplesAction());
        GroovySciWaveletExamplesHelpJMenuItem.setToolTipText("Provides examples of GroovySci scripts for Wavelets. To execute Copy and Paste in editor");
        
        
        JMenuItem GroovySciWEKAExamplesHelpJMenuItem = new JMenuItem("GroovySci WEKA Examples ");
        GroovySciWEKAExamplesHelpJMenuItem.setFont(GlobalValues.uifont);
        GroovySciWEKAExamplesHelpJMenuItem.addActionListener(new GroovySciWEKAExamplesAction());
        GroovySciWEKAExamplesHelpJMenuItem.setToolTipText("Provides examples of GroovySci scripts for WEKA Machine Learning framework. To execute Copy and Paste in editor");
        
        
        examplesMenu.add(GroovySciExamplesHelpJMenuItem);
        examplesMenu.add(GroovySciPlotsExamplesJMenuItem);
        examplesMenu.add(GroovySciExamplesHelpJTreeJMenuItem);
        examplesMenu.add(GroovySciPlotsExamplesHelpJTreeJMenuItem);
        examplesMenu.add(GroovySciWEKAExamplesHelpJMenuItem);
        examplesMenu.add(JavaSGTExamplesPlotHelpJMenuItem);
        examplesMenu.add(GroovySciWaveletExamplesHelpJMenuItem);
                
             
             
                
             
                toolboxesHelpJMenuItem = new JMenuItem("Toolboxes (?)");
                toolboxesHelpJMenuItem.setFont(GlobalValues.uifont);
                toolboxesHelpJMenuItem.addActionListener( 
                        new java.awt.event.ActionListener() {
                   public void actionPerformed(ActionEvent e) { 
                new helpToolboxes().setVisible(true);
               }
             });
             

                

                symbolicAlgebraJMenu.add(startsymJavaJMenuItem);
         
                helpMenu = new JMenu("Help");
                helpMenu.setFont(GlobalValues.uifont);
                
                JMenuItem JavaHelpJMenuItem = new JMenuItem("Java API  JavaHelp");
             JavaHelpJMenuItem.setToolTipText("Extensive help for the Java 6 SDK API platform");
             JavaHelpJMenuItem.setFont(GlobalValues.uifont);
             JavaHelpJMenuItem.addActionListener(new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if  (GlobalValues.JavaCSHObject==null) {
                            GlobalValues.JavaCSHObject = new gExec.gLab.CSHObject();
                            GlobalValues.JavaCSHObject.setHelpSystem(GlobalValues.JavaHelpSetName);   // set the help system to Java JDK help
                            GlobalValues.JavaCSHObject.setConsoleHelp("top");
                            GlobalValues.JavaCSHObject.displayTheHelp();                    
                     }
                        else
            GlobalValues.JavaCSHObject.displayTheHelp();                    }
                }); 

             JMenuItem GroovyHelpJMenuItem = new JMenuItem("Groovy API  JavaHelp");
             GroovyHelpJMenuItem.setToolTipText("Extensive help for the Groovy Language");
             GroovyHelpJMenuItem.setFont(GlobalValues.uifont);
             GroovyHelpJMenuItem.addActionListener(new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if  (GlobalValues.GroovyCSHObject==null) {
                            GlobalValues.GroovyCSHObject = new gExec.gLab.CSHObject();
                            GlobalValues.GroovyCSHObject.setHelpSystem(GlobalValues.GroovyHelpSetName);   // set the help system to Scala help
                            GlobalValues.GroovyCSHObject.setConsoleHelp("top");
                            GlobalValues.GroovyCSHObject.displayTheHelp();
                     }
                        else
            GlobalValues.GroovyCSHObject.displayTheHelp();                    }
                });

             
                
                JMenuItem aboutHelpJMenuItem = new JMenuItem("About...");
                aboutHelpJMenuItem.setFont(GlobalValues.uifont);
                aboutHelpJMenuItem.setFont(GlobalValues.uifont);
                
                aboutHelpJMenuItem.addActionListener( 
          new java.awt.event.ActionListener() {
     public void actionPerformed(java.awt.event.ActionEvent evt) {
                        JPanel  aboutJLab = new AboutGroovyLab();
    	 		EffectsDialog   aboutDialog = new EffectsDialog(GlobalValues.gLabMainFrame, aboutJLab, "About Glab", 1 );
                        installInLayeredPane(aboutDialog);
                        aboutDialog.setLocation(50, 50);
                        aboutDialog.setVisible(true);
                            }
 });
               
             helpMenu.add(JavaHelpJMenuItem);
             helpMenu.add(GroovyHelpJMenuItem);
             helpMenu.add(aboutHelpJMenuItem);
              
                
                mainJMenuBar.add(FileMenu);
                mainJMenuBar.add(editMenu);
                mainJMenuBar.add(symbolicAlgebraJMenu);
                mainJMenuBar.add(librariesMenu);
                mainJMenuBar.add(confMenuGroovy);
                mainJMenuBar.add(confMenuOthers);
                mainJMenuBar.add(appearanceMenu);
                mainJMenuBar.add(wizardsMenu);
                mainJMenuBar.add(examplesMenu);
                mainJMenuBar.add(helpMenu);
                        
                mainJMenuBar.setOpaque(true);
                myId.setJMenuBar(mainJMenuBar);
               

        }

    private void installInLayeredPane(JComponent component) {
        JLayeredPane layeredPane = getRootPane().getLayeredPane();
        layeredPane.add(component, JLayeredPane.PALETTE_LAYER, 20);
        Dimension size = component.getPreferredSize();
        component.setSize(size);
        component.setLocation((getWidth() - size.width) / 2,
                (getHeight() - size.height) / 2);
        component.revalidate();
        component.setVisible(true);
    }


             /**Interpret the last command line entered*/
    public void interpretLine(String line)
    {
                  String answerString="";
                  answerString= interpreter.execWithGroovyShell(line);

                  GlobalValues.userConsole.displayText(answerString);

                  updateHistoryWindow();
                  gExec.gui.WatchWorkspace.displayGroovySciBinding(gExec.gLab.gLab.variablesWorkSpacePanel);
               
            gExec.gLab.gLab.outerPane.revalidate();
                        
    }

    
    
     public static void detectPaths(String watchStr) {
         if (File.pathSeparatorChar==';')  {  // handle Windows file system naming
               int idxOfColon = watchStr.lastIndexOf(':'); 
               watchStr = watchStr.substring(idxOfColon-1, watchStr.length());
            }
            int sepIndex = watchStr.indexOf('!');
                
            String fullJarPath = watchStr.substring(0, sepIndex);
     //       System.out.println("watch string = "+watchStr);
                
        //  test if GroovyLab is installed in a directory containing special charactes, e..g. spaces, symbols, etc)
                //  and quit if so, displaying an appropriate message to the user
                boolean specialCharsInPath = false;
                int pathLen = fullJarPath.length();
                for (int k=0; k<pathLen; k++) {
                    char ch = fullJarPath.charAt(k);
                    if (ch != File.separatorChar && ch != '/'  && ch !=':' && ch!='.' && ch != '-' && ch != '_')
                      if ( Character.isLetterOrDigit(ch)==false ) {
                         specialCharsInPath = true;
                         break;
                  }
                }
                if (specialCharsInPath==true) {
                    JOptionPane.showMessageDialog(null, "Path where GroovyLab is installed: "+fullJarPath+" , contains special characters. Please install GroovyLab in a path name with no special chars and no spaces.", "Please install GroovyLab in a simple path name", JOptionPane.INFORMATION_MESSAGE);
                   System.exit(1);
                }
                
                //System.out.println("fullJarPath = "+fullJarPath);
                String jLabJarName = fullJarPath.substring(fullJarPath.lastIndexOf(File.separatorChar)+1, fullJarPath.length() );
                if (jLabJarName.indexOf(File.separator)!=-1)
                    jLabJarName = jLabJarName.replaceAll(File.separator, "/");
                if (jLabJarName.contains("file:"))
                 jLabJarName = jLabJarName.replaceAll("file:", "");
                
                GlobalValues.jarFilePath = jLabJarName;
                GlobalValues.fullJarFilePath = fullJarPath;
                
                String gLabLibPath = GlobalValues.jarFilePath;
                String gLabHelpPath = GlobalValues.jarFilePath;

//                System.out.println("GLABLIBPATH = "+gLabLibPath);
                 
                // remove jar file name from the path name
                 int dotIndex = gLabLibPath.indexOf(".");
                 int lastPos = dotIndex;
                 while (gLabLibPath.charAt(lastPos)!='/' && gLabLibPath.charAt(lastPos)!='\\'  && lastPos>0)
                             lastPos--;
                 gLabLibPath = gLabLibPath.substring(0, lastPos);
                     
                // remove jar file name from the path name
                 dotIndex = gLabHelpPath.indexOf(".");
                 lastPos = dotIndex;
                 while (gLabHelpPath.charAt(lastPos)!='/' && gLabHelpPath.charAt(lastPos)!='\\'  && lastPos>0)
                             lastPos--;
                  gLabHelpPath = gLabHelpPath.substring(0, lastPos);
                 
                
                 if (gLabLibPath.length()>0) {
                    gLabLibPath = gLabLibPath+File.separator+"lib"+File.separator;
                    gLabHelpPath = gLabHelpPath+File.separator+"help"+File.separator;
                 }
                 else  {
                  gLabLibPath = "lib"+File.separator;
                  gLabHelpPath = "help"+File.separator;
                 }
                     
                 GlobalValues.gLabLibPath = gLabLibPath;
                 GlobalValues.gLabHelpPath = gLabHelpPath;
                 
                 System.out.println("GlobalValues.gLabLibPath = "+GlobalValues.gLabLibPath);
                 System.out.println("GlobalValues.gLabHelpPath = "+GlobalValues.gLabHelpPath);
            
     }
     

      
      public static void main(String[] args)
        {
          int argc;
          if (args!=null)
                argc = args.length;
          else argc = 0;

          if (argc > 0)
              netbeansjLabArg = args[0];
      
            String vers = System.getProperty("java.version");
        if (vers.compareTo("1.5") < 0) {
            System.out.println("!!!GroovyLab: Swing must be run with a 1.5 or higher version VM!!!");
            System.exit(1);
        }
       
            String currentWorkingDirectory = System.getenv("PWD");
            if (currentWorkingDirectory==null)
                currentWorkingDirectory = "c:\\"; 
           String userDir = System.getProperty("user.dir");
             
             if (argc > 0)  {
                 GlobalValues.jarFilePath = args[0];
                 GlobalValues.fullJarFilePath = args[0];
             }
            if (argc>=2) {
                GlobalValues.gLabClassPath = args[1];
            }
            
          
            if (File.separatorChar=='/')  { // detect OS type
        	GlobalValues.hostIsUnix = true;
             }
                        else {   // Windows host
                GlobalValues.hostIsUnix = false;
                if (currentWorkingDirectory == null)   // e.g. for Windows the current working directory is undefined
                currentWorkingDirectory = "C:\\";
            }
            if (GlobalValues.workingDir==null)
                         GlobalValues.workingDir = currentWorkingDirectory;
     
            
            final gLab myGui = new gLab(GlobalValues.gLabClassPath);
             
            gLab.uiMainFrame = myGui;
            
           
            
     SwingUtilities.invokeLater(new Runnable() {
           public void run() {  // run in  */
                
              myGui.setVisible(true);
             (( gExec.gui.gLabConsole)GlobalValues.userConsole).displayPrompt() ;   
             GlobalValues.globalEditorPane.setCaretPosition(GlobalValues.globalEditorPane.getText().length());
             GlobalValues.globalEditorPane.setVisible(true);
             
            replThread = new replClass();
          //  replThread.run();
                
                
        
           }
     });
     
     
     //gExec.gLab.fxgui  fxgui = new gExec.gLab.fxgui();
      //fxgui.main(null);
                
     
   }


  


    public void windowActivated(WindowEvent e){}
    
    public void windowDeactivated(WindowEvent e){}
    
    public void windowClosed(WindowEvent e){  closeGUI(); }
   
    public void windowIconified(WindowEvent e){}

    public void windowDeiconified(WindowEvent e){}

    public void windowOpened(WindowEvent e){}



        public void windowClosing(WindowEvent e)
        {
                closeGUI();
        }

   
   
        

                
   }
   
    
