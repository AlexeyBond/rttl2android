package org.iplusplus.rttf2android.composition;

import android.media.AudioTrack;

import org.iplusplus.rttf2android.composition.samples.SampleCreator;

import java.util.Timer;
import java.util.TimerTask;

public class Player {
    public Player(SampleCreator sampleCreator) {
        this.sampleCreator = sampleCreator;
        this.sampleTrack = this.sampleCreator.createSample(NUM_SAMPLE_FRAMES);
    }

    private void stopSound() {
        sampleTrack.stop();
        this.isPlaying = false;
        raiseOnStopEvent();
    }

    private void raiseOnStopEvent(){
        if (eventListener != null) {
            eventListener.call();
        }
    }

    static double sqrt2_12 = Math.pow(2.0, 1.0 / 12.0);

    private void setupSampleToNote(Note note) {
        sampleTrack.pause();

        if(!note.isPause()) {
            int offFromA2 = note.getOffsetFrom(Note.Offsets.A, 2);

            double freq = 440.0 * Math.pow(sqrt2_12, offFromA2);

            int rate = (int)((double)NUM_SAMPLE_FRAMES * freq);

            sampleTrack.setPlaybackRate(rate);

            sampleTrack.play();
        }
    }

    public synchronized void play(Track track) {
        if (this.isPlaying()) {
            throw new IllegalStateException("already playing");
        }
        this.track = track;
        this.trackCursor = track.openCursor();
        this.isPlaying = true;

        timer.schedule(new NoteSetupTask(), 0);
    }

    public synchronized void stop() {
        stopSound();
    }

    private class NoteSetupTask extends TimerTask {
        @Override
        public void run() {
            if(trackCursor.forward() && isPlaying) {
                Note note = trackCursor.current();

                setupSampleToNote(note);

                long delay = note.getDuration(track.getTempo());

                timer.schedule(new NoteSetupTask(), delay);
            } else {
                stopSound();
            }
        }
    }

    public Track.Cursor getTrackCursor() {
        return trackCursor;
    }

    public boolean isPlaying() {return isPlaying;}

    public void setStopEventListener(ICallback callback){
        eventListener = callback;
    }

    private static int NUM_SAMPLE_FRAMES = 64;

    private Track.Cursor trackCursor;
    private Timer timer = new Timer();
    private Track track = null;
    private AudioTrack sampleTrack;
    private SampleCreator sampleCreator;
    private boolean isPlaying = false;
    private ICallback eventListener;
}
