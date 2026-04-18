package com.example.musicapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.MainActivity;
import com.example.musicapp.R;
import com.example.musicapp.adapters.SongAdapter;
import com.example.musicapp.models.Playlist;
import com.example.musicapp.models.Song;
import com.example.musicapp.player.MusicPlayerManager;
import com.example.musicapp.adapters.PlaylistManager;

import java.util.List;

public class PlaylistDetailFragment extends Fragment {

    private static final String ARG_PLAYLIST = "playlist";

    private Playlist playlist;

    private RecyclerView rvSongs;
    private SongAdapter songAdapter;

    private TextView tvPlaylistName, tvSongCount;
    private ImageView ivPlaylistCover;

    private ImageButton btnBack;
    private Button btnPlay, btnShuffle;

    private MusicPlayerManager playerManager;
    private PlaylistManager playlistManager;
    private List<Song> allSongs;

    public static PlaylistDetailFragment newInstance(Playlist playlist) {
        PlaylistDetailFragment fragment = new PlaylistDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLAYLIST, playlist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playlist = (Playlist) getArguments().getSerializable(ARG_PLAYLIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_playlist_detail, container, false);

        playerManager = MusicPlayerManager.getInstance(requireContext());
        playlistManager = PlaylistManager.getInstance(requireContext());
        allSongs = ((MainActivity) requireActivity()).getAllSongs();

        initViews(view);
        loadSongs();

        return view;
    }

    private void initViews(View view) {

        rvSongs = view.findViewById(R.id.rvSongs);

        tvPlaylistName = view.findViewById(R.id.tvPlaylistName);
        tvSongCount = view.findViewById(R.id.tvSongCount);
        ivPlaylistCover = view.findViewById(R.id.ivPlaylistCover);

        btnBack = view.findViewById(R.id.btnBack);
        btnPlay = view.findViewById(R.id.btnPlay);
        btnShuffle = view.findViewById(R.id.btnShuffle);

        rvSongs.setLayoutManager(new LinearLayoutManager(requireContext()));

        tvPlaylistName.setText(playlist.getName());
        ivPlaylistCover.setImageResource(R.drawable.thumb_song);

        // BACK
        btnBack.setOnClickListener(v ->
                requireActivity().onBackPressed()
        );

        // PLAY
        btnPlay.setOnClickListener(v -> {
            if (playlist.getSongs() != null && !playlist.getSongs().isEmpty()) {
                playerManager.playSong(
                        playlist.getSongs().get(0),
                        playlist.getSongs()
                );
                ((MainActivity) requireActivity()).openFullPlayer();
            } else {
                Toast.makeText(requireContext(),
                        "Playlist kosong",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // SHUFFLE
        btnShuffle.setOnClickListener(v -> {
            if (playlist.getSongs() != null && !playlist.getSongs().isEmpty()) {

                if (!playerManager.isShuffled()) {
                    playerManager.toggleShuffle();
                }

                playerManager.playSong(
                        playlist.getSongs().get(0),
                        playlist.getSongs()
                );

                ((MainActivity) requireActivity()).openFullPlayer();
            } else {
                Toast.makeText(requireContext(),
                        "Playlist kosong",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSongs() {

        List<Song> songs = playlist.getSongs();

        tvSongCount.setText(songs.size() + " songs");

        songAdapter = new SongAdapter(songs, new SongAdapter.OnSongClickListener() {

            @Override
            public void onSongClick(Song song, int position) {
                playerManager.playSong(song, songs);
                ((MainActivity) requireActivity()).openFullPlayer();
            }

            @Override
            public void onMoreClick(Song song, int position, View v) {
                showSongMenu(song, v);
            }
        });

        rvSongs.setAdapter(songAdapter);
    }

    private void showSongMenu(Song song, View anchor) {

        PopupMenu popup = new PopupMenu(requireContext(), anchor);
        popup.getMenuInflater().inflate(R.menu.song_options_menu, popup.getMenu());

        if (!playlist.isSystem()) {
            popup.getMenu().add("Remove from Playlist");
        }

        popup.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (id == R.id.action_play_next) {

                playerManager.addToQueue(song);
                Toast.makeText(requireContext(),
                        "Added to queue",
                        Toast.LENGTH_SHORT).show();
                return true;

            } else if (id == R.id.action_favorite) {

                playlistManager.toggleFavourite(song, allSongs);
                return true;

            } else if (item.getTitle().equals("Remove from Playlist")) {

                playlistManager.removeSongFromPlaylist(song, playlist);
                loadSongs();
                return true;
            }

            return false;
        });

        popup.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSongs();
    }
}