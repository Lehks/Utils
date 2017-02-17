package exception.storageFileParser;

@SuppressWarnings("serial")
public class StorageFileInvalidKeyEnclosureException
		extends StorageFileParseException
{
	public StorageFileInvalidKeyEnclosureException(int line,
			int column, char character)
	{
		super(line, column, character);
	}

	public StorageFileInvalidKeyEnclosureException(String msg,
			int line, int column, char character)
	{
		super(msg, line, column, character);
	}
}
