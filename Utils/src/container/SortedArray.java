package container;

import java.util.Iterator;

import container.sorting.Bubblesort;
import container.sorting.IComparable;
import container.sorting.IComparable.EComparisonResult;
import container.sorting.ISortingAlgorithm;
import container.sorting.InsertionSort;
import container.sorting.Quicksort;
import exception.SizeMismatchException;
import utils.Utils;

/**
 * Represents a normal array that is always sorted according to rules defined 
 * by a {@link IComparable} (some of them for the primitive types and Strings 
 * are already predefined, they are called DEFAULT_*_COMPARABLE and 
 * INVERSE_*_COMPARABLE, whereas * is replaced with the type they can sort).\n
 * \n
 * Although the Type partially called Array, this name tells only half of the
 * truth, since it has the ability to grow / shrink in size. This is never done
 * implicitly, the user must call <code>.shrink()</code> or
 * <code>.setSize(...)</code>. This means, insert(...) will throw an exception, 
 * if the array is full and not implicitly grow.
 *
 * @author 	Lukas Reichmann
 * @version 1.0
 * @see 	IComparable
 * @see 	EComparisonResult
 *
 * @param <T> The type of the elements in the container.
 * 
 */
public class SortedArray<T> implements Iterable<T>
{
	/**
	 * The message that is being given to a ArrayIndexOutOfBoundsException if
	 * the array is full.
	 */
	protected static final String MSG_FULL = "Array is full.";
	
	/**
	 * The message that is being given to a ArrayIndexOutOfBoundsException if
	 * the size and amount of elements does not fit when using the constructors 
	 * <code>SortedArray(IComparable&lt;T&gt; comparable, int size ,
	 * SortingAlgorithm algorithm, T... elements)</code> and <code>
	 * SortedArray(IComparable&lt;T&gt; comparable, int size ,
	 * ISortingAlgorithm&lt;T&gt; algorithm, T... elements)</code>
	 */
	protected static final String MSG_SIZE_MISMATCH = "Passed size is: %d, "
											+ "but amount of elements is: %d";
	
	/**
	 * The comparable that is being used to compare two values when sorting.
	 */
	protected IComparable<T> comparable;

	/**
	 * The array that stores all the values.\n
	 * This array always remains sorted.
	 */
	protected Object[] elements;
	
	/**
	 * The amount of objects that are currently within the array.
	 */
	protected int currentSize = 0;

	/**
	 * Constructs a new {@link SortedArray}.
	 * 
	 * @param comparable 	The {@link IComparable} to compare two values when 
	 * 						sorting.
	 * @param maxSize		The maximum size of the array.
	 */
	public SortedArray(int maxSize, IComparable<T> comparable)
	{
		this.comparable = comparable;
		this.elements = new Object[maxSize];
	}
	
	/**
	 * Creates a {@link SortedArray} from an unsorted array, sorting it using a
	 * specified algorithm that implements the {@link ISortingAlgorithm}
	 * interface.\n
	 * The final container's size is determined by 'size'.
	 * 
	 * @param comparable 		The {@link IComparable} to sort the elements.
	 * @param algorithm			The algorithm used to sort.
	 * @param size				The size of the final container. Must be equal 
	 * 							or higher than the size of 'elements'.
	 * @param elements			The elements that will be part of the 
	 * 							{@link SortedArray}.
	 * 
	 * @throws 
	 * 	SizeMismatchException 	If 'size' is smaller than the amount of 
	 * 							passed elements.
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public SortedArray(IComparable<T> comparable, int size , 
			ISortingAlgorithm<T> algorithm, T... elements)
	{
		this(size, comparable);
		this.elements = checkSizeAndCopyArray(elements, size);
		
		algorithm.sort(comparable, (T[]) this.elements);
		currentSize = elements.length;
	}

	/**
	 * Creates a {@link SortedArray} from an unsorted array, sorting it using a
	 * specified algorithm that implements the {@link ISortingAlgorithm}
	 * interface.\n
	 * The final container's size will be equal to the amount of elements that
	 * were given to the constructor.
	 * 
	 * @param comparable 	The {@link IComparable} to sort the elements.
	 * @param algorithm		The algorithm used to sort.
	 * @param elements		The elements that will be part of the 
	 * 						{@link SortedArray}.
	 */
	@SafeVarargs
	public SortedArray(IComparable<T> comparable, 
			ISortingAlgorithm<T> algorithm, T... elements)
	{
		this(comparable, elements.length, algorithm, elements);
	}
	
