package exception.storageFileParser;

@SuppressWarnings("serial")
public class StorageFileEmptyKeyException extends StorageFileParseException
{
	public StorageFileEmptyKeyException(int line, int column, char character)
	{
		super(line, column, character);
	}

	public StorageFileEmptyKeyException(String msg, int line, int column,
			char character)
	{
		super(msg, line, column, character);
	}

}
