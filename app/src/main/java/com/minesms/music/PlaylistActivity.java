package com.minesms.music;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class PlaylistActivity extends Activity {

    private ArrayList<String> playlist;
    private ListView playlistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        playlistView = findViewById(R.id.playlistView);
        playlist = getIntent().getStringArrayListExtra("PLAYLIST");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playlist);
        playlistView.setAdapter(adapter);

        playlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songPath = playlist.get(position);
                Intent intent = new Intent(PlaylistActivity.this, SongDetailActivity.class);
                intent.putExtra("SONG_PATH", songPath);
                startActivity(intent);
            }
        });
    }
}
