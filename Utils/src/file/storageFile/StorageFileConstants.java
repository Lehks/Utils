package file.storageFile;

/**
 * A class that contains constants that are needed and shared among the classes
 * revolving the {@link StorageFile}.
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see		StorageFile
 *
 */
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
	
	/**
	 * Simply a String that contains only \t.
	 */
	public static final String TAB = "\t";

	/**
	 * Simply a char that contains only \n.
	 */
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
