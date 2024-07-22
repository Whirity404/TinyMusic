package com.minesms.music;

import android.Manifest;
import android.os.Bundle;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.media.MediaPlayer;
import android.app.Activity;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final int REQUEST_PERMISSION = 1;
    private ArrayList<String> songList;
    private ArrayList<String> displayList;
    private ArrayList<String> playlist;
    private ListView listView;
    private MediaPlayer mediaPlayer;
    private Button playlistButton;
    private EditText searchBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 启动前台服务
        Intent serviceIntent = new Intent(this, NotificationKeepService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        playlistButton = findViewById(R.id.playlist_button);
        searchBox = findViewById(R.id.search_box);
        songList = new ArrayList<>();
        displayList = new ArrayList<>();
        playlist = new ArrayList<>();
        mediaPlayer = new MediaPlayer();

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            loadSongs();
        }

        setupListeners();
    }
    private void setupListeners() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songPath = songList.get(position);
                Intent intent = new Intent(MainActivity.this, SongDetailActivity.class);
                intent.putExtra("SONG_PATH", songPath);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String songPath = songList.get(position);
                playlist.add(songPath);
                Toast.makeText(MainActivity.this, "已添加到播放列表", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlaylistToFile(playlist);
                Intent intent = new Intent(MainActivity.this, PlaylistActivity.class);
                intent.putStringArrayListExtra("PLAYLIST", playlist);
                startActivity(intent);
            }
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSongs(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private void loadSongs() {
        File musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        if (musicDir.exists() && musicDir.isDirectory()) {
            File[] files = musicDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && (file.getName().endsWith(".mp3") || file.getName().endsWith(".flac"))) {
                        songList.add(file.getAbsolutePath());
                        String fileName = file.getName();
                        // 去掉文件后缀名
                        if (fileName.endsWith(".mp3")) {
                            fileName = fileName.substring(0, fileName.length() - 4);
                        } else if (fileName.endsWith(".flac")) {
                            fileName = fileName.substring(0, fileName.length() - 5);
                        }
                        displayList.add(fileName);
                    }
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);
    }

    private void filterSongs(String query) {
        if (query.isEmpty()) {
            songList.clear();
            displayList.clear();
            loadSongs(); // 重新加载所有音乐
        } else {
            ArrayList<String> filteredList = new ArrayList<>();
            ArrayList<String> filteredPaths = new ArrayList<>();
            for (String songPath : songList) {
                String fileName = new File(songPath).getName();
                if (fileName.toLowerCase().contains(query.toLowerCase())) {
                    if (fileName.endsWith(".mp3")) {
                        fileName = fileName.substring(0, fileName.length() - 4);
                    } else if (fileName.endsWith(".flac")) {
                        fileName = fileName.substring(0, fileName.length() - 5);
                    }
                    filteredList.add(fileName);
                    filteredPaths.add(songPath);
                }
            }
            displayList.clear();
            displayList.addAll(filteredList);
            songList.clear();
            songList.addAll(filteredPaths);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);
    }
    private void savePlaylistToFile(ArrayList<String> playlist) {
        File playlistDir = new File(getExternalFilesDir(null), "Playlist");
        if (!playlistDir.exists()) {
            playlistDir.mkdirs();
        }

        File playlistFile = new File(playlistDir, "playlist.m3u");
        /*
        try (FileWriter writer = new FileWriter(playlistFile)) {
            writer.write("#EXTM3U\n");
            for (String songPath : playlist) {
                writer.write("#EXTINF:-1," + new File(songPath).getName() + "\n");
                writer.write(songPath + "\n");
            }
            Toast.makeText(this, "播放列表已保存", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "无法保存播放列表", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private long firstBackTime;
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstBackTime > 2000) {
            Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
            firstBackTime = System.currentTimeMillis();
            return;
        }

        super.onBackPressed();
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
