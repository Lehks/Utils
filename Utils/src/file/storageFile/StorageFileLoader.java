package file.storageFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import utils.Utils;

public class StorageFileLoader
{
	private BufferedInputStream istream;
	
	public StorageFileLoader(File file) throws FileNotFoundException
	{
		istream = new BufferedInputStream(new FileInputStream(file));
	}
	
	public StorageFileLoader(InputStream istream)
	{
		if(istream instanceof BufferedInputStream)
			this.istream = (BufferedInputStream) istream;
		else
			this.istream = new BufferedInputStream(istream);
	}
	
	public Entry load() throws IOException
	{
		Entry ret = new Entry(null, null, null); //Construct root entry
		
		Entry nextEntry;
		
		while((nextEntry = readEntry()) != null)
		{
//			System.out.println(nextEntry.getLocalKey() + "=" + nextEntry.getValue());
		}
		
		return ret;
	}
	
	private Entry readEntry() throws IOException
	{
		byte type = (byte) istream.read();
		
		if(type == -1)
			return null;
		
		int depth = readNextInt();
		
		int keyLenght = readNextInt();
		
		byte[] keyArray = new byte[keyLenght];
		
		istream.read(keyArray);
		
		String key = new String(keyArray);
		
		String value = null;
		
		if(type == StorageFileConstants.BINARY_TYPE_VALUE)
		{
			int valueLenght = readNextInt();
			
			byte[] valueArray = new byte[valueLenght];
			
			value = new String(valueArray);
		}
		
		System.out.println();
		System.out.println(type);
		System.out.println(keyLenght + " ");
		System.out.println("\"" + key + "\" \"" + value + "\"");
		
		return new Entry(new ArrayList<>(), key, value);
	}
	
	private int readNextInt() throws IOException
	{
		byte[] retArray = new byte[Utils.INT_SIZE];
		
		istream.read(retArray);
		
		return Utils.toInt(retArray);
	}
}
