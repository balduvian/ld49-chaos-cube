package com.balduvian.cnge.sound;

import org.lwjgl.openal.AL10;

public class Sound {

	private int buffer;
	private int sourceId;
	
	public Sound(String path) {
		sourceId = AL10.alGenSources();
		buffer = AL10.alGenBuffers();
		WaveData waveData = WaveData.create(path);
		AL10.alBufferData(buffer, waveData.format, waveData.data, waveData.samplerate);
		AL10.alSourcei(sourceId, AL10.AL_BUFFER, buffer);
		AL10.alSourcef(sourceId, AL10.AL_GAIN, 1);
		AL10.alSourcef(sourceId, AL10.AL_PITCH, 1);
	}
	
	public void play(boolean loop) {
		AL10.alSourcei(sourceId, AL10.AL_LOOPING, loop ? 1 : 0);
		AL10.alSource3f(sourceId, AL10.AL_POSITION, 0, 0, 0);
		AL10.alSourcePlay(sourceId);
	}

	public void stop() {
		AL10.alSourceStop(sourceId);
	}

	//
	//sets
	//
	public void set_volume(float volume) {
		AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
	}


	public void set_pitch(float pitch) {
		AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch);
	}
	//
	//gets
	//
	public boolean is_playing() {
		return AL10.alGetBoolean(AL10.AL_PLAYING);
	}

	public void destroy() {
		AL10.alDeleteBuffers(buffer);
		AL10.alDeleteSources(sourceId);
	}
}