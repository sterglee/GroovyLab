# Introduction #

`MTJ offers many sparse matrix classes and associated iterative algorithms for the solution of classical problems. `

`GroovyLab started to implement a higher-level interface to this functionality. For example, the CCMatrix class implements a Sparse class based on the Compressed Column Storage format of MTJ. `

## Example of Compressed Column Storage format ##
`Here is an example illustrating some basic operations. `
```


import no.uib.cipr.matrix.*
import no.uib.cipr.matrix.sparse.*


nrows = 10; ncols = 10;
d = new double[nrows][ncols]
d[2][3] = 10
d[4][4] = 44
// create a sparse matrix from the double [][] array
sd = new CCMatrix(d)

d[3][5] =35
sd2 = new CCMatrix(d)

// set an entry using putAt
sd.putAt(2, 1, 21)

// get entries using implicitly getAt
elem2_1 = sd[2,1]
elem2_2 = sd[2,2]


// test matrix addition
sd1  = sd+sd2
sd
sd1

sd10 = sd*100
sd10

sdd = sd1-sd1

```

## Another example of solving sparse systems ##
```

filename = "C:\\matrixData\\t1"

  // load the sparse matrix stored in triplet format
 A =    loadSparse("L:\\NBProjects\\CSPARSEJ\\CSparseJ\\matrix\\t1")  
   
b = vrand(A.Nrows).getv()

x = solve(A, b)   // solve the system with the CSparse method

Ad = SparseToDoubleArray(A)  // convert to double array

residual = Ad*x - b  // verify: should be near zero


   // convert to an MTJ CCMatrix
 ccms =    CSparseToCCMatrix(A)

 xmtj = solve(ccms, b) // solve with the MTJ based iterative solver

xmtj-x  // verify that the two solutions are equal

```