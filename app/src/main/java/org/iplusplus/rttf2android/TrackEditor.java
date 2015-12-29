package org.iplusplus.rttf2android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.iplusplus.rttf2android.editor.EditorDisplay;

public class TrackEditor extends ActionBarActivity {
    EditorDisplay editorDisplay;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_editor);
        editorDisplay = new EditorDisplay(this);
        toolbar = (Toolbar) findViewById(R.id.track_editor_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        int track_position = (int) getIntent().getSerializableExtra("track");

//        Button backspaceButton = (Button) findViewById(R.id.backspaceButton);
//        backspaceButton.setBackground(R.drawable.ic_input_delete);
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
        if(id == R.id.rename_menu_btn){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Rename track");
            final EditText input = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);
            alertDialog.setMessage("Current name: " + editorDisplay.getTheTrack().getName());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            editorDisplay.getTheTrack().setName(input.getText().toString());
                            editorDisplay.save();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else if(id == R.id.save_menu_btn){
            editorDisplay.save();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

        this.editorDisplay.thePlayer.stop();
    }
}
