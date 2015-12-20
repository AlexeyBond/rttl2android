package org.iplusplus.rttf2android;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.iplusplus.rttf2android.editor.EditorDisplay;

import java.util.ArrayList;
import java.util.HashMap;

public class TrackEditor extends ActionBarActivity {

    HashMap<Integer, NoteControl> noteControls = new HashMap();
    int[] buttonsIDs = new int[]{
            R.id.btn1,
            R.id.btn2,
            R.id.btn3,
            R.id.btn4,
            R.id.btn5,
            R.id.btn6,
            R.id.btn7,
            R.id.btn8,
            R.id.btn9,
            R.id.btn10,
            R.id.btn11,
            R.id.btn12};

    EditorDisplay editorDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_editor);
        for (int i = 0; i < buttonsIDs.length; i++) {
            NoteControl nc = new NoteControl(buttonsIDs[i]);
            findViewById(buttonsIDs[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noteControls.get(v.getId()).getAction().use();
                }
            });
            noteControls.put(buttonsIDs[i], nc);
        }

        noteControls.get(R.id.btn1).setAction(new INoteAction() {
            @Override
            public boolean use() {
                Log.d("TAG", "KEK");
                return false;
            }

            @Override
            public String getText() {
                return "C";
            }
        });
        noteControls.get(R.id.btn2).setAction(new INoteAction() {
            @Override
            public boolean use() {
                Log.d("TAG", "KEK");
                return false;
            }

            @Override
            public String getText() {
                return "D";
            }
        });
        noteControls.get(R.id.btn3).setAction(new INoteAction() {
            @Override
            public boolean use() {
                Log.d("TAG", "KEK");
                return false;
            }

            @Override
            public String getText() {
                return "E";
            }
        });
        editorDisplay = new EditorDisplay(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class NoteControl{
        private INoteAction action = new INoteAction() {
            @Override
            public boolean use() {
                return true;
            }

            @Override
            public String getText() {
                return "btn";
            }
        };

        private int id;

        public NoteControl(int id){
            this.id = id;
        }

        public INoteAction getAction() {
            return action;
        }

        public void setAction(INoteAction action) {
            this.action = action;
            ((Button) findViewById(id)).setText(action.getText());
        }
    }

    interface INoteAction{
        boolean use();
        String getText();
    }


}
