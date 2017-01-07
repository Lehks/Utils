package container.sorting;

import utils.Utils;

/**
 * An implementation of the QuickSort algorithm using the 
 * {@link ISortingAlgorithm} interface.
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see		ISortingAlgorithm
 *
 * @param <T>	The type of the elements to be sorted.
 */
public final class Quicksort<T> implements ISortingAlgorithm<T>
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
		return quickSortRec(elements, 0, elements.length - 1, comparable);
	}
	
	/**
	 * A method that is used by the <code>quickSort(...)</code> method.
	 * 
	 * @param elements		The elements to sort.
	 * @param leftBorder	The left border of the sequence that is sorted.
	 * @param rightBorder	The right border of the sequence that is sorted.
	 * @param comparable	The comparable after which to sort.
	 * @return				The sorted array.
	 * 
	 * @param <T>			The type of the elements that will be sorted.
	 */
	private static <T> T[] quickSortRec(T[] elements, 
			int leftBorder, int rightBorder, IComparable<T> comparable)
	{
		if(leftBorder < rightBorder)
		{
			int divider = quickSortDivide(elements,
										leftBorder, rightBorder, comparable);
			
			quickSortRec(elements, leftBorder, divider - 1, comparable);
			quickSortRec(elements, divider + 1, rightBorder, comparable);
		}
		
		return elements;
	}

	/**
	 * A method that is used by the <code>quickSortRec(...)</code> method.
	 * 
	 * @param elements		The elements to sort.
	 * @param leftBorder	The left border of the sequence that is sorted.
	 * @param rightBorder	The right border of the sequence that is sorted.
	 * @param comparable	The comparable after which to sort.
	 * @return				The next border.
	 * 
	 * @param <T>			The type of the elements that will be sorted.
	 */
	private static <T> int quickSortDivide(T[] elements, 
			int leftBorder, int rightBorder, IComparable<T> comparable)
	{
		int i = leftBorder;
		int j = rightBorder - 1;
		
		T pivot = elements[rightBorder];
		
		do
		{
			while(!Utils.isAfter(pivot, elements[i], comparable)
					&& i < rightBorder)
				i++;

			while(!Utils.isBefore(pivot, elements[j], comparable)
					&& j > leftBorder)
				j--;
			
			if(i < j)
				Utils.swapArrayPos(elements, i, j);
		}
		while(i < j);
		
		if(Utils.isAfter(pivot, elements[i], comparable))
			Utils.swapArrayPos(elements, i, rightBorder);
		
		return i;
	}
}
