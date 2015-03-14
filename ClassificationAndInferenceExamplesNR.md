`This page presents examples performing Classification and Inference adapted from Numerical Recipes Ed. 3.`

## Gaussian Mixture Models and k-Means Clustering, p. 842 NR3 ##

> _`Gaussian  mixture models`_ `are one of the simplest examples of classification by ` _`unsupervised learning`_ `They are also one of the simplest examples where solution by the EM (`_`expectation-maximization`_ `algorithm, proves highly successful.`

```

import static com.nr.NRUtil.SQR
import static com.nr.NRUtil.*
import static com.nr.test.NRTestUtil.*
import static java.lang.Math.sqrt

import com.nr.ci.Gaumixmod
import com.nr.ran.Normaldev


    NDIM=2  // dimensionality of the space
    NMEANS=4  // how many clusters to construct
    NPT=1000  // the number of points
    flag=0; sqrt2=sqrt(2.0); 
    ffrac = [0.25,0.25,0.25,0.25] as double []
    mmeans = [0.0,0.0,0.75,0.0,-0.25,-0.25,0.33,0.66] as double []
    gguess = [0.1,0.1,0.7,0.1,-0.2,-0.3,0.3,0.5] as double []
    ssigma = [0.1,0.1,0.02,0.2,0.01,0.1,0.1,0.05] as double[]
    vvec1 = [1.0,0.0,1.0,0.0,sqrt2,sqrt2,sqrt2,sqrt2] as double []
    vvec2 = [0.0,1.0,0.0,1.0,-sqrt2,sqrt2,-sqrt2,sqrt2] as double[]
    frac=buildVector(ffrac)
    offset = new double[NMEANS]
    guess=buildMatrix(NMEANS,NDIM,gguess) 
    means=buildMatrix(NMEANS,NDIM,mmeans)
    sigma=buildMatrix(NMEANS,NDIM,ssigma)
    vec1=buildMatrix(NMEANS,NDIM,vvec1)
    vec2=buildMatrix(NMEANS,NDIM,vvec2)
    x=new double[NPT][NDIM];
    globalflag=false;

    

    // Test Gaumixmod
    println("Testing Gaumixmod")

    ndev=new Normaldev(0.0,1.0,17)

    // Generate four groups of data
    k=0
    for (i=0;i<4;i++) {
      for (j=0;j<(int)(NPT*frac[i]);j++) {
        d0=sigma[i][0]*ndev.dev();
        d1=sigma[i][1]*ndev.dev();
        x[k][0]=means[i][0]+d0*vec1[i][0]+d1*vec2[i][0];
        x[k][1]=means[i][1]+d0*vec1[i][1]+d1*vec2[i][1];
        k++;
      }
    }

gmix = new Gaumixmod(x,guess)

    for (i=0;i<100;i++) {
      flag=gmix.estep();
      if (flag < 1.0e-6) break;
      gmix.mstep();
    }

    // check for convergence
    //    System.out.println("  flag: %f\n", flag);
    localflag = flag > 1.0e-6;
    globalflag = globalflag || localflag;
    if (localflag) {
      fail("*** Gaumixmod: No solution with 100 iterations");
      
    }

    // Check for correct determination of population fractions
    //    System.out.printf(maxel(vecsub(gmix.frac,frac)));
    sbeps=0.005;
    localflag = maxel(vecsub(gmix.frac,frac)) > sbeps;
    globalflag = globalflag || localflag;
    if (localflag) {
      fail("*** Gaumixmod: Population fractions not accurately determined");
      
    }

    // Check for correct determination of means
    for (i=0;i<NMEANS;i++) offset[i]=sqrt(SQR(gmix.means[i][0]-means[i][0])
      +SQR(gmix.means[i][1]-means[i][1]));
    //    System.out.printf(maxel(offset));
    localflag = maxel(offset) > 0.01;
    globalflag = globalflag || localflag;
    if (localflag) {
      fail("*** Gaumixmod: Means are incorrectly identified");
      
    }

    // Check for correct determination of covariance matrices
    for (i=0;i<NMEANS;i++) {
      System.out.printf("%f %f\n",gmix.sig[i][0][0], gmix.sig[i][0][1]);
      System.out.printf("%f %f\n\n",gmix.sig[i][1][0], gmix.sig[i][1][1]);
    }

    if (globalflag) System.out.println("Failed\n");
    else System.out.println("Passed\n");



```

