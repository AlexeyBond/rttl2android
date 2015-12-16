package org.iplusplus.rttf2android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.iplusplus.rttf2android.composition.Player;
import org.iplusplus.rttf2android.composition.Track;
import org.iplusplus.rttf2android.composition.TrackStorage;
import org.iplusplus.rttf2android.composition.samples.SampleCreators;

import java.util.ArrayList;
import java.util.List;

public class MyTracks extends ActionBarActivity {

    private class TrackAdapter extends BaseAdapter {

        private List<Track> tracks;
        private LayoutInflater lInflater;
        private Context context;

        public TrackAdapter(Context context, List<Track> tracks) {
            super();
            this.tracks = tracks;
            this.context = context;
            lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return tracks.size();
        }

        @Override
        public Object getItem(int position) {
            return tracks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = lInflater.inflate(R.layout.track_layout, parent, false);
            }
            TextView trackName = (TextView) view.findViewById(R.id.trackName);
            trackName.setText(tracks.get(position).getName());
            return view;
        }
    }

    public void changeButtonsState() {
        ListView trackListView = (ListView) findViewById(R.id.trackListView);
        Track selectedTrack = (Track) trackListView.getSelectedItem();
        Button editTrackButton = (Button) findViewById(R.id.editTrackButton);
        Button renameButton = (Button) findViewById(R.id.renameButton);
        editTrackButton.setEnabled(selectedTrack != null);
        renameButton.setEnabled(selectedTrack != null);
    }

    ListView trackListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_tracks);


        ListAdapter trackAdapter = new TrackAdapter(this, TrackStorage.getOne().getTracks(0, 10));
        trackListView = (ListView) findViewById(R.id.trackListView);

        trackListView.setAdapter(trackAdapter);
        trackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                trackListView.requestFocusFromTouch();
                trackListView.setSelection(position);
                changeButtonsState();
            }
        });
        changeButtonsState();
//        {
//            Button playButton = (Button)findViewById(R.id.playButton);
//            playButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    play();
//                }
//            });
//        }
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

    public void onEditButtonClick(View btn) {
        Object selected = trackListView.getSelectedItem();
        if (selected != null) {
            Log.d(this.getClass().getName(),selected.toString());
        }
        Intent intent = new Intent(this, TrackEditor.class);
//        intent.putExtra("s", selectedTrack);
        startActivity(intent);
    }
}
