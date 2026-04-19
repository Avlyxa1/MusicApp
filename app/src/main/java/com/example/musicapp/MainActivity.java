package com.example.musicapp;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
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

    // Interface ini dipanggil setiap kali lagu berubah, HomeFragment implements ini
    public interface OnSongChangedListener {
        void onRecentSongsUpdated();
    }

    private OnSongChangedListener songChangedListener;

    private BottomNavigationView bottomNav;
    private View miniPlayer;
    private ImageView ivMiniThumb, ivMiniPlayPause;
    private TextView tvMiniTitle, tvMiniArtist;
    private MusicPlayerManager playerManager;
    private List<Song> allSongs;

    int[] songList = {
            R.raw.song1,
            R.raw.song2,
            R.raw.song3,
            R.raw.song4,
            R.raw.song5,
            R.raw.song6,
            R.raw.song7,
            R.raw.song8,
            R.raw.song9,
            R.raw.song10
    };

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

    // HomeFragment memanggil ini saat onAttach untuk mendaftar sebagai listener
    public void setSongChangedListener(OnSongChangedListener listener) {
        this.songChangedListener = listener;
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

    private Bitmap getAlbumArtFromRaw(Context context, int rawId) {
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            AssetFileDescriptor afd = context.getResources().openRawResourceFd(rawId);
            mmr.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            byte[] artBytes = mmr.getEmbeddedPicture();
            mmr.release();
            if (artBytes != null) {
                return BitmapFactory.decodeByteArray(artBytes, 0, artBytes.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onSongChanged(Song song) {
        runOnUiThread(() -> {
            // Update mini player
            miniPlayer.setVisibility(View.VISIBLE);
            tvMiniTitle.setText(song.getTitle());
            tvMiniArtist.setText(song.getArtist());

            int index = song.getId() - 1;
            Bitmap bitmap = getAlbumArtFromRaw(this, songList[index]);
            if (bitmap != null) {
                ivMiniThumb.setImageBitmap(bitmap);
            } else {
                ivMiniThumb.setImageResource(R.drawable.thumb_song);
            }

            // Notify HomeFragment supaya RecentSongAdapter langsung diupdate
            if (songChangedListener != null) {
                songChangedListener.onRecentSongsUpdated();
            }
        });
    }

    @Override
    public void onPlayStateChanged(boolean isPlaying) {
        runOnUiThread(() ->
                ivMiniPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play)
        );
    }

    @Override
    public void onProgressChanged(int progress, int duration) {}

    @Override
    public void onQueueChanged() {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerManager.setListener(null);
        songChangedListener = null;
    }
}