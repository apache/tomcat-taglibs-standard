/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 

package org.apache.taglibs.standard.lang.spel.test;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import org.apache.taglibs.standard.lang.spel.Evaluator;
import org.apache.taglibs.standard.lang.spel.ParsedExpression;
import org.apache.taglibs.standard.lang.spel.SpelException;

/**
 *
 * <p>This evaluates a single expression many times to gauge the
 * performance characteristics of the evaluator.  This can also be
 * performed from multiple threads to see if there are any
 * synchronization issues.
 *
 * <p>The expression evaluated is "session:bean1a.bean1.int1 < 24".
 *
 * <p>The test can also be run using pure API calls to do the above
 * functionality, which should show how much slower it is to use the
 * expression language to perform the function.
 * 
 * @author Nathan Abramson - Art Technology Group
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author$
 **/

public class PerformanceTest
{
  //-------------------------------------
  // Inner classes
  //-------------------------------------

  static class Runner
    implements Runnable
  {
    int mIterations;
    Evaluator mEvaluator;
    PageContext mPageContext;
    PerformanceTest mTest;
    boolean mUseEvaluator;

    public Runner (int pIterations,
		   Evaluator pEvaluator,
		   PageContext pPageContext,
		   PerformanceTest pTest,
		   boolean pUseEvaluator)
    {
      mIterations = pIterations;
      mEvaluator = pEvaluator;
      mPageContext = pPageContext;
      mTest = pTest;
      mUseEvaluator = pUseEvaluator;
    }

    public void run ()
    {
      mTest.incrementRunningThreadCount ();
      mTest.waitForStartRunning ();

      // Use the evaluator
      if (mUseEvaluator) {
	for (int i = 0; i < mIterations; i++) {
	  try {
	    Object ret =
	      mEvaluator.evaluate ("session:bean1a.bean1.int1 < 24",
				   mPageContext,
				   Boolean.class);
	  }
	  catch (JspException exc) {
	    exc.printStackTrace ();
	  }
	}
      }

      // Use direct API calls
      else {
	for (int i = 0; i < mIterations; i++) {
	  Bean1 b1 = (Bean1) 
	    mPageContext.getAttribute ("bean1a", mPageContext.SESSION_SCOPE);
	  Bean1 b2 = b1.getBean1 ();
	  int val = b2.getInt1 ();
	  boolean b = val < 24;
	  Boolean bobj = b ? Boolean.TRUE : Boolean.FALSE;
	}
      }

      mTest.decrementRunningThreadCount ();
    }
  }

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // Member variables
  //-------------------------------------

  boolean mStartRunning;
  int mRunningThreadCount;

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  public PerformanceTest ()
  {
  }

  //-------------------------------------
  /**
   *
   * Runs the tests
   **/
  public void runTests (int pThreadCount,
			int pIterationCount)
  {
    runTests (pThreadCount,
	      pIterationCount,
	      true);
    runTests (pThreadCount,
	      pIterationCount,
	      false);
  }

  //-------------------------------------
  /**
   *
   * Runs the tests
   **/
  public void runTests (int pThreadCount,
			int pIterationCount,
			boolean pUseEvaluator)
  {
    Evaluator e = new Evaluator ();
    PageContext p = createTestContext ();

    // Start the threads
    for (int i = 0; i < pThreadCount; i++) {
      Runner r = new Runner (pIterationCount,
			     e,
			     p,
			     this,
			     pUseEvaluator);
      new Thread (r).start ();
    }

    // Wait for all the threads to check in
    waitForThreadCount (pThreadCount);

    long startTime = System.currentTimeMillis ();

    // Start the threads
    startRunning ();

    // Wait for all the threads to finish
    waitForThreadCount (0);
    mStartRunning = false;

    int iterations = pThreadCount * pIterationCount;
    long elapsed = System.currentTimeMillis () - startTime;
    double rate = ((double) iterations / (double) elapsed) * 1000.0;

    System.out.println ("Running " +
			(pUseEvaluator ? "with" : "without") +
			" the evaluator yields " + 
			iterations + 
			" iterations in " +
			((double) elapsed / 1000.0) +
			" seconds (" +
			rate +
			" iters/second)");
  }

  //-------------------------------------
  /**
   *
   * Waits for the startRunning flag to be set
   **/
  public synchronized void waitForStartRunning ()
  {
    while (!mStartRunning) {
      try {
	wait ();
      }
      catch (InterruptedException exc) {}
    }
  }

  //-------------------------------------
  /**
   *
   * Start the threads running
   **/
  public synchronized void startRunning ()
  {
    mStartRunning = true;
    notifyAll ();
  }

  //-------------------------------------
  /**
   *
   * Called by the thread to indicate that it is ready to start running
   **/
  public synchronized void incrementRunningThreadCount ()
  {
    mRunningThreadCount++;
    notifyAll ();
  }

  //-------------------------------------
  /**
   *
   * Called by the thread to indicate that it is done running
   **/
  public synchronized void decrementRunningThreadCount ()
  {
    mRunningThreadCount--;
    notifyAll ();
  }

  //-------------------------------------
  /**
   *
   * Waits for the thread count to reach the specified number
   **/
  public synchronized void waitForThreadCount (int pCount)
  {
    while (mRunningThreadCount != pCount) {
      try {
	wait ();
      }
      catch (InterruptedException exc) {}
    }
  }

  //-------------------------------------
  // Test data
  //-------------------------------------
  /**
   *
   * Creates and returns the test PageContext that will be used for
   * the tests.
   **/
  static PageContext createTestContext ()
  {
    PageContext ret = new PageContextImpl ();

    // Create a bean
    {
      Bean1 b1 = new Bean1 ();
      Bean1 b2 = new Bean1 ();

      b1.setBean1 (b2);
      b2.setInt1 (14);

      ret.setAttribute ("bean1a", b1, ret.SESSION_SCOPE);
    }

    return ret;
  }

  //-------------------------------------
  // Main method
  //-------------------------------------
  /**
   *
   * Runs the evaluation test
   **/
  public static void main (String [] pArgs)
  {
    if (pArgs.length != 2) {
      usage ();
      System.exit (1);
    }

    int threadCount = Integer.valueOf (pArgs [0]).intValue ();
    int iterationCount = Integer.valueOf (pArgs [1]).intValue ();
    PerformanceTest test = new PerformanceTest ();
    test.runTests (threadCount, iterationCount);
  }

  //-------------------------------------
  static void usage ()
  {
    System.err.println ("usage: java org.apache.taglibs.standard.lang.spel.test.PerformanceTest {thread count} {#iterations}");
  }

  //-------------------------------------

}
