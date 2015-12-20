package org.iplusplus.rttf2android.editor;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
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

    private TrackEditor theEditorActivity;
    private TextView textView;
    private View cursorBackButtonView;
    private View cursorForwardButtonView;
    private Editable theText = new Editable.Factory().newEditable("");
    private Track theTrack = TrackStorage.getOne().getTracks(0,1).get(0); // TODO: Get a required track
    private Player thePlayer = new Player(SampleCreators.SQUARE.get());
    private Map<Note, NotePosition> notePositionMap;

    private Observer playerCursorObserver = new Observer() {
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

    public EditorDisplay(TrackEditor editorActivity) {
        theEditorActivity = editorActivity;
        textView = (TextView)editorActivity.findViewById(R.id.compositionText);
        cursorBackButtonView = editorActivity.findViewById(R.id.cursorBackButton);
        cursorForwardButtonView = editorActivity.findViewById(R.id.cursorForwardButton);

        textView.setMovementMethod(new ScrollingMovementMethod());

        cursorBackButtonView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {EditorDisplay.this.cursorBack();}});
        cursorForwardButtonView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {EditorDisplay.this.cursorForward();}});

        thePlayer.play(theTrack);
        rebuildText();
        rebuildCursors();
    }

    private void cursorBack() {}
    private void cursorForward() {}

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

        thePlayer.getTrackCursor().addObserver(playerCursorObserver);

        NotePosition p = notePositionMap.get(thePlayer.getTrackCursor().current());
        if (p != null) {
            theText.setSpan(new BackgroundColorSpan(Color.GREEN), p.start, p.end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        Log.d("ED", "cursor rebuild end.");

        textView.setText(theText);
    }
}
