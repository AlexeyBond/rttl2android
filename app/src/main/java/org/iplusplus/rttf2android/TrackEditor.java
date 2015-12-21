package org.iplusplus.rttf2android;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_editor);

        setDefaultNoteControls();

        toolbar = (Toolbar) findViewById(R.id.track_editor_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        int track_position = (int) getIntent().getSerializableExtra("track");

    }

    private void setDefaultNoteControls() {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.track_editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d("MENU", "Key_id pressed: " + id);
        if(id == R.id.home){
            finishActivity(42);
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
