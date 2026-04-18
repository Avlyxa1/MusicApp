package com.example.musicapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.musicapp.fragments.HomeFragment;
import com.example.musicapp.fragments.LibraryFragment;
import com.example.musicapp.fragments.PlayerFragment;
import com.example.musicapp.models.Song;
import com.example.musicapp.player.MusicPlayerManager;
import com.example.musicapp.utils.MusicLoader;
import com.example.musicapp.adapters.PlaylistManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MusicPlayerManager.OnPlayerEventListener {

    private BottomNavigationView bottomNav;
    private View miniPlayer;
    private ImageView ivMiniThumb, ivMiniPlayPause;
    private TextView tvMiniTitle, tvMiniArtist;
    private MusicPlayerManager playerManager;
    private List<Song> allSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        allSongs = MusicLoader.loadAllSongs(this);
        playerManager = MusicPlayerManager.getInstance(this);
        playerManager.setListener(this);

        PlaylistManager.getInstance(this).syncFavourites(allSongs);

        initViews();
        setupBottomNav();
        loadFragment(new HomeFragment());
    }

    private void initViews() {
        bottomNav = findViewById(R.id.bottomNav);
        miniPlayer = findViewById(R.id.miniPlayer);
        ivMiniThumb = findViewById(R.id.ivMiniThumb);
        ivMiniPlayPause = findViewById(R.id.ivMiniPlayPause);
        tvMiniTitle = findViewById(R.id.tvMiniTitle);
        tvMiniArtist = findViewById(R.id.tvMiniArtist);

        miniPlayer.setOnClickListener(v -> openFullPlayer());

        ivMiniPlayPause.setOnClickListener(v -> playerManager.togglePlayPause());

        // Initially hide mini player
        miniPlayer.setVisibility(View.GONE);
    }

    private void setupBottomNav() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (id == R.id.nav_library) {
                loadFragment(new LibraryFragment());
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public void openFullPlayer() {
        if (playerManager.getCurrentSong() == null) return;
        PlayerFragment playerFragment = new PlayerFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
                .add(R.id.fragmentContainer, playerFragment)
                .addToBackStack("player")
                .commit();
    }

    public List<Song> getAllSongs() { return allSongs; }

    @Override
    public void onSongChanged(Song song) {
        runOnUiThread(() -> {
            miniPlayer.setVisibility(View.VISIBLE);
            tvMiniTitle.setText(song.getTitle());
            tvMiniArtist.setText(song.getArtist());
            ivMiniThumb.setImageResource(R.drawable.thumb_song);
        });
    }

    @Override
    public void onPlayStateChanged(boolean isPlaying) {
        runOnUiThread(() -> {
            ivMiniPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        });
    }

    @Override
    public void onProgressChanged(int progress, int duration) {}

    @Override
    public void onQueueChanged() {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerManager.setListener(null);
    }
}