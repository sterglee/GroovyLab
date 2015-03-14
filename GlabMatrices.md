# Introduction #

`This tutorial describes by means of examples the Matrix class of GroovyLab. `


# Tutorial on Matrix class #

`The Matrix class implements zero-indexed two-dimensional dense matrices in GroovySci. That matrices are mostly based on the JAMA library.`


`We implement the basic indexing of the Matrix as usual, e.g. `
```
x = rand(6, 9)
x1_2 = x[1,2] // get the corresponding element
x[1][2] = 12.12 // put a value
               // Do not use x[1,2]=12.12 !!
x[1,2]  // display that value
```


`However, for complex operations that handle Matrix subranges, we have chosen at the new version of GroovyLab to use, the ` **`g(...)`** ` and ` **`s(...)`** `style methods. Below we describe the basics of their utilization. These methods are efficient, and stable but their interface is not stabilized yet, so the source code is the most objective documentation.`

## Constructors ##

`Matrix(rows: Integer, cols: Integer) // Creates a Matrix of size rows, cols initialized to zeros`

```
m=new Matrix(2, 3)
```




`Construct a Matrix by copying the array values, Matrix(double [][] da ), Creates a Matrix initialized with the da array`
```
dd = new double[2][4]

dd[1][1]=11

mdd = new Matrix(dd)

```



`Construction with filling an initial value, Matrix(int N, int M, double value), e.g. `

```
m = new Matrix(6,8, 6.8)
```


`Construction with a double[][] array`

```
md = [ [4.5, 5.6, -4], [ 4.5, 3, -3.4],  [14.5, 2.2, -2.4], [ 40.5, 2.2, -3.4]] as double[][]
m = new Matrix(md)
```

**`Construction using the static method ` _`Mat()`_ ` of the Matrix class: `** `The static method ` _` Mat()`_ ` of the Matrix class, returns a constructed Matrix object from variable argument double parameters. The first two parameters specify the number of rows and columns. For example:`
```
x = 5.5; y = 73.3  // some values
A = Mat( 3, 3,  // number of rows, and columns
        3.4*x, x, -0.4,  // first row
        -cos(x+y), x- sin(x*y), x, // second row
        x-0.3, x/x, y/(2*y)  // third column
        )
```

`Updating matrix elements `

```
a=rand(4,7)  // create a random matrix
a[2][3]= 23.0  // sets the corresponding element
println("element a[2, 3] is now "+a[2,3])
```



## `Matrix Indexing/Setting operations` ##

`The ` _`GroovyLab Matrix`_ `class stores data using a double [][]  array. Generally, this representation is not the optimal, but it is simple and generally efficient. As a useful note, Java arrays storage is row based, and therefore is more efficient to work with fixed rows, i. e. the inner faster changed index should operate on columns.  We can perform indexing in three ways:`

`a. Exploiting the overloading of the array indexing operator [] by the methods ` _`getAt()`_, _`putAt()`_  `of Groovy, as for example:`
```
A = rand(2,3) // a 2 X 3 Matrix
a12 = A[1,2]  // get the element
A[1][2] = 2.3  // put the value
```

`b. By directly accessing the Java` _`double[][]`_ `internal representation, as for example:`
```
A = rand(2,3) // a 2 X 3 Matrix
a12 = A.d[1][2]  // get the element
A.d[1][2] = 2.3  // put the value 
```

`The second way, although not as elegant, is faster. `

`c. By using the` **`gr(), gc(), grc()`** `methods.  We explain these methods: The group of overloaded methods named` _`gr()`_  `(named by the initials get row)  selects  rows of the matrix, i.e. for a specified group of rows, select all the columns.`

`The group of overloaded methods named` _`gc()`_  `(named by the initials get column) selects columns of the matrix, i.e. for a specified group of columns, select all the rows. `

`The group of overloaded methods named` _`grc()`_  `(named by the initials get row-column) selects rows and columns of the matrix, i.e. for a specified group of rows/columns, select all that matrix part.`

`Below we describe these methods for matrix subrange chores  in detail.`

### `Matrix subrange operations` ###

