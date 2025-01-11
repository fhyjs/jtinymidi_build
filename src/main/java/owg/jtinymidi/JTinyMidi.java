package owg.jtinymidi;
import java.nio.Buffer;

/**Midi file renderer based on tinysoundfont/tinymidiloader*/
public class JTinyMidi
{ 
	/**Error code for asking for a preset that does not exist*/
	public static final int NO_SUCH_PRESET = -1;
	/**Error code for passing a {@link Buffer} that is not <code>direct</code> to a function (user error)*/
	public static final int BUFFER_NOT_DIRECT = -2; 
	/**Error code for the C program running out of memory (exceptional)*/
	public static final int OUT_OF_MEMORY = -3; 
	/**Error code for passing an inappropriate handle to a function (user error)*/
	public static final int INVALID_HANDLE = -4;
	/**Error code for passing an invalid soundfont or midi file*/
	public static final int INVALID_FILE = -5;
	
	/**
	 * Loads a soundfont from the given data
	 * @param directBuffer The data (as stored in an sf2 file, must be a direct buffer)
	 * @param size The size of the data in bytes
	 * @return The handle of the soundfont, or any of {@link #INVALID_FILE}, {@link #BUFFER_NOT_DIRECT}, {@link #OUT_OF_MEMORY}.
	 */
	public static native int loadSoundfont(Buffer directBuffer, int size);
	/**
	 * Frees the memory for the given soundfont or stream.
	 * @param soundfont The handle of the soundfont or stream to free.
	 * @return 0 for success, or {@link #INVALID_HANDLE}
	 */
	public static native int close(int handle);

	/**
	 * Opens a stream for a midi file
	 * @param directBuffer The data (as stored in a midi file, must be a direct buffer)
	 * @param size The size of the data in bytes
	 * @param soundfont The handle of the soundfont to use.
	 * @param maxVoices Polyphony limit, nonpositive for no limit.
	 * @param rate The frame rate to use (e.g. 44100).
	 * @param stereo Whether to render the file in stereo (false for mono).
	 * @param looping Whether to loop the midi file.
	 * @return The handle of the stream, or any of {@link #INVALID_FILE}, {@link #BUFFER_NOT_DIRECT}, {@link #OUT_OF_MEMORY}, {@link #INVALID_HANDLE}.
	 */
	public static native int openStream(Buffer directBuffer, int size, int soundfont, int maxVoices, int rate, boolean stereo, boolean looping);
	/**
	 * Renders a part of the midi file into the given buffer (as 32 bit float PCM).
	 * @param stream The stream to read from
	 * @param directBuffer The buffer to put the data into (must be a direct native endian buffer, 
	 * size in bytes must be at least <code>off+(limit*8)</code> for stereo or <code>off+(limit*4)</code> for mono)
	 * @param off The offset into the read buffer, in bytes
	 * @param limit The maximum number of frames to read
	 * @return The actual number of frames read, or any of {@link #BUFFER_NOT_DIRECT}, {@link #INVALID_HANDLE}.
	 */
	public static native int read(int stream, Buffer directBuffer, int off, int limit);
	/**
	 * Stops playing the midi file (with sustain and release).
	 * @param stream The stream for which to stop midi playback
	 * @return 0 for success, or {@link #INVALID_HANDLE}
	 * @see #activeVoiceCount(int)
	 */
	public static native int midiStop(int stream);


	/**
	 * Gets whether midi playback has stopped for the given stream.
	 * @param stream The playback stream
	 * @return 1 if playback has stopped, 0 if it is running, or {@link #INVALID_HANDLE}
	 */
	public static native int isStopped(int stream);
	
	/**
	 * Gets the length of the midi file in milliseconds for the given stream.
	 * @param stream The playback stream
	 * @return The millisecond length, or {@link #INVALID_HANDLE}
	 */
	public static native double getMillisecondLength(int stream);
	
	/**
	 * Gets the current playback position in milliseconds for the given stream.
	 * @param stream The playback stream
	 * @return The millisecond position, or {@link #INVALID_HANDLE}
	 */
	public static native double getMillisecondPosition(int stream);
	/**
	 * Gets the current number of active voices for the given stream. 
	 * This can be used to detect when all instruments have faded out after a {@link #midiStop(int)}.
	 * @param stream The playback stream
	 * @return The active voice count, or {@link #INVALID_HANDLE}
	 */
	public static native int activeVoiceCount(int stream);
	/**
	 * Sets the global gain for the given stream.
	 * @param stream The playback stream
	 * @return 0 for success, or {@link #INVALID_HANDLE}
	 */
	public static native int setGlobalGain(int stream, float gain);
	
	/*
	 * Channel control functions return 0 for success, or any of INVALID_HANDLE, OUT_OF_MEMORY
	 */
	public static native int setChannelPresetIndex(int stream, int channel, int preset_index);
	public static native int setChannelPresetNumber(int stream, int channel, int preset_number, int flag_mididrums);
	public static native int setChannelBank(int stream, int channel, int bank);
	public static native int setChannelBankPreset(int stream, int channel, int bank, int preset_number);
	public static native int setChannelPan(int stream, int channel, float pan);
	public static native int setChannelVolume(int stream, int channel, float volume);
	public static native int setChannelPitchwheel(int stream, int channel, int pitch_wheel);
	public static native int setChannelPitchrange(int stream, int channel, float pitch_range);
	public static native int setChannelTuning(int stream, int channel, float tuning);
	
	public static native int channelMidiControl(int stream, int channel, int controller, int control_value);

	/*
	 * Channel getters return the indicated value, or INVALID_HANDLE
	 */
	public static native int getChannelPresetIndex(int stream, int channel);
	public static native int getChannelPresetBank(int stream, int channel);
	public static native int getChannelPresetNumber(int stream, int channel);
	public static native float getChannelPan(int stream, int channel);
	public static native float getChannelVolume(int stream, int channel);
	public static native int getChannelPitchwheel(int stream, int channel);
	public static native float getChannelPitchrange(int stream, int channel);
	public static native float getChannelTuning(int stream, int channel);
	
	/**
	 * Query for a preset index that matches a bank and preset number
	 * @param handle The soundfont or stream to query
	 * @param bank The bank number
	 * @param preset_number The preset number
	 * @return A preset index that matches the given description, -1 if it does not exist, or INVALID_HANDLE
	 */
	public static native int getPresetIndex(int handle, int bank, int preset_number);
	/**
	 * Query for a preset index that matches a bank and preset number
	 * @param handle The soundfont or stream to query
	 * @param bank The bank number
	 * @param preset_number The preset number
	 * @return A preset index that matches the given description, or any of NO_SUCH_PRESET, INVALID_HANDLE
	 */
	public static native int getPresetCount(int handle);
	/**
	 * Gets the name of the preset at the given index
	 * @param handle The soundfont or stream to query
	 * @param preset_index The preset index
	 * @return The name of the preset at the given index, or null if the handle was invalid or the index was out of bounds
	 */
	public static native String getPresetname(int handle, int preset_index);
	/**
	 * Query for a preset name that matches a bank and preset number
	 * @param handle The soundfont or stream to query
	 * @param bank The bank number
	 * @param preset_number The preset number
	 * @return A preset name that matches the given description, or null if the handle was invalid or there was no such preset
	 */
	public static native String getBankPresetname(int handle, int bank, int preset_number);
}
