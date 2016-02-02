
package org.codehaus.groovy.ant;

import org.codehaus.groovy.ant.Groovyc;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

import java.io.File;

public class GroovycExt  extends Groovyc {
    

    public static void groovyCompile(String baseDir, String  groovyFileName)
    {
        Project pr = new Project();
        pr.setBasedir(baseDir);
        Path pth = new Path(pr);
        Groovyc gc = new Groovyc();
        gc.recreateSrc();
        gc.setSourcepath(pth);
        gc.setDestdir(new File(baseDir));
        File fl = new File(groovyFileName);
        File [] filelist = new File[1];
        filelist[0] = fl;
        gc.addToCompileList(filelist);
        gc.compile();
        
       
    }

    

}
