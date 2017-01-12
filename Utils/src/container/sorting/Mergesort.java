package container.sorting;

import utils.Utils;

/**
 * An implementation of the MergeSort algorithm using the 
 * {@link ISortingAlgorithm} interface.
 * 
 * @author 	Lukas Gross, Lukas Reichmann
 * @version	1.0
 * @see		ISortingAlgorithm
 *
 * @param <T>	The type of the elements to be sorted.
 */
public final class Mergesort<T> implements ISortingAlgorithm<T>
{
	/**
	 * A reference to the array that is given to 
	 * <code>.sort(...)</code> (used in <code>.merge(...)</code>).
	 */
	private T[] elements = null;
	
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
		this.elements = elements;
		sortRec(comparable, elements);
		
		return elements;
	}

	/**
	 * Called by <code>.sort(...)</code>. This is used to switch from
	 * type T to Object.
	 * 
	 * @param comparable	The comparable after which to sort.
	 * @param elements		The array to sort.
	 * @return				The sorted array.
	 */
	@SuppressWarnings("unchecked")
	private Object[] sortRec(IComparable<T> comparable, Object[] elements)
	{
		if(elements.length == 2)
		{
			if(Utils.isBefore((T) elements[0], (T) elements[1], comparable))
			{
				Object temp = elements[0];
				elements[0] = elements[1];
				elements[1] = temp;
			}
			
			return elements;
		}
		
		if(elements.length <= 1)
		{
			return elements;
		}
		
		int m = elements.length / 2;
		
		//Copy left side
		Object[] left = new Object[m];

		System.arraycopy(elements, 0, left, 0, left.length);
		
		//Copy right side
		Object[] right = new Object[elements.length - m];
		
		System.arraycopy(elements, m, right, 0, right.length);
		
		//sort left and right
		Object[] left_sorted = sortRec(comparable, left);
		Object[] right_sorted = sortRec(comparable, right);
		
		//mix both sides
		Object[] elements_sorted = merge(comparable, left_sorted, right_sorted);
		
		return elements_sorted;
	}
	
	/**
	 * Merges two sorted arrays.
	 * 
	 * @param comparable	The comparable after which to sort.
	 * @param a				First array to merge.
	 * @param b				Second array to merge.
	 * @return				The merged array.
	 */
	@SuppressWarnings("unchecked")
	private Object[] merge(IComparable<T> comparable, Object[] a, Object[] b)
	{
		Object[] c = null;
		
		/*
		 * If this is the last array (the last two halves are getting merged),
		 * this method copies the values directly to the array (which is an T[]
		 * and can therefore be returned without any casting related problems)
		 */
		if(a.length + b.length == elements.length)
		{
			c = elements;
		}
		else
		{
			c = new Object[a.length + b.length];
		}
		
		int i_a = 0;
		int i_b = 0;
		int i_c = 0;
		
		while(i_a < a.length && i_b < b.length)
		{
			if(Utils.isBefore((T) b[i_b], (T) a[i_a], comparable))
			{
				c[i_c] = a[i_a];
				i_a++;
			}
			else
			{
				c[i_c] = b[i_b];
				i_b++;
			}
			i_c++;
		}
		
		while(i_a < a.length)
		{
			c[i_c] = a[i_a];
			i_a++;
			i_c++;
		}
		
		while(i_b < b.length)
		{
			c[i_c] = b[i_b];
			i_b++;
			i_c++;
		}
		
		return c;
	}
}
