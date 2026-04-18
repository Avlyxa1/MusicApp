package com.example.musicapp.models;

import java.io.Serializable;

public class Song implements Serializable {
    private int id;
    private String title;
    private String artist;
    private String album;
    private int resourceId;
    private int duration;
    private boolean isFavorite;

    public Song(int id, String title, String artist, String album, int resourceId, int duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.resourceId = resourceId;
        this.duration = duration;
        this.isFavorite = false;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public int getResourceId() { return resourceId; }
    public int getDuration() { return duration; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public String getFormattedDuration() {
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}