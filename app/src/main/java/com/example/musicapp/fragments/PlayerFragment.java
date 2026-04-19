package com.example.musicapp.fragments;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.MainActivity;
import com.example.musicapp.R;
import com.example.musicapp.adapters.QueueAdapter;
import com.example.musicapp.models.Song;
import com.example.musicapp.player.MusicPlayerManager;
import com.example.musicapp.adapters.PlaylistManager;

public class PlayerFragment extends Fragment implements MusicPlayerManager.OnPlayerEventListener {

    private ImageView ivAlbumArt, ivFavorite;
    private TextView tvTitle, tvArtist, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;
    private ImageButton btnBack, btnPrev, btnPlayPause, btnNext, btnShuffle, btnRepeat, btnQueue;
    private RecyclerView rvQueue;
    private View queueContainer;
    private QueueAdapter queueAdapter;
    private MusicPlayerManager playerManager;
    private PlaylistManager playlistManager;
    private boolean isQueueVisible = false;
    private boolean isTracking = false;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        playerManager = MusicPlayerManager.getInstance(requireContext());
        playlistManager = PlaylistManager.getInstance(requireContext());

        initViews(view);
        updateUI();
        playerManager.setListener(this);

        return view;
    }

    private void initViews(View view) {
        ivAlbumArt = view.findViewById(R.id.ivAlbumArt);
        ivFavorite = view.findViewById(R.id.ivFavorite);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvArtist = view.findViewById(R.id.tvArtist);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        tvTotalTime = view.findViewById(R.id.tvTotalTime);
        seekBar = view.findViewById(R.id.seekBar);
        btnBack = view.findViewById(R.id.btnBack);
        btnPrev = view.findViewById(R.id.btnPrev);
        btnPlayPause = view.findViewById(R.id.btnPlayPause);
        btnNext = view.findViewById(R.id.btnNext);
        btnShuffle = view.findViewById(R.id.btnShuffle);
        btnRepeat = view.findViewById(R.id.btnRepeat);
        btnQueue = view.findViewById(R.id.btnQueue);
        rvQueue = view.findViewById(R.id.rvQueue);
        queueContainer = view.findViewById(R.id.queueContainer);

        rvQueue.setLayoutManager(new LinearLayoutManager(requireContext()));

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        btnPlayPause.setOnClickListener(v -> playerManager.togglePlayPause());
        btnPrev.setOnClickListener(v -> playerManager.playPrevious());
        btnNext.setOnClickListener(v -> playerManager.playNext());

        btnShuffle.setOnClickListener(v -> {
            playerManager.toggleShuffle();
            updateShuffleRepeatButtons();
        });

        btnRepeat.setOnClickListener(v -> {
            playerManager.toggleRepeat();
            updateShuffleRepeatButtons();
        });

        btnQueue.setOnClickListener(v -> toggleQueue());

        ivFavorite.setOnClickListener(v -> {
            Song current = playerManager.getCurrentSong();
            if (current != null) {
                playlistManager.toggleFavourite(current, ((MainActivity) requireActivity()).getAllSongs());
                updateFavoriteIcon(current);
                Toast.makeText(requireContext(),
                        current.isFavorite() ? "Added to Favourites" : "Removed from Favourites",
                        Toast.LENGTH_SHORT).show();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tvCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTracking = false;
                playerManager.seekTo(seekBar.getProgress());
            }
        });
    }

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

    // Set album art ke ivAlbumArt, fallback ke thumb_song jika MP3 tidak punya embedded art
    private void setAlbumArt(Song song) {
        int index = song.getId() - 1;
        if (index >= 0 && index < songList.length) {
            Bitmap bitmap = getAlbumArtFromRaw(requireContext(), songList[index]);
            if (bitmap != null) {
                ivAlbumArt.setImageBitmap(bitmap);
                return;
            }
        }
        ivAlbumArt.setImageResource(R.drawable.thumb_song);
    }

    private void toggleQueue() {
        isQueueVisible = !isQueueVisible;
        queueContainer.setVisibility(isQueueVisible ? View.VISIBLE : View.GONE);
        if (isQueueVisible) {
            updateQueueList();
        }
    }

    private void updateQueueList() {
        queueAdapter = new QueueAdapter(playerManager.getQueue(), playerManager.getCurrentIndex(),
                new QueueAdapter.OnQueueItemListener() {
                    @Override
                    public void onRemoveClick(int position) {
                        playerManager.removeFromQueue(position);
                        updateQueueList();
                    }

                    @Override
                    public void onItemClick(int position) {
                        Song song = playerManager.getQueue().get(position);
                        playerManager.playSong(song, playerManager.getQueue());
                        updateQueueList();
                    }
                });
        rvQueue.setAdapter(queueAdapter);
    }

    private void updateUI() {
        Song song = playerManager.getCurrentSong();
        if (song == null) return;

        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());

        // Ganti dari setImageResource(thumb_song) ke setAlbumArt()
        setAlbumArt(song);

        int duration = playerManager.getDuration();
        seekBar.setMax(duration);
        tvTotalTime.setText(formatTime(duration));
        tvCurrentTime.setText(formatTime(playerManager.getCurrentPosition()));
        seekBar.setProgress(playerManager.getCurrentPosition());

        btnPlayPause.setImageResource(playerManager.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);

        updateFavoriteIcon(song);
        updateShuffleRepeatButtons();
    }

    private void updateFavoriteIcon(Song song) {
        ivFavorite.setImageResource(playlistManager.isFavourite(song) ?
                R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
    }

    private void updateShuffleRepeatButtons() {
        btnShuffle.setAlpha(playerManager.isShuffled() ? 1.0f : 0.4f);
        btnRepeat.setAlpha(playerManager.isRepeat() ? 1.0f : 0.4f);
    }

    private String formatTime(int ms) {
        int seconds = ms / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public void onSongChanged(Song song) {
        if (isAdded()) requireActivity().runOnUiThread(this::updateUI);
    }

    @Override
    public void onPlayStateChanged(boolean isPlaying) {
        if (isAdded()) {
            requireActivity().runOnUiThread(() ->
                    btnPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play));
        }
    }

    @Override
    public void onProgressChanged(int progress, int duration) {
        if (isAdded() && !isTracking) {
            requireActivity().runOnUiThread(() -> {
                seekBar.setMax(duration);
                seekBar.setProgress(progress);
                tvCurrentTime.setText(formatTime(progress));
                tvTotalTime.setText(formatTime(duration));
            });
        }
    }

    @Override
    public void onQueueChanged() {
        if (isAdded() && isQueueVisible) {
            requireActivity().runOnUiThread(this::updateQueueList);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        playerManager.setListener((MainActivity) requireActivity());
    }
}