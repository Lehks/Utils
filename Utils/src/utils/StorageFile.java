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
	public static final String MSG_KEY_ALREADY_EXISTING = 
								"The key \"%s\" in line %d already exists.";
	
	public static final String MSG_INVALID_LINE = "Line %d is invalid.";
	
	private static final char COMMENT_PREFIX = '#';
	private static final char PATH_SEPARATOR = '.';
	private static final char KEY_VALUE_SEPARATOR = '=';
	private static final char KEY_VALUE_PERIMETER = '"';
	
	private static final String SUBPATTERN_ANY_TAB = "([\\t]*)";
	
	private static final String SUBPATTERN_KEY = KEY_VALUE_PERIMETER 
												+ "([^/.]*)" 
												+ KEY_VALUE_PERIMETER;
	
	private static final String SUBPATTERN_VALUE = KEY_VALUE_PERIMETER 
												+ "(.*)" 
												+ KEY_VALUE_PERIMETER;
	
	private static final String PATH_SEPERATOR_REGEX = "[" + PATH_SEPARATOR + "]";
	
	private static final Pattern PATTERN_VALUE = Pattern.compile("^" 
											+ SUBPATTERN_ANY_TAB
											+ SUBPATTERN_KEY 
											+ KEY_VALUE_SEPARATOR
											+ SUBPATTERN_VALUE
											+ "$");
	
	private Pattern PATTERN_NO_VALUE = Pattern.compile("^" 
											+ SUBPATTERN_ANY_TAB 
											+ SUBPATTERN_KEY
											+ "$");
	
	private Entry rootEntry;
	
	private Scanner scanner;
	
	private File file;
	
	private ArrayList<String> commentBuffer = new ArrayList<>();
	
	public StorageFile(File file) throws FileNotFoundException
	{
		this.file = file;
		this.scanner = new Scanner(file);
		this.rootEntry = new Entry(null, "ROOT", "ROOT");
		
		load();
		
		scanner.close();
	}
	
	public StorageFile(String path) throws FileNotFoundException
	{
		this(new File(path));
	}
	
	private void load()
	{
		int lastDepth = 0;				//Depth of the last entry
		Entry lastChild = null;			//The last Entry that was added
		Entry lastParent = rootEntry;	//The parent of lastChild
		
		int line = 0;					//The line that is currently processed
		
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
				throw new StorageFileException
									(String.format(MSG_INVALID_LINE, line));
			//-------------------------
			
			/*
			 * If the loop has not been continued up to this point, this line
			 * is not a comment and the new Entry has been created 
			 * (=> this Entry's comments have been set), so the comment buffer
			 * will be a new instance of ArrayList to prevent the comments of
			 * the old entry from getting changed.
			 */
			commentBuffer = new ArrayList<>();
			
			//Add currentEntry to tree
			if(lastDepth == currentDepth)
			{
				if(lastParent.hasChild(currentEntry.getLocalKey()))
					throw new StorageFileException(
									String.format(MSG_KEY_ALREADY_EXISTING, 
											currentEntry.getLocalKey(), line));
				
				lastParent.addChild(currentEntry);
			}
			else if(lastDepth < currentDepth)
			{
				if(lastChild.hasChild(currentEntry.getLocalKey()))
					throw new StorageFileException(
							String.format(MSG_KEY_ALREADY_EXISTING, 
									currentEntry.getLocalKey(), line));
				
				lastChild.addChild(currentEntry);
				
				lastParent = lastChild;
			}
			else
			{
				Entry parent = lastParent;

				if(parent.hasChild(currentEntry.getLocalKey()))
					throw new StorageFileException(
							String.format(MSG_KEY_ALREADY_EXISTING, 
									currentEntry.getLocalKey(), line));
				
				//Decrease depth until the correct depth has been reached
				for(int counter = lastDepth; counter > currentDepth; counter--)
					parent = parent.getParent();
				
				parent.addChild(currentEntry);
				
				lastParent = parent;
			}
			
			lastChild = currentEntry;
			lastDepth = currentDepth;
			//-------------------------
		}
	}
	
	public String get(String key)
	{
		Entry entry = getEntry(key, false);
		
		if(entry != null)
			return entry.getValue();
		
		return null;
	}
	
	public void set(String key, String value)
	{
		Entry entry = getEntry(key, true);
		
		entry.setValue(value);
	}
	
	private Entry getEntry(String key, boolean create)
	{
		String[] splitStrings = key.split(PATH_SEPERATOR_REGEX);
		
		String[] subkeys = new String[splitStrings.length + 1];
				
		System.arraycopy(splitStrings, 0, subkeys, 1, splitStrings.length);
		
		subkeys[0] = rootEntry.getLocalKey();
		
		return rootEntry.get(subkeys, 0, create);
	}
	
	public void save(File file) throws IOException
	{
		FileWriter fw = new FileWriter(file);
		
		fw.append(toString());
		
		fw.close();
	}
	
	public void save(String path) throws IOException
	{
		save(new File(path));
	}

	public void save() throws IOException
	{
		save(file);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		rootEntry.asPrintable(sb, -1);
		
		return sb.toString();
	}
	
	private class Entry
	{
		private ArrayList<String> comments;
		private Entry parent;
		private LinkedList<Entry> children;
		
		private String localKey;
		private String value;
		
		public Entry(ArrayList<String> comments, String localKey, String value)
		{
			this.comments = comments;
			this.parent = null;
			this.children = new LinkedList<>();
			this.localKey = localKey;
			this.value = value;
		}
		
		public Entry getParent()
		{
			return parent;
		}
		
		public void addChild(Entry child)
		{
			child.parent = this;
			children.add(child);
		}
		
		public String getLocalKey()
		{
			return localKey;
		}
		
		public String getValue()
		{
			return value;
		}
		
		public void setValue(String value)
		{
			this.value = value;
		}
		
		public boolean hasChild(String key)
		{
			for(Entry e: children)
				if(e.getLocalKey().equals(key))
					return true;
			
			return false;
		}
		
 		public String getGlobalKey()
		{
			if(parent == rootEntry)
				return getLocalKey();
			else 
				return parent.getGlobalKey() + PATH_SEPARATOR + getLocalKey();

		}
		
		public void asPrintable(StringBuilder sb, int layer)
		{
			if(this != rootEntry)
			{
				for(String s: comments)
					sb.append(COMMENT_PREFIX).append(s).append('\n');
				
				for(int i = 0; i < layer; i++)
					sb.append("\t");
				
				sb.append('"').append(getLocalKey()).append('"');
				
				if(getValue() != null)
					sb.append('=').append('"').append(getValue()).append('"');
				
				sb.append('\n');
			}
			
			for(Entry e: children)
				e.asPrintable(sb, layer + 1);
		}
		
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
		
		@Override
		public String toString()
		{
			return KEY_VALUE_PERIMETER + getGlobalKey() + KEY_VALUE_PERIMETER
					+ KEY_VALUE_SEPARATOR + KEY_VALUE_PERIMETER
					+ getValue() +KEY_VALUE_PERIMETER;
		}
	}
}
