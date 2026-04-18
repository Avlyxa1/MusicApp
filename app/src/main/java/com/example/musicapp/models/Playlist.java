package com.example.musicapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Playlist implements Serializable {
    private int id;
    private String name;
    private List<Song> songs;
    private boolean isSystem;

    public Playlist(int id, String name, boolean isSystem) {
        this.id = id;
        this.name = name;
        this.songs = new ArrayList<>();
        this.isSystem = isSystem;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Song> getSongs() { return songs; }
    public boolean isSystem() { return isSystem; }

    public void addSong(Song song) {
        for (Song s : songs) {
            if (s.getId() == song.getId()) return;
        }
        songs.add(song);
    }

    public void removeSong(Song song) {
        songs.removeIf(s -> s.getId() == song.getId());
    }

    public boolean containsSong(Song song) {
        for (Song s : songs) {
            if (s.getId() == song.getId()) return true;
        }
        return false;
    }

    public int getSongCount() { return songs.size(); }
}