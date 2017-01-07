package container.sorting;

import container.SortedArray;

/**
 * An interface to sort an array with any sorting algorithm with the help of
 * {@link IComparable}s.
 * This is mainly used within the {@link SortedArray} class to sort with
 * custom algorithms.
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see		IComparable
 * 
 * @param <T> The type of the elements in the Array that will be sorted.
 */
public interface ISortingAlgorithm<T>
{
	/**
	 * The method that will be called to sort an array.\n
	 * Note: The method is supposed to modify the original array and also
	 * returning a reference to it (the method is not supposed to copy the
	 * entire array).
	 * 
	 * @param comparable	The comparable to compare the different elements.
	 * @param elements	The array to be sorted.
	 * @return				A reference to the array that was originally 
	 * 						passed to the method.
	 */
	@SuppressWarnings("unchecked")
	public T[] sort(IComparable<T> comparable, T... elements);
}
