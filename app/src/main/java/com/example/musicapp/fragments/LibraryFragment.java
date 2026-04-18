package com.example.musicapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.MainActivity;
import com.example.musicapp.R;
import com.example.musicapp.adapters.PlaylistAdapter;
import com.example.musicapp.adapters.SongAdapter;
import com.example.musicapp.models.Playlist;
import com.example.musicapp.models.Song;
import com.example.musicapp.player.MusicPlayerManager;
import com.example.musicapp.adapters.PlaylistManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class LibraryFragment extends Fragment {

    private RecyclerView rvPlaylists;
    private PlaylistAdapter playlistAdapter;
    private FloatingActionButton fabNewPlaylist;
    private List<Song> allSongs;
    private PlaylistManager playlistManager;
    private MusicPlayerManager playerManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        allSongs = ((MainActivity) requireActivity()).getAllSongs();
        playlistManager = PlaylistManager.getInstance(requireContext());
        playerManager = MusicPlayerManager.getInstance(requireContext());

        initViews(view);
        loadPlaylists();

        return view;
    }

    private void initViews(View view) {
        rvPlaylists = view.findViewById(R.id.rvPlaylists);
        fabNewPlaylist = view.findViewById(R.id.fabNewPlaylist);

        rvPlaylists.setLayoutManager(new LinearLayoutManager(requireContext()));

        fabNewPlaylist.setOnClickListener(v -> showCreatePlaylistDialog());
    }

    private void loadPlaylists() {
        List<Playlist> playlists = playlistManager.getAllPlaylists();
        playlistAdapter = new PlaylistAdapter(playlists, new PlaylistAdapter.OnPlaylistClickListener() {
            @Override
            public void onPlaylistClick(Playlist playlist) {
                openPlaylistDetail(playlist);
            }

            @Override
            public void onPlaylistLongClick(Playlist playlist) {
                if (!playlist.isSystem()) showPlaylistMenu(playlist);
            }
        });
        rvPlaylists.setAdapter(playlistAdapter);
    }

    private void openPlaylistDetail(Playlist playlist) {
        PlaylistDetailFragment fragment = PlaylistDetailFragment.newInstance(playlist);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void showCreatePlaylistDialog() {
        EditText input = new EditText(requireContext());
        input.setHint("Playlist name");
        input.setPadding(48, 32, 48, 16);

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("New Playlist")
                .setView(input)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        playlistManager.createPlaylist(name);
                        loadPlaylists();
                    } else {
                        Toast.makeText(requireContext(), "Please enter a name", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showPlaylistMenu(Playlist playlist) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle(playlist.getName())
                .setItems(new String[]{"Rename", "Delete"}, (dialog, which) -> {
                    if (which == 0) showRenameDialog(playlist);
                    else showDeleteConfirm(playlist);
                })
                .show();
    }

    private void showRenameDialog(Playlist playlist) {
        EditText input = new EditText(requireContext());
        input.setText(playlist.getName());
        input.setPadding(48, 32, 48, 16);

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Rename Playlist")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        playlist.setName(name);
                        playlistManager.save();
                        loadPlaylists();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirm(Playlist playlist) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Playlist")
                .setMessage("Delete \"" + playlist.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    playlistManager.deletePlaylist(playlist);
                    loadPlaylists();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPlaylists();
    }
}