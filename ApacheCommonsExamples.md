## Descriptive Statistics ##
```

// buffer the imports to execute step-wise
import org.apache.commons.math3.stat.descriptive.*   

// Get a DescriptiveStatistics instance
stats = new DescriptiveStatistics()
// Add the data from the array

inputArray = vrand(2000).getv()
for (int i=0; i<inputArray.length; i++)
  stats.addValue(inputArray[i])
  
// Compute some statistics
mean = stats.getMean()
std = stats.getStandardDeviation()
median = stats.getPercentile(50)
```



## Simple Regression ##
```

// buffer the imports to execute step-wise
import org.apache.commons.math3.stat.regression.*

regression = new SimpleRegression()
regression.addData(1d, 2d)
// At this point, with only one observation,
// all regression statistics will return NaN
regression.addData(3d, 3d)
// With only two observations,
// slope and intercept can be computed
// but inference statistics will return NaN
regression.addData(3d, 3d)
// Now all statistics are defined

// Compute some statistics based on observations added so far
// displays intercept of regression line
println(regression.getIntercept())
// displays slope of regression line
println( regression.getSlope())
// displays slope standard error
println(regression.getSlopeStdErr())

//Use the regression model to predict the y value for a new x value.
// displays predicted y value for x = 1.5
println(regression.predict(1.5d))


```

## Gumbel Distribution ##

```


import org.apache.commons.math3.distribution.GumbelDistribution


mu = 7.8
beta = 2.3


// create a Gumbel distribution using the latest Apache Common Maths library
gd = new GumbelDistribution(mu, beta)


x =  vinc(0, 0.01, 30)  // sample the axis, as MATLAB's 0:0.01:30

// now evaluate the density over the x-axis
Nx = x.length()
y= new double[Nx]

for (k in 0..Nx-1)
 y[k]  = gd.density(x[k])

 plot(x, y)


```


# Curve Fitting #
## Overview ##
`The fitting package deals with curve fitting for univariate real functions. When a univariate real function y = f(x) does depend on some unknown parameters p_0,  p_1..., pn-1, curve fitting can be used to find these parameters. It does this by fitting the curve so it remains very close to a set of observed points (x0, y0), (x1, y1) ... (xk-1, yk-1). This fitting is done by finding the parameters values that minimizes the objective function Σ(yi - f(xi))2. This is actually a least-squares problem.`

`For all provided curve fitters, the operating principle is the same. Users must first create an instance of the fitter, then add the observed points and once the complete sample of observed points has been added they must call the fit method which will compute the parameters that best fit the sample. A weight is associated with each observed point, this allows to take into account uncertainty on some points when they come from loosy measurements for example. If no such information exist and all points should be treated the same, it is safe to put 1.0 as the weight for all points.`


```

import org.apache.commons.math3.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer
import org.apache.commons.math3.analysis.ParametricUnivariateFunction
import org.apache.commons.math3.util.FastMath
import org.apache.commons.math3.fitting.*
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction

         CurveFitter fitter = new CurveFitter(new LevenbergMarquardtOptimizer())
fitter.addObservedPoint(-1.00, 2.021170021833143)
fitter.addObservedPoint(-0.99, 2.221135431136975)
fitter.addObservedPoint(-0.98, 2.09985277659314)
fitter.addObservedPoint(-0.97, 2.0211192647627025)
// ... Lots of lines omitted ...
fitter.addObservedPoint( 0.99, -2.4345814727089854)

// The degree of the polynomial is deduced from the length of the array containing
// the initial guess for the coefficients of the polynomial.
init = [ 12.9, -3.4, 2.1 ] as  double [] // 12.9 - 3.4 x + 2.1 x^2

// Compute optimal coefficients.
best = fitter.fit(new PolynomialFunction.Parametric(), init)

// Construct the polynomial that best fits the data.
fitted = new PolynomialFunction(best)
        
```

## Kalman Filter ##

```

import org.apache.commons.math3.filter.*

import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.MatrixDimensionMismatchException
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector
import org.apache.commons.math3.random.JDKRandomGenerator
import org.apache.commons.math3.random.RandomGenerator
import org.apache.commons.math3.util.Precision

        // simulates a vehicle, accelerating at a constant rate (0.1 m/s)

        // discrete time interval
        dt = 0.1d
        // position measurement noise (meter)
        measurementNoise = 10d
        // acceleration noise (meter/sec^2)
        accelNoise = 0.2d

        // A = [ 1 dt ]
        //     [ 0  1 ]
        A = new Array2DRowRealMatrix( [ [1, dt ], [ 0, 1 ]] as double [][])

        // B = [ dt^2/2 ]
        //     [ dt     ]
        B = new Array2DRowRealMatrix([ [Math.pow(dt, 2d) / 2d ], [ dt ]] as double [][])

        // H = [ 1 0 ]
        H = new Array2DRowRealMatrix([ [1d, 0d ]] as double [][])

        // x = [ 0 0 ]
        x = new ArrayRealVector( [0, 0] as double [])

        tmp = new Array2DRowRealMatrix(  [ [ Math.pow(dt, 4d) / 4d, Math.pow(dt, 3d) / 2d ],
                                 [ Math.pow(dt, 3d) / 2d, Math.pow(dt, 2d) ] ] as double [][])

        // Q = [ dt^4/4 dt^3/2 ]
        //     [ dt^3/2 dt^2   ]
        Q = tmp.scalarMultiply(Math.pow(accelNoise, 2))

        // P0 = [ 1 1 ]
        //      [ 1 1 ]
        P0 = new Array2DRowRealMatrix([ [ 1, 1 ],[ 1, 1 ]] as double [][])

        // R = [ measurementNoise^2 ]
        R = new Array2DRowRealMatrix( [  Math.pow(measurementNoise, 2) ] as double [])

        // constant control input, increase velocity by 0.1 m/s per cycle
        u = new ArrayRealVector( [ 0.1d] as double[])
        
        pm = new DefaultProcessModel(A, B, Q, x, P0)
        
        mm = new DefaultMeasurementModel(H, R)
        
        filter = new KalmanFilter(pm, mm)

	  shouldBe1 =  filter.getMeasurementDimension()    // should be 1
       shouldBe2 = filter.getStateDimension()   //  should be 2

        P0data = P0.getData() 
        FilterErrorCovariance =  filter.getErrorCovariance()
        P0dataEqFilterErrorCovariance = P0data-FilterErrorCovariance  // should be zero
        

        // check the initial state
        expectedInitialState = [ 0.0, 0.0 ] as double []
        stateEstimation = filter.getStateEstimation()
        stateEstimationEqExpectedInitialState = stateEstimation - expectedInitialState  // should be zero
        

        randg = new JDKRandomGenerator()

        tmpPNoise = new ArrayRealVector( [  Math.pow(dt, 2d) / 2d, dt ] as double [])

        mNoise = new ArrayRealVector(1)

        // iterate 60 steps
        for (int i = 0; i < 60; i++) {
            filter.predict(u)

            // Simulate the process
             pNoise = tmpPNoise.mapMultiply(accelNoise * randg.nextGaussian())

            // x = A * x + B * u + pNoise
            x = A.operate(x).add(B.operate(u)).add(pNoise)

            // Simulate the measurement
            mNoise.setEntry(0, measurementNoise * randg.nextGaussian())

            // z = H * x + m_noise
            z = H.operate(x).add(mNoise)

            filter.correct(z)

            // state estimate shouldn't be larger than the measurement noise
            diff = Math.abs(x.getEntry(0) - filter.getStateEstimation()[0])
            println("diff = "+diff)
        }


```