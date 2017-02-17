package exception;

@SuppressWarnings("serial")
public class EntryDuplicateException extends Exception
{
	public EntryDuplicateException()
	{
		super();
	}
	
	public EntryDuplicateException(String msg)
	{
		super(msg);
	}
}
