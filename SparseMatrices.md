# Introduction #

**` ATTENTION: this class is in development, features are not yet stable!! `**

`GroovyLab integrates support for sparse matrices based on the CSparse implementation of Timothy A. Davis, translated to Java by Piotr Wendykier. We describe here some sparse matrix operations by means of examples.`


# Handling sparse matrices #

`Suppose that we have a sparse matrix that is stored in a file in triplet format, e.g. the first entry: ` _`2 2 3.0`_`,  means ` _`a[2,2] = 3.0`_`. The file that stores the matrix suppose that it has the following contents:`
```
2 2 3.0
1 0 3.1
3 3 1.0
0 2 3.2
1 1 2.9
3 0 3.5
3 1 0.4
1 3 0.9
0 0 4.5
2 1 1.7
```

`Suppose that we store the matrix in a file: ` _`/home/sp/NBProjects/csparseJ/CSparseJ/matrix/t1`_

`The command to load the sparse matrix is: `

```
 s = loadSparse("/home/sp/NBProjects/csparseJ/CSparseJ/matrix/t1")
```

`We can display its contents with: `

```

s.print()

s.display()
```

`The former prints the contents in the format close to the internal representation while the later in a two-dimensional array format.`

`We can access an element of the sparse matrix as usual, e.g. `
```
 s22 = s[2,2]
```

`takes the corresponding element of the matrix.`

`We can also assign new values, e.g. `
```
s.putAt(1,2, 12)
```

`We can add and multiply two sparse matrices as usual `
```
 s2 = s+s
 sMs = s*s
```

`We can convert the sparse matrix to a double [][] array, as `
```
  ds = toDouble(s)
```

`The transpose of a sparse matrix is obtained with the` _`sparse_t`_ `routine: ~ `

```
 ts = sparse_t(s)
```

` We can add and multiply at a sparse matrix a number, e.g. `
```

s10 = s+10
sm10 = s*10
```

`We can convert a double [][] to a sparse matrix, e.g. `
```

da = new double[10][20]
da[1][2] = 12
da[2][1] = 21
dasparse = fromDoubleArray(da)

```

`We can negate a sparse matrix: `

```
sm = -s
```

`We can also add/subtract/multiply to a number a Sparse matrix: `
```
sm10a = 10+s
sm10s = 10-s
sm10m = 10*s
```


`Applying a closure to all the elements of a sparse matrix can be very convenient: `
```
// define a closure cube that performs x^3
 cube= { a -> return (double)a*(double)a*(double)a}

// map the closure to all the elements of the sparse matrix
 scube = s.map(cube)

// display the matrix
 scube.display()

```