import java.io.BufferedInputStream;
import java.io.File;

import javax.sound.sampled.*;

import java.net.*;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;

import javazoom.jl.player.Player;

public class Sound
{
	private SoundPlayer player;
	
	public Sound(String path)
	{
		int index = path.lastIndexOf(".");
		String ext = path.substring(index + 1);
		
		if(index == -1)
		{
			try
			{
				throw new Exception("No valid extension for music loaded from  " + path);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return;
		}
		
		if(ext.equalsIgnoreCase("wav") || ext.equalsIgnoreCase("aiff") || ext.equalsIgnoreCase("au"))
		{
			//try to open it as a URL
			URL url = Sound.class.getResource(path);
			try
			{
				File f = new File(url.getFile());
				long byteLength = f.length();
				if(byteLength <= 1024*100)
					player = new ClipSoundPlayer(path); //for shorter sound files that can be read directly into memory
				else
					player = new StandardSoundPlayer(path);
			}
			catch(Exception e)
			{
				player = new StandardSoundPlayer(path);
			}
		}
		else if(ext.equalsIgnoreCase("mp3"))
			player = new MP3SoundPlayer(path);
		else if(ext.equalsIgnoreCase("mid"))
			player = new MIDISoundPlayer(path);
		else
		{
			try
			{
				throw new Exception("Invalid music type loaded from: " + path);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void play() {
		player.play();
	}
	
	public boolean isPlaying() {
		return player.isPlaying();
	}
	
	public boolean isPaused() {
		return player.isPaused();
	}
	
	public void stop() {
		player.stop();
	}
	
	public void pause() {
		player.pause();
	}
	
	public void unpause() {
		player.unpause();
	}
}

abstract class SoundPlayer {

	protected Thread thread;
	protected String path;
	protected boolean shouldStop;
	protected boolean isPaused;
	
	public SoundPlayer(String path)
	{
		this.path = path;
	}
	
	public abstract void play();
	
	//returns true even if paused (essentially means is active)
	public boolean isPlaying() {
		return thread != null;
	}

	//returns true if the sound has already been started and is now paused
	public boolean isPaused() {
		return isPaused;
	}
	
	//stops the sound, regardless if it is paused or not
	public void stop() {
		if(thread != null)
		{
			shouldStop = true;
			isPaused = false;
		}
	}
	
	//pauses the sound
	public void pause() {
		if(thread != null)
		{
			isPaused = true;
		}
	}
	
	//unpauses the sound
	public void unpause() {
		if(thread != null)
		{
			isPaused = false;
		}
	}
	
}

class MIDISoundPlayer extends SoundPlayer {
	
	public MIDISoundPlayer(String path) {
		super(path);
	}
	
	public void play() {
		
		if(thread != null)
		{
			if(isPaused())
			{
				unpause();
				return;
			}
			else
				stop();
		}
		
		Runnable r = new Runnable() {
			public void run()
			{
				try
				{
					Sequencer sequencer = MidiSystem.getSequencer();
					sequencer.setSequence(MidiSystem.getSequence(getClass().getResourceAsStream(path)));
					
					sequencer.open();
		            sequencer.start();
					
		            while(!shouldStop && sequencer.isRunning()) {
						
		            	if(isPaused()) {
		            		sequencer.stop();
		            		while(isPaused()) {
		            			try {
		                            Thread.sleep(500);
		            			}
		            			catch(Exception e) {
		            				
		            			}
		            		}
		            		sequencer.start();
		            	}		            	
						
		                try {
		                    Thread.sleep(10); // Check every second
		                } catch(InterruptedException ignore) {
		                }
		            }
		            
		            // Close the MidiDevice & free resources
		            sequencer.stop();
		            sequencer.close();
					
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				thread = null;
			}
			
		};
		
		thread = new Thread(r);
		thread.start();
	}
	
}

class MP3SoundPlayer extends SoundPlayer {

	private Player player;

	//creates a sound at the given path - works for MP3 files
	public MP3SoundPlayer(String path)
	{
		super(path);
	}

	//begins playing the sound in a separate thread
	//the separate thread will begin to sleep if the Sound is paused
	//the thread will "wake up" when it is unpaused
	public void play()
	{	
		if(thread != null)
		{
			if(isPaused())
			{
				unpause();
				return;
			}
			else
				stop();
		}
		
		Runnable r = new Runnable() {
			public void run()
			{
				try
				{
					shouldStop = false;
					isPaused = false;
					player = new Player(getClass().getResourceAsStream(path));
					
					while(!shouldStop)
					{
						boolean success = player.play(1);
						if(!success)
							break;
						
						while(isPaused)
						{
							Thread.sleep(10);
							
							if(shouldStop)
								break;
						}

					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					player.close();
				}
				thread = null;
			}
			
		};
		
		thread = new Thread(r);
		thread.start();
	}
}

class ClipSoundPlayer extends SoundPlayer
{
	private Clip clip;
	private long pausePosition;
	
	public ClipSoundPlayer(String path)
	{
		super(path);
		
		try
		{
			AudioInputStream soundStream = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(path)));
			AudioFormat streamFormat = soundStream.getFormat( );
			DataLine.Info clipInfo = new DataLine.Info( Clip.class, streamFormat );
			
			clip = (Clip)AudioSystem.getLine(clipInfo);
			clip.open(soundStream);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void play()
	{
		if(thread != null)
		{
			if(isPaused())
			{
				unpause();
				return;
			}
			else
				stop();
		}
		
		try
		{
			clip.setMicrosecondPosition(0);
			clip.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean isPlaying() {
		return clip.isRunning();
	}
	
	//returns true if the sound has already been started and is now paused
	public boolean isPaused() {
		return isPaused;
	}
	
	//stops the sound, regardless if it is paused or not
	public void stop() {
		if(isPlaying())
		{
			clip.stop();
			isPaused = false;
		}
	}
	
	//pauses the sound
	public void pause() {
		if(isPlaying())
		{
			isPaused = true;
			pausePosition = clip.getMicrosecondPosition();
			clip.stop();
		}
	}
	
	//unpauses the sound
	public void unpause() {
		if(isPaused)
		{
			isPaused = false;
			clip.setMicrosecondPosition(pausePosition);
			clip.start();
		}
	}
}

class StandardSoundPlayer extends SoundPlayer
{
	private SourceDataLine line;
	private AudioFormat format;
	private AudioInputStream ain;

	//creates a sound at the given path - only works for .wav, .aiff, and .au
	public StandardSoundPlayer(String path)
	{
		super(path);
	}
	
	//begins playing the sound in a separate thread
	//the separate thread will begin to sleep if the Sound is paused
	//the thread will "wake up" when it is unpaused
	public void play()
	{	
		if(thread != null)
		{
			if(isPaused())
			{
				unpause();
				return;
			}
			else
				stop();
		}
		
		ain = null;  // We read audio data from here
		shouldStop = false;
		
		try
		{
			ain = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(path));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		format = ain.getFormat();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,format);

		if (!AudioSystem.isLineSupported(info)) 
		{
			AudioFormat pcm = new AudioFormat(format.getSampleRate(), 16, format.getChannels(), true, false);
			ain = AudioSystem.getAudioInputStream(pcm, ain);
			format = ain.getFormat(); 
			info = new DataLine.Info(SourceDataLine.class, format);
		}
		
		try
		{
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format);  
			line.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		Runnable r = new Runnable() {
			public void run()
			{
			
				try {
					byte[] buffer = new byte[line.getBufferSize()]; // the buffer
		
					shouldStop = false;
					isPaused = false;
					
					while(!shouldStop) {
						
						//pause the music!
						
						int bytesread = ain.read(buffer,0,buffer.length);
						if (bytesread == -1) 
							break;
						
						int offset = 0;
						while(offset < bytesread)
						{
							while(isPaused)
							{
								Thread.sleep(10);
								if(shouldStop)
									break;
							}
							
							offset += line.write(buffer, offset, bytesread-offset);
						}
					}
		
					line.drain();
					line.stop();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				finally { // Always relinquish the resources we use
					if (line != null) 
						line.close();
					try
					{
						if (ain != null) 
							ain.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
				thread = null;
			}
		};
		
		thread = new Thread(r);
		thread.start();
	}
	
	//pauses the sound
	public void pause() {
		super.pause();
		
		if(thread != null)
		{
			synchronized(line)
			{
				line.stop();
			}
		}
	}
	
	//unpauses the sound
	public void unpause() {
		super.unpause();
		
		if(thread != null)
		{
			synchronized(line)
			{
				line.start();
			}
		}
	}
	
}
