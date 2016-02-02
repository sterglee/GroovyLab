class MyClassW {
  def  comp() {
     def s = 0.0
    def     k=0
     while (k < 1000)  {
     def m=0
       while (m < 1000) {
           s += ( k*m)/ 10000000.34
           m++
           }
         k++
       }
     return( s)
  }
}
return MyClassW



mcW = new MyClassW()

tic(); smW = mcW.comp(); tmW = toc() 