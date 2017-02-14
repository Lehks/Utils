package file.storageFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

import exception.StorageFileException;

/**
 * A class to store and read data in a file using a special format.<br>
 * <br>
 * The very basic concept is at follows:<br>
 * <br>
 * Key0=Value0<br>
 * Key1=Value1<br>
 * Key2=Value2<br>
 * <br>
 * {@literal *}note that this only a concept, and not how a StorageFile is
 * actually written.<br>
 * <br>
 * This means, that each value has a unique key assigned to it. These keys are
 * also the way to access a value (the method to access values is <code>
 * .get(...)</code>, so in order to access "Value1", <code>.get("Key1")</code>
 * needs to be called). The combination of a Key and its according value is
 * called an entry.<br>
 * In addition to the structure mentioned above, it is also possible for
 * an entry to have multiple entries as children (the owning entry is then
 * called the parent). This transforms the file into a tree of entries that
 * looks like this:<br>
 * <br>
 * Key0=Value0<br>
 * Key0.Key1=Value1<br>
 * Key2=Value2<br>
 * <br>
 * In this example, the entry with the Key0.Key1 is a child of the entry with
 * the key Key0.<br>
 * In addition, there is a difference between global and local keys. In the
 * previous example, "Key0.Key1" is the global key and "Key1" the local one.
 * This means, that global key is a unique identifier for an entry in the
 * entire tree and the local key is just unique among the children of its
 * parent. Global keys are simply local keys separated by a '.'.<br>
 * Children can also have other children, so something like this is perfectly
 * fine:<br>
 * <br>
 * Key0=Value0<br>
 * Key0.Key1=Value1<br>
 * Key0.Key1.Key2=Value2<br>
 * Key0.Key3=Value3<br>
 * Key4=Value4<br>
 * <br>
 * <br>
 * In order to access a value using <code>.get(...)</code>, the global key 
 * must always be passed.<br>
 * <br>
 * <br>
 * Also, it is possible for an entry to not have a value, which is meant to
 * ease organization. These entries are also called dummy entries, whereas
 * such entries that hold a value are called normal entries. When calling
 * <code>.get(...)</code> to get a dummy value, null will be returned.<br>
 * <br>
 * This is example to show how dummy entries can be used:<br>
 * <br>
 * messages<br>
 * messages.error<br>
 * messages.error.Error0=Error Message 0<br>
 * messages.error.Error1=Error Message 1<br>
 * messages.error.Error2=Error Message 2<br>
 * messages.general<br>
 * messages.general.succes=Success<br>
 * messages.general.done=Done<br>
 * <br>
 * In this case, dummy entries are used to organize the messages into different
 * sections according to their usage.<br>
 * <br>
 * <br>
 * To add new entries to a loaded file from inside a program, the method 
 * <code>.set(String key, String value)</code> is used. This method takes
 * a global key and the value for the entry. It is also possible to change 
 * the value of an already existing entry with this method in exactly the same
 * way.<br>
 * <br>
 * It is also possible to write directly to the file. Those files look like
 * this:<br>
 * <pre>
 *"Key0"="Value0"
 *	"Key1"="Value1"
 *		"Key2"="Value2"
 *	"Key3"="Value3"
 *"Key4"="Value4"
 * </pre>
 * Obviously, in this example there are 5 entries, all of them have the local
 * (!) keys and values numbered from 0-4. In the file itself, tab intends 
 * before the key are used to create the parent/child hierarchy. The parent
 * of an entry is always the next entry above the child entry that has one
 * tab less ahead of it. Those tabs are also called depth, so the entry with
 * the global key "Key0.Key1" has the depth 1 and "Key0.Key1.Key2" 2.<br>
 * Both the key and the value are enclosed in quotation marks.<br>
 * <br>
 * It is also possible to have comments in a file. All lines mark as comment,
 * as soon as the line starts with a # (The # must be at the very start of the
 * line, there is no whitespace allowed ahead of it).
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 *
 */
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
	 * The regex pattern that stands for any amount of tabs. 
	 */
	private static final String SUBPATTERN_ANY_TAB = "([\\t]*)";
	
	/**
	 * The regex pattern that stands for a key (&rarr; a character string of 
	 * arbitrary length that does not contain the KEY_VALUE_SEPERATOR and that
	 * starts and end with the KEY_VALUE_PERIMETER).
	 */
	private static final String SUBPATTERN_KEY = 
									StorageFileConstants.KEY_VALUE_PERIMETER 
									+ "([^/.]*)" 
									+ StorageFileConstants.KEY_VALUE_PERIMETER;
	
	/**
	 * The regex pattern that stands for a key (&rarr; a character string of 
	 * arbitrary length and starts and end with the KEY_VALUE_PERIMETER).
	 */
	private static final String SUBPATTERN_VALUE = 
									StorageFileConstants.KEY_VALUE_PERIMETER 
									+ "(.*)" 
									+ StorageFileConstants.KEY_VALUE_PERIMETER;
	
	/**
	 * The regex pattern wrapper for the PATH_SEPERATOR (this is usually a ".",
	 * which stands for any character in an regular expression and must
	 * therefore be wrapped in [ ] ).
	 */
	private static final String PATH_SEPERATOR_REGEX = 
									"[" 
									+ StorageFileConstants.PATH_SEPARATOR 
									+ "]";
	
	/**
	 * The regex pattern that stands for a dummy entry into a StorageFile.
	 */
	static final Pattern PATTERN_VALUE = 
									Pattern.compile("^" 
									+ SUBPATTERN_ANY_TAB
									+ SUBPATTERN_KEY 
									+ StorageFileConstants.KEY_VALUE_SEPARATOR
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
	 * The depth of the root entry.
	 */
	static final int ROOT_DEPTH = -1;
	
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
	 * @return		True, if it exists, false if not.
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
	 * @return The StorageFile as a {@link String}.
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		rootEntry.asPrintable(sb, ROOT_DEPTH);
		
		return sb.toString();
	}
}
