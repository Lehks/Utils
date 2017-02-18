package exception.storageFileParser;

import file.storageFile.StorageFileParser;

/**
 * The super class of every exception that is thrown by <code>
 * {@link StorageFileParser}.parse()</code> whenever it finds a syntax error. 
 * This class holds some special methods to inform the user about the 
 * error.<br>
 * These are:<br>
 * <code>.getLine()</code>: The line in which the character occurred.<br>
 * <code>.getColumn()</code>: The column of the error. Note that a tab is
 * counted as exactly one character, so be aware that in some editors a tab
 * counts as multiple characters.<br>
 * <code>.getCharacter()</code>: The invalid character (aka. the character in 
 * line <code>.getLine()</code> and at the column <code>.getColumn()
 * </code>).<br>
 * Read the documentation of all the subclasses of this class, to see when
 * they are thrown.<br>
 * Note that the subclasses of this class will have very minimalist
 * documentation, e.g. there won't be any for the constructors, since 
 * the constructors in the subclasses simply call the constructors in this
 * class.
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see		StorageFileParser
 *
 */
@SuppressWarnings("serial")
public class StorageFileParseException extends Exception
{
	/**
	 * The line in which the syntax error occurred.
	 */
	private int line;
	
	/**
	 * The column in which the syntax error occurred.
	 */
	private int column;
	
	/**
	 * The character that triggered the syntax error.
	 */
	private char character;
	
	/**
	 * Constructs a new {@link StorageFileParseException} using the passed
	 * data and sets the message accordingly.
	 * 
	 * @param line		The line in which the syntax error occurred.
	 * @param column	The column in which the syntax error occurred.
	 * @param character	The character that triggered the syntax error.
	 */
	public StorageFileParseException(int line, int column,
			char character)
	{
		this(String.format("Line: %d, Column: %d, Character: %s",
				line, column, character), line, column,
				character);
	}

	/**
	 * Constructs a new {@link StorageFileParseException} using the passed
	 * data.
	 * 
	 * @param line		The line in which the syntax error occurred.
	 * @param column	The column in which the syntax error occurred.
	 * @param character	The character that triggered the syntax error.
	 */
	public StorageFileParseException(String msg, int line, int column,
			char character)
	{
		super(msg);
		
		this.line = line;
		this.column = column;
		this.character = character;
	}

	/**
	 * Returns the line in which the syntax error occurred.
	 * 
	 * @return the line in which the syntax error occurred.
	 */
	public int getLine()
	{
		return line;
	}

	/**
	 * Returns the column in which the syntax error occurred.
	 * 
	 * @return the column in which the syntax error occurred.
	 */
	public int getColumn()
	{
		return column;
	}
	
	/**
	 * Returns the character that triggered the syntax error.
	 * 
	 * @return the character that triggered the syntax error.
	 */
	public char getCharacter()
	{
		return character;
	}
}
