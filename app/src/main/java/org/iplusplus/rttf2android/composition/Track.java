package org.iplusplus.rttf2android.composition;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;

public class Track extends Observable {
    public class Cursor extends Observable {
        private ListIterator<Note> iterator;
        private Note currentNote;

        public boolean forward() {
            if(iterator.hasNext()) {
                currentNote = iterator.next();
                Cursor.this.setChanged();
                Cursor.this.notifyObservers();
                return true;
            }

            return false;
        }

        public boolean backward() {
            if(iterator.hasPrevious()) {
                currentNote = iterator.previous();
                Cursor.this.setChanged();
                Cursor.this.notifyObservers();
                return true;
            }

            return false;
        }

        public Note current() {
            return currentNote;
        }

        public void deleteCurrent() {
            iterator.remove();
            Cursor.this.setChanged();
            Cursor.this.notifyObservers();
            Track.this.setChanged();
            Track.this.notifyObservers();
        }

        public void insertAtLeft(Note note) {
            iterator.add(note);
            iterator.previous();
            currentNote = iterator.next();
            Cursor.this.setChanged();
            Cursor.this.notifyObservers();
            Track.this.setChanged();
            Track.this.notifyObservers();
        }

        private Cursor() {
            this.iterator = notes.listIterator();
        }
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
        if (tempo != this.tempo) {
            this.tempo = tempo;
            this.notifyObservers();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        if (!name.equals(this.name)) {
            this.name = name;
            this.notifyObservers();
        }
    }

    /* Private */

    private List<Note> notes = new LinkedList<>();
    private int tempo = 20;
    private String name;
}
