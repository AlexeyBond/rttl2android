package org.iplusplus.rttf2android.composition.samples;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.AudioManager;

public abstract class SampleCreator {
    public AudioTrack createSample(int frames) {
        short[] sampleData = new short[frames];

        for (int i = 0; i < frames; i++) {
            sampleData[i] = sampleAt(i, frames);
        }

        AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                44000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                2*frames,
                AudioTrack.MODE_STATIC);

        audioTrack.write(sampleData, 0, frames);

        audioTrack.setLoopPoints(0, frames, -1);

        return audioTrack;
    }

    protected abstract short sampleAt(int frame, int ofFrames);
}