	/**
	 * Creates a {@link SortedArray} from an unsorted array, sorting it using a
	 * specified algorithm from the {@link ESortingAlgorithm} enumeration.\n
	 * The final container's size is determined by 'size'.
	 * 
	 * @param comparable 		The {@link IComparable} to sort the elements.
	 * @param algorithm			The algorithm used to sort.
	 * @param size				The size of the final container. Must be equal 
	 * 							or higher than the size of 'elements'.
	 * @param elements			The elements that will be part of the 
	 * 							{@link SortedArray}.
	 * 
	 * @throws SizeMismatchException 	If 'size' is smaller than the amount of 
	 * 									passed elements.
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public SortedArray(IComparable<T> comparable, int size , 
			ESortingAlgorithm algorithm, T... elements)
	{
		this(size, comparable);
		this.elements = checkSizeAndCopyArray(elements, size);
		
		switch (algorithm)
		{
			case BUBBLESORT:
				this.elements = new Bubblesort<T>()
									.sort(comparable, (T[]) this.elements);
				break;
			case INSERTIONSORT:
				this.elements = new InsertionSort<T>()
									.sort(comparable, (T[]) this.elements);
				break;
			case QUICKSORT:
			default:
				this.elements = new Quicksort<T>()
									.sort(comparable, (T[]) this.elements);
				break;
		}

		currentSize = elements.length;
	}
	
	/**
	 * Creates a {@link SortedArray} from an unsorted array, sorting it using a
	 * specified algorithm from the {@link ESortingAlgorithm} enumeration.\n
	 * The final container's size will be equal to the amount of elements that
	 * were given to the constructor.
	 * 
	 * @param comparable 	The {@link IComparable} to sort the elements.
	 * @param algo			The algorithm used to sort.
	 * @param elements		The elements that will be part of the 
	 * 						{@link SortedArray}.
	 */
	@SafeVarargs
	public SortedArray(IComparable<T> comparable, ESortingAlgorithm algo
			, T... elements)
	{
		this(comparable, elements.length, algo, elements);
	}
	
	/**
	 * A helper method that is used by the constructors using instances of the
	 * {@link ISortingAlgorithm} interface.
	 * 
	 * @param elements	The elements to copy.
	 * @param size		The size to check if it is bigger or equals the amount
	 * 					of elements.
	 * 
	 * @return 			The copied array.
	 */
	protected Object[] checkSizeAndCopyArray(T[] elements, int size)
	{
		if(size < elements.length)
			throw new SizeMismatchException(String.format(MSG_SIZE_MISMATCH, 
													size, elements.length));
		
		/*
		 * Copying array to prevent the original one getting changed 
		 * (which would also change this one, making it not sorted anymore) 
		 */
		Object[] arr = new Object[size];
		System.arraycopy(elements, 0, arr, 0, elements.length);
		
		return arr;
	}
	
	/**
	 * Adds a new Element to the array while remaining the order.
	 * 
	 * @param element The element to add.
	 * 
	 * @throws ArrayIndexOutOfBoundsException 	If the {@link SortedArray} is 
	 * 											full.
	 */
	public void insert(T element)
	{
		if(currentSize == getMaxSize())
		{
			throw new ArrayIndexOutOfBoundsException(MSG_FULL);
		}
		
		int i; //Will be initialized in the for-loop
		
		for(i = currentSize - 1; i >= 0 && 
				Utils.isBefore(cast(elements[i]), element, comparable); i--)
		{
			elements[i + 1] = elements[i];
		}
		
		elements[i + 1] = element;
		currentSize++;
	}
	
	/**
	 * Inserts the contents of a collection to the array.
	 * 
	 * @param elements	The elements to add.
	 */
	@SuppressWarnings("unchecked")
	public void addAll(T... elements)
	{
		for(T t: elements)
			insert(t);
	}
	
	/**
	 * Removes an element from the {@link SortedArray} by value.\n
	 * If the element is not contained in the container, the methods will 
	 * return silently.
	 * 
	 * @param element The element to remove.
	 */
	public void delete(T element)
	{
		int result = find(element);
		
		if(result != -1)
			shiftLeft(result);
	}

	/**
	 * Removes an element from the {@link SortedArray} by array.\n
	 * 
	 * @param index The index of the element that will be removed.
	 * 
	 * @throws ArrayIndexOutOfBoundsException 	If the index is smaller than 0 
	 * 											or bigger than the amount of 
	 * 											stored objects - 1. 
	 */
	public void delete(int index)
	{
		checkIndexOutOfBounds(index);
		
		shiftLeft(index);
	}
	
