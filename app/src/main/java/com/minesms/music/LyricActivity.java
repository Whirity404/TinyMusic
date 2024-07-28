package com.minesms.music;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

public class LyricActivity extends Activity {

    private TextView lyricText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric);

        lyricText = findViewById(R.id.lyric_text);

        // 获取传递的歌曲关键词
        String songKeyword = getIntent().getStringExtra("SONG_KEYWORD");
        if (songKeyword != null) {
            loadLyrics(songKeyword);
        }
    }

    private void loadLyrics(String keyword) {
        File lyricsDir = new File(Environment.getExternalStorageDirectory(), "Music");
        if (lyricsDir.exists() && lyricsDir.isDirectory()) {
            for (File file : lyricsDir.listFiles()) {
                if (file.getName().contains(keyword) && file.getName().endsWith(".lrc")) {
                    try {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            StringBuilder lyrics = new StringBuilder();
                            String line;
                            try {
                                while ((line = reader.readLine()) != null) {
                                    lyrics.append(line).append("\n");
                                }
                            } catch (IOException e) {}
                            lyricText.setText(lyrics.toString());
                        }
                    } catch (FileNotFoundException e) {} catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
}

