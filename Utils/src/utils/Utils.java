package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import container.sorting.IComparable;
import container.sorting.IComparable.EComparisonResult;
import exception.SizeMismatchException;

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
	 * The size of an int in bytes.
	 */
	public static final int INT_SIZE_BYTE = 4;
	
	/**
	 * The amount of bits that make up a byte.
	 */
	public static final int BYTE_SIZE_BIT = 8;
	
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
			System.out.println(Messages.INVALID_INPUT_ENTER_NEW_VALUE);
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
			System.out.println(Messages.INVALID_INPUT_ENTER_NEW_VALUE);
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
			System.out.println(Messages.INVALID_INPUT_ENTER_NEW_VALUE);
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
		String input = readString(msg + " " + Messages.ASK_YES_NO_YES + "/" 
				+ Messages.ASK_YES_NO_NO + " ");
		
		if(input.equalsIgnoreCase(Messages.ASK_YES_NO_YES))
		{
			return true;
		}
		else if(input.equalsIgnoreCase(Messages.ASK_YES_NO_YES))
		{
			return false;
		}
		else
		{
			System.out.println(Messages.INVALID_INPUT);
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
	 * == {@link EComparisonResult}.EQUAL</code> for quicker access.<br>
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
				== IComparable.EComparisonResult.EQUAL;
	}
	
	/**
	 * Converts an int to a byte array with the size 4. To convert back to
	 * an int, use <code>.toInt(byte[] bytes)</code>.
	 * 
	 * @param integer	The integer to convert.
	 * @return			A byte array with the converted integer. This array
	 * 					has always the size 4.
	 */
	public static final byte[] toByteArray(int integer)
	{
		byte[] ret = new byte[INT_SIZE_BYTE];
		
		for(int i = 0; i < ret.length; i++)
		{
			ret[i] = (byte) (integer >> (BYTE_SIZE_BIT * i));
		}
		
		return ret;
	}
	
	/**
	 * Converts a byte array (with the size 4) to an int. To get such a byte
	 * array, <code>.toByteArray(int integer)</code> may be used.
	 * 
	 * @param bytes						The byte array to convert. This array's
	 * 									length must be equal to 4, or an
	 * 									exception will be thrown.
	 * @return							The converted integer.
	 * 
	 * @throws SizeMismatchException	If the size of the passed array is not
	 * 									equals 4.
	 */
	public static final int toInt(byte[] bytes)
	{
		if(bytes.length != INT_SIZE_BYTE)
			throw new SizeMismatchException(Messages.SIZE_MUST_BE_4);
		
		int ret = 0;

		final int[] MASKS = 
		{
			0xFF,		//binary: 00000000000000000000000011111111
			0xFF00,		//binary: 00000000000000001111111100000000
			0xFF0000,	//binary: 00000000111111110000000000000000
			0xFF000000	//binary: 00000000000000000000000011111111
		};
		
		for(int i = 0; i < bytes.length; i++)
		{
			ret |= (((int) bytes[i]) << (BYTE_SIZE_BIT * i)) & MASKS[i];
		}
		
		return ret;
	}
	
	/**
	 * Converts the content of the passed file into a String.
	 * 
	 * @param file 	The file to read from.
	 * @return		The content of the file.
	 * @throws FileNotFoundException	If a error occurred while reading
	 * 									from the file.
	 */
	public static final String fileToString(File file) throws FileNotFoundException
	{
		Scanner scanner = new Scanner(file);
		StringBuilder builder = new StringBuilder();
		
		while(scanner.hasNextLine())
		{
			builder.append(scanner.nextLine()).append(System.getProperty("line.separator"));
		}
		
		scanner.close();
		
		return builder.toString();
	}
	
	public static String createTable(String firstColumnTitle, String secondColumnTitle, String thirdColumnTitel,
 			String[] firstColumnContent, String[] secondColumnContent, String[] thirdColumnContent)
 	{
 		
 		int maxLength = firstColumnTitle.length();
 		
 		if(secondColumnTitle.length() > maxLength)
 			maxLength = secondColumnTitle.length();
 		if(thirdColumnTitel.length() > maxLength)
 			maxLength = thirdColumnTitel.length();
 		
 		for(String s: firstColumnContent)
 		{
 			if(s.length() > maxLength)
 				maxLength = s.length();
 		}
 			
 		for(String s: secondColumnContent)
 		{
 			if(s.length() > maxLength)
 				maxLength = s.length();
 		}
 		
 		for(String s: thirdColumnContent)
 		{
 			if(s.length() > maxLength)
 				maxLength = s.length();
 		}
 		
 		StringBuilder builder = new StringBuilder();
 		
 		builder.append(createTableHeading(firstColumnTitle, secondColumnTitle, thirdColumnTitel, maxLength));

		builder.append("\n");
 		
		int lenght = (firstColumnContent.length > secondColumnContent.length ? firstColumnContent.length : secondColumnContent.length);
		
		lenght = (lenght > thirdColumnContent.length ? lenght : thirdColumnContent.length);
		
 		for(int i = 0; i < lenght; i++)
 		{
 			String firstContent = (i < firstColumnContent.length ? firstColumnContent[i] : "");
 			String secondContent = (i < secondColumnContent.length ? secondColumnContent[i] : "");
 			String thirdContent = (i < thirdColumnContent.length ? thirdColumnContent[i] : "");
 
 			builder.append(createTableContent(firstContent, secondContent, thirdContent, maxLength));
 		}
 		
 		return builder.toString();
 	}
 	
 	private static String createTableHeading(String firstColumn, String secondColumn, String thirdColumn, int maxLength)
 	{
 		StringBuilder sb = new StringBuilder();
 		sb.append(String.format("%-" + maxLength + "s | ", firstColumn));
 		sb.append(String.format("%-" + maxLength + "s | ", secondColumn)).append(thirdColumn).append("\n");
 		for(int i = 0; i < maxLength; i++)
 		{
 			sb.append("---");
 		}
		sb.append("------");
 		
 		return sb.toString();
 	}
 	
 	private static String createTableContent(String firstColumn, String secondColumn, String thirdColumn, int maxLength)
 	{
 		StringBuilder sb = new StringBuilder();
 		sb.append(String.format("%-" + maxLength + "s | ", firstColumn));
 		sb.append(String.format("%-" + maxLength + "s | ", secondColumn)).append(thirdColumn).append("\n");
 		return sb.toString();
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