`These operations aim to provide convenient getting/setting of matrix ranges. They are implemented by the methods` _`gr(), gc(), grc()`_ `for getting and`  _`sr(), sc(), src()`_  `for setting values. The design is kept simple and symmetrical. It is also efficient, since it is coded in Java. `

`It is better to understand these operations by means of examples. Let construct an example matrix:`

```
A = Mat( 6, 4,
           1.2, -0.2, 5.6, -0.03,
           4.5, 0.03, 2.3, 0.2,
          0.12, -2.2, 15.6, -11.3,
           5.34, 5.03, 1.3, 0.3,
           0.022, -120.2, 6.778, -10.03,
           145, 10.03, 13, -0.2)
```

`We proceed by describing the get style routines, and then the set style ones.`

### `Get Routines` ###


**`Single row/column select`**

`We can get a column of the matrix as:`
```
  Ac =  A.gc(2)   // get  column 2
```

**`Similarly, for getting a row:`**
```
  Ar = A.gr(2)   // get row 2
```

**`Select a continuous range of rows/columns`**

`We can get a column range of the matrix as:`
```
  Acr =  A.gc(1, 3)   // get  columns 1 to 3 all rows
```

**`Similarly, for getting a row range:`**

```
  Arr = A.gr(1, 3)   // get rows 1 to 3 all columns
  Arr = A[1..3]  // alternative
```

**`We can select a row and column range as`**
```
Arc = A[1..2][2..3]  
```

`or`
```
Arc = A[1..2, 2..3]  
```
**`Additionally, we can specify an increment:`**

```
  Acr =  A.gc(0, 2, 3)   // as MATLAB's  A(:, 0:2:3)
```

**`Similarly we can select rows specifying  an increment:`**

```
Arr = A.gr(0, 2, 4)  // as MATLAB's A(0:2:4, :)
```

`We can extract rectangular parts of the matrix as:`
```
Arc = A.grc(0, 2, 4, 1, 1, 3)  // as MATLAB's  A(0:2:4, 1:1:3)
```

**`Specifying particular rows to extract`**


`We can specify particular rows to extract as:`
```
rowIndices = [3, 1] as int []  // specify the required rows in an int[] array
 erows = A.gr(rowIndices)   // get the rows
```

**`Specifying particular columns to extract`**

`Similarly, we can specify particular columns to extract as:`
```
colIndices = [1, 3, 2] as int []  // specify the required columns in an int[] array
ecols = A.gc(colIndices)   // get the columns
```

**`Extracting submatrices using Groovy ranges`**

`Groovy ranges allow a nice syntax. We can use them as  illustrated:`
```
Ar = A.gr(1..2)    // get rows 1 to 2
Ac = A.gc(2..3)  // get columns 2 to 3
Arc = A.grc(1..2, 2..3)  // get matrix subrange of rows 1 to 2 and columns 2 to 3
```


### `Set Routines` ###

**`Copy a matrix within another matrix`**
```
  x = rand(12, 15)  // a random matrix
  y = ones(2, 3)   // a matrix of 1s
  x.s(2, 2,  y)  // copy the matrix of 1s at the 2,2 position within the random matrix
```

**`Set a row range to a value`**
```
  a = rand(5,9)  // a random matrix
  a.sr(1..2, 8)    // sets rows 1 and 2 to 8
```

**`Set a row submatrix  to a value`**
```
  a = rand(5,9)  // a random matrix
  a.sr(1, 2, 8)    // sets rows 1 and 2 to 8
```

**`Set a column  range to a value`**
```
  a = rand(5, 9)  // a random matrx
  a.sc(2..3, 77.7) // sets cols 2 to 3 to 77.7
```

**`Set a column  submatrix  to a value`**
```
  a = rand(5, 9)  // a random matrix
  a.sc(2, 3, 77.7) // sets cols 2 to 3 to 77.7
```

**`Set a row-column range to a value`**
```
 a = rand(10, 15)  // a random matrix
a.src(2..5, 1..3, 11.2)  // sets rows 2 to 5 and columns 1 to 3 to 11.2
```

**`Set a row-column matrix part to a value`**
```
 a = rand(10, 15)  // a random matrix
a.src(2, 5, 1, 3, 11.2)  // sets rows 2 to 5 and columns 1 to 3 to 11.2
```

## Convenient Matrix indexing/assignment operations ##

