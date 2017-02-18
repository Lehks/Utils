package exception.storageFileParser;

/**
 * Thrown, the parser expects a
 * {@value file.storageFile.StorageFileConstants#KEY_VALUE_SEPARATOR} but can
 * not find one.
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see		StorageFileParseException
 */
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
