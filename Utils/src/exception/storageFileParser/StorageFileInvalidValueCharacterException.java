package exception.storageFileParser;

/**
 * Thrown, if a character is read after the value has been closed using
 * a {@value file.storageFile.StorageFileConstants#KEY_VALUE_PERIMETER}.
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see		StorageFileParseException
 */
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
