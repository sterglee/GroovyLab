package expandRunTime;

import javax.tools.*;
import java.net.URI;
import java.io.*;

public class InMemoryJavaFileObject extends SimpleJavaFileObject {
   private String contents = null;

   public InMemoryJavaFileObject(String className, String contents) throws Exception {
       super(URI.create("string:///" + className.replace('.', '/')
                        + Kind.SOURCE.extension), Kind.SOURCE);
       this.contents = contents;
   }

   public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
       return contents;
   }
}

