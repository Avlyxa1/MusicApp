package com.example.musicapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.MainActivity;
import com.example.musicapp.R;
import com.example.musicapp.adapters.RecentSongAdapter;
import com.example.musicapp.adapters.SongAdapter;
import com.example.musicapp.models.Playlist;
import com.example.musicapp.models.Song;
import com.example.musicapp.player.MusicPlayerManager;
import com.example.musicapp.adapters.PlaylistManager;
import com.example.musicapp.utils.RecentManager;

import java.util.List;

public class HomeFragment extends Fragment implements MainActivity.OnSongChangedListener {

    private RecyclerView rvRecentSongs, rvAllSongs;
    private RecentSongAdapter recentAdapter;
    private SongAdapter allSongsAdapter;
    private List<Song> allSongs;
    private MusicPlayerManager playerManager;
    private PlaylistManager playlistManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            ((MainActivity) context).setSongChangedListener(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setSongChangedListener(null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        allSongs = ((MainActivity) requireActivity()).getAllSongs();
        playerManager = MusicPlayerManager.getInstance(requireContext());
        playlistManager = PlaylistManager.getInstance(requireContext());

        initViews(view);
        loadData();

        return view;
    }

    private void initViews(View view) {
        rvRecentSongs = view.findViewById(R.id.rvRecentSongs);
        rvAllSongs = view.findViewById(R.id.rvAllSongs);

        rvRecentSongs.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        rvAllSongs.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Wajib false agar ScrollView parent yang handle scroll, bukan RecyclerView sendiri
        rvAllSongs.setNestedScrollingEnabled(false);
        // false karena jumlah item bisa berubah
        rvAllSongs.setHasFixedSize(false);
    }

    private void loadData() {
        List<Song> recentSongs = RecentManager.getInstance(requireContext()).getRecentSongs(allSongs);

        if (recentAdapter == null) {
            recentAdapter = new RecentSongAdapter(recentSongs, (song, position) -> {
                playAndNavigate(song, recentSongs);
            });
            rvRecentSongs.setAdapter(recentAdapter);
        } else {
            recentAdapter.updateSongs(recentSongs);
        }

        allSongsAdapter = new SongAdapter(allSongs, new SongAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(Song song, int position) {
                playAndNavigate(song, allSongs);
            }
            @Override
            public void onMoreClick(Song song, int position, View v) {
                showSongMenu(song, v);
            }
        });

        rvAllSongs.setAdapter(allSongsAdapter);

        updatePlayingIndicators();
    }

    @Override
    public void onRecentSongsUpdated() {
        if (!isAdded() || isDetached()) return;
        List<Song> recentSongs = RecentManager.getInstance(requireContext()).getRecentSongs(allSongs);
        if (recentAdapter != null) {
            recentAdapter.updateSongs(recentSongs);
        }
        updatePlayingIndicators();
    }

    private void playAndNavigate(Song song, List<Song> list) {
        playerManager.playSong(song, list);
        ((MainActivity) requireActivity()).openFullPlayer();
    }

    private void showSongMenu(Song song, View anchor) {
        PopupMenu popup = new PopupMenu(requireContext(), anchor);
        popup.getMenuInflater().inflate(R.menu.song_options_menu, popup.getMenu());

        popup.getMenu().findItem(R.id.action_favorite).setTitle(
                playlistManager.isFavourite(song) ? "Remove from Favourites" : "Add to Favourites"
        );

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_play_next) {
                playerManager.addToQueue(song);
                Toast.makeText(requireContext(), "Added to queue", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.action_favorite) {
                playlistManager.toggleFavourite(song, allSongs);
                Toast.makeText(requireContext(),
                        song.isFavorite() ? "Added to Favourites" : "Removed from Favourites",
                        Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.action_add_to_playlist) {
                showAddToPlaylistDialog(song);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showAddToPlaylistDialog(Song song) {
        List<Playlist> playlists = playlistManager.getAllPlaylists();
        String[] playlistNames = new String[playlists.size()];
        for (int i = 0; i < playlists.size(); i++) {
            playlistNames[i] = playlists.get(i).getName();
        }

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Add to Playlist")
                .setItems(playlistNames, (dialog, which) -> {
                    playlistManager.addSongToPlaylist(song, playlists.get(which));
                    Toast.makeText(requireContext(), "Added to " + playlists.get(which).getName(), Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void updatePlayingIndicators() {
        Song current = playerManager.getCurrentSong();
        if (current != null) {
            if (allSongsAdapter != null) allSongsAdapter.setCurrentPlayingId(current.getId());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}