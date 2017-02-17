package exception.storageFileParser;

@SuppressWarnings("serial")
public class StorageFileInvalidValueCharacterException 
		extends StorageFileParseException
{
	public StorageFileInvalidValueCharacterException(int line,
			int column, char character)
	{
		super(line, column, character);
	}

	public StorageFileInvalidValueCharacterException(String msg,
			int line, int column, char character)
	{
		super(msg, line, column, character);
	}
}
