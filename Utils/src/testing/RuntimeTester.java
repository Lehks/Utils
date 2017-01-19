package testing;

/**
 * A class that is made to test the runtime of a procedure. After passing a 
 * {@link RuntimeTesterRunnable} to an instance, the class will test the 
 * runtime of the procedure (later called test-Runnable). Also, the procedure 
 * can be executed multiple times to calculate an average.<br>
 * Additionally, it is possible to set another {@link RuntimeTesterRunnable}, 
 * that will be called <b>each</b> time before calling the test-Runnable 
 * (&#8594; if the repetitions are set to 10, it will be called 10 times)
 * (later called init-Runnable). This {@link RuntimeTesterRunnable} does never 
 * change the result of the test. The init phase can be disabled by setting the 
 * runnable to null.<br> 
 * <br>
 * To quickly use a {@link RuntimeTester}, use:<br>
 * <pre><code>
 * System.out.println(new {@link RuntimeTester}(new RuntimeTesterRunnable()
 * {
 *	{@literal @}Override
 *	public void run()
 *	{
 *		//Code to test
 *	}
 * }).start().toString());
 * </code></pre>
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see 	TestResult
 *
 */
public class RuntimeTester
{
	/**
	 * The {@link RuntimeTesterRunnable} containing the code to test.
	 */
	private RuntimeTesterRunnable runnableTest 	= null;
	
	/**
	 * The {@link RuntimeTesterRunnable} that will be executed every time
	 * before the runtime testing begins.
	 */
	private RuntimeTesterRunnable runnableInit	= null;
	
	/**
	 * Stores how often the {@link RuntimeTesterRunnable} will be executed.
	 */
	private int repetitions 					= 0;
	
	/**
	 * The result of the test.
	 * 
	 * @see TestResult
	 */
	private TestResult result					= null;
	
	/**
	 * An array of elements set by the user. This might be used during testing
	 * (Usually initialized during the init-Runnable).
	 */
	private Object[] customElements				= null;
	
	/**
	 * Constructs a new instance and initializes all three attributes with the
	 * passed parameters.
	 * 
	 * @param runnableInit	A runnable that will be called before the runtime 
	 * 						tests will start.
	 * @param runnableTest	The runnable that will be tested on its runtime.
	 * @param repetitions	How often 'runnableTest' will be called. If this is 
	 * 						&gt; 1, an average can be calculated.
	 */
	public RuntimeTester(RuntimeTesterRunnable runnableInit, 
			RuntimeTesterRunnable runnableTest, int repetitions)
	{
		if(repetitions <= 0)
			throw new IllegalArgumentException
										("Must repeat at least one time.");
		
		if(runnableTest == null)
			throw new IllegalArgumentException("Runnable to test is null.");
		
		setRunnableInit(runnableInit);
		setRunnable(runnableTest);
		setRepetitions(repetitions);
	}
	
	/**
	 * Constructs a new instance and initializes the 
	 * {@link RuntimeTesterRunnable} to test and the amount of repetitions with 
	 * the passed parameters. The test-Runnable be set to null and therefore 
	 * not executed when <code>.start() </code> is called.
	 * 
	 * @param runnableTest	The {@link RuntimeTesterRunnable} to execute.
	 * @param repetitions	The amount of repetitions.
	 */
	public RuntimeTester(RuntimeTesterRunnable runnableTest, int repetitions)
	{
		this(null, runnableTest, repetitions);
	}

	/**
	 * Constructs a new instance and initializes the 
	 * {@link RuntimeTesterRunnable} to execute with the passed parameter and 
	 * the amount of repetitions with 1.
	 * 
	 * @param runnable		The {@link RuntimeTesterRunnable} to execute.
	 */
	public RuntimeTester(RuntimeTesterRunnable runnable)
	{
		this(runnable, 1);
	}
	
	/**
	 * Starts the test. This method can be called multiple times to repeat
	 * the test.<br>
	 * Note: <code>.getResult()</code> will always only return the result of
	 * the last test.
	 * 
	 * @return The result of the test.
	 * 
	 * @see TestResult
	 */
	public TestResult start()
	{
		long milliTotal = 0;
		long nanoTotal = 0;
		
		for(int i = 0; i < repetitions; i++)
		{
			if(runnableInit != null)
			{
				runnableInit.run(this);
			}
			
			long milliStart = System.currentTimeMillis();
			long nanoStart = System.nanoTime();
			
			runnableTest.run(this);
			
			milliTotal += System.currentTimeMillis() - milliStart;
			nanoTotal += System.nanoTime() - nanoStart;
		}
		
		result = new TestResult(milliTotal, nanoTotal, 
								repetitions);
		return result;
	}
	
	/**
	 * Returns the init-Runnable.
	 * 
	 * @return The init-Runnable.
	 */
	public RuntimeTesterRunnable getRunnableInit()
	{
		return runnableInit;
	}

