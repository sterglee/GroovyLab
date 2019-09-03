
package gExec.Interpreter;

// keeps the most recent threads in order to be able to cancel tasks

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PendingThreads {
  static public final int  maxThreadsToKeep =50;
    // we keep the most recently executed maxThreadsToKeep
  static public Thread []  recentThreads = new Thread[maxThreadsToKeep];
  static public int insertCursor = 0;  // cursor that points where to insert a new thread. 
  static public int nthreads = 16;
  static public ExecutorService pool = Executors.newFixedThreadPool(nthreads);
  // add a new thread
  static public void addThread(Thread thread) {
      recentThreads[insertCursor] = thread;
      insertCursor++; // increment current insert location
      if (insertCursor == maxThreadsToKeep)
          insertCursor = 0;
  }
  
  // cancel all pending threads
  static public void cancelPendingThreads() {
      int cntThreadsCanceled = 0;
      for (int i=0; i<maxThreadsToKeep; i++)
          if (recentThreads[i] != null) {
              Thread   examinedThread = recentThreads[i];
              //System.out.println("thread["+i+"] state = "+examinedThread.getState().toString());
              if (examinedThread.getState() != Thread.State.TERMINATED) 
              {
                  examinedThread.stop();  // cancel the thread
                 cntThreadsCanceled++;
          }
              recentThreads[i] = null;
              
          }
      
      System.out.println("\nCancelled "+ cntThreadsCanceled+"  threads");
  }
  
}
