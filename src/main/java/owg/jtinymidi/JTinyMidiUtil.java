package owg.jtinymidi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Locale;

public class JTinyMidiUtil
{
	public static ByteBuffer read(File file) throws IOException
	{
		FileInputStream fin = null;
		try
		{
			fin = new FileInputStream(file);
			int size = (int) file.length();
			ByteBuffer out = ByteBuffer.allocateDirect(size);
			byte[] buf = new byte[4096];
			while(true)
			{
				int r = fin.read(buf);
				if(r == -1)
				{
					out.flip();
					return out;
				}
				if(out.remaining() < r)
					throw new IOException("File too long (expected "+size+", got "+(out.position()+r)+" or more)");
				out.put(buf, 0, r);
			}
		}
		finally
		{
			if(fin != null)
				fin.close();
		}
	}
	
	public static ByteBuffer read(InputStream input) throws IOException
	{
		ByteBuffer out = ByteBuffer.allocateDirect(4096);
		byte[] buf = new byte[4096];
		while(true)
		{
			int r = input.read(buf);
			if(r == -1)
			{
				out.flip();
				return out;
			}
			if(out.remaining() < r)
			{
				out.flip();
				ByteBuffer newOut = ByteBuffer.allocateDirect(out.capacity()*2);
				newOut.put(out);
				out = newOut;
			}
			out.put(buf, 0, r);
		}
	}
	
	public static String getErrorString(int err)
	{
		if(err >= 0)
			return null;
		
		switch (err)
		{
		case JTinyMidi.NO_SUCH_PRESET:
			return "NO_SUCH_PRESET";
		case JTinyMidi.BUFFER_NOT_DIRECT:
			return "BUFFER_NOT_DIRECT";
		case JTinyMidi.OUT_OF_MEMORY:
			return "OUT_OF_MEMORY";
		case JTinyMidi.INVALID_HANDLE:
			return "INVALID_HANDLE";
		case JTinyMidi.INVALID_FILE:
			return "INVALID_FILE";
		default:
			return "Bad error code: "+err;
		}
	}

	public static String getNativeLibraryName()
	{
		String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
		String osArch = System.getProperty("os.arch").toLowerCase(Locale.ENGLISH);
		
		String prefix, ext, arch;
		
		if(osName.startsWith("linux"))
		{
			prefix = "lib";
			ext = ".so";
		}
		else if(osName.startsWith("win"))
		{
			prefix = "";
			ext = ".dll";
		}
		else if(osName.startsWith("mac os"))
		{
			prefix = "lib";
			ext = ".dylib";
		}
		else
			throw new RuntimeException("Unsupported os "+osName);
		
		if(osArch.equals("amd64") || osArch.equals("x86_64"))
			arch = "64";
		else if(osArch.endsWith("86"))
			arch = "32";
		else if(osArch.equals("arm") || osArch.equals("armeabi-v7a") || osArch.equals("armeabi"))
			arch = "ARM";
		else if(osArch.equals("aarch64") || osArch.equals("arm64-v8a"))
			arch = "AARCH64";
		else
			throw new RuntimeException("Unsupported arch "+osArch);
		
		return prefix+"jtinymidi"+arch+ext;
	}
}
