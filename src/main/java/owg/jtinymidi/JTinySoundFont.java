package owg.jtinymidi;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**Soundfont object*/
public class JTinySoundFont
{
	public final int handle; 
	
	public JTinySoundFont(File file) throws IOException
	{
		this(JTinyMidiUtil.read(file));
	}

	public JTinySoundFont(ByteBuffer data) throws IOException
	{
		handle = JTinyMidi.loadSoundfont(data, data.limit());
		if(handle < 0)
			throw new IOException(JTinyMidiUtil.getErrorString(handle));
	}
	
	public void close()
	{
		int r = JTinyMidi.close(handle);
		if(r < 0)
			throw new IllegalStateException(JTinyMidiUtil.getErrorString(r));
	}
}
