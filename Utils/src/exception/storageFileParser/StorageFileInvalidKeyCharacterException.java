package exception.storageFileParser;

@SuppressWarnings("serial")
public class StorageFileInvalidKeyCharacterException
		extends StorageFileParseException
{
	public StorageFileInvalidKeyCharacterException(int line,
			int column, char character)
	{
		super(line, column, character);
	}
	
	public StorageFileInvalidKeyCharacterException(String msg,
			int line, int column, char character)
	{
		super(msg, line, column, character);
	}
}
