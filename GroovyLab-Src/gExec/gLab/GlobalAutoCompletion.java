package gExec.gLab;

import gExec.gui.AutoCompletionGroovySci;
import gExec.gui.AutoCompletionLoader;


public   class GlobalAutoCompletion  {

         
                    public static void initAutoCompletion () {
                        AutoCompletionGroovySci.scanMethodsGroovySci.removeAllElements();
                        
            AutoCompletionLoader  autoCompletionGroovySciLoader  = new AutoCompletionLoader();
            autoCompletionGroovySciLoader.getAutoCompletionInfo();
            
            
                    }
                }
   
               