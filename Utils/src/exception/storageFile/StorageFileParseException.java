package exception.storageFile;

@SuppressWarnings("serial")
public class StorageFileParseException extends Exception
{
	private int line;
	private int column;
	private char character;
	
	public StorageFileParseException(int line, int column,
			char character)
	{
		this(String.format("Line: %d, Column: %d, Character: %s",
				line, column, character), line, column,
				character);
	}
	
	public StorageFileParseException(String msg, int line, int column,
			char character)
	{
		super(msg);
		
		this.line = line;
		this.column = column;
		this.character = character;
	}

	public int getLine()
	{
		return line;
	}

	public int getColumn()
	{
		return column;
	}
	
	public char getCharacter()
	{
		return character;
	}
}
