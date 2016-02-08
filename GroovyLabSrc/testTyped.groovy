class MyClass {
   double comp() {
     double s = 0.0
     for (k in 0..1000)
       for (m in 0..100)
           s += ( k*m)/ 10000000.34
     return( s)
  }
}
return MyClass



mc = new MyClass()

tic(); sm = mc.comp(); tm = toc() 