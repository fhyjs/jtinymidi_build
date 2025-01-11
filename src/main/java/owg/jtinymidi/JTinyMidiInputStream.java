package owg.jtinymidi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static java.lang.Math.min;

public class JTinyMidiInputStream extends InputStream
{
	public final int sampleRate;
	public final boolean stereo;
	public final boolean littleEndian;
	
	public final int handle;
	
	protected final byte[] dummy = new byte[1];
	protected final ByteBuffer pcm;
	
	public JTinyMidiInputStream(File file, JTinySoundFont soundfont, int sampleRate, boolean stereo, boolean looping) throws IOException
	{
		this(JTinyMidiUtil.read(file), soundfont, sampleRate, stereo, looping);
	}
	public JTinyMidiInputStream(ByteBuffer data, JTinySoundFont soundfont, int sampleRate, boolean stereo, boolean looping) throws IOException
	{
		this(data, soundfont.handle, 0, sampleRate, stereo, looping);
	}
	public JTinyMidiInputStream(ByteBuffer data, int soundfont, int maxVoices, int sampleRate, boolean stereo, boolean looping) throws IOException
	{
		handle = JTinyMidi.openStream(data, data.limit(), soundfont, maxVoices, sampleRate, stereo, looping);
		if(handle < 0)
			throw new IOException(JTinyMidiUtil.getErrorString(handle));
		this.sampleRate = sampleRate;
		this.stereo = stereo;
		this.littleEndian = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
		
		pcm = ByteBuffer.allocateDirect(4096);
	}
	
	protected int readDirect(ByteBuffer buf, int pos, int len) throws IOException
	{
		if(JTinyMidi.isStopped(handle)==1 && JTinyMidi.activeVoiceCount(handle)==0)
			return -1;
		
		int frames = len>>>(stereo?3:2);
		int r = JTinyMidi.read(handle, buf, pos, frames);
		if(r < 0)
			throw new IOException(JTinyMidiUtil.getErrorString(r));
		return r << (stereo?3:2);
	}
	
	@Override
	public int available() throws IOException
	{
		return JTinyMidi.isStopped(handle)==1 && JTinyMidi.activeVoiceCount(handle)==0 ? 0 : pcm.capacity();
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		int r = readDirect(pcm, 0, min(len, pcm.capacity()));
		if(r > 0)
		{
			pcm.get(b, off, r);
			pcm.rewind();
		}
		return r;
	}


	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
	
	@Override
	public int read() throws IOException {
		int rd = read(dummy, 0, 1);
		if(rd == -1)
			return -1;
		return dummy[0];
	}
	
	@Override
	public void close() throws IOException
	{
		int r = JTinyMidi.close(handle);
		if(r < 0)
			throw new IllegalStateException(JTinyMidiUtil.getErrorString(r));
	}
}
