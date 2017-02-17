package file.storageFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import exception.EntryDuplicateException;
import exception.IllegalDepthException;
import utils.Messages;
import utils.Utils;

/**
 * Loads a StorageFile from byte data (See {@link StorageFileParser} for 
 * documentation about byte data). This class relies on the byte data being
 * free from errors, since it does no error checks on things that were 
 * already checked by the  {@link StorageFileParser} (very important, if it is 
 * attempted to load faulty byte data, it is possible for the program to throw 
 * e.g. a {@link OutOfMemoryError}).<br>
 * The only checking that the loader does is:<br>
 * Depth checks: The loader will not succeed if an entry has an invalid depth
 * (e.g. an entry has a depth of 2 but its parent has a depth of 0).<br>
 * Duplication checks: If two or more entries share the same global key.<br>
 * After all, the {@link StorageFileLoader} is not a class that is used by
 * a user, since it returns a {@link Entry} which again is not usable on its
 * own.
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see		StorageFile
 * @see		StorageFileParser
 *
 */
public class StorageFileLoader
{
	/**
	 * The stream that stores the byte data.
	 */
	private BufferedInputStream istream;
	
	/**
	 * The last entry that was added.
	 */
	private Entry previousEntry;
	
	/**
	 * The entry that is currently getting processed.
	 */
	private Entry currentEntry;
	
	/**
	 * The depth of 'previousEntry' (Stored here, since the entry does not 
	 * store it's depth).
	 */
	private int previousDepth;

	/**
	 * The depth of 'currentEntry' (Stored here, since the entry does not store
	 * it's depth).
	 */
	private int currentDepth;
	
	/**
	 * Loads the {@link StorageFile} using the byte data in the passed file.
	 * 
	 * @param file						The file that contains the byte data.
	 * @throws FileNotFoundException	If the file could not be read.
	 */
	public StorageFileLoader(File file) throws FileNotFoundException
	{
		istream = new BufferedInputStream(new FileInputStream(file));
	}
	
	/**
	 * Loads the {@link StorageFile} using the byte data in the passed stream.
	 * 
	 * @param istream	The stream that contains the byte data.
	 */
	public StorageFileLoader(InputStream istream)
	{
		if(istream instanceof BufferedInputStream)
			this.istream = (BufferedInputStream) istream;
		else
			this.istream = new BufferedInputStream(istream);
	}
	
	/**
	 * Loads the {@link StorageFile} from the resource that was passed in the 
	 * constructor.
	 * 
	 * @return							The root of the read 
	 * 									{@link StorageFile}.
	 * @throws IOException				If an I/O error occurred.
	 * @throws IllegalDepthException	If one of the entries has an illegal 
	 * 									depth.
	 * @throws EntryDuplicateException	If two or more entries share the same 
	 * 									global key.
	 */
	public Entry load() throws IOException, IllegalDepthException, 
							EntryDuplicateException
	{
		Entry root = new Entry(null, null, null); //Construct root entry
		
		previousEntry = root;
		
		previousDepth = StorageFileConstants.ROOT_DEPTH;
		
		for(readEntry(); currentEntry != null; readEntry())
		{
			if(currentDepth == previousDepth)
			{
				previousEntry.getParent().addChild(currentEntry);
			}
			else if(currentDepth == previousDepth + 1)
			{
				previousEntry.addChild(currentEntry);
			}
			else if(currentDepth < previousDepth)
			{
				Entry parent = previousEntry.getParent();
				
				for(int i = previousDepth; i > currentDepth; i--)
				{
					parent = parent.getParent();
				}
				
				parent.addChild(currentEntry);
			}
			else
			{
				throw new IllegalDepthException(String.format
						(Messages.ILLEGAL_DEPTH, currentDepth, currentDepth + 1));
			}
			
			//Update previous* attributes with the one from this iteration
			previousDepth = currentDepth;
			previousEntry = currentEntry;
		}
		
		return root;
	}
	
	/**
	 * Reads the next entry from the input stream and stores it in 
	 * 'currentEntry'. Also updates 'currentDepth'.
	 * 
	 * @throws IOException If an I/O error occurred.
	 */
	private void readEntry() throws IOException
	{
		byte type = (byte) istream.read();
		
		if(type == -1)
		{
			currentEntry = null;
			return;
		}
		
		currentDepth = readNextInt();
		
		String key = readNextString();
		
		String value = null;
		
		if(type == StorageFileConstants.BYTE_TYPE_NORMAL)
		{
			value = readNextString();
		}
		
		int commentBufferSize = readNextInt();
		
		ArrayList<String> commentBuffer = new ArrayList<>(commentBufferSize);
		
		for(int i = 0; i < commentBufferSize; i++)
			commentBuffer.add(readNextString());
		
		currentEntry = new Entry(commentBuffer, key, value);
	}
	
	/**
	 * Reads the next 4 bytes in the input stream and converts them into an int 
	 * using <code>{@link Utils}.toInt(...)</code>.
	 * 
	 * @return				The read int.
	 * @throws IOException	If an I/O error occurred.
	 */
	private int readNextInt() throws IOException
	{
		byte[] retArray = new byte[Utils.INT_SIZE_BYTE];
		
		istream.read(retArray);
		
		return Utils.toInt(retArray);
	}
	
	/**
	 * Reads the next string in the input stream by first reading an int that
	 * is the lenght of the string in bytes and then reading the bytes that 
	 * make up the string.
	 * 
	 * @return				The read string.
	 * @throws IOException	If an I/O error occurred.
	 */
	private String readNextString() throws IOException
	{
		int lenght = readNextInt();

		byte[] retArray = new byte[lenght];
		
		istream.read(retArray);

		return new String(retArray);
	}
}
