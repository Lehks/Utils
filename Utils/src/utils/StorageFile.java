package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exception.StorageFileException;

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
	private static final char COMMENT_PREFIX = '#';
	
	/**
	 * The character that is used to separate local keys within a global key.
	 */
	private static final char PATH_SEPARATOR = '.';
	
	/**
	 * The character that separates a key from a value ("KEY"="VALUE", = is 
	 * said separator).
	 */
	private static final char KEY_VALUE_SEPARATOR = '=';
	
	/**
	 * The character that encloses keys and values.
	 */
	private static final char KEY_VALUE_PERIMETER = '"';
	
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
	private static final Pattern PATTERN_VALUE = Pattern.compile("^" 
											+ SUBPATTERN_ANY_TAB
											+ SUBPATTERN_KEY 
											+ KEY_VALUE_SEPARATOR
											+ SUBPATTERN_VALUE
											+ "$");
	
	/**
	 * The regex pattern that stands for a normal entry into a StorageFile.
	 */
	private static final Pattern PATTERN_NO_VALUE = Pattern.compile("^" 
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
		
		load();
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
	 * Loads the file that is currently stored in the attribute 'file'. This
	 * method is only called by the constructor.
	 * 
	 * @throws FileNotFoundException	If an I/O Error occurred.
	 */
	private void load() throws FileNotFoundException
	{
		Scanner scanner = new Scanner(file);
		
		int lastDepth = 0;				//Depth of the last entry
		Entry lastChild = null;			//The last Entry that was added
		Entry lastParent = rootEntry;	//The parent of lastChild
		
		int line = 0;					//The line that is currently processed
		
		ArrayList<String> commentBuffer = new ArrayList<>();
		
		while(scanner.hasNextLine())
		{
			line++;
			
			String nextLine = scanner.nextLine();
			
			if(nextLine.trim().isEmpty())
				continue; 	//If the current line is empty, there is no need 
							//to go on
			
			//Check if the line is a comment
			if(nextLine.charAt(0) == COMMENT_PREFIX)
			{
				commentBuffer.add(nextLine.substring(1));
				continue; //If line is a comment, there is no need to go on
			}

			//Fill currentEntry and currentDepth using regex
			int currentDepth = 0;
			Entry currentEntry = null;
			
			Matcher matcherValue = PATTERN_VALUE.matcher(nextLine);
			Matcher matcherNoValue = PATTERN_NO_VALUE.matcher(nextLine);
			
			if(matcherValue.find()) //If this is a entry with a value
			{
				currentDepth = matcherValue.group(1).length();
				currentEntry = new Entry(commentBuffer, matcherValue.group(2), 
						matcherValue.group(3));
			}
			else if(matcherNoValue.find()) //If this is a entry without a value
			{
				currentDepth = matcherNoValue.group(1).length();
				currentEntry = new Entry(commentBuffer, matcherNoValue.group(2), 
						null);
			}
			else 	//If both matchers could not match the current line, then
					//there is an invalid line -> error
			{
				scanner.close();
				throw new StorageFileException
									(String.format(MSG_INVALID_LINE, line));
			}
			//-------------------------
			
			/*
			 * If the loop has not been continued up to this point, this line
			 * is not a comment and the new Entry has been created 
			 * (=> this Entry's comments have been set), so the comment buffer
			 * will be a new instance of ArrayList to prevent the comments of
			 * the old entry from getting changed.
			 */
			commentBuffer = new ArrayList<>();
			
			lastParent = putEntryIntoTree(lastDepth, currentDepth, scanner, 
								currentEntry, lastParent, lastChild, line);
			
			lastChild = currentEntry;
			lastDepth = currentDepth;
			//-------------------------
		}
		
		scanner.close();
	}
	
	/**
	 * Puts 'currentEntry' into the tree structure. This method is only used
	 * in <code>.load()</code>.<br>
	 * Returns the new value for 'lastParent' in .load().
	 * 
	 * @param lastDepth		The depth of the last entry.
	 * @param currentDepth	The depth of the current entry.
	 * @param scanner		The scanner that is used to read the file.
	 * @param currentEntry	The entry to add.
	 * @param lastParent	The parent of the last entry.
	 * @param lastChild		The child that was added the last time.
	 * @param line			The current line in the file.
	 */
	private Entry putEntryIntoTree(int lastDepth, int currentDepth, 
			Scanner scanner, Entry currentEntry, Entry lastParent, 
			Entry lastChild, int line)
	{
		/*
		 * If the current entry has the same depth as the last one (=> the 
		 * current and last entry share the same parent).
		 */
		if (lastDepth == currentDepth)
		{
			checkKey(scanner, lastParent, currentEntry.getLocalKey(), line);

			lastParent.addChild(currentEntry);
			
			return lastParent;
		}
		
		/*
		 * If the current entry's depth is exactly one higher than the last one
		 * (=> the current entry is a child of the last one).
		 */
		if (lastDepth == currentDepth - 1)
		{
			checkKey(scanner, lastChild, currentEntry.getLocalKey(), line);

			lastChild.addChild(currentEntry);

			return lastChild;
		}
		
		/*
		 * If the current entry's depth is smaller of the last one 
		 */
		if (lastDepth > currentDepth)
		{
			Entry parent = lastParent;

			// Decrease depth until the correct depth has been reached
			for (int counter = lastDepth; counter > currentDepth; counter--)
				parent = parent.getParent();

			checkKey(scanner, parent, currentEntry.getLocalKey(), line);

			parent.addChild(currentEntry);

			return parent;
		}
		
		/*
		 * If none of the cases above matched, currentDepth is invalid
		 * (most likely currentDepth > (lastDepth + 1)).
		 */
		throw new StorageFileException(String.format(MSG_INVALID_DEPTH, line));
	}
	
	/**
	 * Checks if 'entry' has a child that hold the key 'key'.
	 * 
	 * If true, 'scanner' will be closed and an {@link StorageFileException} 
	 * with the message MSG_KEY_ALREADY_EXISTING will be thrown; if false,
	 * noting will happen.<br>
	 * This method is only called by <code>.load()</code>.
	 * 
	 * @param scanner	The scanner that will be closed.
	 * @param entry		The Entry that will be checked.
	 * @param key		The key to search for.
	 * @param line		The line in the read file that holds the Entry with the
	 * 					key 'key'. This is given to the exception.
	 */
	private void checkKey(Scanner scanner, Entry entry, String key, int line)
	{
		if(entry.hasChild(key))
		{
			scanner.close();
			throw new StorageFileException
					(String.format(MSG_KEY_ALREADY_EXISTING, key, line));
		}
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
	
	private class Entry
	{
		/**
		 * The comment that belong to this entry.
		 */
		private ArrayList<String> comments;
		
		/**
		 * The parent of this entry.
		 */
		private Entry parent;
		
		/**
		 * The children of this entry.
		 */
		private LinkedList<Entry> children;
		
		/**
		 * The local key of this entry.
		 */
		private String localKey;
		
		/**
		 * The value of this entry.
		 */
		private String value;
		
		/**
		 * Constructs a new Entry using the passed parameters (And setting 
		 * 'parent' to null and initializing 'children' with <code>
		 * new LinkedList<>()</code>).
		 * 
		 * @param comments	The comments that belong to the new entry.
		 * @param localKey	The local key of the new entry.
		 * @param value		The value of the new entry.
		 */
		public Entry(ArrayList<String> comments, String localKey, String value)
		{
			this.comments = comments;
			this.parent = null;
			this.children = new LinkedList<>();
			this.localKey = localKey;
			this.value = value;
		}
		
		/**
		 * Returns the parent.
		 * 
		 * @return the parent.
		 */
		public Entry getParent()
		{
			return parent;
		}
		
		/**
		 * Adds a child to the entry and sets the child's parent to this entry.
		 * 
		 * @param child	The new child.
		 */
		public void addChild(Entry child)
		{
			child.parent = this;
			children.add(child);
		}
		
		/**
		 * Returns the local key.
		 * 
		 * @return The local key.
		 */
		public String getLocalKey()
		{
			return localKey;
		}

		/**
		 * Returns the value.
		 * 
		 * @return The value.
		 */
		public String getValue()
		{
			return value;
		}
		
		/**
		 * Sets a new value for this entry.
		 * 
		 * @param value The new value.
		 */
		public void setValue(String value)
		{
			this.value = value;
		}
		
		/**
		 * Checks if this entry has a child with the local key child.
		 * 
		 * @param key 	The local key to check.
		 * @return		True, if a child with the passed key exists, false if
		 * 				not.
		 */
		public boolean hasChild(String key)
		{
			for(Entry e: children)
				if(e.getLocalKey().equals(key))
					return true;
			
			return false;
		}

 		/**
 		 * Writes this entry and all of it's children to the passed 
 		 * {@link StringBuilder}.
 		 * 
 		 * @param sb	The {@link StringBuilder} to write to.
 		 * @param depth	The depth of the current entry.
 		 */
		public void asPrintable(StringBuilder sb, int depth)
		{
			if(this != rootEntry)
			{
				for(String s: comments)
					sb.append(COMMENT_PREFIX).append(s).append('\n');
				
				for(int i = 0; i < depth; i++)
					sb.append("\t");
				
				sb.append(KEY_VALUE_PERIMETER).append(getLocalKey())
				.append(KEY_VALUE_PERIMETER);
				
				if(getValue() != null)
					sb.append(KEY_VALUE_SEPARATOR).append(KEY_VALUE_PERIMETER)
					.append(getValue()).append(KEY_VALUE_PERIMETER);
				
				sb.append('\n');
			}
			
			for(Entry e: children)
				e.asPrintable(sb, depth + 1);
		}
		
		/**
		 * Searches for the entry specified by 'subkeys', if 'create' is true,
		 * then a new entry will be created if one could not be found.
		 * 
		 * @param subkeys	The keys to the entry that is searched for.
		 * @param index		The index of the local key that is currently 
		 * 					checked.
		 * @param create	If true, a new entry will be created if one could
		 * 					not be found.
		 * @return			The found entry, or null if one could not be found.
		 */
		public Entry get(String[] subkeys, int index, boolean create)
		{
			if(index == subkeys.length - 1)
			{
				return this;
			}
			else
			{
				for(Entry child: children)
				{
					if(child.getLocalKey().equals(subkeys[index + 1]))
					{
						return child.get(subkeys, index + 1, create);
					}
				}
				
				if(create)
				{
					children.add(new Entry(new ArrayList<>(), 
													subkeys[index + 1], null));
					return children.get(children.size() - 1).get
												(subkeys, index + 1, create);
				}
				
				return null;
			}	
		}
	}
}
