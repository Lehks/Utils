package container;

import java.util.NoSuchElementException;

public class ArrayQueue<T>
{
	protected Object[] elements	= null;
	protected int writeHead		= 0;
	protected int readHead		= 0;
	protected int currentSize	= 0;
	
	protected static final int GROWTH_FACTOR = 2;
	
	public static final String MSG_EMPTY = "Queue is empty.";
	
	public static ArrayQueue<Character> makeCharacterArrayQueue(char[] arr)
	{
		Character[] chars = new Character[arr.length];
		
		for(int i = 0; i < arr.length; i++)
			chars[i] = arr[i];
		
		return new ArrayQueue<>(chars);
	}
	
	public static ArrayQueue<Character> makeCharacterArrayQueue(String str)
	{
		return makeCharacterArrayQueue(str.toCharArray());
	}
	
	public static ArrayQueue<Byte> makeByteArrayQueue(byte[] arr)
	{
		Byte[] bytes = new Byte[arr.length];
		
		for(int i = 0; i < arr.length; i++)
			bytes[i] = arr[i];
		
		return new ArrayQueue<>(bytes);
	}
	
	public static ArrayQueue<Short> makeShortArrayQueue(short[] arr)
	{
		Short[] shorts = new Short[arr.length];
		
		for(int i = 0; i < arr.length; i++)
			shorts[i] = arr[i];
		
		return new ArrayQueue<>(shorts);
	}
	
	public static ArrayQueue<Integer> makeIntegerArrayQueue(int[] arr)
	{
		Integer[] ints = new Integer[arr.length];
		
		for(int i = 0; i < arr.length; i++)
			ints[i] = arr[i];
		
		return new ArrayQueue<>(ints);
	}
	
	public static ArrayQueue<Long> makeLongArrayQueue(long[] arr)
	{
		Long[] longs = new Long[arr.length];
		
		for(int i = 0; i < arr.length; i++)
			longs[i] = arr[i];
		
		return new ArrayQueue<>(longs);
	}
	
	public static ArrayQueue<Float> makeFloatArrayQueue(float[] arr)
	{
		Float[] floats = new Float[arr.length];
		
		for(int i = 0; i < arr.length; i++)
			floats[i] = arr[i];
		
		return new ArrayQueue<>(floats);
	}
	
	public static ArrayQueue<Double> makeDoubleArrayQueue(double[] arr)
	{
		Double[] doubles = new Double[arr.length];
		
		for(int i = 0; i < arr.length; i++)
			doubles[i] = arr[i];
		
		return new ArrayQueue<>(doubles);
	}
	
	public ArrayQueue()
	{
		this(0);
	}
	
	public ArrayQueue(int size)
	{
		elements = new Object[size];
	}
	
	public ArrayQueue(T[] initialElements)
	{
		elements = new Object[initialElements.length];
		
		System.arraycopy(initialElements, 0, elements, 0, 
													initialElements.length);
		
		writeHead = initialElements.length;
		currentSize = initialElements.length;
	}
	
	public void push(T element)
	{
		if(currentSize >= elements.length)
			grow();
		
		elements[writeHead] = element;
		
		incrementWriteHead();
		currentSize++;
	}
	
	public T pull()
	{
		T ret = peak();
		
		incrementReadHead();
		currentSize--;
		
		return ret;
	}
	
	public T peak()
	{
		check();

		return get(readHead);
	}
	
	public void grow()
	{
		Object[] newElements = new Object[(elements.length + 1) * 
		                                  						GROWTH_FACTOR];
		
		for(int i = 0; i < currentSize; i++)
		{
			newElements[i] = elements[makeIndex(i)];
		}
		
		writeHead = currentSize;
		readHead = 0;
		elements = newElements;
	}
	
	public int getMaxSize()
	{
		return elements.length;
	}
	
	public int getCurrentSize()
	{
		return currentSize;
	}
	
	public boolean isEmpty()
	{
		return currentSize == 0;
	}
	
	@SuppressWarnings("unchecked")
	private T get(int i)
	{
		return (T) elements[i];
	}
	
	private void check()
	{
		if(isEmpty())
			throw new NoSuchElementException(MSG_EMPTY);
	}
	
	private void incrementWriteHead()
	{
		writeHead = increment(writeHead);
	}
	
	private void incrementReadHead()
	{
		readHead = increment(readHead);
	}
	
	private int increment(int i)
	{
		if(i >= elements.length - 1)
			i = 0;
		else
			i++;
		
		return i;
	}

	private int makeIndex(int index)
	{
		int ret = index + readHead;
		
		if(ret >= elements.length)
			 ret -= elements.length;
		
		return ret;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < currentSize; i++)
			sb.append(get(makeIndex(i))).append("\n");
		
		return sb.toString();
	}
}
