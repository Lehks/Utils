package utils;

import exception.SizeMismatchException;

public class Messages
{
	private Messages() {}
	
/* STORAGE FILE [-PARSER, -LOADER] */
	
	/**
	 * The message that is being printed, if the file that is passed to 
	 * a constructor is actually a directory.
	 */
	public static final String MSG_FILE_IS_DIRECTORY = "The passed file is a "
														+ "directory.";
	
	public static final String ILLEGAL_DEPTH 		= "Illegal depth of %d. "
									+ "Maximum legal depth in this case is %d";
	
	public static final String STREAM_CLOSED = "Stream is closed.";
	
/* UTILS */
	
	public static final String INVALID_INPUT = "Invalid Input. ";
	
	public static final String INVALID_INPUT_ENTER_NEW_VALUE = 
									INVALID_INPUT + " Please enter a new value.";
	
	public static final String ASK_YES_NO_YES 	= "Y";
	public static final String ASK_YES_NO_NO 	= "N";
	
	public static final String SIZE_MUST_BE_4 = "Size must be 4.";
	
/* SORTED ARRAY */
	/**
	 * The message that is being given to a ArrayIndexOutOfBoundsException if
	 * the array is full.
	 */
	public static final String MSG_FULL = "Array is full.";
	
	/**
	 * The message that is being given to a {@link SizeMismatchException} if
	 * the size and amount of elements does not fit when using the constructors 
	 * <code>SortedArray(IComparable&lt;T&gt; comparable, int size ,
	 * SortingAlgorithm algorithm, T... elements)</code> and <code>
	 * SortedArray(IComparable&lt;T&gt; comparable, int size ,
	 * ISortingAlgorithm&lt;T&gt; algorithm, T... elements)</code>
	 */
	public static final String MSG_SIZE_MISMATCH = "Passed size is: %d, "
											+ "but amount of elements is: %d";
	
	/**
	 * The message that is being given to a {@link SizeMismatchException} 
	 * thrown by SortedArray(int, IComparable) when the maximum size is
	 * &lt; 0.
	 */
	public static final String MSG_SIZE_SMALLER_ZERO 
													= "Size must not be < 0.";
	
}
