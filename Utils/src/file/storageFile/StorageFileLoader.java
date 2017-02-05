package file.storageFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;

import exception.StorageFileException;

public class StorageFileLoader
{
	/**
	 * The scanner that reads the file to load.
	 */
	private Scanner scanner;
	
	/**
	 * The number of the line that is currently processed.
	 */
	private int line = 0;
	
	/**
	 * The line that is currently processed.
	 */
	private String currentLine;
	
	/**
	 * The buffer that stores all the comments for the next entry.
	 */
	private ArrayList<String> commentBuffer = new ArrayList<>();
	
	/**
	 * The depth of the last entry.
	 */
	private int lastDepth = 0;
	
	/**
	 * The depth of the current entry.
	 */
	private int currentDepth = 0;
	
	/**
	 * The entry that was added the last time.
	 */
	private Entry lastChild = null;
	
	/**
	 * The parent of lastChild.
	 */
	private Entry lastParent;
	
	public Entry load(File file) throws FileNotFoundException
	{
		Entry ret = new Entry(null, "ROOT", "ROOT");
		scanner = new Scanner(file);
		
		lastParent = ret;			//The parent of lastChild
		
		while(scanner.hasNextLine())
		{
			line++;
			
			currentLine = scanner.nextLine();
			
			if(currentLine.trim().isEmpty())
				continue; 	//If the current line is empty, there is no need 
							//to go on
			
			//Check if the line is a comment
			if(currentLine.charAt(0) == StorageFile.COMMENT_PREFIX)
			{
				commentBuffer.add(currentLine.substring(1));
				continue; //If line is a comment, there is no need to go on
			}

			//Fill currentEntry and currentDepth using regex
			Entry currentEntry = makeEntry();
			
			/*
			 * If the loop has not been continued up to this point, this line
			 * is not a comment and the new Entry has been created 
			 * (=> this Entry's comments have been set), so the comment buffer
			 * will be a new instance of ArrayList to prevent the comments of
			 * the old entry from getting changed.
			 */
			commentBuffer = new ArrayList<>();
			
			putEntryIntoTree(currentEntry);
			
			lastParent = currentEntry.getParent();
			lastChild = currentEntry;
			lastDepth = currentDepth;
		}
		
		scanner.close();
		
		return ret;
	}
	
	private Entry makeEntry()
	{
		Matcher matcherValue = StorageFile.PATTERN_VALUE.matcher(currentLine);
		Matcher matcherNoValue = StorageFile.PATTERN_NO_VALUE.matcher(currentLine);
		
		if(matcherValue.find()) //If this is a entry with a value
		{
			currentDepth = matcherValue.group(1).length();
			return new Entry(commentBuffer, matcherValue.group(2), 
					matcherValue.group(3));
		}
		else if(matcherNoValue.find()) //If this is a entry without a value
		{
			currentDepth = matcherNoValue.group(1).length();
			return new Entry(commentBuffer, matcherNoValue.group(2), null);
		}
		else 	//If both matchers could not match the current line, then
				//there is an invalid line -> error
		{
			scanner.close();
			throw new StorageFileException
								(String.format(StorageFile.MSG_INVALID_LINE, line));
		}
	}
	
	private void putEntryIntoTree(Entry currentEntry)
	{
		/*
		 * If the current entry has the same depth as the last one (=> the 
		 * current and last entry share the same parent).
		 */
		if (lastDepth == currentDepth)
		{
			checkKey(lastParent, currentEntry.getLocalKey());

			lastParent.addChild(currentEntry);
		}
		
		/*
		 * If the current entry's depth is exactly one higher than the last one
		 * (=> the current entry is a child of the last one).
		 */
		if (lastDepth == currentDepth - 1)
		{
			checkKey(lastChild, currentEntry.getLocalKey());

			lastChild.addChild(currentEntry);
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

			checkKey(parent, currentEntry.getLocalKey());

			parent.addChild(currentEntry);
		}
		
		/*
		 * If none of the cases above matched, currentDepth is invalid
		 * (most likely currentDepth > (lastDepth + 1)).
		 */
		throw new StorageFileException(String.format(StorageFile.MSG_INVALID_DEPTH, line));
	}
	
	private void checkKey(Entry entry, String key)
	{
		if(entry.hasChild(key))
		{
			scanner.close();
			throw new StorageFileException
					(String.format(StorageFile.MSG_KEY_ALREADY_EXISTING, key, line));
		}
	}
}
