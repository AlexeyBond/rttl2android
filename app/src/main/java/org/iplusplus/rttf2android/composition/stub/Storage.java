package org.iplusplus.rttf2android.composition.stub;

import android.support.annotation.NonNull;

import org.iplusplus.rttf2android.composition.TrackParser;
import org.iplusplus.rttf2android.composition.TrackStorage;
import org.iplusplus.rttf2android.composition.Track;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class Storage extends TrackStorage {
    @NonNull
    @Override
    public List<Track> getTracks(int first, int max) {
        List<Track> result = new LinkedList<>();

        Iterator<Track> storageIterator = storage.iterator();

        try {
            while (first-- != 0)
                storageIterator.next();

            while (max-- != 0)
                result.add(storageIterator.next());
        } catch (NoSuchElementException noElement) {
            return result;
        }

        return result;
    }

    @Override
    public void saveTrack(@NonNull Track track) {}

    @Override
    public void deleteTrack(@NonNull Track track) {}

    private List<Track> storage = new LinkedList<>();

    {
        try {
            storage.add(TrackParser.parseTrack(new StringReader(
                    "Stand My Ground:d=4, o=5, b=112:g., 8f, 8g., 16f, 8g., 16g#, 2g., 8a#., 16g#, 8g, 2f," +
                    "8p, 8f., 16g, 8f, d#., g#4, g#, g., 8f, 8g., 16f, 8g., 16g#, 2g., 8a#.," +
                    "16g#, 8g, 2f, 8p, 8f., 16g, 8f, d#., c#, a#4, 1c, c.6, 8a#, 8c.6, 16a#," +
                    "8c.6, 16c#6, 2c.6, 8d#.6, 16c#6, 8c6, 2a#, 8p, 8a#., 16c6, 8a#, g#., c#6," +
                    "c6, c.6, 8a#, 8c.6, 16a#, 8c.6, 16c#6, 2c.6, 8d#.6, 16c#6, 8c6, 2a#, 8p," +
                    "8a#., 16c6, 8a#, 2g#, 8p, 8g., 16f, 1f")));
            storage.add(TrackParser.parseTrack(new StringReader(
                    "Jillian:d=4,o=5,b=160:g#,g#,a,p,f#,f#,g#,p,e,e,f#,d#,c#,c,c#,p,g#," +
                    "g#,f#,f#,e,e,a,p,e,e,d#,a,g#,c#6,c6,g#,g#,g#,a,p,f#,f#,g#,p,e,e,f#,d#,c#,c," +
                    "c#,2c#,e,2f#,e,d#.,16e,16d#,2c#.")));
            storage.add(TrackParser.parseTrack(new StringReader(
                    "Bond:d=4,o=5,b=320:c,8d,8d,d,2d,c,c,c,c,8d#,8d#,2d#,d,d,d,c,8d,8d,d,2d,c,c,c,c," +
                    "8d#,8d#,d#,2d#,d,c#,c,c6,1b.,g,f,1g.")));
            storage.add(TrackParser.parseTrack(new StringReader(
                    "Imperial:d=4,o=5,b=80:8d.,8d.,8d.,8a#4,16f,8d.,8a#4,16f,d.,32p,8a.,8a.,8a.,8a#," +
                    "16f,8c#.,8a#4,16f,d.,32p,8d.6,8d,16d,8d6,32p,8c#6,16c6,16b,16a#,8b,32p,16d#,8g#" +
                    ",32p,8g,16f#,16f,16e,8f,32p,16a#4,8c#,32p,8a#4,16c#,8f.,8d,16f,a.,32p,8d.6,8d," +
                    "16d,8d6,32p,8c#6,16c6,16b,16a#,8b,32p,16d#,8g#")));
            storage.add(TrackParser.parseTrack(new StringReader(
                    "HauntHouse: d=4,o=5,b=108: 2a4, 2e, 2d#, 2b4, 2a4, 2c, 2d, 2a#4, 2e., e, 1f4," +
                    "1a4, 1d#, 2e., d, 2c., b4, 1a4, 1p, 2a4, 2e, 2d#, 2b4, 2a4, 2c, 2d, 2a#4, 2e." +
                    ", e, 1f4, 1a4, 1d#, 2e., d, 2c., b4, 1a4")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Storage single = new Storage();
}