	/**
	 * Deletes all elements from the array.\n
	 * Afterwards, <code>getCurrentSize()</code> will return 0, but
	 * <code>getMaxSize()</code> will remain unchanged.
	 */
	public void clear()
	{
		currentSize = 0;
	}
	
	/**
	 * Finds an element and returns its index. Returns -1 if element could not
	 * be found.
	 * 
	 * @param element 	The element to search for.
	 * @return			The index of the element.
	 */
	public int find(T element)
	{
		/*
		 * Checks if the element to search for would be before or after the 
		 * bounds of the array, which means it is not within the array.
		 */
		if(checkValueOutOfBounds(element))
		{
			return -1;
		}
		
		int leftBorder = 0;
		int rightBorder = currentSize - 1;
		int middle = (leftBorder + rightBorder) / 2;
		
		while(leftBorder <= rightBorder)
		{
			IComparable.EComparisonResult result = 
					comparable.compare(cast(elements[middle]), element);
			
			if(result == IComparable.EComparisonResult.EQUALS)
				return middle;
			else if(result == IComparable.EComparisonResult.BEFORE)
				rightBorder = middle - 1;
			else
				leftBorder = middle + 1;
			
			middle = (leftBorder + rightBorder) / 2;
		}
		
		return -1;
	}
	
	/**
	 * Returns weather 'element' is part of this array or not.
	 * 
	 * @param element	The element to search for.
	 * @return			True, if the element is contained, false if not.
	 */
	public boolean contains(T element)
	{
		return find(element) != -1;
	}
	
	/**
	 * Returns the value of an element at a given index.
	 * 
	 * @param index 	The index of the element that will be returned.
	 * @return			The element.
	 * 
	 * @throws ArrayIndexOutOfBoundsException 	If the index is smaller than 0 
	 * 											or bigger than the amount of 
	 * 											stored objects - 1. 
	 */
	public T get(int index)
	{
		checkIndexOutOfBounds(index);
		
		return cast(elements[index]);
	}
	
	/**
	 * Returns weather the array is empty or not. Calling this method is
	 * equivalent to calling <code>(getCurrentSize() == 0)</code>.
	 * 
	 * @return	True, if it is empty, false if not.
	 */
	public boolean isEmpty()
	{
		return currentSize == 0;
	}
	
	/**
	 * Shrinks the maximum size to the amount of stored objects (if that is not
	 * already the case).\n
	 * After calling <code>.shrink()</code> <code>.getCurrentSize() == 
	 * .getMaxSize()</code> is always true.
	 */
	public void shrink()
	{
		if(currentSize < getMaxSize())
		{
			setSize(currentSize);
		}
	}
	
	/**
	 * Sets the maximum size of the array to the size that is passed to 
	 * the method (this size can be bigger or smaller than the original one). 
	 * If this size is smaller than the amount of objects that are currently in 
	 * the array, the elements at the end of the array that do not fit into 
	 * the new one will be deleted.
	 * 
	 * @param newSize	The new maximum size of the array.
	 */
	public void setSize(int newSize)
	{
		Object[] newElements = new Object[newSize];
		
		System.arraycopy(elements, 0, newElements, 0, newSize);
		
		elements = newElements;
	}
	
	/**
	 * Returns the {@link IComparable} of this {@link SortedArray}.
	 * 
	 * @return The {@link IComparable}.
	 */
	public IComparable<T> getComparable()
	{
		return comparable;
	}
	
	/**
	 * Returns the maximum amount of objects that can be stored in the
	 * container.
	 * 
	 * @return	The maximum size.
	 */
	public int getMaxSize()
	{
		return elements.length;
	}

	
	/**
	 * Returns the amount of objects that are currently being stored in the
	 * container.
	 * 
	 * @return	The amount of objects.
	 */
	public int getCurrentSize()
	{
		return currentSize;
	}
	
	/**
	 * Returns weather the value of an object would be before the first object 
	 * in the container or after the last one, which means that the element
	 * cannot be in the container.
	 * 
	 * @param 	element The element to check.
	 * @return 	True, if the element is out of the bounds, false if not.
	 */
	protected boolean checkValueOutOfBounds(T element)
	{
		return 	Utils.isBefore(cast(elements[0]), element, comparable) || 
				Utils.isAfter(cast(elements[currentSize - 1]), element, comparable);
	}

	
	/**
	 * Checks if the index is smaller than 0 or bigger or equals the amount 
	 * of the objects in the container.
	 * 
	 * @param index The index to check.
	 * 
	 * @throws ArrayIndexOutOfBoundsException 	If the index is smaller than 0 
	 * 											or bigger than the amount of 
	 * 											stored objects - 1. 
	 */
	protected void checkIndexOutOfBounds(int index)
	{
		if(index < 0 || index >= currentSize)
			throw new ArrayIndexOutOfBoundsException(index);
		
	}
	
