package com.balduvian.cnge.sound;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.ALC10.*;

public class AL_Management
{

	private long device, context;
	private ALCCapabilities deviceCaps;
	
	public AL_Management() {
		device = alcOpenDevice((ByteBuffer) null);
		if (device == 0)
			throw new IllegalStateException("Failed to open the default device.");
	
		deviceCaps = ALC.createCapabilities(device);
	
		context = alcCreateContext(device, (IntBuffer) null);
		if (context == 0)
		 throw new IllegalStateException("Failed to create an OpenAL context.");
		
		alcMakeContextCurrent(context);
		AL.createCapabilities(deviceCaps);
	}
	
	public void destroy() {
		ALC10.alcDestroyContext(context);
		ALC10.alcCloseDevice(device);
	}
}