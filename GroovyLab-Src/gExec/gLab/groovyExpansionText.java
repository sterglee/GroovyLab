
package gExec.gLab;

public class groovyExpansionText {

    public static String expansionText = " // this script expands the basic Groovy operators \n "+
            " //   Number  +  Matrix \n"+
" Number.metaClass.plus = { Matrix m -> ma = m.plus(delegate);  ma } \n\n"+
"//   Number - Matrix \n"+
" Number.metaClass.minus = { Matrix m -> mm = m.multiply(-1); mm = mm.plus(delegate);  mm } \n"+
"  //   Number * Matrix \n"+
" Number.metaClass.multiply = { Matrix m -> mm = m.multiply(delegate); mm } \n\n"+
" Number.metaClass.div =  \n"+
"   { Matrix m -> N = m.size()[0]; M = m.size()[1];  // get size of matrix m \n"+
"      mm = new Matrix(N, M, (double) delegate); // create a new Matrix with all its elements equal to the Number \n"+
"      Matrix im = groovySci.math.array.inv(m);     \n"+
"      mm = im * delegate;  \n"+
"      mm \n"+
"   } \n"+
" \n"+
" //////////////////////////////////////////////////////////////////////////////////// \n"+
" // double [] OPERATIONS \n"+
" \n"+
"// multiply Number * double [] \n"+
" Number.metaClass.multiply =  \n"+
"  {  double []  m ->  \n"+
"        N = m.size();    res = new double[N]; \n"+
"        for (k=0; k<N; k++)  res[k] = delegate * m[k]; \n"+
"      res  \n"+
"}     \n"+
"\n"+
"// multiply  double [] * Number \n"+
"double [].metaClass.multiply = \n"+
"  {  Number  num -> \n"+
"        N = delegate.size();    res = new double[N]; \n"+
"        for (k=0; k<N; k++)  res[k] = delegate[k] * num; \n"+
"      res \n"+
"}   \n"+
"\n"+       
"\n"+        
"\n"+
"//  Number + double [] \n"+
"Number.metaClass.plus = \n"+
 " {  double []  m -> \n"+
 "       N = m.size();    res = new double[N]; \n"+
 "       for (k=0; k<N; k++)  res[k] = delegate + m[k]; \n"+
 "     res  \n"+
"}   \n"+
"\n"+
"// double [] + Number \n"+
"double [].metaClass.plus = \n"+ 
"  {  Number  num ->  \n"+
"        N = delegate.size();    res = new double[N]; \n"+
"        for (k=0; k<N; k++)  res[k] = delegate[k] + num ; \n"+
"      res \n"+
"}    \n"+
" \n"+
" \n"+
"//  Number - double [] \n"+
"Number.metaClass.minus = \n"+ 
"  {  double []  m ->  \n"+
"        N = m.size();    res = new double[N]; \n"+
"        for (k=0; k<N; k++)  res[k] = delegate - m[k];\n"+
"      res \n"+
"} \n"+   
"\n"+
"// double [] - Number\n"+
"double [].metaClass.minus = \n"+
"  {  Number  num -> \n"+
"        N = delegate.size();    res = new double[N];\n"+
"        for (k=0; k<N; k++)  res[k] = delegate[k] - num;\n"+
"      res \n"+
"} \n"+  
"\n"+
"// double [] + double []     \n"+
"double[].metaClass.plus = {double [] added -> \n"+
"     N=delegate.size();  \n"+
"     res = new double[N];  // create result array\n"+
"     for (k=0; k<N; k++)  res[k] = delegate[k]+added[k];  // perform the addition\n"+
"     res\n"+
"} \n"+     
"\n"+
"// double [] - double[]\n"+
"double[].metaClass.minus = {double [] sub -> \n"+
"     N=delegate.size();  \n"+
"     res = new double[N];  // create result array\n"+
"     for (k=0; k<N; k++)  res[k] = delegate[k]-sub[k];  // perform the subtraction\n"+
"     res\n"+
"} \n"+      
"    \n"+
"// double [] * double[] as inner product\n"+
"double[].metaClass.multiply = {double [] m -> \n"+
"     N=delegate.size();  \n"+
"     res = new double[N];  // create result array\n"+
"     for (k=0; k<N; k++)  res[k] = delegate[k]*m[k];  // perform the inner product multiplication\n"+
"     res\n"+
"}\n"+      
"\n"+
"// double [] / double [] as element-wise division\n"+
"double[].metaClass.div = {double [] dm -> \n"+
"     N=delegate.size();  \n"+
"     res = new double[N];  // create result array\n"+
"     for (k=0; k<N; k++)  res[k] = delegate[k]/dm[k];  // perform the element-wise division\n"+
"     res\n"+
"}\n"+      
"\n"+
"//////////////////////////////////////////////////////////////////////////\n"+
"// double [][] OPERATIONS\n"+
"\n"+
"// multiply Number * double [][]\n"+
"Number.metaClass.multiply = \n"+
 " {  double [][]  m -> \n"+
 "       N = m.size();  M = m[0].size();\n"+
 "  res = new double[N][M];\n"+
 "       for (r=0; r<N; r++) \n"+
 "          for (c=0; c<M; c++)\n"+
 "                res[r][c] = delegate * m[r][c];\n"+
 "     res \n"+
"}\n"+   
"\n"+
"// multiply  double [][] * Number\n"+
"double[][].metaClass.multiply = \n"+
"  {  Number  num -> \n"+
"        N = delegate.size();  M = delegate[0].size();\n"+
"   res = new double[N][M];\n"+
"        for (r=0; r<N; r++) \n"+
"           for (c=0; c<M; c++)\n"+
"                 res[r][c] = delegate[r][c] * num;\n"+
"      res \n"+
"}   \n"+
"\n"+
"\n"+
"// Number + double [][] \n"+
"Number.metaClass.plus = \n"+
"  {  double [][]  m -> \n"+
"        N = m.size();  M = m[0].size();\n"+
"   res = new double[N][M];\n"+
"        for (r=0; r<N; r++) \n"+
"           for (c=0; c<M; c++)\n"+
"                 res[r][c] = delegate + m[r][c];\n"+
"      res \n"+
"}   \n"+
"\n"+
"// double [][] + Number\n"+
"double[][].metaClass.plus = \n"+
"  {  Number  num -> \n"+
"        N = delegate.size();  M = delegate[0].size();\n"+
"   res = new double[N][M];\n"+
"        for (r=0; r<N; r++) \n"+
"           for (c=0; c<M; c++)\n"+
"                 res[r][c] = delegate[r][c] + num;\n"+
"      res \n"+
"}\n"+   
"\n"+
"\n"+
"// Number - double [][] \n"+
"Number.metaClass.minus = \n"+
"  {  double [][]  m -> \n"+
"        N = m.size();  M = m[0].size();\n"+
"   res = new double[N][M];\n"+
"        for (r=0; r<N; r++) \n"+
"           for (c=0; c<M; c++)\n"+
"                 res[r][c] = delegate - m[r][c];\n"+
"      res \n"+
"}\n"+   
"\n"+
"// double [][] - Number\n"+
"double[][].metaClass.minus = \n"+
"  {  Number  num -> \n"+
"        N = delegate.size();  M = delegate[0].size();\n"+
"   double[][] res = new double[N][M];\n"+
"        for (r=0; r<N; r++) \n"+
"           for (c=0; c<M; c++)\n"+
"                 res[r][c] = delegate[r][c] - num;\n"+
"      res \n"+
"} \n"+
"         \n"+  
"            // double [][] +double [][]\n"+
"double[][].metaClass.plus = \n"+
"  { double [][] m -> \n"+
"        N1 = delegate.size();  M1 = delegate[0].size();\n"+
"        N2 = m.size(); M2 = m[0].size();\n"+
"        N = N1; if (N2<N) N = N2;\n"+
"        M = M1;  if (M2<M) M = M2;\n"+
"\n"+
"   double[][] res = new double[N][M];\n"+
"        for (r=0; r<N; r++) \n"+
"           for (c=0; c<M; c++)\n"+
"                 res[r][c] = delegate[r][c] + m[r][c];\n"+
"      res \n"+
"}   \n"+
"\n"+
"// double [][] - double [][]\n"+
"double[][].metaClass.minus = \n"+
"  { double [][] m -> \n"+
"        N1 = delegate.size();  M1 = delegate[0].size();\n"+
"        N2 = m.size(); M2 = m[0].size();\n"+
"        N = N1; if (N2<N) N = N2;\n"+
"        M = M1;  if (M2<M) M = M2;\n"+
"\n"+
"   double[][] res = new double[N][M];\n"+
"        for (r=0; r<N; r++) \n"+
"           for (c=0; c<M; c++)\n"+
"                 res[r][c] = delegate[r][c] - m[r][c];\n"+
"      res \n"+
"}   \n"+
"\n"+
"// double [][] * double [][]\n"+
"double [][].metaClass.multiply = \n"+
"  {  double [][] v2 ->\n"+
"       double[][] v1 = delegate;\n"+
"       v1Rows = v1.size();  //  rows of the result matrix\n"+
"       v2Cols = v2[0].size();  //  cols of the result matrix\n"+
"       double [][] result = new double[v1Rows][v2Cols];\n"+
"       v1Cols = v1[0].size();\n"+
"       v1Colj = new double[v1Cols];\n"+
"\n"+
"    for (int j = 0; j < v2Cols; j++) {\n"+
"      for (int k = 0; k < v1Cols; k++) \n"+
"        v1Colj[k] =v2[k][j];\n"+
"      \n"+
"      for (int i = 0; i < v1Rows; i++) {\n"+
"        double[] Arowi = v1[i];\n"+
"        double s = 0;\n"+
"        for (int k = 0; k < v1Cols; k++) \n"+
"          s = s + Arowi[k]*v1Colj[k];\n"+
"       \n"+
"       result[i][j] = s;\n"+
"    }\n"+
"   }\n"+
"     result\n"+
" }\n";

    
    

}

