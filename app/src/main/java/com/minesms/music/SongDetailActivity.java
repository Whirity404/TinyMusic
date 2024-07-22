package com.minesms.music;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SongDetailActivity extends Activity {

    private MediaPlayer mediaPlayer;
    private Button playPauseButton;
    private Button shareButton;
    private Button addToPlaylistButton;
    private ArrayList<String> playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);

        ImageView albumCover = findViewById(R.id.album_cover);
        TextView songTitle = findViewById(R.id.song_title);
        playPauseButton = findViewById(R.id.play_pause_button);
        shareButton = findViewById(R.id.share_button);
        addToPlaylistButton = findViewById(R.id.add_to_playlist_button);

        Intent intent = getIntent();
        final String songPath = intent.getStringExtra("SONG_PATH");

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(songPath);

        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        byte[] artBytes = mmr.getEmbeddedPicture();

        if (title != null) {
            songTitle.setText(title);
        } else {
            songTitle.setText("Unknown Title");
        }

        if (artBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.length);
            albumCover.setImageBitmap(bitmap);
        } else {
            albumCover.setImageResource(R.drawable.default_cover);
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(songPath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start(); // 重新开始播放
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playPauseButton.setText("Play");
                } else {
                    mediaPlayer.start();
                    playPauseButton.setText("Pause");
                }
            }
        });

        // 设置初始按钮文本
        playPauseButton.setText(mediaPlayer.isPlaying() ? "Pause" : "Play");

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("audio/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(songPath));
                startActivity(Intent.createChooser(shareIntent, "Share song using"));
            }
        });

        // 初始化播放列表
        playlist = new ArrayList<>();

        addToPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlist.add(songPath);
                Toast.makeText(SongDetailActivity.this, "已添加到播放列表", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish(); // 确保活动被销毁
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
