package file.storageFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.Scanner;

import container.ArrayQueue;
import exception.storageFile.StorageFileInvalidKeyCharacterException;
import exception.storageFile.StorageFileInvalidKeyEnclosureException;
import exception.storageFile.StorageFileInvalidSeparatorException;
import exception.storageFile.StorageFileInvalidValueCharacterException;
import exception.storageFile.StorageFileParseException;

public class StorageFileParser
{
	/**
	 * The current line as a String. This is used to determine, if the line is
	 * a comment, empty or an entry line and to determine the column of the
	 * character that is currently processed (which is done by <code>
	 * .getColumn()</code>).
	 */
	private String currentLineStr;
	
	/**
	 * A queue that stores all the characters that still need to be processed.
	 */
	private ArrayQueue<Character> currentLine;
	
	/**
	 * The reader that is used to parse.
	 */
	private BufferedReader reader;
	
	/**
	 * The line that is currently processed. Used when an exception is thrown
	 * to give further information.
	 */
	private int line = 0;

	/**
	 * Constructs a new {@link StorageFileParser} that will parse the contents 
	 * of the passed {@link File}.
	 * 
	 * @param file						The file to take the code from.
	 * @throws FileNotFoundException	If the passed file could not be found.
	 */
	public StorageFileParser(File file) throws FileNotFoundException
	{
		reader = new BufferedReader(new FileReader(file));
	}
	
	/**
	 * Constructs a new {@link StorageFileParser} that will parse the contents
	 * of the passed {@link Reader}.<br>
	 * This {@link Reader} will not be closed, but <code>.parse()</code> will 
	 * read until its end.<br>
	 * Furthermore, this passed {@link Reader} will be either wrapped into a 
	 * {@link BufferedReader} or, in the case that the passed one was 
	 * originally already a {@link BufferedReader}, simple cast to one. This 
	 * is the reason, why <code>.getReader()</code> returns a 
	 * {@link BufferedReader}.
	 * 
	 * @param reader The reader to take the code from.
	 */
	public StorageFileParser(Reader reader)
	{
		if(reader instanceof BufferedReader)
			this.reader = (BufferedReader) reader;
		else
			this.reader = new BufferedReader(reader);
	}

	public void parse() throws StorageFileParseException
	{
		Scanner scanner = new Scanner(reader);

		while (scanner.hasNextLine())
		{
			line++;

			currentLineStr = scanner.nextLine();
			
			currentLine = ArrayQueue.makeCharacterArrayQueue(currentLineStr);

			if (!currentLineStr.trim().startsWith("#"))
				expr_anyEntry();
		}
		
		scanner.close();
	}

	/**
	 * Allows any sort of entry.
	 * 
	 * @throws StorageFileParseException	If the statement could not be 
	 * 										parsed.
	 */
	private void expr_anyEntry() throws StorageFileParseException
	{
		try
		{
			expr_valueEntry();
			System.out.println("line v: \t\t" + line);
		}
		catch (NoSuchElementException e)
		{
			currentLine = ArrayQueue.makeCharacterArrayQueue(currentLineStr);
			expr_noValueEntry();
			System.out.println("line nv: \t\t" + line);
		}
	}

	/**
	 * Allows an entry without a value.
	 * 
	 * @throws StorageFileParseException	If the statement could not be 
	 * 										parsed.
	 */
	private void expr_noValueEntry() throws StorageFileParseException
	{
		expr_entryStart();
		expr_anyWhiteSpace();
		expr_lineEnd();
	}


	/**
	 * Allows an entry with a value.
	 * 
	 * @throws StorageFileParseException	If the statement could not be 
	 * 										parsed.
	 */
	private void expr_valueEntry() throws StorageFileParseException
	{
		expr_entryStart();
		expr_anyWhiteSpace();
		expr_keyValueSeparator();
		expr_anyWhiteSpace();
		expr_value();
		expr_anyWhiteSpace();
		expr_lineEnd();
	}

