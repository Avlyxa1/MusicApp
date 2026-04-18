package com.example.musicapp.adapters;

import android.content.Context;

import com.example.musicapp.models.Playlist;
import com.example.musicapp.models.Song;
import com.example.musicapp.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class PlaylistManager {
    private static com.example.musicapp.adapters.PlaylistManager instance;
    private List<Playlist> playlists;
    private Playlist favouritePlaylist;
    private Context context;
    private List<Integer> favoriteIds;
    private static int nextPlaylistId = 100;

    private PlaylistManager(Context context) {
        this.context = context.getApplicationContext();
        load();
    }

    public static synchronized com.example.musicapp.adapters.PlaylistManager getInstance(Context context) {
        if (instance == null) {
            instance = new com.example.musicapp.adapters.PlaylistManager(context);
        }
        return instance;
    }

    private void load() {
        favoriteIds = FileUtils.loadFavoriteIds(context);
        playlists = FileUtils.loadPlaylists(context);

        favouritePlaylist = null;
        for (Playlist p : playlists) {
            if (p.getName().equals("Favourites") && p.isSystem()) {
                favouritePlaylist = p;
                break;
            }
        }

        if (favouritePlaylist == null) {
            favouritePlaylist = new Playlist(1, "Favourites", true);
            playlists.add(0, favouritePlaylist);
        }

        for (Playlist p : playlists) {
            if (p.getId() >= nextPlaylistId) {
                nextPlaylistId = p.getId() + 1;
            }
        }
    }

    public void syncFavourites(List<Song> allSongs) {
        favouritePlaylist.getSongs().clear();
        for (Song song : allSongs) {
            if (favoriteIds.contains(song.getId())) {
                song.setFavorite(true);
                favouritePlaylist.addSong(song);
            }
        }
    }

    public void toggleFavourite(Song song, List<Song> allSongs) {
        if (favoriteIds.contains(song.getId())) {
            favoriteIds.remove(Integer.valueOf(song.getId()));
            song.setFavorite(false);
            favouritePlaylist.removeSong(song);
        } else {
            favoriteIds.add(song.getId());
            song.setFavorite(true);
            favouritePlaylist.addSong(song);
        }
        FileUtils.saveFavoriteIds(context, favoriteIds);
        save();
    }

    public boolean isFavourite(Song song) {
        return favoriteIds.contains(song.getId());
    }

    public Playlist createPlaylist(String name) {
        Playlist playlist = new Playlist(nextPlaylistId++, name, false);
        playlists.add(playlist);
        save();
        return playlist;
    }

    public void deletePlaylist(Playlist playlist) {
        if (!playlist.isSystem()) {
            playlists.remove(playlist);
            save();
        }
    }

    public void addSongToPlaylist(Song song, Playlist playlist) {
        playlist.addSong(song);
        save();
    }

    public void removeSongFromPlaylist(Song song, Playlist playlist) {
        if (!playlist.isSystem()) {
            playlist.removeSong(song);
            save();
        }
    }

    public List<Playlist> getAllPlaylists() { return playlists; }
    public Playlist getFavouritePlaylist() { return favouritePlaylist; }

    public List<Playlist> getUserPlaylists() {
        List<Playlist> result = new ArrayList<>();
        for (Playlist p : playlists) {
            if (!p.isSystem()) result.add(p);
        }
        return result;
    }

    public void save() {
        FileUtils.savePlaylists(context, playlists);
    }
}