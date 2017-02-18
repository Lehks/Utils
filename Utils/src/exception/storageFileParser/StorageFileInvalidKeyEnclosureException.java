package exception.storageFileParser;

/**
 * Thrown, if the parser expects a
 * {@value file.storageFile.StorageFileConstants#KEY_VALUE_PERIMETER} but can
 * not fin one.
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see		StorageFileParseException
 */
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