	/**
	 * Allows leading tabs and a key.
	 * 
	 * @throws StorageFileParseException	If the statement could not be 
	 * 										parsed.
	 */
	private void expr_entryStart() throws StorageFileParseException
	{
		expr_leadingWhiteSpace();
		expr_key();
	}

	/**
	 * Pulls any leading tab and allows any amount (0 explicitly included).
	 * 
	 * @throws StorageFileParseException	If the statement could not be 
	 * 										parsed.
	 */
	private void expr_leadingWhiteSpace()
	{	
		if(currentLine.peak() == '\t')
		{
			currentLine.pull();
			expr_leadingWhiteSpace();
		}
	}

	/**
	 * Allows any string that does not include a '.' and that is enclosed by
	 * '"'.
	 * 
	 * @throws StorageFileParseException	If the statement could not be 
	 * 										parsed.
	 */
	private void expr_key() throws StorageFileParseException
	{
		expr_keyValueEnclosure();
		expr_keyStr();
		expr_keyValueEnclosure();
	}

	/**
	 * Allows only a single '"'.
	 * 
	 * @throws StorageFileParseException	If the statement could not be 
	 * 										parsed.
	 */
	private void expr_keyValueEnclosure() throws StorageFileParseException
	{
		if (currentLine.peak() != '"')
		{
			throw new StorageFileInvalidKeyEnclosureException(line,
					getColumn(), currentLine.peak());
		}
		
		currentLine.pull();
	}

	/**
	 * Allows any string that does not contain a '.'.
	 * 
	 * @throws StorageFileParseException	If the statement could not be 
	 * 										parsed.
	 */
	private void expr_keyStr() throws StorageFileParseException
	{
		if(currentLine.peak() != '"')
		{
			if (currentLine.peak() == '.')
			{
				throw new StorageFileInvalidKeyCharacterException(line,
						getColumn(), currentLine.peak());
			}
			
			currentLine.pull();
			
			expr_keyStr();
		}
	}

	/**
	 * Allows only a single '='.
	 * 
	 * @throws StorageFileParseException	If the statement could not be 
	 * 										parsed.
	 */
	private void expr_keyValueSeparator() throws StorageFileParseException
	{
		if (currentLine.peak() != '=')
		{
			throw new StorageFileInvalidSeparatorException(line, getColumn(),
					currentLine.peak());
		}
		
		currentLine.pull();
	}

	/**
	 * Allows any string that is enclosed by '"'.
	 * 
	 * @throws StorageFileParseException	If the statement could not be 
	 * 										parsed.
	 */
	private void expr_value() throws StorageFileParseException
	{
		expr_keyValueEnclosure();
		expr_valueStr();
		expr_keyValueEnclosure();
	}

	/**
	 * Allows any string.
	 */
	private void expr_valueStr()
	{
		if(currentLine.peak() != '"')
		{
			currentLine.pull();
			
			expr_valueStr();
		}
	}

	/**
	 * Does not check a specific character, but it checks if the line queue is
	 * empty.
	 * 
	 * @throws StorageFileParseException	If the statement could not be 
	 * 										parsed.
	 */
	private void expr_lineEnd() throws StorageFileParseException
	{
		if (!currentLine.isEmpty())
		{
			throw new StorageFileInvalidValueCharacterException(line,
					getColumn(), currentLine.peak());
		}
	}
	
	/**
	 * Pulls any character that is either a space or a tab.
	 */
	private void expr_anyWhiteSpace()
	{
		if(!currentLine.isEmpty() &&
				(currentLine.peak() == ' ' || currentLine.peak() == '\t'))
		{
			currentLine.pull();
			
			expr_anyWhiteSpace();
		}
	}
	
	/**
	 * Returns the column of the character that is currently being processed
	 * within its line. This is used when {@link StorageFileParseException} are
	 * getting thrown.
	 * 
	 * @return The column.
	 */
	private int getColumn()
	{
		return currentLineStr.length() - currentLine.getCurrentSize() + 1;
	}
	
	/**
	 * Returns the {@link BufferedReader} that is used internally. 
	 * 
	 * @return The reader.
	 */
	public BufferedReader getReader()
	{
		return reader;
	}
}
