package org.iplusplus.rttf2android.editor;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.iplusplus.rttf2android.R;
import org.iplusplus.rttf2android.TrackEditor;
import org.iplusplus.rttf2android.composition.Note;
import org.iplusplus.rttf2android.composition.Player;
import org.iplusplus.rttf2android.composition.Track;
import org.iplusplus.rttf2android.composition.TrackStorage;
import org.iplusplus.rttf2android.composition.samples.SampleCreators;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class EditorDisplay {
    private class NotePosition {
        public int start;
        public int end;
    }

    private static int[] NOTE_CONTROL_IDS = {R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,R.id.btn5,R.id.btn6,R.id.btn8};
    private static String[] NOTE_BASE_NAMES = {"C", "D", "E", "F", "G", "A", "B"};
    private static int BACKSPACE_CONTROL_ID = R.id.backspaceButton;
    private static int TEMPO_CONTROL_ID = R.id.btn7;
    private static int OCTAVE_CONTROL_ID = R.id.btn9;
    private static int DIEZ_CONTROL_ID = R.id.btn12;
    private static int DURATION_CONTROL_ID = R.id.btn10;
    private static int DOT_CONTROL_ID = R.id.btn11;
    private static int PLAY_CONTROL_ID = R.id.playButton;

    private TrackEditor theEditorActivity;
    private TextView textView;
    private View cursorBackButtonView;
    private View cursorForwardButtonView;
    private Editable theText = new Editable.Factory().newEditable("");
    private Track theTrack = TrackStorage.getOne().getTracks(0,3).get(2); // TODO: Get a required track
    private Player thePlayer = new Player(SampleCreators.SQUARE.get());
    private Map<Note, NotePosition> notePositionMap;
    private Track.Cursor theEditCursor;

    private Observer cursorObserver = new Observer() {
        @Override
        public void update(Observable observable, Object data) {
            theEditorActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EditorDisplay.this.rebuildCursors();
                }
            });
        }
    };

    private Observer trackObserver = new Observer() {
        @Override
        public void update(Observable observable, Object data) {
            theEditorActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rebuildText();
                }
            });
        }
    };

    class NoteButtonOnClickListener implements View.OnClickListener {
        String name, baseName;

        NoteButtonOnClickListener(String baseName) {
            this.baseName = baseName;
            this.name = baseName;
        }

        @Override
        public void onClick(View view) {
            synchronized (thePlayer) {
                if (!thePlayer.isPlaying()) {
                    theEditCursor.insertAtLeft(Note.fromString(name, Note.defaultDefaults));
                    rebuildText();
                    rebuildCursors();
                }
            }
        }
    }

    class BackspaceListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            synchronized (thePlayer) {
                if (!thePlayer.isPlaying()) {
                    try {
                        theEditCursor.deleteCurrent();
                        theEditCursor.backward();//ХЗ как оно работает
                    } catch (Throwable ignoreFuckingErrors) {}
                    rebuildText();
                    rebuildCursors();
                }
            }
        }
    }

    class PlayClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            synchronized (thePlayer) {
                if (thePlayer.isPlaying()) {
                    thePlayer.stop();
                } else {
                    thePlayer.play(theTrack);
                    thePlayer.getTrackCursor().addObserver(cursorObserver);
                }
            }
        }
    }

    public EditorDisplay(TrackEditor editorActivity) {
        theEditorActivity = editorActivity;
        textView = (TextView)editorActivity.findViewById(R.id.compositionText);
        cursorBackButtonView = editorActivity.findViewById(R.id.cursorBackButton);
        cursorForwardButtonView = editorActivity.findViewById(R.id.cursorForwardButton);

        textView.setMovementMethod(new ScrollingMovementMethod());

        cursorBackButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditorDisplay.this.cursorBack();
            }
        });
        cursorForwardButtonView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {EditorDisplay.this.cursorForward();}});

        theEditCursor = theTrack.openCursor();
        theEditCursor.addObserver(cursorObserver);

        thePlayer.play(theTrack);
        rebuildText();
        rebuildCursors();

        for (int i = 0; i < NOTE_CONTROL_IDS.length; ++i) {
            theEditorActivity
                    .findViewById(NOTE_CONTROL_IDS[i])
                    .setOnClickListener(new NoteButtonOnClickListener(NOTE_BASE_NAMES[i]));
        }

        theEditorActivity.findViewById(PLAY_CONTROL_ID).setOnClickListener(new PlayClickListener());
        theEditorActivity.findViewById(BACKSPACE_CONTROL_ID).setOnClickListener(new BackspaceListener());
    }

    private void cursorBack() {
        theEditCursor.backward();
    }
    private void cursorForward() {
        theEditCursor.forward();
    }

    public void rebuildText() {
        notePositionMap = new HashMap<>();

        Track.Cursor c = theTrack.openCursor();

        StringBuilder sb = new StringBuilder();

        while (c.forward()) {
            Note n = c.current();
            String s = n.toString();
            NotePosition p = new NotePosition();
            p.start = sb.length();
            p.end = p.start + s.length();
            notePositionMap.put(n, p);
            sb.append(s);
            sb.append(' ');
        }

        theText.clear();
        theText.append(sb.toString());
        textView.setText(theText);
    }

    public void rebuildCursors() {
        theText.clearSpans();
        Log.d("ED", "cursor rebuild start.");

        thePlayer.getTrackCursor().addObserver(cursorObserver);

        NotePosition p = notePositionMap.get(thePlayer.getTrackCursor().current());
        if (p != null) {
            theText.setSpan(new BackgroundColorSpan(Color.GREEN), p.start, p.end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        p = notePositionMap.get(theEditCursor.current());

        if (p != null) {
            theText.setSpan(new BackgroundColorSpan(Color.BLUE), p.end, p.end+1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        Log.d("ED", "cursor rebuild end.");

        textView.setText(theText);
    }
}
