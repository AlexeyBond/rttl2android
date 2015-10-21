package org.iplusplus.rttf2android;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.iplusplus.rttf2android.composition.Player;
import org.iplusplus.rttf2android.composition.Track;
import org.iplusplus.rttf2android.composition.samples.SampleCreators;

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
        Track track = Track.readFrom("8d.,8d.,8d.,8a#4,16f,8d" +
                ".,8a#4,16f,d.,32p,8a.,8a.,8a.,8a#,16f,8c#.,8a" +
                "#4,16f,d.,32p,8d6.,8d,16d,8d6,32p,8c#6,16c6,1" +
                "6b,16a#,8b,32p,16d#,8g#,32p,8g,16f#,16f,16e,8" +
                "f,32p,16a#4,8c#,32p,8a#4,16c#,8f.,8d,16f,a.,3" +
                "2p,8d6.,8d,16d,8d6,32p,8c#6,16c6,16b,16a#,8b," +
                "32p,16d#,8g#,32p,8g,16f#,16f,16e,8f,32p,16a#4" +
                ",8c#,32p,8a#4,16f,8d.,8a#4,16f,d."
        );

//        Track track = Track.readFrom("1e,2d,4c,8b");

        Player player = new Player(SampleCreators.SINE.get());

        player.play(track);
    }
}