## K-Means Clustering, NR3 p. 849 ##

```

import static com.nr.NRUtil.SQR
import static com.nr.NRUtil.buildMatrix
import static com.nr.NRUtil.buildVector
import static com.nr.test.NRTestUtil.maxel
import static java.lang.Math.sqrt

import com.nr.ci.Kmeans
import com.nr.ran.Normaldev

    flag=0; NDIM=2; NMEANS=4; NPT=1000;
    sqrt2=sqrt(2.0)
    
    ffrac = [0.25,0.25,0.25,0.25] as double []
    mmeans = [0.0,0.0,0.75,0.0,-0.25,-0.25,0.33,0.66] as double []
    gguess = [0.1,0.1,0.7,0.1,-0.2,-0.3,0.3,0.5] as double []
    ssigma= [0.1,0.1,0.02,0.2,0.01,0.1,0.1,0.05] as double[]
    vvec1 = [1.0,0.0,1.0,0.0,sqrt2,sqrt2,sqrt2,sqrt2] as double []
    vvec2 = [0.0,1.0,0.0,1.0,-sqrt2,sqrt2,-sqrt2,sqrt2] as double []
    count=new int[NMEANS]
    error=new int[NMEANS]
    frac=buildVector(ffrac)
    offset=new double[NMEANS]
    guess=buildMatrix(NMEANS,NDIM,gguess)
    means=buildMatrix(NMEANS,NDIM,mmeans)
    sigma=buildMatrix(NMEANS,NDIM,ssigma)
    vec1=buildMatrix(NMEANS,NDIM,vvec1)
    vec2=buildMatrix(NMEANS,NDIM,vvec2)
    x=new double[NPT][NDIM]
    globalflag=false

    

    // Test Kmeans
    println("Testing Kmeans")

    ndev = new Normaldev(0.0,1.0,17)

    // Generate four groups of data
    k=0
    for (i=0;i<4;i++) {
      count[i]=(int)(NPT*frac[i]);
      for (j=0;j<count[i];j++) {
        d0=sigma[i][0]*ndev.dev();
        d1=sigma[i][1]*ndev.dev();
        x[k][0]=means[i][0]+d0*vec1[i][0]+d1*vec2[i][0];
        x[k][1]=means[i][1]+d0*vec1[i][1]+d1*vec2[i][1];
        k++;
      }
    }

    kmean=new Kmeans(x,guess);  // *** put in some weird guesses

    for (i=0;i<100;i++) {
      flag=kmean.estep();
      if (flag == 0) break;
      kmean.mstep();
    }

    // check for convergence
//    System.out.println("  flag: %f\n", flag);
    localflag = flag > 0;
    globalflag = globalflag || localflag;
    if (localflag) {
      fail("*** Kmeans: No solution with 100 iterations");
      
    }

    // Check for correct populations
//    for (i=0;i<NMEANS;i++) System.out.printf(kmean.count[i] << " ";
//    System.out.printf(endl;
    for (i=0;i<NMEANS;i++) {
      error[i]=kmean.count[i]-count[i];
      localflag = (error[i] > 0 ? error[i] : -error[i]) > 15;
      globalflag = globalflag || localflag;
      if (localflag) {
        fail("*** Kmeans: Populations are not the correct approximate size");
        
      }
    }

    // Check group assignments
    k=0;
    for (i=0;i<NMEANS;i++) {
      error[i]=0;
      for (j=0;j<count[i];j++) {
        if (kmean.assign[k] != i) error[i]++;
        k++;
      }
//      System.out.printf(error[i] << " ";
    }
//    System.out.printf(endl;

    // Check for correct determination of means
    for (i=0;i<NMEANS;i++) offset[i]=sqrt(SQR(kmean.means[i][0]-means[i][0])
      +SQR(kmean.means[i][1]-means[i][1]));
//    System.out.printf(maxel(offset));
    sbeps=0.02;
    localflag = maxel(offset) > sbeps;
    globalflag = globalflag || localflag;
    if (localflag) {
      fail("*** Kmeans: Means are incorrectly identified");
      
    }

    if (globalflag) System.out.println("Failed\n");
    else System.out.println("Passed\n");
  
```

