
package gExec.gui;

import gExec.Interpreter.GlobalValues;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import gExec.gLab.EditorPaneHTMLHelp;


public class VecOperationsToolbar  extends JPanel {
        JButton bconstr, bcolon, blinspace, blogspace;
        JButton bones, bzeros, bfill, brand;
        JButton bsum, bmean, bmin, bmax;
        JButton bsin, bcos, btan, basin, bacos, batan,  bsinh, bcosh, btanh;
        JButton bsqrt, blog, bexp, babs, bfloor, bceil;
        JButton bhelp;
        
    public VecOperationsToolbar() {
        JPanel vectorPanel1 = new JPanel();
        JPanel vectorPanel2 = new JPanel();
        JPanel vectorPanel3 = new JPanel();
        JPanel vectorPanel4 = new JPanel();
         
        setLayout(new GridLayout(5,1));

        bhelp = new JButton("Help");
        bhelp.setToolTipText("HTML Help on Vector operations");
        bhelp.addActionListener(new ActionListener() {
  @Override
            public void actionPerformed(ActionEvent e) {
       EditorPaneHTMLHelp  inPlaceHelpPane = new EditorPaneHTMLHelp("Vec.html");
        if (GlobalValues.useSystemBrowserForHelp==false) {
  inPlaceHelpPane.setSize(GlobalValues.figFrameSizeX, GlobalValues.figFrameSizeY);
  inPlaceHelpPane.setLocation(GlobalValues.sizeX/4, GlobalValues.sizeY/4);
  inPlaceHelpPane.setVisible(true);
        }
  }
        });
          
        
        bconstr = new JButton(" new Vec(...)");
        bconstr.setToolTipText("specify a Vector from its elements");
        
         
        bcolon = new JButton("vinc");
        bcolon.setToolTipText("implements colon operator, e.g. var  t = vinc(0, 0.01, 10) is as t = 0:0.01:10");
        

        blinspace = new JButton("vlinspace");
        blinspace.setToolTipText("Linearly spaced vector, vlinspace(x1, x2, N) generates N points between x1 and x2");
        
        blogspace = new JButton("vlogspace");
        blogspace.setToolTipText("Logarithmically spaced vector, vlogspace(x1, x2, N, base) generates N points between x1 and x2, with base logarithm");
        
        
        bones = new JButton("vones");
        bones.setToolTipText("creating matrices that consist of ones    (e.g.: vones(20) will return an 20 element vector of ones)");
        
        bzeros = new JButton("vzeros");
        bzeros.setToolTipText("creating vectors that consist of zeros    (e.g.: vzeros(23) will return an  23 element vector)");
        

        bfill = new JButton("vfill");
        bfill.setToolTipText("creating vectors that consist of a given value (e.g.: vfill(10, 2.4) will return a  10 element vector with values 2.4)");
        
        brand = new JButton("vrand");
        brand.setToolTipText("construct a matrix filled with pseudorandom values(e.g.: vrand(20)  will return a vector with  20 pseudorandom values ");
                
        
        
        bsum = new JButton("sum");
        bsum.setToolTipText( "the sum of all values of the Vector");
        
        
        bmean = new JButton("mean");
        bmean.setToolTipText( "the mean of all values of the Vector");
        
        bmin = new JButton("min");
        bmin.setToolTipText( "the minimum of all values of the Vector");
        
        bmax = new JButton("max");
        bmax.setToolTipText( "the maximum of all values of the Vector");
        
        bsin = new JButton("sin");
        bsin.setToolTipText("Vec sin( Vec x). Computes the sine of x");
        
        bcos= new JButton("cos");
        bcos.setToolTipText("Vec  cos( Vec x) ;   Computes the cosine of x");
        
        btan= new JButton("tan");
        btan.setToolTipText("Vec  tan(Vec  x)  \n Computes the tangent of x");
        
        bsinh = new JButton("sinh");
        bsin.setToolTipText("Vec sinh(Vec  x) ; \n Computes hyperbolic sine of x");
        
        bcosh= new JButton("cosh");
        bcosh.setToolTipText("Vec cosh(Vec  x); \n Computes the hyperbolic cosine of x");
        
        btanh= new JButton("tanh");
        btanh.setToolTipText("Vec  tanh(Vec  x); \n Computes the hyperbolic tangent of x");
        
        basin = new JButton("asin");
        basin.setToolTipText("Vec asin( Vec x) \n Computes the arc sine of x");
        
        bacos= new JButton("acos");
        bacos.setToolTipText("Vec acos( Vec x); \n Computes the arc cosine of x");
        
        batan= new JButton("atan");
        batan.setToolTipText("Vec atan(Vec  x); \n Computes the arc tangent of x");
        
        babs = new JButton("abs");
        babs.setToolTipText("Vec abs(Vec  x); \n Computes the absolute value of x");
        
        bsqrt = new JButton("sqrt");
        bsqrt.setToolTipText("Vec sqrt(Vec  x) \n Computes the square root of x");
        
        bfloor = new JButton("floor");
        bfloor.setToolTipText("Vec floor(Vec x); \n Computes the nearest integer smaller than x");

        bceil = new JButton("ceil");
        bceil.setToolTipText("Vec ceil(Vec  x) \n Computes the nearest integer larger than x");
        
        blog= new JButton("log");
        blog.setToolTipText("Vec log(Vec  x):    Computes the natural logarithm ");
        
        bexp= new JButton("exp");
        bexp.setToolTipText("Vec exp( Vec x); Computes the e^x");
        
        
        
        vectorPanel1.add(bcolon);   vectorPanel1.add(bhelp);         vectorPanel1.add(bconstr);        
        vectorPanel1.add(blinspace);  vectorPanel1.add(blogspace);
        
        vectorPanel2.add(bones);  vectorPanel2.add(bzeros);  vectorPanel2.add(bfill); vectorPanel2.add(brand);
        vectorPanel2.add(bsum); 
        
        vectorPanel3.add(babs); vectorPanel3.add(bmean);
        vectorPanel3.add(bmin);  vectorPanel3.add(bmax);
        vectorPanel3.add(bceil); vectorPanel3.add(bfloor); vectorPanel3.add(bsqrt);
        
        vectorPanel4.add(blog); vectorPanel4.add(bexp);
        vectorPanel4.add(bsin); vectorPanel4.add(bcos); vectorPanel4.add(btan);  vectorPanel4.add(basin); vectorPanel4.add(bacos); 
        vectorPanel4.add(batan);
        

        add(vectorPanel1);       add(vectorPanel2);   add(vectorPanel3);  add(vectorPanel4); 
    }
   }


