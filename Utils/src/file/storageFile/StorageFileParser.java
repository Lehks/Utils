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
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import container.ArrayQueue;
import exception.storageFileParser.StorageFileEmptyKeyException;
import exception.storageFileParser.StorageFileInvalidKeyCharacterException;
import exception.storageFileParser.StorageFileInvalidKeyEnclosureException;
import exception.storageFileParser.StorageFileInvalidSeparatorException;
import exception.storageFileParser.StorageFileInvalidValueCharacterException;
import exception.storageFileParser.StorageFileParseException;
import utils.Messages;
import utils.Utils;

/**
 * A class that parses a {@link StorageFile}, aka. do syntax error checks
 * and, in the case that no such errors were found, it will output byte data, 
 * which then can be loaded by a {@link StorageFileLoader}.<br>
 * It is also possible to export this byte data into e.g. a file (or to be more
 * specific, into any {@link OutputStream}).<br>
 * Afterwards, a {@link StorageFileLoader} can load a {@link StorageFile} 
 * directly from said {@link OutputStream}.<br>
 * The usage of the {@link StorageFileParser} is actually not all that hard, 
 * in order to parse a File (or the content of a Reader), the respective 
 * constructor needs to be called. Afterwards, <code>.parse()</code> will start 
 * the parsing.<br>
 * If <code>.parse()</code> does finish without throwing any exceptions, 
 * the it was successful and the returned {@link ByteArrayInputStream}
 * holds the parsed byte data (This stream can also be retrieved afterwards
 * by calling <code>.getOutput()</code>).<br>
 * In the case of a syntax error, <code>.parse()</code> will throw a subclass
 * of {@link StorageFileParseException} (depending on the error 
 * that occurred).<br>
 * Note that the parser only does per line error checks, so e.g. duplication
 * of keys or invalid depths are done by the {@link StorageFileLoader}.
 * <br>
 * In the following, the structure of the byte data is explained:<br>
 * Before the entire structure can be explained, the single constructs that
 * make up the byte data need to be clarified.<br>
 * The byte data is made up of multiple parts and each part has its own data
 * type and a name. This combination is given as follows: data type : name.
 * Those parts are always separated by a semicolon.<br>
 * These are the available data types (they map directly to the corresponding
 * Java data types):<br>
 * byte: A single byte. These can be written directly to the byte data.<br>
 * int: A number made up of four bytes. They are being split up to bytes
 * using <code>{@link Utils}.toByteArray(...)</code>.<br>
 * string: A sequence of characters. When writing a string, the length
 * of the string in bytes is written and then the content of the string (in 
 * bytes) will be written.<br>
 * <br>
 * This is what a single entry looks like:<br>
 * { byte : type ; int : depth ; string : key ; ( string : value ) ; 
 * int: comment amount ; [ string : comment ]<sub>i</sub> }<br>
 * <br>
 * Type: The type of the entry. This is either BYTE_TYPE_DUMMY 
 * ({@value file.storageFile.StorageFileConstants#BYTE_TYPE_DUMMY}), if the 
 * entry is a dummy entry, or BYTE_TYPE_NORMAL 
 * ({@value file.storageFile.StorageFileConstants#BYTE_TYPE_NORMAL}), if the 
 * entry is a normal entry.<br>
 * Depth: The depth of the entry.<br>
 * Key: The key of the entry.<br>
 * Value: The value of the entry. This is only present, if the type is 
 * BYTE_TYPE_NORMAL (therefore the round braces).<br>
 * Comment amount: The amount of comments that come with the entry. This is 
 * always  &ge; 0.<br>
 * Comments: The comments that come with the key. These comments repeat i 
 * times, whereas i &#8712; N and 0 &lt; i &le; comment amount. If comment 
 * amount = 0, this part will not be present.
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
	
	/**
	 * The data of the entry that is currently being parsed.
	 */
	private EntryData entryData = new EntryData();
	
	/**
	 * Buffers the comments that will be passed to the next entry. This is
	 * stored outside the entry data, since the entry data will be reset
	 * even if the parsing has not failed yet.
	 */
	private LinkedList<String> commentBuffer = new LinkedList<>();
	
	/**
	 * The current state of the parser.
	 */
	private State state = State.NOT_STARTED;
	
	/**
	 * Constructs a new {@link StorageFileParser} that will parse the content
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
	 * Constructs a new {@link StorageFileParser} that will parse the content
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
		if(state != State.NOT_STARTED)
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
			
			if(!currentLineStr.trim().isEmpty())
			{
				/*
				 * At this point, currentLineStr will always have length > 0
				 */
				if (currentLineStr.charAt(0) !=
											StorageFileConstants.COMMENT_PREFIX)
				{
					expr_anyEntry();
				}
				else
				{
					commentBuffer.push(currentLineStr.substring(1));
				}
			}
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
			entryData.setType(StorageFileConstants.BYTE_TYPE_NORMAL);
		}
		catch (NoSuchElementException e)
		{
			reset(false);
			currentLine = ArrayQueue.makeCharacterArrayQueue(currentLineStr);
			expr_noValueEntry();
			entryData.setType(StorageFileConstants.BYTE_TYPE_DUMMY);
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
		//TAB is a string with the length of 0, peak() returns a char
		while(currentLine.peak() == StorageFileConstants.TAB.charAt(0))
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
				(currentLine.peak() == ' ' || 
				currentLine.peak() == StorageFileConstants.TAB.charAt(0)))
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
	 * </code> would return true), nothing will happen.<br>
	 * This method will call <code>{@link OutputStream}.close()</code>.
	 * 
	 * @param ostream		The stream to write to.
	 * @throws IOException	If an I/O error occurred when writing to the 
	 * 						{@link OutputStream}.
	 */
	public void write(OutputStream ostream) throws IOException
	{
		if(!isDone())
			throw new IllegalStateException(Messages.STREAM_CLOSED);
		
		BufferedOutputStream stream;
		
		if(ostream instanceof BufferedOutputStream)
			stream = (BufferedOutputStream) ostream;
		else
			stream = new BufferedOutputStream(ostream);
		
		stream.write(outputStream.toByteArray());
		
		stream.close();
	}
	
	/**
	 * Writes the current parsing results to the output stream and calls
	 * <code>.reset()</code> afterwards.
	 */
	private void writeToBufferAndReset()
	{
		entryData.write(outputStream);
		
		reset(true);
	}
	
	/**
	 * Constructs a new {@link EntryData} and makes 'entryData' reference this
	 * new instance, which allows that new instance to be used to parse the
	 * next entry.
	 */
	private void reset(boolean resetCommentBuffer)
	{
		entryData = new EntryData();
		
		if(resetCommentBuffer)
			commentBuffer.clear();
	}
	
	/**
	 * A class to hold information about an entry.
	 * 
	 * @author Lukas Reichmann
	 */
	private class EntryData
	{
		/**
		 * The type of the entry. This is either BYTE_TYPE_DUMMY if the
		 * entry does not hold a value, or BYTE_TYPE_NORMAL if the entry
		 * does hold a value.
		 */
		private byte type							= 0;
		
		/**
		 * The depth of the entry.
		 */
		private int depth							= 0;
		
		/**
		 * The key of the entry.
		 */
		private StringBuilder key					= new StringBuilder();
		
		/**
		 * The value of the entry.
		 */
		private StringBuilder value					= new StringBuilder();
		
		/**
		 * Sets the type of the entry.
		 * @param type 	The new type.
		 */
		public void setType(byte type)
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
		 * 
		 * @param stream	The stream to write to.
		 */
		public void write(ByteArrayOutputStream stream)
		{
			try
			{
				stream.write(type);
				
				writeInt(stream, depth);
				
				writeString(stream, key.toString());

				if(type == StorageFileConstants.BYTE_TYPE_NORMAL)
				{
					writeString(stream, value.toString());
				}
				
				writeInt(stream, commentBuffer.size());
				
				for(String s: commentBuffer)
					writeString(stream, s);
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
	
	/**
	 * Writes an int to the passed stream using <code>Utils.toByteArray()</code>.
	 * 
	 * @param stream		The stream to write into.
	 * @param i				The int to write.
	 * @throws IOException	If an I/O error occured.
	 */
	private void writeInt(ByteArrayOutputStream stream, int i) throws IOException
	{
		stream.write(Utils.toByteArray(i));
	}
	
	/**
	 * Writes a String to the passed stream using the following structure:<br>
	 * First, the size of the String in bytes is is written as an int.<br>
	 * Seccond, the String is converted to an byte array and this array is
	 * then written to the stream.
	 * 
	 * @param stream		The stream to write into.
	 * @param i				The int to write.
	 * @throws IOException	If an I/O error occured.
	 */
	private void writeString(ByteArrayOutputStream stream, String s)
																throws IOException
	{
		byte[] bytes = s.toString().getBytes();
		
		writeInt(stream, bytes.length);

		stream.write(bytes);
	}
	
	/**
	 * An enumeration to store the state of the parser internally.
	 * 
	 * @author 	Lukas Reichmann
	 * @version 1.0
	 */
	private enum State
	{
		/**
		 * If the parser has not been started yet (<code>.parse()</code>
		 * has not been called yet).
		 */
		NOT_STARTED,
		
		/**
		 * If <code>.parse()</code> has thrown a {@link StorageFileParseException}.
		 */
		FAILED,
		
		/**
		 * If <code>.parse()</code> has finished without any errors.
		 */
		FINISHED
	}
}
