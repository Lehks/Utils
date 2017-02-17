package file.storageFile;

public class StorageFileConstants
{
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
	
	public static final String TAB = "\t";
	
	public static final char NEW_LINE = '\n';
	
	/**
	 * The value of a dummy entry in byte data.
	 */
	public static final byte BYTE_TYPE_DUMMY 	= 0;
	
	/**
	 * The value of a normal entry in byte data.
	 */
	public static final byte BYTE_TYPE_NORMAL	= 1;
	
	/**
	 * The depth of the root entry.
	 */
	public static final int ROOT_DEPTH = -1;
	
}