## Markov models and Hidden Markov Models, NR3 p, 856 ##

```

import static com.nr.NRUtil.buildMatrix
import com.nr.ci.HMM
import com.nr.ran.Ran

    N=1000
    M=5
    K=5
    
    sum=0
    
    aatrans = [      // Transition matrix
      0.0,0.7,0.1,0.0,0.2,
      0.2,0.4,0.0,0.2,0.2,
      0.0,1.0,0.0,0.0,0.0,
      0.0,0.3,0.0,0.7,0.0,
      0.1,0.1,0.0,0.0,0.8
    ] as double []
    
    bb = [         // Symbol probabilities for each state
      0.2,0.0,0.0,0.8,0.0,
      0.2,0.0,0.6,0.2,0.0,
      0.0,1.0,0.0,0.0,0.0,
      0.3,0.2,0.4,0.1,0.0,
      0.5,0.0,0.0,0.0,0.5
    ] as double []
    
    state= new int[N]
    symbols= new int[N]
    
    atrans=buildMatrix(M,M,aatrans)
    b=buildMatrix(M,K,bb)
    globalflag=false

    

    // Test HMM
    println("Testing HMM")

    // Generate the Markov sequence of states
    HMM.markovgen(atrans,state,0,17)

    // Generate the sequence of symbols emitted
    myran=new Ran(17)
    for (i=0;i<N;i++) {
      r=myran.doub();
      sum=0.0;
      for (j=0;j<K;j++) {
        sum += b[state[i]][j];
        if (r < sum) {
          symbols[i]=j;
          break;
        }
      }
    }

    // Try to discover the model, given the symbols
   hmm=new HMM(atrans,b,symbols)
    hmm.forwardbackward()

    // Inspect results
    jmax=0; ncorrect=0;
    
    for (i=0;i<N;i++) {
      test=0;
      for (j=0;j<M;j++) {
        if (hmm.pstate[i][j] > test) {
          test=hmm.pstate[i][j];
          jmax=j;
        }
      }
      if (jmax == state[i]) ncorrect++;
    }
    
    printf("Fraction correct: %f\n", (double)(ncorrect)/N)
    localflag =( (double)(ncorrect)/N) < 0.75
    globalflag = globalflag || localflag
    if (localflag) {
      fail("*** HMM: Actual state was not the top probability more than 25% of the time");
      
    }

    // Inspect reconstructed transition matrix
    jpen=0
    ncorrect=0
    for (i=0;i<N;i++) {
      test=0.0;
      for (j=0;j<M;j++) {
        if (hmm.pstate[i][j] > test) {
          test=hmm.pstate[i][j];
          jmax=j;
        }
      }

      // Find second largest
      test=0.0;
      for (j=0;j<M;j++) {
        if (j != jmax) {
          if (hmm.pstate[i][j] > test) {
            test=hmm.pstate[i][j];
            jpen=j;
          }
        }
      }
    
//      System.out.printf(state[i] << " %f\n", jmax);
      if (jmax == state[i] || jpen == state[i]) ncorrect++;
    }
    printf("Fraction correct: %f\n", (double)(ncorrect)/N)
    beforeBW=(double)(ncorrect)/N
    localflag = ((double)(ncorrect)/N) < 0.95
    globalflag = globalflag || localflag;
    if (localflag) {
      fail("*** HMM: Actual state was not in top 2 probabilities more than 5% of the time");
      
    }
    
//    System.out.println("Log-likelihood: %f\n", hmm.loglikelihood());

    // Test Baum-Welch reestimation
    for (i=0;i<100;i++) {
      hmm.baumwelch();
      hmm.forwardbackward();

    }

    // Inspect reconstructed transition matrix
    ncorrect=0;
    for (i=0;i<N;i++) {
      test=0.0;
      for (j=0;j<M;j++) {
        if (hmm.pstate[i][j] > test) {
          test=hmm.pstate[i][j];
          jmax=j;
        }
      }

      // Find second largest
      test=0.0;
      for (j=0;j<M;j++) {
        if (j != jmax) {
          if (hmm.pstate[i][j] > test) {
            test=hmm.pstate[i][j];
            jpen=j;
          }
        }
      }
    
       if (jmax == state[i] || jpen == state[i]) ncorrect++;
    }
    
    printf("Fraction correct after Baum-Welch: %f\n", (double)(ncorrect)/N)
    afterBW=(double)(ncorrect)/N
    localflag = (afterBW <= beforeBW)
    globalflag = globalflag || localflag;
    if (localflag) {
      fail("*** HMM: Baum-Welch reestimation did not improve model");
      
    }

    if (globalflag)  println("Failed\n")
    else  println("Passed\n")
  
```

