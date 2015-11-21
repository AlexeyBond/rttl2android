package org.iplusplus.rttf2android;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.iplusplus.rttf2android.composition.Player;
import org.iplusplus.rttf2android.composition.Track;
import org.iplusplus.rttf2android.composition.TrackStorage;
import org.iplusplus.rttf2android.composition.samples.SampleCreators;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        {
            Button playButton = (Button)findViewById(R.id.playButton);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void play() {
        List<Track> tracks = TrackStorage.getOne().getTracks(0, 10);

        Track track = tracks.get(0);

        Player player = new Player(SampleCreators.SINE.get());

        player.play(track);
    }
}
