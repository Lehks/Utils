package exception;

import container.SortedArray;

/**
 * An exception thrown by <code>{@link SortedArray}.SortedArray
 * (IComparable&lt;T&gt; comparable, int size , ISortingAlgorithm&lt;T&gt; 
 * algorithm, T... elements)</code> and <code>{@link SortedArray}
 * .SortedArray(IComparable&lt;T&gt; comparable, int size, 
 * ISortingAlgorithm&lt;T&gt; algorithm, T... elements)</code>. 
 * See the doc of those constructors for further details, when the exception 
 * is thrown.
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see		SortedArray
 *
 */
public class SizeMismatchException extends RuntimeException
{
	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -4441916856750922380L;
	
	/**
	 * Constructs a new SizeMismatchException with the specified message.
	 * 
	 * @param msg	The message to be printed with the exception.
	 */
	public SizeMismatchException(String msg)
	{
		super(msg);
	}
}