## Hierarchical Clustering by Phylogenetic Trees, NR3 p.868 ##

```

import static com.nr.NRUtil.buildMatrix
import static java.lang.Math.abs

import com.nr.ci.Phylo_clc


NSEQ=16; NCHAR=16
    sbeps=1.0e-15
    
    ssequence = [
      3,1,1,1,1,1,1,1,0,1,1,1,1,1,2,2,
      3,1,1,1,1,1,1,1,0,1,1,1,1,1,2,3,
      3,0,1,1,1,1,1,1,0,1,1,1,1,1,3,1,
      3,3,1,1,1,1,1,1,0,1,1,1,1,1,3,1,
      3,1,1,1,2,1,1,1,2,1,1,3,1,1,1,1,
      3,1,1,1,0,1,1,1,2,1,1,3,1,1,1,1,
      3,1,1,1,1,1,1,1,2,2,1,0,1,1,1,1,
      3,1,1,1,1,1,1,1,2,0,1,0,1,1,1,1,
      2,1,1,0,1,1,3,1,1,1,1,1,3,1,1,1,
      2,1,1,0,1,1,3,1,1,1,1,1,2,1,1,1,
      2,1,1,0,1,1,0,1,1,1,0,1,1,1,1,1,
      2,1,1,0,1,1,0,1,1,1,3,1,1,1,1,1,
      2,1,3,2,1,1,1,0,1,1,1,1,1,1,1,1,
      2,1,0,2,1,1,1,0,1,1,1,1,1,1,1,1,
      2,1,1,2,1,3,1,3,1,1,1,1,1,1,1,1,
      2,1,1,2,1,0,1,3,1,1,1,1,1,1,1,1
    ] as int[] 
    
    sequence = buildMatrix(NSEQ,NCHAR,ssequence)
    dist = new double[NSEQ][NSEQ]
    globalflag=false

    
    // Test Phylo_clc
    println("Testing Phylo_clc")


    // Calculate hamming distance for all sequence pairs
    for (i=0;i<NSEQ;i++) {
      for (j=0;j<NSEQ;j++) {
        hamming=0;
        for (k=0;k<NCHAR;k++) {
          if (sequence[i][k] != sequence[j][k]) hamming++;
        }
        dist[i][j]=(double)(hamming);
      }
    }

    // Create the agglomerative phylogenetic tree
tree = new Phylo_clc(dist)

    // Inspect the tree
    localflag = tree.root != 30;
    globalflag = globalflag || localflag;
    if (localflag) {
      fail("*** Phylo_clc: Tree does not have expected number of nodes");
      
    }

    localflag = (tree.t[tree.root].nel != NSEQ)
    globalflag = globalflag || localflag
    if (localflag) {
      fail("*** Phylo_clc: Root node does not contain all the elements");
      
    }

    localflag = (tree.t[tree.t[tree.root].ldau].nel != NSEQ/2)
    globalflag = globalflag || localflag
    if (localflag) {
      fail("*** Phylo_clc: Left side of tree does not report half the elements");
      
    }

    localflag = (tree.t[tree.t[tree.root].rdau].nel != NSEQ/2)
    globalflag = globalflag || localflag
    if (localflag) {
      fail("*** Phylo_clc: Right side of tree does not report half the elements");
      
    }

    localflag = (tree.t[tree.root].modist != 0.0)
    globalflag = globalflag || localflag
    if (localflag) {
      fail("*** Phylo_clc: Mother distance of root is not zero");
      
    }

    for (i=0;i<NSEQ;i++) {
      mother = tree.t[i].mo;
      
      localflag = (tree.t[tree.t[mother].ldau].modist != tree.t[tree.t[mother].rdau].modist);
      globalflag = globalflag || localflag;
      if (localflag) {
        fail("*** Phylo_clc: Left and right daughter of a leaf reported different mother distances");
        
      }

      localflag = (tree.t[i].modist != 0.5);
      globalflag = globalflag || localflag;
      if (localflag) {
        fail("*** Phylo_clc: For this tree, all mother distances should be 0.5");
        
      }

      ndif=0;
      for (j=0;j<NCHAR;j++) 
        if (sequence[tree.t[mother].ldau][j] != sequence[tree.t[mother].rdau][j]) ndif++;
//      System.out.printf(double(ndif)/2.0);

      localflag = abs(tree.t[tree.t[mother].ldau].modist - 0.5*ndif) > sbeps;
      globalflag = globalflag || localflag;
      if (localflag) {
        fail("*** Phylo_clc: Hamming distance between two daughters is not twice the mother distance");
        
      }
    }

//    System.out.println("Tree:");
//    print_tree(tree,tree.root);

//    System.out.println("Check expected order of leafs on tree:");
    Check_tree ct = new Check_tree(tree);
    ct.test(tree.root);

    localflag = ct.flag;
    globalflag = globalflag || localflag;
    if (localflag) {
      fail("*** Phylo_clc: Leaves of the tree were not encountered in the expected order");
      
    }

    if (globalflag) System.out.println("Failed\n");
    else System.out.println("Passed\n");

  

  class Check_tree {
    int i;
    boolean flag;
    Phylo_clc tree;

    Check_tree(Phylo_clc ttree) {
      tree = ttree;
      i=0;
      flag = false;
    }
    void test(int node) {
      if (tree.t[node].ldau != -1) {
        test(tree.t[node].ldau);
      } else { 
        flag = flag || (node != i++);
        return;
      }

      if (tree.t[node].rdau != -1) {
        test(tree.t[node].rdau);
      } else {
        flag = flag || (node != i++);
        return;
      }
    }
  }



```

