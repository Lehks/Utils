package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StorageFile
{
	public static final String MSG_KEY_ALREADY_EXISTING = 
													"The key %s already exists.";
	
	public static final char COMMENT_PREFIX = '#';
	
	private Entry rootEntry;
	
	private Scanner scanner;
	
	private Pattern pattern = Pattern.compile("\"([^/.]*)\"=\"(.*)\"");
	
	private ArrayList<String> commentBuffer = new ArrayList<>();
	
	public StorageFile(File file) throws FileNotFoundException
	{
		scanner = new Scanner(file);
		rootEntry = new Entry(null, "ROOT", "ROOT");
		
		load();
		
		scanner.close();
		
		
		StringBuilder sb = new StringBuilder();
		
		rootEntry.asPrintable(sb, -1);
		
		System.out.println(sb);
	}
	
	public StorageFile(String path) throws FileNotFoundException
	{
		this(new File(path));
	}
	
	private void load()
	{
		int lastDepth = 0;
		Entry lastChild = null;
		Entry lastParent = rootEntry;
		
		while(scanner.hasNextLine())
		{
			String nextLine = scanner.nextLine();
			String trimmedLine = nextLine.trim();
			
			if(trimmedLine.isEmpty())
				continue; 	//If the current line is empty, there is no need to go 
							//further on
			
			Entry currentEntry = makeEntry(nextLine, trimmedLine);
			
			if(currentEntry == null)
				continue; 	//If made entry returned null, a comment has been 
							//created => Can't put an Entry into the tree.
			
			int currentDepth = makeDepth(nextLine, lastDepth);
			
			if(lastDepth == currentDepth)
			{
				lastParent.addChild(currentEntry);
			}
			else if(lastDepth < currentDepth)
			{
				lastChild.addChild(currentEntry);
				
				lastParent = lastChild;
			}
			else
			{
				Entry parent = lastParent;
				
				//Decrease depth until the correct depth has been reached
				for(int counter = lastDepth; counter > currentDepth; counter--)
					parent = parent.getParent();
				
				parent.addChild(currentEntry);
				
				lastParent = parent;
			}
			
			lastChild = currentEntry;
			lastDepth = currentDepth;
		}
	}
	
	private Entry makeEntry(String nextLine, String trimmedLine)
	{
		if(nextLine.charAt(0) == COMMENT_PREFIX)
		{
			commentBuffer.add(nextLine.substring(1));
			return null;
		}
		
		Matcher matcher = pattern.matcher(trimmedLine);
		
		if(!matcher.find())
			throw new RuntimeException("3");
		
		Entry currentEntry = new Entry(commentBuffer, matcher.group(1), 
				matcher.group(2));
		
		commentBuffer = new ArrayList<>();
		
		return currentEntry;
	}
	
	private int findFirstNotTab(String str)
	{
		for(int i = 0; i < str.length(); i++)
			if(str.charAt(i) != '\t')
				return i;
		
		return -1;
	}
	
	private int makeDepth(String nextLine, int currentDepth)
	{
		int newDepth = findFirstNotTab(nextLine);
		
		if(newDepth == -1)
			throw new RuntimeException("1");
		else if(newDepth > currentDepth + 1)
			throw new RuntimeException("2");
		
		return newDepth;
	}
	
	public String get(String key)
	{
		String[] subkeys = {key};
				
		if(key.contains("."))
			subkeys = key.split(".");
		
		return rootEntry.get(subkeys, 0);
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
		
		public LinkedList<Entry> getChildren()
		{
			return children;
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
		
		public String getGlobalKey()
		{
			if(parent == rootEntry)
				return getLocalKey();
			else 
				return parent.getGlobalKey() + "." + getLocalKey();

		}
		
		public void asPrintable(StringBuilder sb, int layer)
		{
			if(this != rootEntry)
			{
				for(String s: comments)
					sb.append(COMMENT_PREFIX).append(s).append('\n');
				
				for(int i = 0; i < layer; i++)
					sb.append("\t");
				
				sb.append('"').append(getLocalKey()).append('"').append('=');
				sb.append('"').append(getValue()).append('"').append('\n');
			}
			
			for(Entry e: children)
				e.asPrintable(sb, layer + 1);
		}
		
		public String get(String[] subkeys, int index)
		{
			System.out.println(index);
			System.out.println(subkeys.length);
			
			if(index == subkeys.length - 1)
			{
				return this.value;
			}
			else
			{
				for(Entry child: children)
				{
					if(child.getLocalKey().equals(subkeys[index]))
					{
						return child.get(subkeys, index + 1);
					}
				}
				
				return null;
			}	
		}
		
		@Override
		public String toString()
		{
			return "\"" + getGlobalKey() + "\"=\"" + getValue() + "\"";
		}
	}
}
