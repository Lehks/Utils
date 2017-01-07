package container.sorting;

import container.sorting.IComparable.EComparisonResult;
import utils.Utils;

/**
 * An implementation of the BubbleSort algorithm using the 
 * {@link ISortingAlgorithm} interface.
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see		ISortingAlgorithm
 *
 * @param <T>	The type of the elements to be sorted.
 */
public final class Bubblesort<T> implements ISortingAlgorithm<T>
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
		return new ISortingAlgorithm<T>()
		{
			@Override
			public T[] sort(IComparable<T> comparable, T... elements)
			{
				for (int i = 1; i < elements.length; i++)
				{
					for (int j = i; j > 0 && comparable.compare(elements[j - 1], 
							elements[j]) == EComparisonResult.BEFORE; j--)
					{
						Utils.swapArrayPos(elements, j - 1, j);
					}
				}

				return elements;
			}
		}.sort(comparable, elements);
	}
}
