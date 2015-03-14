# Introduction #

`GroovySci provides limited support for loading/saving to Matlab .mat file format. We describe the basic routines by means of examples. `


# Examples #

`We can save individual GroovySci variables to .mat files. These variables should be of types double[], double [][], Vec, Matrix. For example: `

```
 //    save a Matrix    
       t = inc(1,1, 1000)
       x = sin(0.12*t)
       matFileName = "testMatrixX"
       save(matFileName, "x")
      
 //     save  a Vector
      vt = vinc(0, 0.01, 10)
      vx = sin(2.3*vt)
      vecFileName = "testVecX"
      save(vecFileName, "vx")
          
```

`We can also save all the variables of types double[], double [][], Vec, Matrix to a .mat file. For example: `

```
      // define a Matrix
       t = inc(1,1, 1000)
       x = sin(0.12*t)
      
      // define a Vector
      vt = vinc(0, 0.01, 10)
      vx = sin(2.3*vt)

      varsFileName = "testAllVars"
      save(varsFileName)
          
```


`We can load all the saved variables from a .mat file with the load command as the following example illustrates: `

```
load("testAllVars.mat")
```