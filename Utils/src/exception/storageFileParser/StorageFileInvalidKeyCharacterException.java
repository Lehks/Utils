package exception.storageFileParser;

/**
 * Thrown, if a key contains a 
 * {@value file.storageFile.StorageFileConstants#PATH_SEPARATOR}.
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see		StorageFileParseException
 */
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
