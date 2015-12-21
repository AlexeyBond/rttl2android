package org.iplusplus.rttf2android.editor;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private Track theTrack;
    public Player thePlayer = new Player(SampleCreators.SQUARE.get());
    private Map<Note, NotePosition> notePositionMap;
    private Track.Cursor theEditCursor;
    private boolean diez_enable = false;

    private int statusDurationBase = 4;
    private boolean statusDurationDot = false;
    private int statusOctave = 4;

    private Note.Defaults noteDefaults = new Note.Defaults() {
        @Override
        public int getDefaultOctave() {
            return statusOctave;
        }

        @Override
        public int getDefaultDurationDenominator() {
            return statusDurationBase;
        }
    };

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
        @Override
        public void onClick(View view) {
            synchronized (thePlayer) {
                if (!thePlayer.isPlaying()) {
                    theEditCursor.insertAtLeft(Note.fromString(
                            ((Button)view).getText().toString() + (statusDurationDot?".":""),
                            noteDefaults));
                    rebuildText();
                    rebuildCursors();
                }
            }
        }
    }

    class TempoClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int t = theTrack.getTempo();
            t += 20;
            if (t > 400)
                t = 60;
            theTrack.setTempo(t);
            setupButtonLabels();
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
                        theEditCursor.forward();
                    } catch (Throwable ignoreFuckingErrors) {}
                    rebuildText();
                    rebuildCursors();
                }
            }
        }
    }

    class DiezControlListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            diez_enable = !diez_enable;
            setupButtonLabels();
        }
    }

    class DurationClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            statusDurationBase *= 2;
            if(statusDurationBase > 32)
                statusDurationBase = 1;
            setupButtonLabels();
        }
    }

    class OctaveClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            statusOctave++;
            if (statusOctave > 8)
                statusOctave = 2;
            setupButtonLabels();
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

    class DotControlListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            statusDurationDot = !statusDurationDot;
            setupButtonLabels();
        }
    }

    public EditorDisplay(TrackEditor editorActivity) {
        theEditorActivity = editorActivity;
        textView = (TextView)editorActivity.findViewById(R.id.compositionText);
        cursorBackButtonView = editorActivity.findViewById(R.id.cursorBackButton);
        cursorForwardButtonView = editorActivity.findViewById(R.id.cursorForwardButton);

        int trackId = theEditorActivity.getIntent().getIntExtra("track", 0);
        if (trackId < 0) {
            theTrack = new Track();
            // TODO: open rename dialog, save to storage
        } else {
            theTrack = TrackStorage.getOne().getTracks(trackId, 1).get(0);
        }

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
                    .setOnClickListener(new NoteButtonOnClickListener());
        }

        theEditorActivity.findViewById(PLAY_CONTROL_ID).setOnClickListener(new PlayClickListener());
        theEditorActivity.findViewById(BACKSPACE_CONTROL_ID).setOnClickListener(new BackspaceListener());
        theEditorActivity.findViewById(DIEZ_CONTROL_ID).setOnClickListener(new DiezControlListener());
        theEditorActivity.findViewById(DOT_CONTROL_ID).setOnClickListener(new DotControlListener());
        theEditorActivity.findViewById(TEMPO_CONTROL_ID).setOnClickListener(new TempoClickListener());
        theEditorActivity.findViewById(OCTAVE_CONTROL_ID).setOnClickListener(new OctaveClickListener());
        theEditorActivity.findViewById(DURATION_CONTROL_ID).setOnClickListener(new DurationClickListener());

        setupButtonLabels();
    }

    void setupButtonLabels() {
        for (int i = 0; i < NOTE_CONTROL_IDS.length; ++i) {
            String t = NOTE_BASE_NAMES[i] + (diez_enable?"#":"");
            try {
                Note.getOffsetFromString(t);
            } catch (Throwable e) {
                t = "P";
            }
            ((Button) theEditorActivity.findViewById(NOTE_CONTROL_IDS[i])).setText(t);
        }

        ((Button) theEditorActivity.findViewById(DIEZ_CONTROL_ID)).setText("#");
        ((Button) theEditorActivity.findViewById(DOT_CONTROL_ID)).setText(".");
        ((Button) theEditorActivity.findViewById(TEMPO_CONTROL_ID)).setText("T: " + String.valueOf(theTrack.getTempo()));
        ((Button) theEditorActivity.findViewById(OCTAVE_CONTROL_ID)).setText("O: " + String.valueOf(statusOctave));
        ((Button) theEditorActivity.findViewById(DURATION_CONTROL_ID)).setText("D: " + String.valueOf(statusDurationBase));

        ((TextView) theEditorActivity.findViewById(R.id.note_value))
                .setText( String.valueOf(statusOctave) + "X" +
                        (statusDurationDot ? "." : "") + String.valueOf(statusDurationBase));

        ((TextView) theEditorActivity.findViewById(R.id.temp_value))
                .setText(String.valueOf(theTrack.getTempo()));
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
