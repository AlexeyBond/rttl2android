package org.iplusplus.rttf2android.composition;

import android.app.Activity;
import android.support.annotation.NonNull;

import org.iplusplus.rttf2android.composition.storage.SQLiteStorage;
import org.iplusplus.rttf2android.composition.stub.Storage;

import java.util.List;

public abstract class TrackStorage {
    /**
     * Reads not more than {@code max} compositions from storage starting with the {@code first}.
     *
     * @param first
     * @param max
     * @return collection of compositions
     */
    @NonNull
    public abstract List<Track> getTracks(int first, int max);

    /**
     * Saves the changes made in composition to storage.
     *
     * @param track the composition to save
     */
    public abstract void saveTrack(@NonNull Track track);

    /**
     * Deletes the track from the storage.
     *
     * @param track the track
     */
    public abstract void deleteTrack(@NonNull Track track);

    public static TrackStorage getOne(Activity activity) {
        return new SQLiteStorage(activity);
    }
}
