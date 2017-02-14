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
}