	/**
	 * Sets a new init-Runnable. Set this to null to prevent the 
	 * {@link RuntimeTesterRunnable} form being called.
	 * 
	 * @param runnableInit	The new init-Runnable.
	 */
	public void setRunnableInit(RuntimeTesterRunnable runnableInit)
	{
		this.runnableInit = runnableInit;
	}

	/**
	 * Returns the test-Runnable.
	 * 
	 * @return The test-Runnable.
	 */
	public RuntimeTesterRunnable getRunnable()
	{
		return runnableTest;
	}

	/**
	 * Sets a new test-Runnable.
	 * 
	 * @param runnable The new test-Runnable.
	 */
	public void setRunnable(RuntimeTesterRunnable runnable)
	{
		this.runnableTest = runnable;
	}

	/**
	 * Returns the amount of repetitions.
	 * 
	 * @return The repetitions.
	 */
	public int getRepetitions()
	{
		return repetitions;
	}

	/**
	 * Sets the amount of repetitions.
	 * 
	 * @param repetitions The new amount of repetitions.
	 */
	public void setRepetitions(int repetitions)
	{
		this.repetitions = repetitions;
	}

	/**
	 * Returns the result of the {@link TestResult} of the last test.
	 * 
	 * @return The test result.
	 */
	public TestResult getResult()
	{
		return result;
	}

	/**
	 * Returns the custom elements.
	 * 
	 * @return 	The custom elements.
	 */
	public Object[] getCustomElements()
	{
		return customElements;
	}

	/**
	 * Sets the custom elements.
	 * 
	 * @param customElements	The new custom elements.
	 */
	public void setCustomElements(Object[] customElements)
	{
		this.customElements = customElements;
	}

	/**
	 * A class to represent the result of a test executed by
	 * <code>.star()</code> in {@link RuntimeTester}.<br>
	 * This class contains informations about the total and average time (in
	 * milli- and nanoseconds) and how often the test was executed. The latter
	 * is the same as <code>.setRepetitions</code> from {@link RuntimeTester}
	 * would return, if the amount of repetitions has not been changed since
	 * the last time <code>.start()</code> was called.
	 * 
	 * @author 	Lukas Reichmann
	 * @version	1.0
	 * @see		RuntimeTester
	 * 
	 */
	public class TestResult
	{
		/**
		 * The total time in milliseconds.
		 */
		long milliTime 	= 0;
		
		/**
		 * The total time in nanoseconds.
		 */
		long nanoTime 	= 0;
		
		/**
		 * The amount of repetitions that the test was run.
		 */
		int repetitions = 0;
		
		/**
		 * Constructs a new instance.
		 * 
		 * @param milliTime		The total time in milliseconds.
		 * @param nanoTime		The total time in nanoseconds.
		 * @param repetitions	The amount of repetitions.
		 */
		public TestResult(long milliTime, long nanoTime, int repetitions)
		{
			this.milliTime = milliTime;
			this.nanoTime = nanoTime;
			this.repetitions = repetitions;
		}
		
		/**
		 * Returns the total time in milliseconds.
		 *  
		 * @return The time.
		 */
		public long getMilliTime()
		{
			return milliTime;
		}
		
		/**
		 * Returns the total time in nanoseconds.
		 *  
		 * @return The time.
		 */
		public long getNanoTime()
		{
			return nanoTime;
		}

		/**
		 * Returns the average time in milliseconds.
		 *  
		 * @return The time.
		 */
		public long getAverageMilliTime()
		{
			return milliTime / repetitions;
		}

		/**
		 * Returns the average time in nanoseconds.
		 *  
		 * @return The time.
		 */
		public long getAverageNanoTime()
		{
			return nanoTime / repetitions;
		}

		/**
		 * Returns how often the test was executed.
		 *  
		 * @return The amount of repetitions.
		 */
		public int getRepetitions()
		{
			return repetitions;
		}
		
		/**
		 * Converts the result into a human readable table.
		 */
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();

			sb.append(String.format
					("      | %9s | %12s\n", "Millisecs", "Nanosecs"));
			sb.append(String.format
					("%032d\n", 0).replace('0', '-'));
			sb.append(String.format
					("%5s | %9d | %12d\n", "Abs.:", milliTime, nanoTime));
			sb.append(String.format
					("%5s | %9d | %12d\n", "Avg.:", getAverageMilliTime(),
							getAverageNanoTime()));
			sb.append("\n");
			sb.append("Repetitions: ").append(repetitions);
			
			return sb.toString();
		}
	}
	
	/**
	 * A interface that behaves similar to link java.lang.Runnable, but the
	 * <code>.run(...)</code> method of this one has a Parameter that holds
	 * a reference to the {@link RuntimeTester} that called the method.
	 * 
	 * @author Lukas Reichmann
	 *
	 */
	public interface RuntimeTesterRunnable
	{
		/**
		 * The method that the interface is about.
		 * 
		 * @param rt	The {@link RuntimeTester} that the method was called 
		 * 				by.
		 */
		public void run(RuntimeTester rt);
	}
}