`We demonstrate some more MATLAB-like handling of submatrices. Note that for ranges, the assignment uses Groovy-like indexing, while the indexing uses Java like. The example demonstrates these elegant operations: `

```
x = rand(80, 100)
x[2..3, 0..1] = 6.5353  // a rectangular subrange

yx = x[2..3][0..1]    // get back the assigned range, all elements should be 6.5353

y = x[2..3, 0..1]    // another way to get back the assigned range, all elements should be 6.5353

x[4..5] = -0.45454  // assign rows 4 to 5 all columns


// assignment using ranges
x = rand(90, 90)
x[(0..20).by(2), 0..1] = 99
yx = x[(0..20).by(2)][ 0..1]  // select the assigned range

// assignment using ranges
x = rand(90,90)
x[0..1, (0..30).by(3)] = 33.3
yx =  x[0..1][ (0..30).by(3)]   // select the assigned range


// assignment using ranges
x = rand(90,90)
x[(2..14).by(5), (0..30).by(3)] = -77.3
yx = x[(2..14).by(5)][(0..30).by(3)]  // select the assigned range



row=2; col = 3
y = x[row..row+2][col..col+5]  // get a matrix range

yy = x[(row..row+50).by(2)][ (col..col+30).by(3)] // like MATLAB's x(row:2:row+50, col:3:col+30)
```

## Operators ##

`The following operators are available:  Matrix + Matrix, Matrix + Number, 	Matrix - Matrix, Matrix - Number, Matrix * Matrix, Matrix * Number, 		Matrix / Matrix, Matrix / Number, Number + Matrix, Number * Matrix`

```
a = rand(5, 8)
aa = a+a
a5 = a+5
ap5 = 5+a
a6 = a*6.8
a6m = 6.8*a
a85 = rand(8, 5)
ama = a*a85
```

## Static operations ##

`Some available static operations : `

```
oo = ones(4, 10)   // a matrix with all ones

oo4 = sum(oo)   // perform sum of the columns
sum(sum(oo))   // total sum of the matrix elements

oo2 = fill(4, 10, 2.0)  // a matrix filled with 2 values
prod(oo2)  // perform product of the columns
prod(prod(oo2))  // product of all the matrix elements

csoo = cumsum(oo)  // perform a cummulative sum across columns
cpoo2 = cumprod(oo2)   // perform a cummulative product across columns

aa = rand(5,5)
aai = inv(aa)  // compute the matrix inverse

A = rand(5,5)
b = rand(5,1)
X = solve(A, b) //returns X Matrix verifying A*X = b. 
rank(A)  // the rank of A
trace(A)  // the trace of A
det(A)  // the determinant of A
cond(A)  // the condition number of A
norm1(A)  // norm 1 of A
norm2(A)  // norm 2 of A
normF(A)  // Frobenius norm of A
normInf(A)  // norm inf of A
dot(A, A)  // the dot product of A by itself

```

# Selecting the specified rows and columns of a matrix #

`Extracting sets of rows and columns from a matrix is many times a convenient operation. `

`We can extract the columns specified with true values with an array`  _`colIndices`_`.The new matrix is formed by using all the rows of the original matrix but with using only the specified columns. Example: `
```
  testMat = M(" 1.0 2.0 3.0 4.0; 5.0 6.0 7.0 8.0; 9 10 11 12")
  colIndices = [true, false, true, false] as boolean []
  extract0_2cols = testMat.gc(colIndices)
```

`We can extract the rows specified with true values with an array ` _`rowIndices`_`. The new matrix is formed by using all the columns of the original matrix but with using only the specified rows. Example: `

```
 testMat = M(" 1.0 2.0 3.0 4.0; 5.0 6.0 7.0 8.0; 9 10 11 12")
 rowIndices = [true, false, true] as boolean []
 extract0_2rows = testMat.gr(rowIndices)
```



`We can extract the rows specified  with  an  array ` _`rowIndices`_`. The new matrix is formed by using all the columns of the original matrix but with using only the specified rows. The rows at the new matrix are arranged in the order specified with the array `_`rowIndices`_`. Example: `

