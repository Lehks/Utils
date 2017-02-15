package file.storageFile;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.Scanner;

import container.ArrayQueue;
import exception.storageFile.StorageFileEmptyKeyException;
import exception.storageFile.StorageFileInvalidKeyCharacterException;
import exception.storageFile.StorageFileInvalidKeyEnclosureException;
import exception.storageFile.StorageFileInvalidSeparatorException;
import exception.storageFile.StorageFileInvalidValueCharacterException;
import exception.storageFile.StorageFileParseException;
import utils.Utils;

/**
 * 
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see		StorageFileLoader
 * @see		StorageFile
 */
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
	 * The stream that the byte data is written to.
	 */
	private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	
	private EntryData entryData = new EntryData();
	
	private State state = State.NOT_STARTED;
	
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

	/**
	 * Attempts to parse the data provided via the constructors and, if
	 * successful, returns the results in a stream. If the parsing failed,
	 * various exceptions can be thrown, all of which are a subclass of
	 * {@link StorageFileParseException}.<br>
	 * As soon as the parsing has finished without throwing anything, 
	 * <code>.isDone()</code> will return true.<br>
	 * If it is attempted to restart the parser if it has already been started,
	 * this method returns the same as <code>.getOutput()</code> would
	 * return.
	 * 
	 * @return								A stream with the results.
	 * @throws StorageFileParseException	If the parsing failed.
	 */
	public ByteArrayInputStream parse() throws StorageFileParseException
	{
		if(!isDone())
			return getOutput();
			
		/*
		 * The program won't get here a second time after .parse() has been
		 * called once; state will be updated to FINISHED, after the method
		 * finished successfully.
		 */
		state = State.FAILED;
		
		Scanner scanner = new Scanner(reader);

		while (scanner.hasNextLine())
		{
			line++;

			currentLineStr = scanner.nextLine();
			
			currentLine = ArrayQueue.makeCharacterArrayQueue(currentLineStr);
			
			/*
			 * Needs a ' "" + ' ahead of it, since COMMENT_PREFIX is a char 
			 * and String.startsWith(...) only takes a String.
			 */
			if (!currentLineStr.trim().startsWith
									("" + StorageFileConstants.COMMENT_PREFIX))
				expr_anyEntry();
		}
		
		state = State.FINISHED;
		
		scanner.close();
		
		return new ByteArrayInputStream(outputStream.toByteArray());
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
			entryData.setType(StorageFileConstants.BINARY_TYPE_VALUE);
			System.out.println("line v: \t\t" + line);
		}
		catch (NoSuchElementException e)
		{
			reset();
			currentLine = ArrayQueue.makeCharacterArrayQueue(currentLineStr);
			expr_noValueEntry();
			entryData.setType(StorageFileConstants.BINARY_TYPE_NO_VALUE);
			System.out.println("line nv: \t\t" + line);
		}
		
		writeToBufferAndReset();
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
		while(currentLine.peak() == '\t')
		{
			currentLine.pull();
			entryData.setDepth(entryData.getDepth() + 1);
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
		if (currentLine.peak() != StorageFileConstants.KEY_VALUE_PERIMETER)
		{
			throw new StorageFileInvalidKeyEnclosureException(line,
					getColumn(), currentLine.peak());
		}
		
		currentLine.pull();
	}

	/**
	 * Allows any nonempty string that also does not contain a '.'.
	 * 
	 * @throws StorageFileParseException	If the statement could not be 
	 * 										parsed.
	 */
	private void expr_keyStr() throws StorageFileParseException
	{
		while(currentLine.peak() != StorageFileConstants.KEY_VALUE_PERIMETER)
		{
			if (currentLine.peak() == StorageFileConstants.PATH_SEPARATOR)
			{
				throw new StorageFileInvalidKeyCharacterException(line,
						getColumn(), currentLine.peak());
			}
			
			entryData.getKey().append(currentLine.pull());
		}
		
		if(entryData.getKey().length() == 0)
		{
			throw new StorageFileEmptyKeyException(line, getColumn(),
																currentLine.peak());
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
		if (currentLine.peak() != StorageFileConstants.KEY_VALUE_SEPARATOR)
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
		while(currentLine.peak() != StorageFileConstants.KEY_VALUE_PERIMETER)
		{
			entryData.getValue().append(currentLine.pull());
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
		while(!currentLine.isEmpty() &&
				(currentLine.peak() == ' ' || currentLine.peak() == '\t'))
		{
			currentLine.pull();
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
	
	/**
	 * Returns the generated byte data in a stream to be read from. This method
	 * can be called multiple times and it will always return a new stream.<br>
	 * If the parser has not been started yet or the parsing failed, this method
	 * will return null.
	 * 
	 * @return The stream to read from.
	 */
	public ByteArrayInputStream getOutput()
	{
		if(!isDone())
			return null;
		
		return new ByteArrayInputStream(outputStream.toByteArray());
	}
	
	/**
	 * Returns, weather the parsing has been finished or not. <br>
	 * If false is returned, this can have two reasons: Either, <code>
	 * .parse()</code> has not been called yet, or it has been called but
	 * failed.
	 * 
	 * @return True, if parsing has been done successfully, false if not.
	 */
	public boolean isDone()
	{
		return state == State.FINISHED;
	}
	
	/**
	 * If parsing was successful, the byte data will be written to the passed
	 * {@link OutputStream}. If the parsing was not successful (<code>.isDone()
	 * </code> would return true), nothing will happen.
	 * 
	 * @param ostream		The stream to write to.
	 * @throws IOException	If an I/O error occurred when writing to the 
	 * 						{@link OutputStream}.
	 */
	public void write(OutputStream ostream) throws IOException
	{
		if(!isDone())
			return;
		
		BufferedOutputStream stream;
		
		if(ostream instanceof BufferedOutputStream)
			stream = (BufferedOutputStream) ostream;
		else
			stream = new BufferedOutputStream(ostream);
		
		stream.write(outputStream.toByteArray());
	}
	
	/**
	 * Writes the current parsing results to the output stream and calls
	 * <code>.reset()</code> afterwards.
	 */
	private void writeToBufferAndReset()
	{
		entryData.write(outputStream);
		
		reset();
	}
	
	/**
	 * Constructs a new {@link EntryData} and makes 'entryData' reference this
	 * new instance, which allows that new instance to be used to parse the
	 * next entry.
	 */
	private void reset()
	{
		entryData = new EntryData();
	}
	
	/**
	 * A class to hold information about an entry.
	 * 
	 * @author Lukas Reichmann
	 */
	private class EntryData
	{
		/**
		 * The type of the entry. This is either BINARY_TYPE_NO_VALUE if the
		 * entry does not hold a value, or BINARY_TYPE_VALUE if the entry
		 * does hold a value.
		 */
		private int type				= 0;
		
		/**
		 * The depth of the entry.
		 */
		private int depth				= 0;
		
		/**
		 * The key of the entry.
		 */
		private StringBuilder key		= new StringBuilder();
		
		/**
		 * The value of the entry.
		 */
		private StringBuilder value		= new StringBuilder();
		
		/**
		 * Sets the type of the entry.
		 * @param type 	The new type.
		 */
		public void setType(int type)
		{
			this.type = type;
		}
		
		/**
		 * Returns the depth of the entry.
		 * @return 	The depth.
		 */
		public int getDepth()
		{
			return depth;
		}

		/**
		 * Sets the type of the entry.
		 * @param type 	The new type.
		 */
		public void setDepth(int depth)
		{
			this.depth = depth;
		}
		
		/**
		 * Returns the key of the entry.
		 * @return 	The key.
		 */
		public StringBuilder getKey()
		{
			return key;
		}
		
		/**
		 * Returns the value of the entry.
		 * @return 	The value.
		 */
		public StringBuilder getValue()
		{
			return value;
		}
		
		/**
		 * Writes the entry data into the passed {@link ByteArrayOutputStream}.
		 * @param stream	The stream to write to.
		 */
		public void write(ByteArrayOutputStream stream)
		{
			try
			{
				stream.write(type);
				stream.write(Utils.toByteArray(depth));
				stream.write(Utils.toByteArray(key.length()));
				
				stream.write(key.toString().getBytes());

				if(type == StorageFileConstants.BINARY_TYPE_VALUE)
				{
					outputStream.write(Utils.toByteArray(value.length()));
					outputStream.write(value.toString().getBytes());
				}
			}
			catch (IOException e) 
			{
				/*
				 * With a ByteArrayOutputStream, this exception should never 
				 * happen.
				 * (Still needs catching, since the exception is declared in
				 * OutputStream)
				 */
			}
		}
	}
	
	private enum State
	{
		NOT_STARTED,
		FAILED,
		FINISHED
	}
}
