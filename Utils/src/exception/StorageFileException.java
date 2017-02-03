package exception;

@SuppressWarnings("serial")
public class StorageFileException extends RuntimeException
{
	public StorageFileException()
	{
		super();
	}
	
	public StorageFileException(String msg)
	{
		super(msg);
	}
}
