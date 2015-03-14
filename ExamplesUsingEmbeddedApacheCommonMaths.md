# Examples #


## F - Distribution ##

```

// create an F-Distribution with degrees of freedom 2 and 4
fx  = new FDistribution(2, 4)

t = Inc(0,0.01,10)
N = t.length
x = new double[N]
// compute the cumulative distribution function
for (k in 0..N-1) 
  x[k] = fx.cumulativeProbability(t[k])
	

 plot(t, x, "F-Distribution")


```


## Normal Distribution ##

```
// create a normal distribution with mean, stdv

mean = 4.7
stdv = 9.8

nx  = new NormalDistribution(mean, stdv)

t = Inc(mean-3*stdv,0.01, mean+3*stdv)
N = t.length
x = new double[N]
// compute the cumulative distribution function
for (k in 0..N-1) 
  x[k] = nx.cumulativeProbability(t[k])
        

 plot(t, x, "Normal Distribution")


```

## Using the Brent Solver to locate the zero of a function ##

```

       f = new Sin()
        
        solver = new BrentSolver()
        // Somewhat benign interval. The function is monotone.
        result = solver.solve(100, f, 3, 4)
     

```