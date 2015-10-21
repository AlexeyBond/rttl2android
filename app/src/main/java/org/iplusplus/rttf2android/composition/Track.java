package org.iplusplus.rttf2android.composition;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.regex.Pattern;

public class Track extends Observable {
    public class Cursor {
        private ListIterator<Note> iterator;
        private Note currentNote;

        public boolean forward() {
            if(iterator.hasNext()) {
                currentNote = iterator.next();
                return true;
            }

            return false;
        }

        public boolean backward() {
            if(iterator.hasPrevious()) {
                currentNote = iterator.previous();
                return true;
            }

            return false;
        }

        public Note current() {
            return currentNote;
        }

        public void deleteCurrent() {
            iterator.remove();
            Track.this.notifyObservers();
        }

        public void insertAtLeft(Note note) {
            iterator.add(note);
            iterator.previous();
            currentNote = iterator.next();
            Track.this.notifyObservers();
        }

        private Cursor() {
            this.iterator = notes.listIterator();
        }
    }

    public static Track readFrom(String source) {
        String[] notes = notesSeparatorPattern.split(source);

        Track track = new Track();

        for(String noteStr : notes) {
            Note note = Note.fromString(noteStr, Note.defaultDefaults);

            track.notes.add(note);
        }

        return track;
    }

    public Cursor openCursor() {
        return new Cursor();
    }

    /**
     * @return tempo in bpm (beats per minute).
     */
    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    /* Private */

    static Pattern notesSeparatorPattern = Pattern.compile("[, \t]+");

    private List<Note> notes = new LinkedList<>();
    private int tempo = 20;

}
