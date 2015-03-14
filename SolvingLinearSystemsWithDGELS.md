# Introduction #
> _`LAPACK`_ `is used in GroovyLab with its` _`JLAPACK`_ `translation to Java. The following scripts demonstrates how the ` _`DGELS`_ `JLAPACK routine can be used in GroovyLab to solve both overdetermined and underdetermined systems. `

> ## Overdetermined case ##
```
 

A = [[3.5, 5.6, -2], [ 4.5, 6.7, 9.4], [2.3, -2.3, 9.4]] as double [][]

b = [ [-0.2],[0.45],[-3.4]]  as double [][]

x  = A / b    // solve the system
A*x-b

// test DGELS
lssol = DGELS(A, b)

// test overdetermined case, i.e. more equations than unknowns
ARowsMoreThanCols = [ [3.5, 5.6, -2], [4.5, 6.7, 9.4], [2.3, -2.3, 9.4],[8.1, 2.3, -0.2]] as double[][]
bRowsMoreThanCols =  [[-0.2],[0.45],[-3.4],[-0.3]] as double [][]

lssolOverDetermined = DGELS(ARowsMoreThanCols, bRowsMoreThanCols)

/* Matlab
  ARowsMoreThanCols = [3.5 5.6 -2; 4.5 6.7 9.4; 2.3 -2.3 9.4; 8.1 2.3 -0.2];
  bRowsMoreThanCols = [-0.2; 0.45; -3.4; -0.3];
  lssolOverDetermined = ARowsMoreThanCols \ bRowsMoreThanCols
 */
```

## Underdetermined case ##
```
// test underdetermined case, i.e. more unknowns than equations
AColsMoreThanRows= [[13.5, 5.6,  9.3, -2], [3.5, 3.7, 9.4, -0.7],[2.3, -2.3, 0.9, 9.4]] as double [][]
bColsMoreThanRows=  [[-0.4], [0.5], [-3.4], [-7.8]] as double [][]

lssolUnderDetermined = DGELS(AColsMoreThanRows, bColsMoreThanRows)

/* Matlab
  AColsMoreThanRows = [13.5  5.6  9.3 -2; 3.5 3.7 9.4 -0.7; 2.3 -2.3 0.9 9.4];
  bColsMoreThanRows = [-0.4; 0.5; -3.4; 9.7];
  lssolUnderDetermined = AColsMoreThanRows  \ bColsMoreThanRows
 */
 


```