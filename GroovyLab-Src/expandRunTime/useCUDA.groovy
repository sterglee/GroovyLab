package expandRunTime;

  
                  
    

  groovySci.math.array.Matrix.metaClass.multiply = {  
                    groovySci.math.array.Matrix.metaClass.multiply =  {
                  
    groovySci.math.array.Matrix m ->   // the input Matrix \n"+ 
        
                    if (delegate.Nrows()*delegate.Ncols()*m.Ncols() > 100000)  {
                       delegate.fmul(m)  
                    }
                     else 
                    delegate.multiplySerial(m) 
        }
      
      }
  



