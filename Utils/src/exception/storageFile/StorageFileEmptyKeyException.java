package exception.storageFile;

@SuppressWarnings("serial")
public class StorageFileEmptyKeyException extends StorageFileParseException
{
	public static final String MSG_DEFAULT = "The key is empty.";
	
	public StorageFileEmptyKeyException(int line, int column, char character)
	{
		super(MSG_DEFAULT, line, column, character);
	}

	public StorageFileEmptyKeyException(String msg, int line, int column,
			char character)
	{
		super(msg, line, column, character);
	}

}