	/**
	 * Casts an object to a reference to T (to get rid of warning / the need to
	 * add the {@link SuppressWarnings} annotation to many methods.).\n
	 * Does not do any sort of checking if the cast is possible.
	 * 
	 * @param 	element The reference to cast.
	 * @return 	The casted reference.
	 */
	@SuppressWarnings("unchecked")
	protected T cast(Object element)
	{
		return (T) element;
	}
	
	/**
	 * Moves all elements in the array to the left, starting at a given 
	 * index.\n
	 * The element at this index will be overridden.
	 * 
	 * @param end The index.
	 */
	protected void shiftLeft(int end)
	{
		for(int i = end + 1; i < getMaxSize(); i++)
		{
			elements[i - 1] = elements[i];
		}
		
		currentSize--;
	}
	
	/**
	 * Converts the contents of a {@link SortedArray} into a standard array.\n
	 * Note: All the elements from the {@link SortedArray} are being copied into 
	 * a new array, which is then returned.
	 * 
	 * @return The new array.
	 */
	public Object[] toArray()
	{
		Object[] ret = new Object[getCurrentSize()];
		
		for(int i = 0; i < currentSize; i++)
		{
			ret[i] = elements[i];
		}
		
		return ret;
	}

	
	/**
	 * Returns the iterator that is used within a for-each loop.
	 * 
	 * @return 	The iterator.
	 * 
	 * @see	Iterable
	 * @see Iterator
	 */
	@Override
	public Iterator<T> iterator()
	{
		return new Iterator<T>()
		{
			/**
			 * The index of the next element that will be returned.
			 */
			private int index = 0;
			
			/**
			 * Returns, if there is an element left in the container get with 
			 * <code>next()</code>.
			 * 
			 * @return True, if there is an element left, false if not.
			 */
			@Override
			public boolean hasNext()
			{
				return index < currentSize;
			}

			/**
			 * Returns the next element in the container.
			 * 
			 * @return The next element.
			 */
			@Override
			public T next()
			{
				return get(index++);
			}
			
			/**
			 * Removes the element that would be the next to be returned by
			 * <code>next()</code>.
			 */
			@Override
			public void remove()
			{
				delete(index);
			}
		};
	}
	
	/**
	 * Returns weather this and the {@link SortedArray} 'obj' are equal.\n
	 * Two {@link SortedArray}s are equal if:\n
	 * -The current amount of stored elements is equal\n
	 * -The maximum size is equal\n
	 * -The {@link IComparable} is equal\n
	 * -All the elements are equal and at the same position in the array.\n
	 * Note: T must have implemented <code>.equals(...)</code> properly.
	 * 
	 * @param obj	The object that the array is compared against.
	 * @return		True, if the objects are equal, false if not.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj)
	{
		SortedArray<T> otherArray = (SortedArray<T>) obj;
		
		if(	currentSize 	!= otherArray.currentSize ||
			elements.length != otherArray.elements.length ||
			comparable 		!= otherArray.comparable)
		{
			return false;
		}
		
		for(Object o: elements)
		{
			if(!o.equals(obj))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Returns the contents of this array as a Strings.\n
	 * This methods returns all elements in one line, separated by a ';'. To
	 * get one element per line, call 
	 * <code>.toString().replace("; ", "\n")</code>.
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("Maximum size: ");
		sb.append(elements.length).append(", Current size: ")
		.append(currentSize).append("; ");
		
		for(int i = 0; i < currentSize; i++)
		{
			sb.append("Index: ").append(i)
			.append(" Value: ").append(elements[i] + "; ");
		}
		
		return sb.toString();
	}
	
	/**
	 * An enumeration to represent different sorting algorithms for quick 
	 * access when using the <code>SortedArray(IComparable&lt;T&gt; comparable,
	 * int size , SortingAlgorithm algorithm, T... elements)</code>
	 * constructor.
	 * 
	 * @author 	Lukas Reichmann
	 * @version	1.0
	 * @see		SortedArray
	 */
	public enum ESortingAlgorithm
	{
		/**
		 * Representation of the BubbleSort algorithm.
		 */
		BUBBLESORT,
		
		/**
		 * Representation of the InsertionSort algorithm.
		 */
		INSERTIONSORT,
		
		/**
		 * Representation of the QuickSort algorithm.
		 */
		QUICKSORT;
	}
}
