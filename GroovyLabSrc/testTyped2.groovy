@groovy.transform.CompileStatic
class MyClass2 {
   double comp() {
     double s = 0.0
     for (k in 0..1000)
       for (m in 0..100)
           s += ( k*m)/ 10000000.34
     return( s)
  }
}
return MyClass2



mc2 = new MyClass2()

tic(); sm2 = mc2.comp(); tm2 = toc() 