package exception.storageFileParser;

import file.storageFile.StorageFileConstants;

/**
 * Thrown, if a key is empty (one 
 * {@value StorageFileConstants#KEY_VALUE_PERIMETER} directly follows another).
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see		StorageFileParseException
 */
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
