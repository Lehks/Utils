package exception.storageFileParser;

@SuppressWarnings("serial")
public class StorageFileInvalidSeparatorException
		extends StorageFileParseException
{
	public StorageFileInvalidSeparatorException(int line,
			int column, char character)
	{
		super(line, column, character);
	}
	
	public StorageFileInvalidSeparatorException(String msg,
			int line, int column, char character)
	{
		super(msg, line, column, character);
	}
}
