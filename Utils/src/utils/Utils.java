package utils;

import java.util.Scanner;

import container.sorting.IComparable;
import container.sorting.IComparable.EComparisonResult;

/**
 * A collection of public and static methods that provide general utility.
 * 
 * @author Lukas Gross, Lukas Reichmann
 * @version 1.0
 *
 */
public class Utils 
{
	/**
	 * A constant value that is used by the .isEqual(double, double) method
	 * when calling .isEqual(double, double, double) as the third parameter.
	 */
	public static final double EPSILON = 1.0e-8;
	
	/**
	 * The {@link Scanner} which will be used by all the .read*() methods and. 
	 * This instance is going to stay null as long as one of the aforementioned 
	 * methods has not been called yet. As soon, as one of them is called, the 
	 * {@link Scanner} will be initialized and that instance will be kept until 
	 * the end of the program.<br>
	 * This extra effort is done to minimize RAM usage, since a static attribute
	 * would stay in the memory for the entire runtime of the program, even if the 
	 * .read*() methods are never used.<br>
	 * All of this memory handling is hidden from the user.
	 */
	private static Scanner scanner = null;
	
	/**
	 * Since everything in the {@link Utils} is static, there is no need to 
	 * construct an instance.
	 */
	private Utils()
	{}
	
	/*
	 * Reads a String from the standard input and converts it to an int using 
	 * the class java.lang.Integer.
	 * 
	 * This eases error handling, since no InputMismatchExceptions can occur 
	 * anymore. In addition to that, the method call .nextLine() will always 
	 * read the entire input stream, preventing any, possibly invalid and 
	 * problematic, tokens from staying in the input stream.
	 * 
	 * This method does not throw any exceptions if the input has been invalid, 
	 * the method just recursively calls itself until a proper input has been
	 * made.
	 */
	/**
	 * Reads a line from the standard input and converts it to an int. In the 
	 * case of failure, the method calls itself recursively until a valid input 
	 * has been made. 
	 * 
	 * @param message 	A short hint that is printed before the user will be 
	 * 					asked to enter something.
	 * 
	 * @return			The converted int.
	 */
	public static int readInt(String message)
	{
		checkScanner();
		
		System.out.print(message);
		String input = scanner.nextLine();

		try
		{
			return Integer.parseInt(input);
		}
		catch (NumberFormatException e)
		{
			System.out.println("Invalid Input. Please enter a new value.");
			return readInt(message);
		}
	}
	
	/**
	 * Reads a line from the standard input and converts it to an double. In the 
	 * case of failure, the method calls itself recursively until a valid input 
	 * has been made. 
	 * 
	 * @param message 	A short hint that is printed before the user will be 
	 * 					asked to enter something.
	 * 
	 * @return			The converted double.
	 */
	public static double readDouble(String message)
	{
		checkScanner();
		
		System.out.print(message);
		String input = scanner.nextLine();

		try
		{
			return Double.parseDouble(input);
		}
		catch (NumberFormatException e)
		{
			System.out.println("Invalid Input. Please enter a new value.");
			return readDouble(message);
		}
	}

	/**
	 * Reads a line from the standard input and converts it to an long. In the 
	 * case of failure, the method calls itself recursively until a valid input 
	 * has been made. 
	 * 
	 * @param message 	A short hint that is printed before the user will be 
	 * 					asked to enter something.
	 * 
	 * @return			The converted long.
	 */
	public static long readLong(String message)
	{
		checkScanner();
		
		System.out.print(message);
		String input = scanner.nextLine();

		try
		{
			return Long.parseLong(input);
		}
		catch (NumberFormatException e)
		{
			System.out.println("Invalid Input. Please enter a new value.");
			return readLong(message);
		}
	}
	
	/**
	 * Reads a line from the standard input and returns it.
	 * 
	 * @param message 	A short hint that is printed before the user will be 
	 * 					asked to enter something.
	 * 
	 * @return			The read line.
	 */
	public static String readString(String message)
	{
		checkScanner();
		
		System.out.print(message);
		return scanner.nextLine();
	}
	
	/**
	 * Compares two double values using the isEqual(double, double, double)
	 * method. The third double ('epsilon', aka. the "margin of error") will
	 * be given the value 1.0e-8.<br>
	 * <br>
	 * See isEqual(double, double).
	 * 
	 * @param a		The first of the two values that are being compared.
	 * @param b		The second of the two values that are being compared.
	 * 
	 * @return		See isEqual(double, double).
	 */
	public static boolean isEqual(double a, double b)
	{
		return isEqual(a, b, EPSILON);
	}
	
	/**
	 * Compares two double values in a way that is suitable for the
	 * impreciseness of floating point arithmetic by actually checking if both
	 * values are very close to each other.
	 * 
	 * @param a			The first of the two values that are being compared.
	 * @param b			The second of the two values that are being compared.
	 * @param epsilon	The maximum amount by which 'a' and 'b' may differ.
	 * 
	 * @return			True, if 'a' and 'b' are "equal" (according to our
	 * 					calculation), false if not.
	 */
	public static boolean isEqual(double a, double b, double epsilon)
	{
		return isBetween(a, b - epsilon, b + epsilon);
	}
	
