package owg.jtinymidi;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**Demo program for JavaSound*/
public class JSDemo
{
	public static void main(String[] args) throws IOException, LineUnavailableException
	{
		//The JTinyMidi native library must be loaded first
		System.load(new File("lib/"+JTinyMidiUtil.getNativeLibraryName()).getAbsolutePath());
		
		JTinySoundFont soundfont = new JTinySoundFont(new File(args[0]));
		JTinyMidiInputStream in = new JTinyMidiInputStream(new File(args[1]), soundfont, 44100, true, false);
		
		AudioFormat format = new AudioFormat(44100, 16, 2, true, ByteOrder.nativeOrder()==ByteOrder.BIG_ENDIAN);
		SourceDataLine line = AudioSystem.getSourceDataLine(format);
		line.open(format, 4096*6);
		line.start();
		
		//JTinyMidi outputs floating point samples, need to convert to short
		byte[] buf = new byte[4096];
		ByteBuffer bytes = ByteBuffer.wrap(buf).order(ByteOrder.nativeOrder());
		FloatBuffer floats = bytes.asFloatBuffer();
		ShortBuffer shorts = bytes.asShortBuffer(); 

		while(true)
		{
			int r = in.read(buf);
			if(r == -1)
				break;
			//Convert from float to short in-place
			for(int i = r/4; i>0; i--)
			{
				float f = floats.get();
				short s = (short) (max(-1f, min(1f, f))*32767f);
				shorts.put(s);
			}
			floats.rewind();
			shorts.rewind();
			line.write(buf, 0, r/2);
		}

		line.stop();
		line.close();
		in.close();
		soundfont.close();
	}
}
