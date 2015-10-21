package org.iplusplus.rttf2android.composition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Note {
    /* Public **/

    public interface Offsets {
        int C = 0;
        int Cd = 1;
        int D = 2;
        int Dd = 3;
        int E = 4;
        int F = 5;
        int Fd = 6;
        int G = 7;
        int Gd = 8;
        int A = 9;
        int Ad = 10;
        int B = 11;

        int Octave = 12;
    }

    public interface Defaults {
        int getDefaultOctave();
        int getDefaultDurationDenominator();
    }

    public static Defaults defaultDefaults = new Defaults() {
        @Override
        public int getDefaultOctave() {
            return 5;
        }

        @Override
        public int getDefaultDurationDenominator() {
            return 4;
        }
    };

    @Nullable
    public static Note fromString(@NonNull String string, Defaults defaults) {
        Matcher matcher = notePattern.matcher(string.toUpperCase());
        if(!matcher.matches()) {
            Log.e("COMPOSITION",String.format("Invalid note syntax: '%s'", string));
            return null;
        }

        int durationNom = 1;
        int durationDenominator = parseIntOrDefault(
                matcher.group(NOTE_PATTERN_DURATION_GROUP),
                defaults.getDefaultDurationDenominator());

        int octave = parseIntOrDefault(
                matcher.group(NOTE_PATTERN_OCTAVE_GROUP),
                defaults.getDefaultOctave());
        int offset = 0;
        boolean isPause = false;
        {
            String note = matcher.group(NOTE_PATTERN_PITCH_GROUP);

            if(note.equals("P")) {
                isPause = true;
            } else {
                offset = getOffsetFromString(note);
            }
        }
        boolean dotted = !matcher.group(NOTE_PATTERN_DOTTING_GROUP).isEmpty();

        if(dotted) {
            durationNom = 3;
            durationDenominator *= 2;
        }

        return new Note(octave,offset,durationNom,durationDenominator,isPause);
    }

    @Override
    public String toString() {
        /*TODO: Serialize note to string.*/
        return "";
    }

    public int getOffsetFrom(int note, int octave) {
        int fromC0 = this.octave * 12 + this.note;
        return fromC0 - (octave*Offsets.Octave + note);
    }

    /**
     * Returns duration of note
     *
     * @param tempo tempo of composition in bpm
     * @return duration of note in milliseconds
     */
    public int getDuration(int tempo) {
        final int msPerMinute = 1000 * 60;

        int msPerWholeNote = msPerMinute / tempo;

        int duration = (msPerWholeNote * lengthNominator) / lengthDenominator;

        Log.d("COMPOSITION", String.format("Playing note for %dms",duration));

        return duration;
    }

    public boolean isPause() {
        return isPause;
    }

    /* Private **/

    private Note(int octave, int note, int lengthNominator, int lengthDenominator, boolean isPause) {
        this.note = note;
        this.octave = octave;
        this.lengthNominator = lengthNominator;
        this.lengthDenominator = lengthDenominator;
        this.isPause = isPause;

        this.normalize();
    }

    private static int parseIntOrDefault(String string, int default_) {
        if(string == null || string.isEmpty()) {
            return default_;
        }

        return Integer.valueOf(string);
    }

    private static Pattern notePattern = Pattern.compile("^([0-9]{1,2})?([A-G]#?|P)([0-9])?(\\.?)$");
    private static int NOTE_PATTERN_DURATION_GROUP = 1;
    private static int NOTE_PATTERN_PITCH_GROUP = 2;
    private static int NOTE_PATTERN_OCTAVE_GROUP = 3;
    private static int NOTE_PATTERN_DOTTING_GROUP = 4;

    private static int getOffsetFromString(String string) {
        return noteStrings.get(string);
    }

    private static String getStringOfOffset(int offset) {
        return noteStrings.inverse().get(offset);
    }

    private static BiMap<String,Integer> noteStrings;

    static {
        noteStrings = HashBiMap.create();

        noteStrings.put("C", Offsets.C);
        noteStrings.put("C#", Offsets.Cd);
        noteStrings.put("D", Offsets.D);
        noteStrings.put("D#", Offsets.Dd);
        noteStrings.put("E", Offsets.E);
        noteStrings.put("F", Offsets.F);
        noteStrings.put("F#", Offsets.Fd);
        noteStrings.put("G", Offsets.G);
        noteStrings.put("G#", Offsets.Gd);
        noteStrings.put("A", Offsets.A);
        noteStrings.put("A#", Offsets.Ad);
        noteStrings.put("B", Offsets.B);
    }

    private void normalize() {
        /*TODO: Normalize offset/octave, size.*/
    }

    private int octave;
    private int note;
    private int lengthNominator;
    private int lengthDenominator;
    private boolean isPause;
}
