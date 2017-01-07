package container.sorting;

import utils.Utils;

/**
 * An implementation of the InsertionSort algorithm using the 
 * {@link ISortingAlgorithm} interface.
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see		ISortingAlgorithm
 *
 * @param <T>	The type of the elements to be sorted.
 */
public final class InsertionSort<T> implements ISortingAlgorithm<T>
{
	/**
	 * The method that represents the algorithm.
	 * 
	 * @param comparable	The comparable after which to sort.
	 * @param elements		The array to sort.
	 * @return				The sorted array.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T[] sort(IComparable<T> comparable, T... elements)
	{
		for(int i = 1; i < elements.length; i++)
		{
			T temp = elements[i];
			int j;
			
			for(j = i ;j > 0 && 
					Utils.isAfter(temp, elements[j - 1], comparable); j--)
			{
				elements[j] = elements[j - 1];
			}
			
			elements[j] = temp;
		}
		
		return elements;
	}
}