	/**
	 * Checks if the value of parameter 'a' is in between the values of 'b' and
	 * 'c', aka. b &lt; a &lt; c.
	 * 
	 * @param a		The value that needs to be between 'b' and 'c'
	 * @param b		The lower limit of 'a'.
	 * @param c		The upper limit of 'a'.
	 * 
	 * @return		True if condition b &lt; a &lt; c is fulfilled,
	 * 				false if not.
	 */
	public static boolean isBetween(double a, double b, double  c)
	{
		return b < a && a < c;
	}
	
	/**
	 * Asks the user a yes or no question.
	 * 
	 * @param 	msg A hint to tell the user what the question is about.
	 * @return 	The answer of the user.
	 */
	public static boolean askYesNo(String msg)
	{
		String input = readString(msg + " Y/N: ");
		
		if(input.equalsIgnoreCase("Y"))
		{
			return true;
		}
		else if(input.equalsIgnoreCase("N"))
		{
			return false;
		}
		else
		{
			System.out.println("Invalid input.");
			return askYesNo(msg);
		}
	}
	
	/**
	 * Asks the user if the stack trace of an exception should be printed or 
	 * not.
	 * 
	 * @param e The exception to be printed.
	 */
	public static void printException(Exception e)
	{
		if(askYesNo("Do you want to print the exception's stack trace?"))
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Swaps the contents of an array at two indices.
	 * 
	 * @param arr	The array with the elements to swap.
	 * @param i1 	The first index.
	 * @param i2	The second index.
	 * 
	 * @param <T>	The type of the elements in the array.
	 */
	public static <T> void swapArrayPos(T[] arr, int i1, int i2)
	{
		T temp = arr[i1];
		arr[i1] = arr[i2];
		arr[i2] = temp;
	}
	
	/**
	 * Returns weather the passed array is sorted (according to 'comparable').
	 * 
	 * @param comparable	The {@link Comparable} by which the order of the
	 * 						elements is defined.
	 * @param elements		The array to be checked.
	 * @return				True, if the array is sorted, false if not.
	 * 
	 * @param <T>			The type of the elements in the array.
	 * 
	 * @see					IComparable
	 */
	@SuppressWarnings("unchecked")
	public static <T> boolean isSorted(IComparable<T> comparable,
															T... elements)
	{
		for(int i = 1; i < elements.length; i++)
		{
			if(isBefore(elements[i - 1], elements[i], comparable))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * A wrapper for <code>{@link IComparable}.compare(...)
	 * == {@link EComparisonResult}.BEFORE</code> for quicker access.<br>
	 * Returns weather 'other' is before 'source' according to 
	 * 'comparable'.
	 * 
	 * @param source 		The source element.
	 * @param other 		The other element.
	 * @param comparable	The comparable that defines the relation of the
	 * 						elements.
	 * @return 				True, if 'other' is before 'source', false 
	 * 						if not.
	 * 
	 * @param <T>			The type of the elements in the array.
	 * 
	 * @see	IComparable
	 */
	public static <T> boolean isBefore(T source, T other,
								IComparable<T> comparable)
	{
		return comparable.compare(source, other) 
				== IComparable.EComparisonResult.BEFORE;
	}
	
	/**
	 * A wrapper for <code>{@link IComparable}.compare(...)
	 * == {@link EComparisonResult}.AFTER</code> for quicker access.<br>
	 * Returns weather 'other' is after 'source' according to 
	 * 'comparable'.
	 * 
	 * @param source 		The source element.
	 * @param other 		The other element.
	 * @param comparable	The comparable that defines the relation of the
	 * 						elements.
	 * @return 				True, if 'other' is after 'source', false
	 * 						if not.
	 * 
	 * @param <T>			The type of the elements in the array.
	 * 
	 * @see	IComparable
	 */
	public static <T> boolean isAfter(T source, T other,
								IComparable<T> comparable)
	{
		return comparable.compare(source, other) 
				== IComparable.EComparisonResult.AFTER;
	}
	
	/**
	 * A wrapper for <code>{@link IComparable}.compare(...)
	 * == {@link EComparisonResult}.EQUALS</code> for quicker access.<br>
	 * Returns weather 'other' is equal to 'source' according to 
	 * 'comparable'.
	 * 	
	 * @param source 		The source element.
	 * @param other 		The other element.
	 * @param comparable	The comparable that defines the relation of the
	 * 						elements.
	 * @return 				True, if 'other' is equal to 'source',
	 * 						false if not.
	 * 
	 * @param <T>			The type of the elements in the array.
	 * 
	 * @see	IComparable
	 */
	public static <T> boolean isEqual(T source, T other,
										IComparable<T> comparable)
	{
		return comparable.compare(source, other) 
				== IComparable.EComparisonResult.EQUALS;
	}
	
	/**
	 * Small helper method to check if the scanner has yet to  be initialized. 
	 * If yes, the scanner will be initialized; if not, nothing is going to 
	 * happen. 
	 */
 	private static void checkScanner()
	{
		if(scanner == null)
			scanner = new Scanner(System.in);
	}	
}
