package org.iplusplus.rttf2android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.iplusplus.rttf2android.composition.Player;
import org.iplusplus.rttf2android.composition.Track;
import org.iplusplus.rttf2android.composition.TrackStorage;
import org.iplusplus.rttf2android.composition.samples.SampleCreators;

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

    ListView trackListView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_list);

        ListAdapter trackAdapter = new TrackAdapter(this, TrackStorage.getOne(this).getTracks(0, 20));
        trackListView = (ListView) findViewById(R.id.trackListView);

        trackListView.setAdapter(trackAdapter);
        trackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                trackListView.requestFocusFromTouch();
                trackListView.setSelection(position);
                onEditButtonClick(position);
            }
        });
        toolbar = (Toolbar) findViewById(R.id.track_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.track_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_track) {
            // TODO: do it normal way
            Intent intent = new Intent(this, TrackEditor.class);
            intent.putExtra("track", -1);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void play() {
        List<Track> tracks = TrackStorage.getOne(this).getTracks(0, 10);

        Track track = tracks.get(0);

        Player player = new Player(SampleCreators.SINE.get());

        player.play(track);
    }

    public void onEditButtonClick(int position) {
        Intent intent = new Intent(this, TrackEditor.class);
        intent.putExtra("track", position);
        startActivity(intent);
    }
}
