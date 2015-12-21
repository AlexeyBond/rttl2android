package org.iplusplus.rttf2android;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
        if (id == R.id.home) {
            finishActivity(42);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

        this.editorDisplay.thePlayer.stop();
    }
}
