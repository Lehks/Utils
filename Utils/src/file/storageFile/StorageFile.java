package file.storageFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exception.StorageFileException;
import utils.Pointer;

public class StorageFile
{
	/**
	 * The message that is being printed, if a key that should be added does
	 * already exist.
	 */
	public static final String MSG_KEY_ALREADY_EXISTING = 
								"The key \"%s\" in line %d already exists.";
	
	/**
	 * The message that is being printed, if <code>.load()</code> reads a
	 * faulty line.
	 */
	public static final String MSG_INVALID_LINE = "Line %d is invalid.";
	
	/**
	 * The message that is being printed, if the file that is passed to 
	 * a constructor is actually a directory.
	 */
	public static final String MSG_FILE_IS_DIRECTORY = "The passed file is a "
														+ "directory.";
	
	/**
	 * The message that is being printed, if an entry has an invalid depth.
	 */
	public static final String MSG_INVALID_DEPTH = "The depth of the line in %s"
															+ " is invalid.";
	
	/**
	 * The character that marks a comment.
	 */
	public static final char COMMENT_PREFIX = '#';
	
	/**
	 * The character that is used to separate local keys within a global key.
	 */
	public static final char PATH_SEPARATOR = '.';
	
	/**
	 * The character that separates a key from a value ("KEY"="VALUE", = is 
	 * said separator).
	 */
	public static final char KEY_VALUE_SEPARATOR = '=';
	
	/**
	 * The character that encloses keys and values.
	 */
	public static final char KEY_VALUE_PERIMETER = '"';
	
	/**
	 * The regex pattern that stands for any amount of tabs. 
	 */
	private static final String SUBPATTERN_ANY_TAB = "([\\t]*)";
	
	/**
	 * The regex pattern that stands for a key (&rarr; a character string of 
	 * arbitrary length that does not contain the KEY_VALUE_SEPERATOR and that
	 * starts and end with the KEY_VALUE_PERIMETER).
	 */
	private static final String SUBPATTERN_KEY = KEY_VALUE_PERIMETER 
												+ "([^/.]*)" 
												+ KEY_VALUE_PERIMETER;
	
	/**
	 * The regex pattern that stands for a key (&rarr; a character string of 
	 * arbitrary length and starts and end with the KEY_VALUE_PERIMETER).
	 */
	private static final String SUBPATTERN_VALUE = KEY_VALUE_PERIMETER 
												+ "(.*)" 
												+ KEY_VALUE_PERIMETER;
	
	/**
	 * The regex pattern wrapper for the PATH_SEPERATOR (this is usually a ".",
	 * which stands for any character in an regular expression and must
	 * therefore be wrapped in [ ] ).
	 */
	private static final String PATH_SEPERATOR_REGEX = "[" 
														+ PATH_SEPARATOR 
														+ "]";
	
	/**
	 * The regex pattern that stands for a dummy entry into a StorageFile.
	 */
	static final Pattern PATTERN_VALUE = Pattern.compile("^" 
											+ SUBPATTERN_ANY_TAB
											+ SUBPATTERN_KEY 
											+ KEY_VALUE_SEPARATOR
											+ SUBPATTERN_VALUE
											+ "$");
	
	/**
	 * The regex pattern that stands for a normal entry into a StorageFile.
	 */
	static final Pattern PATTERN_NO_VALUE = Pattern.compile("^" 
											+ SUBPATTERN_ANY_TAB 
											+ SUBPATTERN_KEY
											+ "$");
	
	/**
	 * The parent of all entries that have the depth 0.
	 */
	private Entry rootEntry;
	
	/**
	 * The file that the StorageFile will be read from (and saved to with 
	 * <code>.save()</code>).
	 */
	private File file;
	
	/**
	 * Constructs a new {@link StorageFile}.
	 * 
	 * @param file			The file that the data will be read from. If it 
	 * 						does not exist, it will be created.
	 * @throws IOException 	If an I/O Error occurred.
	 */
	public StorageFile(File file) throws IOException
	{
		this.file = file;
		this.rootEntry = new Entry(null, "ROOT", "ROOT");
		
		if(file.isDirectory())
			throw new StorageFileException(MSG_FILE_IS_DIRECTORY);
		
		if(!file.exists())
			file.createNewFile();
		
//		load();
		rootEntry = new StorageFileLoader().load(file);
	}
	
