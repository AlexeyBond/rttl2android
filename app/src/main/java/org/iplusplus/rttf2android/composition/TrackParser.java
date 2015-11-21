package org.iplusplus.rttf2android.composition;

import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class TrackParser {
    private static Pattern globalDelimiter = Pattern.compile(":[ ]*");
    private static Pattern notesDelimiter = Pattern.compile("[:, ]+");
    private static Pattern defaultsPattern = Pattern.compile(
            "[ ]*(?:(?:(?:d=([0-9]+))|(?:o=([0-9]+))|(?:b=([0-9]+)))[, ]*)+");
    private static int defaultsPattern_group_d = 1;
    private static int defaultsPattern_group_o = 2;
    private static int defaultsPattern_group_b = 3;


    public static Track parseTrack(Reader reader)
            throws IOException {
        Scanner scanner = new Scanner(reader);
        Track track = new Track();
        Note.Defaults defaults;

        scanner.useDelimiter(globalDelimiter);
        track.setName(scanner.next());

        scanner.next(defaultsPattern);
        {
            MatchResult match = scanner.match();
            track.setTempo(Integer.valueOf(match.group(defaultsPattern_group_b)));
            final int octave = Integer.valueOf(match.group(defaultsPattern_group_o));
            final int duration = Integer.valueOf(match.group(defaultsPattern_group_d));

            defaults = new Note.Defaults() {
                @Override
                public int getDefaultOctave() {
                    return octave;
                }

                @Override
                public int getDefaultDurationDenominator() {
                    return duration;
                }
            };
        }

        scanner.useDelimiter(notesDelimiter);
        Track.Cursor cursor = track.openCursor();

        while (scanner.hasNext())
            cursor.insertAtLeft(Note.fromString(scanner.next(), defaults));

        return track;
    }
}
