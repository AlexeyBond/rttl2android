package org.iplusplus.rttf2android.composition.storage;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.iplusplus.rttf2android.R;
import org.iplusplus.rttf2android.composition.Track;
import org.iplusplus.rttf2android.composition.TrackParser;
import org.iplusplus.rttf2android.composition.TrackStorage;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SQLiteStorage extends TrackStorage {
    SQLiteOpenHelper openHelper;
    HashMap<Track, Long> idMap = new HashMap<>();
    List<Track> cache = new LinkedList<>();

    public SQLiteStorage(final Activity activity) {
        openHelper = new SQLiteOpenHelper(activity, "3310tracks", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.beginTransaction();
                try {
                    String s = activity.getString(R.string.db_init_v1);
                    for (String q : s.split(";"))
                        db.execSQL(q+";");
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
        };
    }

    private void fillCacheTo(int count) {
        int first = cache.size();
        if (count <= first) {
            return;
        }
        Cursor c = openHelper.getReadableDatabase()
//                .query("THETRACKS", null, null, null, null, null, "ID", String.format("%d OFFSET %d", count - first, first));
                .rawQuery("SELECT * FROM THETRACKS ORDER BY ID LIMIT ? OFFSET ?",
                        new String[] {String.valueOf(count-first), String.valueOf(first)});
        try {
            if (c.moveToFirst()) {
                int idColIndex = c.getColumnIndex("ID");
                int trackColIndex = c.getColumnIndex("TRACK");

                do {
                    String str = c.getString(trackColIndex);
                    Reader r = new StringReader(str);
                    Track track = TrackParser.parseTrack(r);
                    long id = c.getLong(idColIndex);
                    idMap.put(track, id);
                    cache.add(track);
                } while (c.moveToNext());
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            c.close();
        }
    }

    @NonNull
    public List<Track> getTracks(int first, int max) {
        List<Track> found = new LinkedList<>();

        fillCacheTo(first + max);

        for (int i = first; (i < (first+max)) && (i < cache.size()); ++i)
            found.add(cache.get(i));

        return found;
    }

    public void saveTrack(@NonNull Track track) {
        Long id = idMap.get(track);
        String serialized = track.toString();
        if (id != null) {
            SQLiteDatabase db = openHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                SQLiteStatement statement = db
                        .compileStatement("UPDATE THETRACKS SET TRACK=? WHERE ID=?;");
                statement.bindString(1, serialized);
                statement.bindLong(2, (long) id);
                statement.execute();
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
                db.close();
            }
        } else {
            SQLiteDatabase db = openHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues cv = new ContentValues();
                cv.put("TRACK", serialized);
                id = db.insert("THETRACKS", null, cv);
                idMap.put(track, id);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
                db.close();
            }
        }
    }

    public void deleteTrack(@NonNull Track track) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            SQLiteStatement statement = openHelper.getWritableDatabase()
                    .compileStatement("DELETE FROM THETRACKS WHERE ID=?;");
            statement.bindLong(1, idMap.get(track));
            statement.execute();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}
