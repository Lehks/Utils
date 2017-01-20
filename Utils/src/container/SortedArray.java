package container;

import java.util.Iterator;

import container.sorting.Bubblesort;
import container.sorting.IComparable;
import container.sorting.IComparable.EComparisonResult;
import container.sorting.ISearchCondition;
import container.sorting.ISortingAlgorithm;
import container.sorting.InsertionSort;
import container.sorting.Quicksort;
import exception.SizeMismatchException;
import utils.Utils;

/**
 * Represents a normal array that is always sorted according to rules defined 
 * by a {@link IComparable} (some of them for the primitive types and Strings 
 * are already predefined, they are called DEFAULT_*_COMPARABLE and 
 * INVERSE_*_COMPARABLE, whereas * is replaced with the type they can sort).<br>
 * <br>
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
 * @see		ISearchCondition
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
	 * The message that is being given to a {@link SizeMismatchException} if
	 * the size and amount of elements does not fit when using the constructors 
	 * <code>SortedArray(IComparable&lt;T&gt; comparable, int size ,
	 * SortingAlgorithm algorithm, T... elements)</code> and <code>
	 * SortedArray(IComparable&lt;T&gt; comparable, int size ,
	 * ISortingAlgorithm&lt;T&gt; algorithm, T... elements)</code>
	 */
	protected static final String MSG_SIZE_MISMATCH = "Passed size is: %d, "
											+ "but amount of elements is: %d";
	
	/**
	 * The message that is being given to a {@link SizeMismatchException} 
	 * thrown by SortedArray(int, IComparable) when the maximum size is
	 * &lt; 0.
	 */
	protected static final String MSG_SIZE_SMALLER_ZERO 
													= "Size must not be < 0.";
	
	/**
	 * The comparable that is being used to compare two values when sorting.
	 */
	protected IComparable<T> comparable;

	protected ISearchCondition<T, T> standartSearchCondition;
	
	/**
	 * The array that stores all the values.<br>
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
	 * @param maxSize		The maximum size of the array.
	 * @param comparable 	The {@link IComparable} to compare two values when 
	 * 						sorting.
	 * 
	 * @throws SizeMismatchException 	If the given maximum size of the array 
	 * 									is &lt; 0.
	 */
	public SortedArray(int maxSize, IComparable<T> comparable)
	{
		if(maxSize < 0)
			throw new SizeMismatchException(MSG_SIZE_SMALLER_ZERO);
		
		this.comparable = comparable;
		this.elements = new Object[maxSize];
		
		this.standartSearchCondition = makeStandartSearchCondition();
	}
	
	/**
	 * Creates a {@link SortedArray} from an unsorted array, sorting it using a
	 * specified algorithm that implements the {@link ISortingAlgorithm}
	 * interface.<br>
	 * The final container's size is determined by 'size'.<br>
	 * <br>
	 * There are already some default implementations of some common sorting
	 * algorithms (e.g. QuickSort) in the container.sorting package.
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
	 * 
	 * @see InsertionSort
	 * @see Quicksort
	 * @see Bubblesort
	 * 
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public SortedArray(IComparable<T> comparable, int size, 
			ISortingAlgorithm<T> algorithm, T... elements)
	{
		this.comparable = comparable;
		this.elements = checkSizeAndCopyArray(elements, size);
		this.currentSize = elements.length;
		
		this.standartSearchCondition = makeStandartSearchCondition();
		
		algorithm.sort(comparable, (T[]) this.elements);
	}

	/**
	 * Creates a {@link SortedArray} from an unsorted array, sorting it using a
	 * specified algorithm that implements the {@link ISortingAlgorithm}
	 * interface.<br>
	 * The final container's size will be equal to the amount of elements that
	 * were given to the constructor.<br>
	 * <br>
	 * There are already some default implementations of some common sorting
	 * algorithms (e.g. QuickSort) in the container.sorting package.
	 * 
	 * @param comparable 	The {@link IComparable} to sort the elements.
	 * @param algorithm		The algorithm used to sort.
	 * @param elements		The elements that will be part of the 
	 * 						{@link SortedArray}.
	 * 
	 * @see InsertionSort
	 * @see Quicksort
	 * @see Bubblesort
	 * 
	 */
	@SafeVarargs
	public SortedArray(IComparable<T> comparable, 
			ISortingAlgorithm<T> algorithm, T... elements)
	{
		this(comparable, elements.length, algorithm, elements);
	}
	
	/**
	 * Creates a {@link SortedArray} from an unsorted array, sorting it using 
	 * the {@link Quicksort} algorithm.<br>
	 * The final container's size is determined by 'size'.
	 * 
	 * @param comparable 		The {@link IComparable} to sort the elements.
	 * @param size				The size of the final container. Must be equal 
	 * 							or higher than the size of 'elements'.
	 * @param elements			The elements that will be part of the 
	 * 							{@link SortedArray}.
	 * 
	 * @throws 
	 * 	SizeMismatchException 	If 'size' is smaller than the amount of 
	 * 							passed elements.
	 */
	@SafeVarargs
	public SortedArray(IComparable<T> comparable, int size, T... elements)
	{
		this(comparable, size, new Quicksort<>(), elements);
	}
	
	/**
	 * Creates a {@link SortedArray} from an unsorted array, sorting it using 
	 * the {@link Quicksort} algorithm.<br>
	 * The final container's size will be equal to the amount of elements that
	 * were given to the constructor.
	 * 
	 * @param comparable 	The {@link IComparable} to sort the elements.
	 * @param elements		The elements that will be part of the 
	 * 						{@link SortedArray}.
	 */
	@SafeVarargs
	public SortedArray(IComparable<T> comparable, T... elements)
	{
		this(comparable, new Quicksort<>(), elements);
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
	
	protected ISearchCondition<T, T> makeStandartSearchCondition()
	{
		return new ISearchCondition<T, T>()
		{
			@Override
			public EComparisonResult isCorrectElement(T element, T customObj)
			{
				return comparable.compare(element, customObj);
			}
			
			@Override
			public boolean canSearchBinary()
			{
				return true;
			}
		};
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
			throw new ArrayIndexOutOfBoundsException(MSG_FULL);
		
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
	 * Removes an element from the {@link SortedArray} by value.<br>
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
	 * Removes an element from the {@link SortedArray} by array.<br>
	 * 
	 * @param index The index of the element that will be removed.
	 * 
	 * @throws ArrayIndexOutOfBoundsException 	If the index is smaller than 0 
	 * 											or bigger than the amount of 
	 * 											stored objects - 1. 
	 */
	public void deleteAtIndex(int index)
	{
		checkIndexOutOfBounds(index);
		
		shiftLeft(index);
	}
	
	/**
	 * Deletes all elements from the array.<br>
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
	 * @return			The index of the element, or -1 if not found.
	 */
	public int find(T element)
	{
		return find(standartSearchCondition, element);
	}
	
	/**
	 * Finds the first element in the container that has a certain trait (that 
	 * is specified in the {@link ISearchCondition}) and returns its index
	 * (or -1 if it could not be found).<br>
	 * Note: If <code>.canSearchBinary()</code> in the {@link ISearchCondition} 
	 * returns false (which it does by default), then this is much slower than
	 * <code>.find(T element)</code> since this method will then search
	 * sequentially.
	 * 
	 * @param condition	The {@link ISearchCondition} by which to identify the 
	 * 					correct object.
	 * @param customObj	The custom object used in {@link ISearchCondition}. 
	 * 					See the documentation of that class for further 
	 * 					information.
	 * @return			The index of the element if it was found, if not -1.
	 * 
	 * @param <T1>		Type of the custom object.
	 * 
	 * @see ISearchCondition
	 */
	public <T1> int find(ISearchCondition<T, T1> condition, T1 customObj)
	{
		if(!condition.canSearchBinary())
			return findSequentially(condition, customObj);
		else
			return findBinary(condition, customObj);
		
	}
	
	/**
	 * A helper to <code>.find(ISearchCondition<T, T1> condition,
	 *  T1 customObj)</code> that is called when that method needs to search
	 *  sequentially. Parameter are the same that were passed to said <code>
	 *  .find(...)</code>.
	 *  
	 * @param condition	See <code>.find(ISearchCondition<T, T1> condition,
	 *  				T1 customObj)</code>
	 * @param customObj	See <code>.find(ISearchCondition<T, T1> condition,
	 *  				T1 customObj)</code>
	 *  @param <T1>		See <code>.find(ISearchCondition<T, T1> condition,
	 *  				T1 customObj)</code>.
	 *  
	 * @return			See <code>.find(ISearchCondition<T, T1> condition,
	 *  				T1 customObj)</code>.
	 */
	protected <T1> int findSequentially(ISearchCondition<T, T1> condition, 
																	T1 customObj)
	{
		for(int i = 0; i < getCurrentSize(); i++)
		{
			if(condition.isCorrectElement(get(i), customObj) 
												== EComparisonResult.EQUAL)
				return i;
		}
		
		return -1;
	}
	
	/**
	 * A helper to <code>.find(ISearchCondition<T, T1> condition,
	 *  T1 customObj)</code> that is called when that method needs to search
	 *  binary. Parameter are the same that were passed to said <code>
	 *  .find(...)</code>.
	 *  
	 * @param condition	See <code>.find(ISearchCondition<T, T1> condition,
	 *  				T1 customObj)</code>
	 * @param customObj	See <code>.find(ISearchCondition<T, T1> condition,
	 *  				T1 customObj)</code>
	 *  @param <T1>		See <code>.find(ISearchCondition<T, T1> condition,
	 *  				T1 customObj)</code>.
	 *  
	 * @return			See <code>.find(ISearchCondition<T, T1> condition,
	 *  				T1 customObj)</code>.
	 */
	protected <T1> int findBinary(ISearchCondition<T, T1> condition, T1 customObj)
	{
		if(	condition.isCorrectElement(get(0), customObj)
													== EComparisonResult.BEFORE ||
			condition.isCorrectElement(get(getCurrentSize() - 1), customObj)
													== EComparisonResult.AFTER)
		{
			return -1;
		}
		
		int leftBorder = 0;
		int rightBorder = currentSize - 1;
		int middle = (leftBorder + rightBorder) / 2;
		
		while(leftBorder <= rightBorder)
		{
			IComparable.EComparisonResult result = 
							condition.isCorrectElement(get(middle), customObj);
			
			if(result == IComparable.EComparisonResult.EQUAL)
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
	 * already the case).<br>
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
		if(newSize == getMaxSize())
			return;
		
		Object[] newElements = new Object[newSize];
		
		if(newSize < currentSize)
			currentSize = newSize;
		
		System.arraycopy(elements, 0, newElements, 0,
				(newSize < currentSize ? newSize : currentSize));
		
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
				Utils.isAfter(cast(elements[currentSize - 1]), element,
																comparable);
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
	 * add the {@link SuppressWarnings} annotation to many methods.).<br>
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
	 * index.<br>
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
	 * Converts the contents of a {@link SortedArray} into a standard array.<br>
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
				deleteAtIndex(index);
			}
		};
	}
	
	/**
	 * Returns weather this and the {@link SortedArray} 'obj' are equal.<br>
	 * Two {@link SortedArray}s are equal if:<br>
	 * -The current amount of stored elements is equal<br>
	 * -The maximum size is equal<br>
	 * -All the elements are equal and at the same position in the array.<br>
	 * Note: T must have implemented <code>.equals(...)</code> properly and 
	 * this method does not check if the {@link SortedArray}s have the same
	 * {@link IComparable}.
	 * 
	 * @param obj	The object that the array is compared against.
	 * @return		True, if the objects are equal, false if not.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof SortedArray) || obj == null)
			return false;
		
		SortedArray<T> otherArray = (SortedArray<T>) obj;
		
		if(	currentSize 	!= otherArray.currentSize ||
			elements.length != otherArray.elements.length)
			return false;
		
		for(int i = 0; i < currentSize; i++)
		{
			if(!elements[i].equals(otherArray.get(i)))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Returns the contents of this array as a Strings.<br>
	 * This methods returns all elements in one line, separated by a ';'. To
	 * get one element per line, call 
	 * <code>.toString().replace("; ", "\n")</code>.
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("Maximum size: ");
		sb.append(elements.length).append(", Current size: ")
		.append(currentSize);
		
		for(int i = 0; i < currentSize; i++)
		{
			sb.append("; ").append("Index: ").append(i)
			.append(" Value: ").append(elements[i]);
		}
		
		return sb.toString();
	}
}
