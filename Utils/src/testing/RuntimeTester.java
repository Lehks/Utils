package testing;

/**
 * A class that is made to test the runtime of a procedure. After passing a 
 * {@link Runnable} to an instance, the class will test the runtime of the
 * procedure. Also, the procedure can be executed multiple times to calculate 
 * an average.<br>
 * <br>
 * To quickly use a {@link RuntimeTester}, use:<br>
 * <pre><code>
 * System.out.println(new {@link RuntimeTester}(new {@link Runnable}()
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
	 * The {@link Runnable} to execute.
	 */
	private Runnable runnable 	= null;
	
	/**
	 * Stores how often the {@link Runnable} will be executed.
	 */
	private int repetitions 	= 0;
	
	/**
	 * The result of the test.
	 * 
	 * @see TestResult
	 */
	private TestResult result	= null;
	
	/**
	 * Constructs a new instance and initializes the {@link Runnable} to
	 * execute and the amount of repetitions with the passed parameters.
	 * 
	 * @param runnable		The {@link Runnable} to execute.
	 * @param repetitions	The amount of repetitions.
	 */
	public RuntimeTester(Runnable runnable, int repetitions)
	{
		if(repetitions <= 0)
			throw new IllegalArgumentException
										("Must repeat at least one time.");
		
		if(runnable == null)
			throw new IllegalArgumentException("Runnable is null.");
		
		this.runnable = runnable;
		this.repetitions =repetitions;
	}

	/**
	 * Constructs a new instance and initializes the {@link Runnable} to
	 * execute with the passed parameter and the amount of repetitions with 0.
	 * 
	 * @param runnable		The {@link Runnable} to execute.
	 */
	public RuntimeTester(Runnable runnable)
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
		long milliStart = System.currentTimeMillis();
		long nanoStart = System.nanoTime();
		
		for(int i = 0; i < repetitions; i++)
			runnable.run();
		
		long nanoEnd = System.nanoTime();
		long milliEnd = System.currentTimeMillis();
		
		result = new TestResult(milliEnd - milliStart, nanoEnd - nanoStart, 
								repetitions);
		return result;
	}
	
	/**
	 * Returns the {@link Runnable} that will be executed.
	 * 
	 * @return The runnable.
	 */
	public Runnable getRunnable()
	{
		return runnable;
	}

	/**
	 * Sets a new {@link Runnable}.
	 * 
	 * @param runnable The new runnable.
	 */
	public void setRunnable(Runnable runnable)
	{
		this.runnable = runnable;
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

			sb.append(String.format("      | %9s | %12s\n", "Millisecs", "Nanosecs"));
			sb.append(String.format("%032d\n", 0).replace('0', '-'));
			sb.append(String.format("%5s | %9d | %12d\n", "Abs.:", milliTime, nanoTime));
			sb.append(String.format("%5s | %9d | %12d\n", "Avg.:", getAverageMilliTime(), getAverageNanoTime()));
			sb.append("\n");
			sb.append("Repetitions: ").append(repetitions);
			
			return sb.toString();
		}
	}
}
