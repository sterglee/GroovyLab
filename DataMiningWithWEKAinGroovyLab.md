# Introduction #

`jlabgroovy presents a higher level interface to WEKA algorithms than pure Java. The first step in using WEKA is to install the WEKA toolbox, that is provided with the jlabgroovy's downloads. That step is very easy by means of the "GroovySci toolboxes" tab. Then we can execute the examples that we provide.`


# WEKA Examples #

## Multilayer Perceptron ##
```


// requires WEKA toolbox to be installed
// trains a WEKA Multilayer Perceptron on the data of an .arff file

dataFile =  getFile("Please specify your data file")
data = groovySciCommands.BasicCommands.readARFFFile(dataFile)   // read the .arff file as a GroovySci Matrix
 N = data.darray.length   //  number of myInstances
 M = data.darray[0].length   //  number of attributes (i.e. dimensionality)
//  use half of the data for training and half for testing
 trainData = data[0, 2, N-1]   // extract rows from 1, up to N, step 2, i.e. data(0:2:N-1, :)
 testData = data[1, 2, N-1]  // i.e. data(1:2:N-1, :)

ClassIdx = M-1
TestingClassLabel = data[ClassIdx, 1, ClassIdx, true]   // i.e. data(:, ClassIdx)

learningRate = getDouble("Enter learning rate", 0.1)
momentum = getDouble("Enter momentum", 0.01)

mlpNet =  new weka.classifiers.functions.MultilayerPerceptron()
mlpNet.setLearningRate(learningRate)
mlpNet.setMomentum(momentum)


   rinsts = new java.io.FileReader(dataFile)
   myInstances = new weka.core.Instances(rinsts)   // construct WEKA Instances structure from the double[][] representation of GroovySci's Mat  structure
   myInstances.setClassIndex(ClassIdx-1)
   mlpNet.buildClassifier(myInstances)   // build the classifier

    netResults = new double[N]
    instEnum = myInstances.enumerateInstances()
    ctp = 0   // current testing pattern
    while (instEnum.hasMoreElements()) {
          currentInstance =  (weka.core.Instance) instEnum.nextElement()
          classification = mlpNet.classifyInstance(currentInstance)
          netResults[ctp] = classification
          ctp++
       }

 figure(1)
 plot(netResults, Color.RED)
 
Ns = TestingClassLabel.numRows()
labels = new double[Ns]
for (k in 0..Ns-1)
  labels[k] = TestingClassLabel[k,0]

 hold("on")
 plot( labels, Color.BLUE)



```