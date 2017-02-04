package file.storageFile;

import java.util.ArrayList;
import java.util.LinkedList;

public class Entry
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
		if(depth >= 0)
		{
			for(String s: comments)
				sb.append(StorageFile.COMMENT_PREFIX).append(s).append('\n');
			
			for(int i = 0; i < depth; i++)
				sb.append("\t");
			
			sb.append(StorageFile.KEY_VALUE_PERIMETER).append(getLocalKey())
			.append(StorageFile.KEY_VALUE_PERIMETER);
			
			if(getValue() != null)
				sb.append(StorageFile.KEY_VALUE_PERIMETER)
				.append(StorageFile.KEY_VALUE_SEPARATOR)
				.append(getValue()).append(StorageFile.KEY_VALUE_PERIMETER);
			
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
