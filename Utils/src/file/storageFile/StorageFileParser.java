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
	 * This {@link Reader} will not be closed, but <code>.parse()</code> will read
	 * until its end.<br>
	 * Furthermore, this passed {@link Reader} will be either wrapped into a 
	 * {@link BufferedReader} or, in the case that the passed one was originally
	 * already a {@link BufferedReader}, simple cast to one. This is the reason,
	 * why <code>.getReader()</code> returns a {@link BufferedReader}.
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
				anyEntry();
		}
		
		scanner.close();
	}

	/**
	 * Does not throw anything, as long as the currentLine represents either
	 * an entry with or without a value.
	 * 
	 * @throws StorageFileParseException	If the statement could not be parsed.
	 */
	private void anyEntry() throws StorageFileParseException
	{
		try
		{
			valueEntry();
			System.out.println("line v: \t\t" + line);
		}
		catch (NoSuchElementException e)
		{
			currentLine = ArrayQueue.makeCharacterArrayQueue(currentLineStr);
			noValueEntry();
			System.out.println("line nv: \t\t" + line);
		}
	}

	private void noValueEntry() throws StorageFileParseException
	{
		entryStart();
		anyWhiteSpace();
		lineEnd();
	}

	private void valueEntry() throws StorageFileParseException
	{
		entryStart();
		anyWhiteSpace();
		keyValueSeparator();
		anyWhiteSpace();
		value();
		anyWhiteSpace();
		lineEnd();
	}

	private void entryStart() throws StorageFileParseException
	{
		leadingWhiteSpace();
		key();
	}

	private void leadingWhiteSpace()
	{	
		if(currentLine.peak() == '\t')
		{
			currentLine.pull();
			leadingWhiteSpace();
		}
	}

	private void key() throws StorageFileParseException
	{
		keyValueEnclosure();
		keyStr();
		keyValueEnclosure();
	}

	private void keyValueEnclosure() throws StorageFileParseException
	{
		if (currentLine.peak() != '"')
		{
			throw new StorageFileInvalidKeyEnclosureException(line,
					getColumn(), currentLine.peak());
		}
		
		currentLine.pull();
	}

	private void keyStr() throws StorageFileParseException
	{
		if(currentLine.peak() != '"')
		{
			if (currentLine.peak() == '.')
			{
				throw new StorageFileInvalidKeyCharacterException(line,
						getColumn(), currentLine.peak());
			}
			
			currentLine.pull();
			
			keyStr();
		}
	}

	private void keyValueSeparator() throws StorageFileParseException
	{
		if (currentLine.peak() != '=')
		{
			throw new StorageFileInvalidSeparatorException(line, getColumn(),
					currentLine.peak());
		}
		
		currentLine.pull();
	}

	private void value() throws StorageFileParseException
	{
		keyValueEnclosure();
		valueStr();
		keyValueEnclosure();
	}

	private void valueStr()
	{
		if(currentLine.peak() != '"')
		{
			currentLine.pull();
			
			valueStr();
		}
	}

	private void lineEnd() throws StorageFileParseException
	{
		if (!currentLine.isEmpty())
		{
			throw new StorageFileInvalidValueCharacterException(line,
					getColumn(), currentLine.peak());
		}
	}
	
	private void anyWhiteSpace()
	{
		if(!currentLine.isEmpty() &&
				(currentLine.peak() == ' ' || currentLine.peak() == '\t'))
		{
			currentLine.pull();
			
			anyWhiteSpace();
		}
	}
	
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
