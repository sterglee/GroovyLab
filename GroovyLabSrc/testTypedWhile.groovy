
@groovy.transform.CompileStatic
class MyClassWUT {
 
def  comp() {
 
double s = 0.0
    int     k=0
     while (k < 1000)  {
     int m=0
       while (m < 1000) {
           s += ( k*m)/ 10000000.34
           m++
           }
         k++
       }
     return( s)
  }
}
return MyClassWUT



mcWUT = new MyClassWUT()

tic(); smWUT = mcWUT.comp(); tmWUT = toc() 