	/**
	 * Constructs a new {@link StorageFile}. This constructor calls
	 * <code>StorageFile(File file)</code> using a new File that holds
	 * 'path' as path.
	 * 
	 * @param path			The path to the file.
	 * @throws IOException 	If an I/O Error occurred.
	 */
	public StorageFile(String path) throws IOException
	{
		this(new File(path));
	}
	
	/**
	 * Searches for the entry that has the passed key. If said entry could be
	 * found, this entry's value will be returned, if not null will be 
	 * returned.
	 * 
	 * @param key	The key of the entry to find.
	 * @return		The value of the entry, or null if the entry does not
	 * 				exist.
	 */
	public String get(String key)
	{
		Entry entry = getEntry(key, false);
		
		if(entry != null)
			return entry.getValue();
		
		return null;
	}
	
	/**
	 * Sets the value of an existing entry or adds a new entry if it does not
	 * exist yet (an then sets the value of that new entry to the passed 
	 * value).
	 * 
	 * @param key	The key of the entry to search for / create.
	 * @param value	The value of the entry that is identified by 'key'.
	 */
	public void set(String key, String value)
	{
		Entry entry = getEntry(key, true);
		
		entry.setValue(value);
	}
	
	/**
	 * Searches the entry-tree for the entry with the passed key. If 'create'
	 * is true, then the entry with the key 'key' will be created in the
	 * case that it does not exist yet.
	 * 
	 * @param key		The key to the entry to search for.
	 * @param create	If true, the entry that is identified by 'key' will
	 * 					be created if it does not exist yet (if false, noting
	 * 					will be created).
	 * @return			The entry that is identified by 'key', or null, if
	 * 					'create' is false and the specified entry could not
	 * 					be found.
	 */
	private Entry getEntry(String key, boolean create)
	{
		String[] splitStrings = key.split(PATH_SEPERATOR_REGEX);
		
		String[] subkeys = new String[splitStrings.length + 1];
				
		System.arraycopy(splitStrings, 0, subkeys, 1, splitStrings.length);
		
		subkeys[0] = rootEntry.getLocalKey();
		
		return rootEntry.get(subkeys, 0, create);
	}
	
	/**
	 * Returns weather an entry with the passed key already exists or not.
	 * 
	 * @param key	The key of the entry to search for.
	 * @return		True, if it exsists, false if not.
	 */
	public boolean contains(String key)
	{
		return getEntry(key, false) != null;
	}
	
	/**
	 * Returns the file that was passed to the constructor.
	 * @return The file.
	 */
	public File getFile()
	{
		return file;
	}
	
	/**
	 * Saves the StorageFile to the specified file.
	 * 
	 * @param file			The file to save to. If this file does not exist 
	 * 						yet, it will be created.
	 * 
	 * @throws IOException	If an I/O error occurred.
	 */
	public void save(File file) throws IOException
	{
		FileWriter fw = new FileWriter(file);
		
		fw.append(toString());
		
		fw.close();
	}
	
	/**
	 * Saves the StorageFile to the file at the specified path.
	 * 
	 * @param path			The path to the file that the file will be saved 
	 * 						to.
	 * @throws IOException	If an I/O error occurred.
	 */
	public void save(String path) throws IOException
	{
		save(new File(path));
	}

	/**
	 * Saves the StorageFile to the file that was originally passed to the 
	 * constructor.
	 * 
	 * @throws IOException If an I/O error occurred.
	 */
	public void save() throws IOException
	{
		save(file);
	}
	
	/**
	 * Returns this StorageFile as String. This is exactly what will be written
	 * to a file by <code>.save(...)</code>.
	 * 
	 * @return The StorageFile a {@link String}.
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		rootEntry.asPrintable(sb, -1);
		
		return sb.toString();
	}
}