## Support Vector Machines ##

```

import com.nr.ci.Svm
import com.nr.ci.Svmgausskernel
import com.nr.ci.Svmlinkernel
import com.nr.ci.Svmpolykernel
import com.nr.ran.Normaldev
import com.nr.ran.Ran

    M=1000; N=2;
    omega=1.3
    
    x=new double[2]
    y=new double[M]
    data=new double[M][N]
    globalflag=false

    

    // Test Svm
    println("Testing Svm")

    // Create two disjoint sets of points
    myran=new Ran(17)
    for (i=0;i<M/2;i++) {
      y[i]=1.0;
      a=myran.doub();
      b=2.0*myran.doub()-1.0;
      data[i][0]=1.0+(a-b);
      data[i][1]=1.0+(a+b);
    }

    for (int i=M/2;i<M;i++) {
      y[i]=-1.0;
      a=myran.doub();
      b=2.0*myran.doub()-1.0;
      data[i][0]=-1.0-(a-b);
      data[i][1]=-1.0-(a+b);
    }
    
    // Linear kernel
    linkernel=new Svmlinkernel(data,y)
    linsvm=new Svm(linkernel)
    lambda=10
    k=0
    while (true)  {
      test=linsvm.relax(lambda,omega)
      k++
     if (test < 1.0e-3 || k >  100) break;
    } 
   
   nerror=0
    for (i=0;i<M;i++) {
      nerror += ((y[i]==1.0) != (linsvm.predict(i) >= 0.0) ? 1 : 0);
    }
  printf("Errors: %d\n", nerror);

    // Polynomial kernel
    polykernel=new Svmpolykernel(data,y,1.0,1.0,2.0)
    polysvm=new Svm(polykernel)
    lambda=10
    k=0
    while (true)  {
      test=polysvm.relax(lambda,omega)
      k++
    if (test < 1.0e-3 || k >  100) break;
     } 
     
    nerror=0;
    for (i=0;i<M;i++) {
      nerror += ((y[i]==1.0) != (polysvm.predict(i) >= 0.0) ? 1 : 0);
    }
    printf("Errors: %d\n", nerror)

    // Gaussian kernel
    gausskernel=new Svmgausskernel(data,y,1.0)
    gausssvm=new Svm(gausskernel)
    lambda=10
    k=0
    while (true) {
      test=gausssvm.relax(lambda,omega)
      k++
      if (test < 1.0e-3 || k >  100) break;
     
    }
    nerror=0
    for (i=0;i<M;i++) {
      nerror += ((y[i]==1.0) != (gausssvm.predict(i) >= 0.0) ? 1 : 0)
    }
    printf("Errors: %d\n", nerror);

    
    // Need to add tests for harder test case and resolve issue that the two
    // support vectors give an erroneous indication for two of the kernels above

    // Example similar to the book
    ndev=new Normaldev(0.0,0.5,17)
    for (j=0;j<4;j++) {   // Four quadrants
      for (int i=0;i<(int)(M/4);i++) {
        k=(int)((M/4)*j+i)
        if (j == 0) {
          y[k]=1.0;
          data[k][0]=1.0+ndev.dev();
          data[k][1]=1.0+ndev.dev();
        } else if (j == 1) {
          y[k]=-1.0;
          data[k][0]=-1.0+ndev.dev();
          data[k][1]=1.0+ndev.dev();
        } else if (j == 2) {
          y[k]=1.0;
          data[k][0]=-1.0+ndev.dev();
          data[k][1]=-1.0+ndev.dev();
        } else {
          y[k]=-1.0;
          data[k][0]=1.0+ndev.dev();
          data[k][1]=-1.0+ndev.dev();
        }
      }
    }
        
    // Linear kernel
    linkernel2=new Svmlinkernel(data,y)
    linsvm2=new Svm(linkernel2)
    printf("Errors: ")
    for (lambda=0.001;lambda<10000;lambda *= 10) {
      k=0;
     while (true)  {
        test=linsvm2.relax(lambda,omega);
        k++;
        if (test < 1.0e-3 || k >  100) break;
      }
      
      nerror=0;
      for (i=0;i<M;i++) {
        nerror += ((y[i]==1.0) != (linsvm2.predict(i) >= 0.0) ? 1 : 0);
      }
      printf("%d ",nerror)
      // Test new data
      nerror=0;
      for (j=0;j<4;j++) {   // Four quadrants
        for (i=0;i<M/4;i++) {
          if (j == 0) {
            yy=1.0
            x[0]=1.0+ndev.dev()
            x[1]=1.0+ndev.dev()
          } else if (j == 1) {
            yy=-1.0;
            x[0]=-1.0+ndev.dev()
            x[1]=1.0+ndev.dev()
          } else if (j == 2) {
            yy=1.0;
            x[0]=-1.0+ndev.dev()
            x[1]=-1.0+ndev.dev()
          } else {
            yy=-1.0
            x[0]=1.0+ndev.dev()
            x[1]=-1.0+ndev.dev()
          }
          nerror += ((yy==1.0) != (linsvm2.predict(x) >= 0.0) ? 1 : 0)
        }
      }
      printf("%d    ",nerror)
    }
    println();

    // Polynomial kernel
    polykernel2 = new Svmpolykernel(data,y,1.0,1.0,4.0)
    polysvm2=new Svm(polykernel2)
    printf("Errors: ");
    for (lambda=0.001;lambda<10000;lambda *= 10) {
      k=0
      while (true) {
        test=polysvm2.relax(lambda,omega)
        k++
        if (test < 1.0e-3 || k >  100) break;
      } 
      // Test training set
      nerror=0
      for (i=0;i<M;i++) {
        nerror += ((y[i]==1.0) != (polysvm2.predict(i) >= 0.0) ? 1 : 0)
      }
      printf("%d ",nerror)
      // Test new data
      nerror=0;
      for (j=0;j<4;j++) {   // Four quadrants
        for (i=0;i<M/4;i++) {
          if (j == 0) {
            yy=1.0;
            x[0]=1.0+ndev.dev()
            x[1]=1.0+ndev.dev()
          } else if (j == 1) {
            yy=-1.0;
            x[0]=-1.0+ndev.dev()
            x[1]=1.0+ndev.dev()
          } else if (j == 2) {
            yy=1.0;
            x[0]=-1.0+ndev.dev()
            x[1]=-1.0+ndev.dev()
          } else {
            yy=-1.0;
            x[0]=1.0+ndev.dev()
            x[1]=-1.0+ndev.dev()
          }
          nerror += ((yy==1.0) != (polysvm2.predict(x) >= 0.0) ? 1 : 0)
        }
      }
      printf("%d    ",nerror)
    }
    println();

    // Gaussian kernel
    gausskernel2=new Svmgausskernel(data,y,1.0)
    gausssvm2=new Svm(gausskernel2)
    printf("Errors: ")
    for (lambda=0.001;lambda<10000;lambda *= 10) {
      k=0
      while (true)  {
        test=gausssvm2.relax(lambda,omega)
        k++
        if (test < 1.0e-3 || k >  100) break;
      }
      nerror=0
      for (i=0;i<M;i++) {
        nerror += ((y[i]==1.0) != (gausssvm2.predict(i) >= 0.0) ? 1 : 0)
      }
      printf("%d ",nerror)
      // Test new data
      nerror=0
      for (j=0;j<4;j++) {   // Four quadrants
        for (i=0;i<M/4;i++) {
          if (j == 0) {
            yy=1.0
            x[0]=1.0+ndev.dev()
            x[1]=1.0+ndev.dev()
          } else if (j == 1) {
            yy=-1.0;
            x[0]=-1.0+ndev.dev()
            x[1]=1.0+ndev.dev()
          } else if (j == 2) {
            yy=1.0;
            x[0]=-1.0+ndev.dev()
            x[1]=-1.0+ndev.dev()
          } else {
            yy=-1.0
            x[0]=1.0+ndev.dev()
            x[1]=-1.0+ndev.dev()
          }
          nerror += ((yy==1.0) != (gausssvm2.predict(x) >= 0.0) ? 1 : 0)
        }
      }
      printf("%d    ",nerror)
    }
    println()

  // Test the algorithm on test data after learning
  // Do a scan over lambda to find best value

    localflag = false;
    globalflag = globalflag || localflag;
    if (localflag) {
      fail("*** Svm: *************************");
      
    }

    if (globalflag) System.out.println("Failed\n");
    else System.out.println("Passed\n");
  

```