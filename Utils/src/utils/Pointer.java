package utils;

public class Pointer<T>
{
	private T element;

	public Pointer(T element)
	{
		set(element);
	}
	
	public Pointer()
	{
		this(null);
	}
	
	public T get()
	{
		return element;
	}

	public void set(T element)
	{
		this.element = element;
	}
}