```
 testMat = M(" 1.0 2.0 3.0 4.0; 5.0 6.0 7.0 8.0; 9 10 11 12; 13 14 15 16; 17 18 19 20")
 rowIndices = [3, 1] as int []
 extract3_1rows = testMat.gr(rowIndices)
```

`We can extract the columns specified with true values with an array ` _`colIndices`_ `.The new matrix is formed by using all the rows of the original matrix but with using only the specified columns.`
```
 testMat = M(" 1.0 2.0 3.0 4.0; 5.0 6.0 7.0 8.0; 9 10 11 12")
 colIndices = [true, false, true, false] as boolean[]
 extract0_2cols = testMat.gc(colIndices)
```

## Filtering rows and columns of a matrix ##

`We can filter all the rows/columns of the matrix according to a predicate. The predicate is a function from the Int index of row/column to a boolean value. The relevant routines are as follows: `
```

  Matrix  filterRows(Closure predicate)
  Matrix  filterColumns(Closure predicate)

```

`For example, to return all the even numbered rows and columns of a matrix:`
```
      x = rand(10, 13)
      isEven = {  int n ->  if (n % 2 == 0) true else false  }  // define the predicate
      xevenRows = x.filterRows(isEven)
      xevenCols = x.filterColumns(isEven)
   
```

> ## Displaying the contents of a matrix ##

`By default a matrix is displayed using the  results returned by ` _`toString()`_ `, that however truncate large matrices, since there is a severe performance problem if we compute and display the resulting huge strings.`

`We can however display the whole contents using the ` _`print()`_ `method.`

`Also, we can browse the matrix contents with the possibility of altering them, using a convenient JTable based presentation, with the ` _`browse()`_ `method. For example: `

```
x = rand(30, 25)
x.print() // display the contents
x.browse()  // browse them using a JTable, editing cells affects the contents of the matrix

```


`We can also pass a variable name to the browse command, to help the user especially when many matrix contents windows are open: `

```
myMatrix  = rand(10, 15)
myMatrix.browse("myMatrix")  // browse them using a JTable, editing cells affects the contents of the matrix

```

## `Linear Algebra operations from JAMA` ##

### `Solving linear systems` ###

`We can solve linear systems using routines from many libraries. For example. `

```

// create a 3X3 matrix
A =  Mat(3, 3, 2.3, 0.23, -1.1,  0.02, -0.7, 6, 0.45, -7.8, 8.9)

// the right hand side
b = Mat(3, 1, 5, 4.5, -0.9)

// solve using QR decomposition
xqr = QR_solve(A, b)

// solve using LU decomposition
xlu = LU_solve(A, b)

// default solver uses LU decomposition
xs = solve(A, b)

// solve using EJML library
xejml = solveEJML(A, b)


// solve using MTJ library
xmtj = solveMTJ(A, b)


// solve using Apache Commons library
xac = solveAC(A, b)

```

## `Matrix map operations` ##

`We can map a closure to each element of the Matrix using the ` _`map, imap, pmap, pimap` `methods, that perform a map of the method returning a new matrix (i.e. map), a parallel map returning a new matrix (i.e. pmap), and the corresponding in-place operations (i.e. imap, pimap). The following example illustrates these functions.`_

```

n = 3000

x = ones(n, n)

// the closure to be mapped to the Matrix
 mul10 = { double x ->  x*10.0}

// parallel map operation
tic()
xx = x.pmap(mul10)
tmp = toc()


// serial map operation
tic()
xx2 = x.map(mul10)
tms = toc()

// serial in-place map operation
xxi = ones(n,n)
tic()
xxi.map(mul10)
tmsi = toc()

// parallel map in-place
tic()
 x.pimap(mul10)
tmip = toc()

```

## Parallel and in-place operations ##

`GroovyLab started to support multithreaded and in-place operations on Matrix type. We demonstrate below these operations with the ` **`sin`** ` function. Similar is for the other basic functions, e.g. cos, tan, cosh, log, etc`

```

n=3000

x = rand(n,n)

//parallel sin operation
tic()
xx = x.psin()
tmp = toc()

// serial sin operation
tic()
xxs = x.sin()
tms = toc()


// parallel in-place sin operation
tic()
xxi = x.pisin()
tmpi = toc()

// serial in-place  sin operation
tic()
xxsi = x.isin()
tmsi = toc()




```