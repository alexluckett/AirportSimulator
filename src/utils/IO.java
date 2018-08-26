package utils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads from an input stream 
 * For use ONLY on streams which are guaranteed to be small in size relative to available ram
 * 
 * @author Jason Harrison
 * @version v1.00 05/04/2014
 */
public class IO {

	public IO()
	{
		//static functions - no constructor needed
	}
	
	/**
	 * Reads a stream into a byte array
	 * @param stream
	 * @return byte[] from the InputStream stream
	 * @throws IOException
	 */
	public static byte[] ReadStreamToByteArray(InputStream stream) throws IOException
	{
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		int i;
		
		// 4096 is a typical NTFS cluster size to read less than this would just be a waste of processor cycles and/or HDD access 
		// and 4096 not so big as to waste a large chunk of ram
		byte[] byteBuffer = new byte[4096];  
		while ((i = stream.read(byteBuffer)) != -1)
		{
			bo.write(byteBuffer, 0, i);
		}
		bo.flush();
		return bo.toByteArray();
	}

	/**
	 * Reads a stream to a UTF-8 string
	 * @param stream
	 * @return String from the InputStream stream in UTF-8
	 * @throws IOException
	 */
	public static String ReadStreamToString(InputStream stream) throws IOException
	{
		return  new String(ReadStreamToByteArray(stream), "UTF-8");
	}
	
	/**
	 * Reads a stream to a UTF-16 string
	 * @param stream
	 * @return String from the InputString in UTF-16
	 * @throws IOException
	 */
	public static String ReadStreamToUnicodeString(InputStream stream) throws IOException
	{
		return  new String(ReadStreamToByteArray(stream), "UTF-16");
	}
	
}